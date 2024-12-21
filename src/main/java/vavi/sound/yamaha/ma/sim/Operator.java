/*
 * https://github.com/but80/fmfm.core
 */

package vavi.sound.yamaha.ma.sim;

import java.util.Arrays;

import vavi.sound.yamaha.ma.sim.EnvelopeGenerator.Stage;

import static vavi.sound.yamaha.ma.ymf.ymfdata.Data.FeedbackTable;
import static vavi.sound.yamaha.ma.ymf.ymfdata.Data.WaveformIndexShift;
import static vavi.sound.yamaha.ma.ymf.ymfdata.Data.WaveformLen;
import static vavi.sound.yamaha.ma.ymf.ymfdata.Data.WaveformLenBits;
import static vavi.sound.yamaha.ma.ymf.ymfdata.Data.Waveforms;


class Operator {

    boolean isModulator;

    int dt;
    int ksr;
    int mult;
    int ksl;
    int ar;
    int dr;
    int sl;
    int sr;
    int rr;
    int xof;
    int ws;
    double feedbackCoef;
    int keyScaleNumber;
    int fnum;
    int block;
    int bo;

    EnvelopeGenerator envelopeGenerator;

    Chip chip;
    int channelID;
    int operatorIndex;
    PhaseGenerator phaseGenerator;

    Operator(int channelID, int operatorIndex, Chip chip) {
        this.chip = chip;
        this.channelID = channelID;
        this.operatorIndex = operatorIndex;

        phaseGenerator = new PhaseGenerator(chip.sampleRate);
        envelopeGenerator = new EnvelopeGenerator(chip.sampleRate);
        isModulator = false;
        bo = 1;
    }


    void reset() {
        this.phaseGenerator.reset();
        this.envelopeGenerator.reset();
    }

    void resetAll() {
        this.bo = 1;
        this.phaseGenerator.resetAll();
        this.envelopeGenerator.resetAll();
    }

    String dump() {
        var eg = this.envelopeGenerator;
        var pg = this.phaseGenerator;

        var lvdb = Math.log10(eg.currentLevel) * 20.0;
        var lv = (int) ((96.0 + lvdb) / 8.0);
        if (lv < 0) {
            lv = 0;
        }
        var lvstr = "|".repeat(lv);

        var cm = "C";
        if (this.isModulator) {
            cm = "M";
        }
        var am = "-";
        if (eg.eam) {
            am = "%d".formatted(eg.dam);
        }
        var vb = "-";
        if (pg.evb) {
            vb = "%d".formatted(pg.dvb);
        }
        var phase = pg.phaseFrac64 >> WaveformIndexShift;
        byte[] phstr = ("        ").getBytes();
        phstr[(int) (phase >> (WaveformLenBits - 3))] = '|';
        return "%d: %s mul=%02d ws=%02d adssr=%02d,%02d,%02d,%02d,%02d tl=%f am=%s vb=%s dt=%d ksr=%d fb=%3.2f ksn=%02d ksl=%f st=%s ph=%s lv=%03d %s".formatted(
                this.operatorIndex,
                cm,
                this.mult,
                this.ws,
                this.ar,
                this.dr,
                this.sl,
                this.sr,
                this.rr,
                eg.tlCoef,
                am,
                vb,

                // actualAR        ,
                // arDiffPerSample ,
                // drCoefPerSample ,
                // srCoefPerSample ,
                // rrCoefPerSample ,
                // sustainLevel    ,
                // currentLevel    ,

                // this.phaseGenerator,
                this.dt,
                this.ksr,
                this.feedbackCoef,
                this.keyScaleNumber,
                eg.kslCoef,
                // this.fnum,
                // this.block,
                // this.bo,
                // this.xof,
                eg.stage,
                Arrays.toString(phstr),
                (int) (Math.floor(lvdb)),
                lvstr
        );
    }

    void setEAM(int v) {
        this.envelopeGenerator.eam = v != 0;
    }

    void setEVB(int v) {
        this.phaseGenerator.evb = v != 0;
    }

    void setDAM(int v) {
        this.envelopeGenerator.dam = v;
    }

    void setDVB(int v) {
        this.phaseGenerator.dvb = v;
    }

    void setDT(int v) {
        this.dt = v;
        this.updateFrequency();
    }

    void setKSR(int v) {
        // TODO: BOの影響は受けるのか？
        this.ksr = v;
        this.updateEnvelope();
    }

    void setMULT(int v) {
        this.mult = v;
        this.updateFrequency();
    }

    void setKSL(
            int v) {
        // TODO: BOの影響は受けるのか？
        this.ksl = v;
        this.envelopeGenerator.setKeyScalingLevel(this.fnum, this.block, this.bo, this.ksl);
    }

    void setTL(int v) {
        this.envelopeGenerator.setTotalLevel(v);
    }

    void setAR(int v) {
        this.ar = v;
        this.envelopeGenerator.setActualAR(this.ar, this.ksr, this.keyScaleNumber);
    }

    void setDR(int v) {
        this.dr = v;
        this.envelopeGenerator.setActualDR(this.dr, this.ksr, this.keyScaleNumber);
    }

    void setSL(int v) {
        this.sl = v;
        this.envelopeGenerator.setActualSustainLevel(this.sl);
    }

    void setSR(int v) {
        this.sr = v;
        this.envelopeGenerator.setActualSR(this.sr, this.ksr, this.keyScaleNumber);
    }

    void setRR(int v) {
        this.rr = v;
        this.envelopeGenerator.setActualRR(this.rr, this.ksr, this.keyScaleNumber);
    }

    void setXOF(int v) {
        this.xof = v;
    }

    void setWS(int v) {
        this.ws = v;
    }

    void setFB(int v) {
        this.feedbackCoef = FeedbackTable[v];
    }

    double next(int modIndex, double modulator) {
        var phaseFrac64 = this.phaseGenerator.getPhase(modIndex);
        if (this.envelopeGenerator.stage == Stage.stageOff) {
            return 0;
        }
        var envelope = this.envelopeGenerator.getEnvelope(modIndex);

        var sampleIndex = (long) (phaseFrac64) >> WaveformIndexShift;
        sampleIndex += (long) ((modulator + WaveformLen) * WaveformLen);
        return Waveforms[this.ws][(int) (sampleIndex & 1023)] * envelope;
    }

    void keyOn() {
        if (0 < this.ar) {
            this.envelopeGenerator.keyOn();
        } else {
            this.envelopeGenerator.stage = Stage.stageOff;
        }
    }

    void keyOff() {
        if (this.xof == 0) {
            this.envelopeGenerator.keyOff();
        }
    }

    void setFrequency(int fnum, int blk, int bo) {
        this.keyScaleNumber = (blk + 1 - bo) * 2 + (fnum >> 9);
        // TODO: BOの影響は受けるのか？
        if (this.keyScaleNumber < 0) {
            this.keyScaleNumber = 0;
        } else if (15 < this.keyScaleNumber) {
            this.keyScaleNumber = 15;
        }
        this.fnum = fnum;
        this.block = blk;
        this.bo = bo;
        this.updateFrequency();
        this.updateEnvelope();
        this.envelopeGenerator.setKeyScalingLevel(this.fnum, this.block, this.bo, this.ksl);
    }

    void updateFrequency() {
        this.phaseGenerator.setFrequency(this.fnum, this.block, this.bo, this.mult, this.dt);
    }

    void updateEnvelope() {
        this.envelopeGenerator.setActualAR(this.ar, this.ksr, this.keyScaleNumber);
        this.envelopeGenerator.setActualDR(this.dr, this.ksr, this.keyScaleNumber);
        this.envelopeGenerator.setActualSR(this.sr, this.ksr, this.keyScaleNumber);
        this.envelopeGenerator.setActualRR(this.rr, this.ksr, this.keyScaleNumber);
    }
}
