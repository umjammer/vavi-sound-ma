/*
 * https://github.com/but80/fmfm.core
 */

package vavi.sound.yamaha.ma.ymf.ymfdata;

import java.util.function.Function;


public class Data {

//    /** Frac64 is a type that represents a fixed-point number between 0 and 1 as an unsigned 64-bit integer. */
//    type Frac64 long;

    /** FloatToFrac64 converts a value from a float64 to a Frac64. */
    public static long floatToFrac64(double v) {
        return (long) (v * Pow64Of2);
    }

    /** MulUint64 returns the result of multiplying Frac64 by a uint64 value. */
    public static long mulUint64(long rhs, long v) {
        return v * rhs;
    }

    /** MulInt32Frac32 returns the result of multiplying Frac64 by a value of type Int32Frac32. */
    public static long mulInt32Frac32(long v, long rhs) {
        return (v >> 32) * rhs;
    }

//    /** Int32Frac32 is a type that represents a fixed-point number between 0 and 2^32 (inclusive) as an unsigned 64-bit integer. */
//    type Int32Frac32 long;

    /** DebugDumpFPS is the frequency [FPS] at which debug dumps are displayed. */
    public static final int DebugDumpFPS = 30;

    /** ChannelCount is the maximum number of channels. */
    public static final int ChannelCount = 32;

    /** SampleRate is the internal sample rate [Hz]. */
    public static final double SampleRate = 48000;

    /** A3Note is the A3 note number in the MIDI message. */
    public static final int A3Note = 9 + 12 * 4;

    /** A3Freq is the frequency of A3 [Hz]. */
    public static final double A3Freq = 440.0;

    /** FNUMCoef is the coefficient used to convert between frequency and FNUM. */
    public static final double FNUMCoef = (1 << 19) / SampleRate * .5;

    /** Pow32Of2 is 2 to the 32nd power. */
    public static final double Pow32Of2 = (1L << 32);

    /** Pow63Of2 is 2 to the power of 63. */
    public static final double Pow63Of2 = (1L << 63);

    /** Pow64Of2 is 2 to the 64th power. */
    public static final double Pow64Of2 = Pow63Of2 * 2.0;

    /** ModulatorMultiplier is the amplification factor for the modulator output when input to another operator. */
    public static final double ModulatorMultiplier = 4.0;

    /** ModulatorMatrix is a matrix representing the operator used as a modulator in each alg. */
    public static final boolean[][] ModulatorMatrix = {
            {true, false, false, false},
            {false, false, false, false},
            {false, false, false, false},
            {true, true, true, false},
            {true, true, true, false},
            {true, false, true, false},
            {false, true, true, false},
            {false, true, false, false},
    };

    /** CarrierMatrix is a matrix representing the operators used as carriers in each alg. */
    public static final boolean[][] CarrierMatrix = {
            {false, true, false, false},
            {true, true, false, false},
            {true, true, true, true},
            {false, false, false, true},
            {false, false, false, true},
            {false, true, false, true},
            {true, false, false, true},
            {true, false, true, true},
    };

    /** The VolumeTable is a table of coefficients that affect amplitude depending on the volume and expression of MIDI messages. */
    public static final double[] VolumeTable = {
            1e30, 47.9, 42.6, 37.2, 33.1, 29.8, 27.0, 24.6,
            22.4, 20.6, 18.9, 17.3, 15.9, 14.6, 13.4, 12.2,
            11.1, 10.1, 9.2, 8.3, 7.4, 6.6, 5.8, 5.1,
            4.4, 3.6, 3.0, 2.3, 1.7, 1.1, 0.6, 0.0,
    };

    /** PanTable is a table of coefficients that are applied to the left and right amplitudes by panning MIDI messages. */
    public static final double[][] PanTable = new double[128][2];

    /** DTCoef is a table of the frequency difference [Hz] added by the DT parameter and the most significant bit of BLOCK and FNUM. */
    public static final double[][] DTCoef = {
            {0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00},
            {0.00, 0.00, 0.05, 0.05, 0.05, 0.05, 0.09, 0.09, 0.14, 0.14, 0.18, 0.23, 0.27, 0.32, 0.37, 0.37},
            {0.05, 0.05, 0.09, 0.09, 0.14, 0.14, 0.18, 0.23, 0.27, 0.32, 0.41, 0.46, 0.59, 0.64, 0.73, 0.73},
            {0.09, 0.09, 0.14, 0.14, 0.18, 0.23, 0.28, 0.32, 0.41, 0.46, 0.59, 0.64, 0.87, 0.91, 1.00, 1.00},
            {0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00},
            {-0.00, -0.00, -0.05, -0.05, -0.05, -0.05, -0.09, -0.09, -0.14, -0.14, -0.18, -0.23, -0.27, -0.32, -0.37, -0.37},
            {-0.05, -0.05, -0.09, -0.09, -0.14, -0.14, -0.18, -0.23, -0.27, -0.32, -0.41, -0.46, -0.59, -0.64, -0.73, -0.73},
            {-0.09, -0.09, -0.14, -0.14, -0.18, -0.23, -0.28, -0.32, -0.41, -0.46, -0.59, -0.64, -0.87, -0.91, -1.00, -1.00},
    };

    /**
     * A table of vibrato and tremolo frequencies determined by the LFO parameters.
     * The unit is an increment per sample, where one cycle is 2 to the power of 64.
     */
    public static final long[] LFOFrequency = new long[4];

    /** The length of the modulation (vibrato and tremolo) amplitude table. */
    public static final int ModTableLen = 8192;

    /**
     * The number of bits required to index into the modulation amplitude table.
     * ModTableLen is 2 to the power of ModTableLenBits.
     */
    public static final int ModTableLenBits = 13;

    /**
     * A function that changes the position of the modulation amplitude table from 2^64 to the power of 1.
     * The number of bits to shift right when converting to an index.
     */
    public static final int ModTableIndexShift = 64 - ModTableLenBits;

    /**
     * A table of coefficients applied to frequency by vibrato (DVB).
     * It is expressed as a 32-bit integer part and a 32-bit decimal part.
     */
    public static final long[][] VibratoTableInt32Frac32 = new long[4][ModTableLen];

    /** TremoloTable is a table of coefficients applied to amplitude by tremolo (DAM). */
    public static final double[][] TremoloTable = new double[4][ModTableLen];

    /** FeedbackTable is a table of coefficients that affect the amplitude of the signal fed back by the FB parameters. */
    public static final double[] FeedbackTable = {
            0, 1.0 / 32.0, 1.0 / 16.0, 1.0 / 8.0, 1.0 / 4.0, 1.0 / 2.0, 1.0, 2.0
    };

    /** MultTable2 is a table of coefficients applied to frequencies by MULT parameters. Divide by 2 before use. */
    public static final long[] MultTable2 = {
            1, 1 * 2, 2 * 2, 3 * 2, 4 * 2, 5 * 2, 6 * 2, 7 * 2, 8 * 2, 9 * 2, 10 * 2, 10 * 2, 12 * 2, 12 * 2, 15 * 2, 15 * 2
    };

    /**
     * KSLTable is a table of amplitude attenuation due to KSL parameters.
     * The subscripts are the upper 5 bits of KSL, BLOCK, and FNUM, respectively.
     */
    public static final double[][][] KSLTable = new double[4][8][32];

    /** WaveformLen is the length of the waveform table. */
    public static final int WaveformLen = 1024;

    /**
     * WaveformLenBits is the number of bits required to index into the waveform table.
     * 2 to the power of WaveformLenBits is WaveformLen.
     */
    public static final int WaveformLenBits = 10;

    /**
     * WaveformIndexShift is the number of bits to shift right
     * when converting from a 2^64 value into a wavetable index.
     */
    public static final int WaveformIndexShift = 64 - WaveformLenBits;

    /** Waveforms is a wave table. */
    public static double[][] Waveforms = new double[32][];

    public static double calculateIncrement(double begin, double end, double period) {
        return (end - begin) / SampleRate * (1.0 / period);
    }

    public static double triSin(double phase) {
        phase *= 4.0;
        if (phase < 1.0) {
            return phase;
        }
        if (phase < 3.0) {
            return 2.0 - phase;
        }
        return phase - 4.0;
    }

    public static double triCos(double phase) {
        phase *= 4.0;
        if (phase < 2.0) {
            return 1.0 - phase;
        }
        return phase - 3.0;
    }

    static {
        // generate volume table
        for (var i = 0; i < VolumeTable.length; i++) {
            VolumeTable[i] = Math.pow(10.0, -VolumeTable[i] / 20.0);
        }
        VolumeTable[0] = .0;

        // generate pan table
        for (var i = 0; i < 128; i++) {
            var a = Math.PI * .5 * (double) i / 127.0;
            PanTable[i][0] = Math.cos(a);
            PanTable[i][1] = Math.sin(a);
        }

        // generate vibrato table
        // https://github.com/yamaha-webmusic/ymf825board/blob/991485a4cbbe07d84cca707701999875fbc17c74/manual/fbd_spec3.md#dam-eam-dvb-evb
        double[] vibratoDepth = {
                3.4, 6.7, 13.5, 26.8
        };
        for (var dvb = 0; dvb < 4; dvb++) {
            for (var i = 0; i < ModTableLen; i++) {
                var phase = (double) (i) / (double) (ModTableLen);
                var cent = triSin(phase) * vibratoDepth[dvb];
                var v = Math.pow(2.0, cent / 1200.0);
                VibratoTableInt32Frac32[dvb][i] = (long) (v * Pow32Of2);
            }
        }

        // generate tremolo table
        // https://github.com/yamaha-webmusic/ymf825board/blob/991485a4cbbe07d84cca707701999875fbc17c74/manual/fbd_spec3.md#dam-eam-dvb-evb
        double[] tremoloDepth = {
                1.3, 2.8, 5.8, 11.8
        }; // dB
        for (var dam = 0; dam < 4; dam++) {
            for (var i = 0; i < ModTableLen; i++) {
                var phase = (double) (i) / (double) (ModTableLen);
                var v = (triCos(phase) - 1.0) * .5 * tremoloDepth[dam];
                TremoloTable[dam][i] = Math.pow(10.0, v / 20.0);
            }
        }

        // generate KSL table
        double[] kslBases = {
                .0, .08, 1.0 / 15.0, 1.0 / 15.0
        };
        double[] kslBlockCoefs = {
                .0, 3.0, 1.5, 6.01
        };
        double[] kslFnum5Coefs = {
                .0, .38, .185, .75
        };
        for (var ksl = 0; ksl < 4; ksl++) {
            for (var block = 0; block < 8; block++) {
                for (var fnum5 = 0; fnum5 < 32; fnum5++) {
                    var fnum5lim = fnum5;
                    if (15 < fnum5lim) {
                        fnum5lim = 15;
                    }
                    var v = kslBases[ksl] - kslBlockCoefs[ksl] * (double) (block - 2) - kslFnum5Coefs[ksl] * (double) (fnum5lim - 7);
                    if (block < 2 || .0 <= v) {
                        v = .0;
                    }
                    KSLTable[ksl][block][fnum5] = Math.pow(10.0, v / 20.0);
                }
            }
        }

        // convert lfo frequency
        {
            double[] lfoFreqHz = {
                    1.8, 4.0, 5.9, 7.0
            };
            for (var i = 0; i < lfoFreqHz.length; i++) {
                LFOFrequency[i] = (long) (lfoFreqHz[i] / SampleRate * Pow64Of2);
            }
        }

        // generate waveform table
        {
		/*

			  Waveforms are fully upwardly compatible

			  OPL3:
				SIN   | 0:^v 1:^- 2:^^ 3:''
				SINx2 | 4:▚- 5:"-
				SQR   | 6:▀▄
				EXP   | 7:＼

			  MA-5:
				SIN   | 0:^v 1:^- 2:^^ 3:''
				SINx2 | 4:▚- 5:"-
				SQR   | 6:▀▄ 14:▀-
				SQRx2 | 22:▘▘ 30:▘-
				EXP   | 7:＼
				CSIN  | 8:▀▄ 9:^- 10:^^ 11:''
				CSINx2| 12:▚- 13:"-
				TRI   | 16:▀▄ 17:^- 18:^^ 19:''
				TRIx2 | 20:▚- 21:"-
				SAW   | 24:▀▄ 25:▀- 26:▀▀ 27:''
				SAWx2 | 28:▚- 29:"-
		*/

            for (var i = 0; i < Waveforms.length; i++) {
                Waveforms[i] = new double[WaveformLen];
            }

            // ^v to ^-
            Function<double[], double[]> copyHalf = (double[] src) -> {
                var dst = new double[WaveformLen];
                for (var i = 0; i < 512; i++) {
                    dst[i] = src[i];
                    dst[512 + i] = 0;
                }
                return dst;
            };

            // ^v to ^^
            Function<double[], double[]> copyAbs = (double[] src) -> {
                var dst = new double[WaveformLen];
                for (var i = 0; i < 512; i++) {
                    dst[i] = src[i];
                    dst[512 + i] = src[i];
                }
                return dst;
            };

            // ^v to ''
            Function<double[], double[]> copyAbsQuarter = (double[] src) -> {
                var dst = new double[WaveformLen];
                for (var i = 0; i < 256; i++) {
                    dst[i] = src[i];
                    dst[256 + i] = .0;
                    dst[512 + i] = src[i];
                    dst[768 + i] = .0;
                }
                return dst;
            };

            // ^v to ▚-
            Function<double[], double[]> copyOct = (double[] src) -> {
                var dst = new double[WaveformLen];
                for (var i = 0; i < 512; i++) {
                    dst[i] = src[i * 2];
                    dst[512 + i] = .0;
                }
                return dst;
            };

            // ^v to "-
            Function<double[], double[]> copyAbsOct = (double[] src) -> {
                var dst = new double[WaveformLen];
                for (var i = 0; i < 256; i++) {
                    dst[i] = src[i * 2];
                    dst[256 + i] = src[i * 2];
                    dst[512 + i] = .0;
                    dst[768 + i] = .0;
                }
                return dst;
            };

            //
            // sine wave
            for (var i = 0; i < WaveformLen; i++) {
                Waveforms[0][i] = Math.sin(2 * Math.PI * (double) (i) / WaveformLen);
            }
            var sineTable = Waveforms[0];

            // SIN   | 0:^v 1:^- 2:^^ 3:''
            Waveforms[1] = copyHalf.apply(sineTable);
            Waveforms[2] = copyAbs.apply(sineTable);
            Waveforms[3] = copyAbsQuarter.apply(sineTable);
            // SINx2 | 4:▚- 5:"-
            Waveforms[4] = copyOct.apply(sineTable);
            Waveforms[5] = copyAbsOct.apply(sineTable);

            //
            // square wave
            for (var i = 0; i < 512; i++) {
                Waveforms[6][i] = 1.0;
                Waveforms[6][512 + i] = -1.0;
            }
            var squareTable = Waveforms[6];

            // SQR   | 6:▀▄ 14:▀-
            Waveforms[14] = copyHalf.apply(squareTable);
            // SQRx2 | 22:▘▘ 30:▘-
            Waveforms[22] = copyAbsQuarter.apply(squareTable);
            Waveforms[30] = copyOct.apply(Waveforms[14]);

            //
            // exponential
            for (var i = 0; i < 512; i++) {
                var x = (double) (i) * 16.0 / 256.0;
                Waveforms[7][i] = Math.pow(2.0, -x);
                Waveforms[7][1023 - i] = -Math.pow(2.0, -(x + 1.0 / 16.0));
            }

            //
            // clipped sinewave
            for (var i = 0; i < WaveformLen; i++) {
                var theta = 2 * Math.PI * (double) (i) / WaveformLen;
                Waveforms[8][i] = Math.max(-1.0, Math.min(Math.sin(theta) * Math.sqrt(2), 1.0));
            }
            var csineTable = Waveforms[8];

            // CSIN  | 8:▀▄ 9:^- 10:^^ 11:''
            Waveforms[9] = copyHalf.apply(csineTable);
            Waveforms[10] = copyAbs.apply(csineTable);
            Waveforms[11] = copyAbsQuarter.apply(csineTable);
            // CSINx2| 12:▚- 13:"-
            Waveforms[12] = copyOct.apply(csineTable);
            Waveforms[13] = copyAbsOct.apply(csineTable);

            //
            // triangle wave
            for (var i = 0; i < 256; i++) {
                Waveforms[16][i] = (double) (i) / 256.0;
                Waveforms[16][256 + i] = (256.0 - (double) (i)) / 256.0;
                Waveforms[16][512 + i] = -(double) (i) / 256.0;
                Waveforms[16][768 + i] = -(256.0 - (double) (i)) / 256.0;
            }
            var triTable = Waveforms[16];

            // TRI   | 16:▀▄ 17:^- 18:^^ 19:''
            Waveforms[17] = copyHalf.apply(triTable);
            Waveforms[18] = copyAbs.apply(triTable);
            Waveforms[19] = copyAbsQuarter.apply(triTable);
            // TRIx2 | 20:▚- 21:"-
            Waveforms[20] = copyOct.apply(triTable);
            Waveforms[21] = copyAbsOct.apply(triTable);

            //
            // saw wave
            for (var i = 0; i < 512; i++) {
                Waveforms[24][i] = (double) (i) / 512.0;
                Waveforms[24][i + 512] = (double) (i) / 512.0 - 1.0;
            }
            var sawTable = Waveforms[24];

            // SAW   | 24:▀▄ 25:▀- 26:▀▀ 27:''
            Waveforms[25] = copyHalf.apply(sawTable);
            Waveforms[26] = copyAbs.apply(sawTable);
            Waveforms[27] = copyAbsQuarter.apply(sawTable);
            // SAWx2 | 28:▚- 29:"-
            Waveforms[28] = copyOct.apply(sawTable);
            Waveforms[29] = copyAbsOct.apply(sawTable);
        }
    }
}
