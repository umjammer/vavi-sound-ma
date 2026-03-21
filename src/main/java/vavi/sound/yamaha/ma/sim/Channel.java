/*
 * https://github.com/but80/fmfm.core
 */

package vavi.sound.yamaha.ma.sim;

import java.lang.System.Logger;

import vavi.sound.yamaha.ma.sim.EnvelopeGenerator.Stage;

import static java.lang.System.getLogger;
import static vavi.sound.yamaha.ma.ymf.ymfdata.Data.CarrierMatrix;
import static vavi.sound.yamaha.ma.ymf.ymfdata.Data.LFOFrequency;
import static vavi.sound.yamaha.ma.ymf.ymfdata.Data.ModTableIndexShift;
import static vavi.sound.yamaha.ma.ymf.ymfdata.Data.ModulatorMatrix;
import static vavi.sound.yamaha.ma.ymf.ymfdata.Data.ModulatorMultiplier;
import static vavi.sound.yamaha.ma.ymf.ymfdata.Data.PanTable;
import static vavi.sound.yamaha.ma.ymf.ymfdata.Data.SampleRate;
import static vavi.sound.yamaha.ma.ymf.ymfdata.Data.VolumeTable;


/**
 * Channel is the channel of the audio source.
 * <pre>
 * ==================================================
 * MA-5
 *
 * ALG=0
 * (FB)1 -> 2 -> OUT
 *
 * ALG=1
 * (FB)1 -> | -> OUT
 * 2 -> |
 *
 * ALG=2
 * (FB)1 -> | -> OUT
 * 2 -> |
 * (FB)3 -> |
 * 4 -> |
 *
 * ALG=3
 * (FB)1 ------> | -> 4 -> OUT
 * 2 -> 3 -> |
 *
 * ALG=4
 * (FB)1 -> 2 -> 3 -> 4 -> OUT
 *
 * ALG=5
 * (FB)1 -> 2 -> | -> OUT
 * (FB)3 -> 4 -> |
 *
 * ALG=6
 * (FB)1 -----------> | -> OUT
 * 2 -> 3 -> 4 -> |
 *
 * ALG=7
 * (FB)1 ------> | -> OUT
 * 2 -> 3 -> |
 * 4 ------> |
 *
 * ==================================================
 * OPL3
 *
 * | ADDR | 7 | 6 | 5 | 4 | 3 | 2 | 1 | 0 |
 * |C0..C8|CHD|CHC|CHB|CHA|    F B    |CNT|
 *
 * ===== 2 operators mode =====
 *
 * CNT = 0
 * (FB)OP1 -> OP2 -> OUT
 *
 * CNT = 1
 * (FB)OP1 -> |
 * OP2 -> | -> OUT
 *
 * ===== 4 operators mode =====
 *
 * |Channel No.|  1  |  2  |  3  |  4  |  5  |  6  |
 * |CNT Address|C0,C3|C1,C4|C2,C5|C0,C3|C1,C4|C2,C5|
 * |    A1     |       L         |        H        |
 *
 * CNT(Cn) = 0, CNT(Cn+3) = 0
 * (FB)OP1 -> OP2 -> OP3 -> OP4 -> OUT
 *
 * CNT(Cn) = 0, CNT(Cn+3) = 1
 * (FB)OP1 -> OP2 -> |
 * OP3 -> OP4 -> | -> OUT
 *
 * CNT(Cn) = 1, CNT(Cn+3) = 0
 * (FB)OP1 ---------------> |
 * OP2 -> OP3 -> OP4 -> | -> OUT
 *
 * CNT(Cn) = 1, CNT(Cn+3) = 1
 * (FB)OP1 --------> |
 * OP2 -> OP3 -> |
 * OP4 --------> | -> OUT
 * </pre>
 */
public class Channel {

    private static final Logger logger = getLogger(Channel.class.getName());

    static final int noModulator = 0;

    int channelID;
    int midiChannelID;

    Chip chip;
    int fnum;
    int kon;
    int block;
    int alg;
    int panpot;
    int chpan;
    int volume;
    int expression;
    int velocity;
    int bo;

    double feedbackBlendPrev;
    double feedbackBlendCurr;
    double feedback1Prev;
    double feedback1Curr;
    double feedback3Prev;
    double feedback3Curr;
    double feedbackOut1;
    double feedbackOut3;
    double attenuationCoef;
    long modIndexFrac64;
    long lfoFrequency;
    double panCoefL;
    double panCoefR;

    Operator[] operators = new Operator[4];

    Channel(int channelID, Chip chip) {
        this.chip = chip;
        this.channelID = channelID;

        // 48000Hz:     |prev|curr|
        // 44100Hz: | prev | curr |
        this.feedbackBlendCurr = .5 * SampleRate / chip.sampleRate;
        if (1.0 < this.feedbackBlendCurr) {
            this.feedbackBlendCurr = 1.0;
        }
        this.feedbackBlendPrev = 1.0 - this.feedbackBlendCurr;

        for (var i = 0; i < this.operators.length; i++) {
            this.operators[i] = new Operator(channelID, i, chip);
        }

        this.resetAll();
    }

    void reset() {
        // TODO Does the modulation reset with each note?
        this.modIndexFrac64 = 0;
        this.feedback1Prev = .0;
        this.feedback1Curr = .0;
        this.feedback3Prev = .0;
        this.feedback3Curr = .0;
        this.feedbackOut1 = .0;
        this.feedbackOut3 = .0;
        for (var op : this.operators) {
            op.phaseGenerator.reset();
            op.envelopeGenerator.reset();
        }
    }

    void resetAll() {
        this.midiChannelID = -1;
        this.fnum = 0;
        this.kon = 0;
        this.block = 0;
        this.alg = 0;
        this.panpot = 15;
        this.chpan = 64;
        this.volume = 100;
        this.expression = 127;
        this.velocity = 0;
        this.bo = 1;
        this.setLFO(0);
        this.updatePanCoef();
        this.updateAttenuation();
        for (var op : this.operators) {
            op.resetAll();
        }
    }

    boolean isOff() {
        int i = 0;
        for (var op : this.operators) {
            if ((!CarrierMatrix[this.alg][i++])) {
                continue;
            }
            if ((op.envelopeGenerator.stage != Stage.Off)) {
                return false;
            }
        }
        return true;
    }

    double currentLevel() {
        var result = .0;
        int i = 0;
        for (var op : this.operators) {
            if ((CarrierMatrix[this.alg][i++])) {
                var eg = op.envelopeGenerator;
                var v = eg.currentLevel * eg.kslTlCoef;
                if (result < v) {
                    result = v;
                }
            }
        }
        return result;
    }

    String dump() {
        var lv = (int) ((96.0 + Math.log10(this.currentLevel()) * 20.0) / 8.0);
        var lvstr = "|".repeat(lv);
        StringBuilder result = new StringBuilder("[%02d] midi=%02d alg=%d pan=%03d+%03d vol=%03d exp=%03d vel=%03d freq=%03d+%d-%d modidx=%04d %s\n".formatted(
                this.channelID,
                this.midiChannelID,
                this.alg,
                this.panpot,
                this.chpan,
                this.volume,
                this.expression,
                this.velocity,
                // this.attenuationCoef,
                this.fnum,
                this.block,
                this.bo,
                this.modIndexFrac64 >> ModTableIndexShift,
                // this.lfoFrequency,
                // this.panCoefL,
                // this.panCoefR,
                lvstr));
        for (var op : this.operators) {
            result.append("  ").append(op.dump()).append("\n");
        }
        return result.toString();
    }

    void setKON(int v) {
        if (v == 0) {
            this.keyOff();
            if (this.isOff()) {
                this.resetAll();
            }
        } else {
            this.keyOn();
        }
    }

    void keyOn() {
        if (this.kon != 0) {
            return;
        }
        for (var op : this.operators) {
            op.keyOn();
        }
        this.kon = 1;
    }

    void keyOff() {
        if ((this.kon == 0)) {
            return;
        }
        for (var op : this.operators) {
            op.keyOff();
        }
        this.kon = 0;
    }

    void setBLOCK(int v) {
        this.block = v;
        this.updateFrequency();
    }

    void setFNUM(int v) {
        this.fnum = v;
        this.updateFrequency();
    }

    void setALG(int v) {
        if (this.alg != v) {
            this.reset();
        }
        this.alg = v;
        this.feedback1Prev = 0;
        this.feedback1Curr = 0;
        this.feedback3Prev = 0;
        this.feedback3Curr = 0;
        int i = 0;
        for (var op : this.operators) {
            op.isModulator = ModulatorMatrix[this.alg][i++];
        }
    }

    void setLFO(int v) {
        this.lfoFrequency = LFOFrequency[v];
    }

    void setPANPOT(int v) {
        this.panpot = v;
        this.updatePanCoef();
    }

    void setCHPAN(int v) {
        this.chpan = v;
        this.updatePanCoef();
    }

    void updatePanCoef() {
        var pan = this.chpan + (this.panpot - 15) * 4;
        if (pan < 0) {
            pan = 0;
        } else if (127 < pan) {
            pan = 127;
        }
        this.panCoefL = PanTable[pan][0];
        this.panCoefR = PanTable[pan][1];
    }

    void setVOLUME(int v) {
        this.volume = v;
        this.updateAttenuation();
    }

    void setEXPRESSION(int v) {
        this.expression = v;
        this.updateAttenuation();
    }

    void setVELOCITY(int v) {
        this.velocity = v;
        this.updateAttenuation();
    }

    void updateAttenuation() {
        this.attenuationCoef = VolumeTable[this.volume >> 2] * VolumeTable[this.expression >> 2] * VolumeTable[this.velocity >> 2];
    }

    void setBO(int v) {
        this.bo = v;
        this.updateFrequency();
    }

    double[] next() {
        double result = 0;
        double op1out = 0;
        double op2out;
        double op3out = 0;
        double op4out;

        var op1 = this.operators[0];
        var op2 = this.operators[1];
        var op3 = this.operators[2];
        var op4 = this.operators[3];

        var modIndex = (int) (this.modIndexFrac64 >> ModTableIndexShift);
        this.modIndexFrac64 += this.lfoFrequency;

        switch (this.alg) {

            case 0:
                // (FB)1 -> 2 -> OUT
                if (op2.envelopeGenerator.stage == Stage.Off) {
                    return new double[] {0, 0};
                }

                op1out = op1.next(modIndex, this.feedbackOut1);

                result = op2.next(modIndex, op1out * ModulatorMultiplier);

            case 1:
                // (FB)1 -> | -> OUT
                //     2 -> |
                if (op1.envelopeGenerator.stage == Stage.Off && op2.envelopeGenerator.stage == Stage.Off) {
                    return new double[] {0, 0};
                }

                op1out = op1.next(modIndex, this.feedbackOut1);
                op2out = op2.next(modIndex, noModulator);

                result = op1out + op2out;

            case 2:
                // (FB)1 -> | -> OUT
                //     2 -> |
                // (FB)3 -> |
                //     4 -> |
                if (op1.envelopeGenerator.stage == Stage.Off &&
                        op2.envelopeGenerator.stage == Stage.Off &&
                        op3.envelopeGenerator.stage == Stage.Off &&
                        op4.envelopeGenerator.stage == Stage.Off) {
                    return new double[] {0, 0};
                }

                op1out = op1.next(modIndex, this.feedbackOut1);
                op2out = op2.next(modIndex, noModulator);
                op3out = op3.next(modIndex, this.feedbackOut3);
                op4out = op4.next(modIndex, noModulator);

                result = op1out + op2out + op3out + op4out;

            case 3:
                // (FB)OP1 --------> | -> OP4 -> OUT
                //     OP2 -> OP3 -> |
                if (op4.envelopeGenerator.stage == Stage.Off) {
                    return new double[] {0, 0};
                }

                op1out = op1.next(modIndex, this.feedbackOut1);
                op2out = op2.next(modIndex, noModulator);
                op3out = op3.next(modIndex, op2out * ModulatorMultiplier);

                result = op4.next(modIndex, (op1out + op3out) * ModulatorMultiplier);

            case 4:
                // (FB)OP1 -> OP2 -> OP3 -> OP4 -> OUT
                if (op4.envelopeGenerator.stage == Stage.Off) {
                    return new double[] {0, 0};
                }

                op1out = op1.next(modIndex, this.feedbackOut1);
                op2out = op2.next(modIndex, op1out * ModulatorMultiplier);
                op3out = op3.next(modIndex, op2out * ModulatorMultiplier);

                result = op4.next(modIndex, op3out * ModulatorMultiplier);

            case 5:
                // (FB)OP1 -> OP2 -> | -> OUT
                // (FB)OP3 -> OP4 -> |
                if (op2.envelopeGenerator.stage == Stage.Off && op4.envelopeGenerator.stage == Stage.Off) {
                    return new double[] {0, 0};
                }

                op1out = op1.next(modIndex, this.feedbackOut1);
                op2out = op2.next(modIndex, op1out * ModulatorMultiplier);

                op3out = op3.next(modIndex, this.feedbackOut3);
                op4out = op4.next(modIndex, op3out * ModulatorMultiplier);

                result = op2out + op4out;

            case 6:
                // (FB)OP1 ---------------> | -> OUT
                //     OP2 -> OP3 -> OP4 -> |
                if (op1.envelopeGenerator.stage == Stage.Off && op4.envelopeGenerator.stage == Stage.Off) {
                    return new double[] {0, 0};
                }

                op1out = op1.next(modIndex, this.feedbackOut1);
                op2out = op2.next(modIndex, noModulator);
                op3out = op3.next(modIndex, op2out * ModulatorMultiplier);
                op4out = op4.next(modIndex, op3out * ModulatorMultiplier);

                result = op1out + op4out;

            case 7:
                // (FB)OP1 --------> | -> OUT
                //     OP2 -> OP3 -> |
                //     OP4 --------> |
                if (op1.envelopeGenerator.stage == Stage.Off &&
                        op3.envelopeGenerator.stage == Stage.Off &&
                        op4.envelopeGenerator.stage == Stage.Off) {
                    return new double[] {0, 0};
                }

                op1out = op1.next(modIndex, this.feedbackOut1);
                op2out = op2.next(modIndex, noModulator);
                op3out = op3.next(modIndex, op2out * ModulatorMultiplier);
                op4out = op4.next(modIndex, noModulator);

                result = op1out + op3out + op4out;
        }

        if (op1.feedbackCoef != .0) {
            this.feedback1Prev = this.feedback1Curr;
            this.feedback1Curr = op1out * op1.feedbackCoef;
            this.feedbackOut1 = this.feedback1Prev * this.feedbackBlendPrev + this.feedback1Curr * this.feedbackBlendCurr;
        }

        if (op3.feedbackCoef != .0) {
            this.feedback3Prev = this.feedback3Curr;
            this.feedback3Curr = op3out * op3.feedbackCoef;
            this.feedbackOut3 = this.feedback3Prev * this.feedbackBlendPrev + this.feedback3Curr * this.feedbackBlendCurr;
        }

        result *= this.attenuationCoef;
        return new double[] {result * this.panCoefL, result * this.panCoefR};
    }

    void updateFrequency() {
        for (var op : this.operators) {
            op.setFrequency(this.fnum, this.block, this.bo);
        }
    }

    public int getFNum() {
        return fnum;
    }
}
