/*
 * https://github.com/but80/fmfm.core
 */

package vavi.sound.yamaha.ma.sim;

import static vavi.sound.yamaha.ma.ymf.ymfdata.Data.DTCoef;
import static vavi.sound.yamaha.ma.ymf.ymfdata.Data.FNUMCoef;
import static vavi.sound.yamaha.ma.ymf.ymfdata.Data.MultTable2;
import static vavi.sound.yamaha.ma.ymf.ymfdata.Data.VibratoTableInt32Frac32;
import static vavi.sound.yamaha.ma.ymf.ymfdata.Data.floatToFrac64;
import static vavi.sound.yamaha.ma.ymf.ymfdata.Data.mulInt32Frac32;
import static vavi.sound.yamaha.ma.ymf.ymfdata.Data.mulUint64;


public class PhaseGenerator {

    double sampleRate;
    boolean evb;
    int dvb;
    long phaseFrac64;
    long phaseIncrementFrac64;

    PhaseGenerator(double sampleRate) {
        this.sampleRate = sampleRate;

        this.reset();
    }

    final void reset() {
        this.phaseFrac64 = 0;
    }

    void resetAll() {
        this.evb = false;
        this.dvb = 0;
        this.phaseIncrementFrac64 = 0;
        this.reset();
    }

    void setFrequency(int fnum, int block, int bo, int mult, int dt) {
        var baseFrequency = (double) (fnum << (int) (block + 3 - bo)) / (16.0 * FNUMCoef);

        var ksn = block << 1 | fnum >> 9;
        var operatorFrequency = baseFrequency + DTCoef[dt][ksn];

        this.phaseIncrementFrac64 = floatToFrac64(operatorFrequency / this.sampleRate);

        // 端数切り捨て後に掛けないとオペレータ間でズレる
        this.phaseIncrementFrac64 = mulUint64(this.phaseIncrementFrac64, MultTable2[mult]);
        this.phaseIncrementFrac64 >>= 1;
    }

    long getPhase(int vibratoIndex) {
        if (this.evb) {
            this.phaseFrac64 += mulInt32Frac32(this.phaseIncrementFrac64, VibratoTableInt32Frac32[this.dvb][vibratoIndex]);
        } else {
            this.phaseFrac64 += this.phaseIncrementFrac64;
        }
        return this.phaseFrac64;
    }
}
