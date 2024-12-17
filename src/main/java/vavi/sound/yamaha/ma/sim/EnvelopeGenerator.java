/*
 * https://github.com/but80/fmfm.core
 */

package vavi.sound.yamaha.ma.sim;

import static vavi.sound.yamaha.ma.ymf.ymfdata.Data.KSLTable;
import static vavi.sound.yamaha.ma.ymf.ymfdata.Data.TremoloTable;


public class EnvelopeGenerator {

    enum Stage {
        stageOff("-"),
        stageAttack("A"),
        stageDecay("D"),
        stageSustain("S"),
        stageRelease("R");
        //default -> "?";
        final String s;

        Stage(String s) {
            this.s = s;
        }
    }

    public static final double epsilon = 1.0 / 32768.0;

    double sampleRate;
    EnvelopeGenerator.Stage stage;
    boolean eam;
    int dam;
    double arDiffPerSample;
    double drCoefPerSample;
    double srCoefPerSample;
    double rrCoefPerSample;
    double kslCoef;
    double tlCoef;
    double kslTlCoef;
    double sustainLevel;
    double currentLevel;

    public EnvelopeGenerator(double sampleRate) {
        this.sampleRate = sampleRate;

        this.resetAll();
    }

    void reset() {
        this.currentLevel = .0;
        this.stage = stage.stageOff;
    }

    void resetAll() {
        this.eam = false;
        this.dam = 0;
        this.sustainLevel = .0;
        this.

                setTotalLevel(63);
        this.

                setKeyScalingLevel(0, 0, 1, 0);
        this.

                reset();
    }

    public void setActualSustainLevel(int sl) {
        if (sl == 0x0f) {
            this.sustainLevel = 0;
        } else {
            var slDB = -3.0 * (double) sl;

            this.sustainLevel = Math.pow(10.0, slDB / 20.0);
        }
    }

    public void setTotalLevel(int tl) {
        if (63 <= tl) {
            this.tlCoef = .0;
            this.kslTlCoef = .0;
            return;
        }
        var tlDB = (double) tl * -0.75;
        this.tlCoef = Math.pow(10.0, tlDB / 20.0);

        this.kslTlCoef = this.kslCoef * this.tlCoef;
    }

    public void setKeyScalingLevel(int fnum, int block, int bo, int ksl) {
        var blkbo = block + 1 - bo;
        if (blkbo < 0) {
            blkbo = 0;
        } else if (7 < blkbo) {
            blkbo = 7;
        }
        this.kslCoef = KSLTable[ksl][blkbo][fnum >> 5];
        this.kslTlCoef = this.kslCoef * this.tlCoef;
    }

    public void setActualAR(int attackRate, int ksr, int keyScaleNumber) {
        if (attackRate <= 0) {
            this.arDiffPerSample = .0;
            return;
        }
        var ksn = (keyScaleNumber >> 1) + (keyScaleNumber & 1);
        var sec = attackTimeSecAt1[ksr][ksn] / (double) (1 << (attackRate - 1));
        this.arDiffPerSample = 1.0 / (sec * this.sampleRate);
    }

    public void setActualDR(int dr, int ksr, int keyScaleNumber) {
        if (dr == 0) {
            this.drCoefPerSample = 1.0;
        } else {
            var dbPerSecAt4 = decayDBPerSecAt4[ksr][keyScaleNumber] / 2.0;
            var dbPerSample = dbPerSecAt4 * (double) (1 << dr) / 16.0 / this.sampleRate;
            this.drCoefPerSample = Math.pow(10, -dbPerSample / 10);
        }
    }

    public void setActualSR(int sr, int ksr, int keyScaleNumber) {
        if (sr == 0) {
            this.srCoefPerSample = 1.0;
        } else {
            var dbPerSecAt4 = decayDBPerSecAt4[ksr][keyScaleNumber] / 2.0;
            var dbPerSample = dbPerSecAt4 * (double) (1 << sr) / 16.0 / this.sampleRate;
            this.srCoefPerSample = Math.pow(10, -dbPerSample / 10);
        }
    }

    public void setActualRR(int rr, int ksr, int keyScaleNumber) {
        if (rr == 0) {
            this.rrCoefPerSample = 1.0;
        } else {
            var dbPerSecAt4 = decayDBPerSecAt4[ksr][keyScaleNumber] / 2.0;
            var dbPerSample = dbPerSecAt4 * (double) (1 << rr) / 16.0 / this.sampleRate;
            this.rrCoefPerSample = Math.pow(10, -dbPerSample / 10);
        }
    }

    public double getEnvelope(int tremoloIndex) {
        switch (this.stage) {
            case stageAttack:
                this.currentLevel += this.arDiffPerSample;
                if (this.currentLevel < 1.0) {
                    break;
                }
                this.currentLevel = 1.0;
                this.stage = Stage.stageDecay;
                //fallthrough;
            case stageDecay:
                if (this.sustainLevel < this.currentLevel) {
                    this.currentLevel *= this.drCoefPerSample;
                    break;
                }
                this.stage = Stage.stageSustain;
                //fallthrough;
            case stageSustain:
                if (epsilon < this.currentLevel) {
                    this.currentLevel *= this.srCoefPerSample;
                } else {
                    this.stage = Stage.stageOff;
                }
                break;
            case stageRelease:
                if (epsilon < this.currentLevel) {
                    this.currentLevel *= this.rrCoefPerSample;
                } else {
                    this.currentLevel = .0;
                    this.stage = Stage.stageOff;
                }
                break;
        }

        var result = this.currentLevel;
        if (this.eam) {
            result *= TremoloTable[this.dam][tremoloIndex];
        }
        return result * this.kslTlCoef;
    }

    public void keyOn() {
        this.stage = Stage.stageAttack;
    }

    public void keyOff() {
        if (this.stage != Stage.stageOff) {
            this.stage = Stage.stageRelease;
        }
    }

    // DR/SR/RR=4 における共通の減衰速度 [振幅dB/sec]
    // ・使用時は2で割ってエネルギーdBに変換
    // ・DR/SR/RR が1増えると速度は2倍になる
    static final double[][] decayDBPerSecAt4 = {
            // 添字は keyScaleNumber (0..15)
            {17.9342, 17.9342, 17.9342, 17.9342, 17.9342, 22.4116, 22.4116, 22.4116, 22.4116, 26.9076, 26.9076, 26.9076, 26.9076, 31.3661, 31.3661, 31.3661},      // KSR=0
            {17.9465, 22.4376, 22.4376, 31.4026, 31.4026, 44.8696, 44.8696, 62.7959, 62.7959, 89.6707, 89.6707, 125.5546, 125.5546, 179.2684, 179.2684, 250.9128}, // KSR=1
    };

    static final double[][] attackTimeSecAt1 = {
            {3.07068, 3.07068, 3.07068, 2.45670, 2.45670, 2.04699, 2.04699, 1.75471, 1.75471},
            {3.07082, 2.45660, 1.75489, 1.22816, 0.87737, 0.61414, 0.43876, 0.30714, 0.21935},
    };
}
