/*
 * https://github.com/but80/fmfm.core
 */

package vavi.sound.yamaha.ma.ymf.ymfdata;

import java.util.function.Function;


public class Data {

//	/** Frac64 は、0 以上 1 未満の固定小数点数を符号なし64ビット整数で表現する型です。 */
//	type Frac64 long;

    /** FloatToFrac64 は、float64 から Frac64 に値を変換します。 */
    public static long floatToFrac64(double v) {
        return (long) (v * Pow64Of2);
    }

    /** MulUint64 は、Frac64 に uint64 型の値を掛けた値を返します。 */
    public static long mulUint64(long rhs, long v) {
        return v * rhs;
    }

    /** MulInt32Frac32 は、Frac64 に Int32Frac32 型の値を掛けた値を返します。 */
    public static long mulInt32Frac32(long v, long rhs) {
        return (v >> 32) * rhs;
    }

    /** Int32Frac32 は、0 以上 2^32 未満の固定小数点数を符号なし64ビット整数で表現する型です。 */
//	type Int32Frac32 long;

    // DebugDumpFPS は、デバッグとしてダンプ表示を行う頻度 [FPS] です。
    public static final int DebugDumpFPS = 30;

    // ChannelCount は、最大チャンネル数です。
    public static final int ChannelCount = 32;

    // SampleRate は、内部的なサンプルレート[Hz]です。
    public static final double SampleRate = 48000;

    // A3Note は、MIDIメッセージにおけるA3のノートナンバーです。
    public static final int A3Note = 9 + 12 * 4;

    // A3Freq は、A3の周波数[Hz]です。
    public static final double A3Freq = 440.0;

    // FNUMCoef は、周波数とFNUMを相互に変換する際に使用する係数です。
    public static final double FNUMCoef = (1 << 19) / SampleRate * .5;

    // Pow32Of2 は、2の32乗です。
    public static final double Pow32Of2 = (1L << 32);

    // Pow63Of2 は、2の63乗です。
    public static final double Pow63Of2 = (1L << 63);

    // Pow64Of2 は、2の64乗です。
    public static final double Pow64Of2 = Pow63Of2 * 2.0;

    // ModulatorMultiplier は、モジュレータの出力を他のオペレータに入力する際の増幅率です。
    public static final double ModulatorMultiplier = 4.0;

    // ModulatorMatrix は、各 alg でモジュレータとして使用されるオペレータを表すマトリクスです。
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

    // CarrierMatrix は、各 alg でキャリアとして使用されるオペレータを表すマトリクスです。
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

    // VolumeTable は、MIDIメッセージのボリュームやエクスプレッションによって振幅にかかる係数のテーブルです。
    public static final double[] VolumeTable = {
            1e30, 47.9, 42.6, 37.2, 33.1, 29.8, 27.0, 24.6,
            22.4, 20.6, 18.9, 17.3, 15.9, 14.6, 13.4, 12.2,
            11.1, 10.1, 9.2, 8.3, 7.4, 6.6, 5.8, 5.1,
            4.4, 3.6, 3.0, 2.3, 1.7, 1.1, 0.6, 0.0,
    };

    // PanTable は、MIDIメッセージのパンによって左右それぞれの振幅にかかる係数のテーブルです。
    public static final double[][] PanTable = new double[128][2];

    // DTCoef は、DTパラメータ および BLOCKとFNUM上位1ビットによって加わる周波数差分[Hz]のテーブルです。
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

    // LFOFrequency は、LFOパラメータによって決まるビブラートやトレモロの周波数のテーブルです。
    // 単位は、2の64乗を1周とする1サンプルあたりの増分です。
    public static final long[] LFOFrequency = new long[4];

    // ModTableLen は、モジュレーション（ビブラートやトレモロ）の振幅テーブルの長さです。
    public static final int ModTableLen = 8192;

    // ModTableLenBits は、モジュレーション振幅テーブルのインデックスに必要なビット数です。
    // 2 の ModTableLenBits 乗が ModTableLen になります。
    public static final int ModTableLenBits = 13;

    // ModTableIndexShift は、2の64乗を1周とする値からモジュレーション振幅テーブルの
    // インデックスに変換する際、右シフトするビット数です。
    public static final int ModTableIndexShift = 64 - ModTableLenBits;

    // VibratoTableInt32Frac32 は、ビブラート（DVB）によって周波数にかかる係数のテーブルです。
    // 整数部32bit・小数部32bitで表されます。
    public static final long[][] VibratoTableInt32Frac32 = new long[4][ModTableLen];

    // TremoloTable は、トレモロ（DAM）によって振幅にかかる係数のテーブルです。
    public static final double[][] TremoloTable = new double[4][ModTableLen];

    // FeedbackTable は、FBパラメータによってフィードバックされる信号の振幅にかかる係数のテーブルです。
    public static final double[] FeedbackTable = {
            0, 1.0 / 32.0, 1.0 / 16.0, 1.0 / 8.0, 1.0 / 4.0, 1.0 / 2.0, 1.0, 2.0
    };

    // MultTable2 は、MULTパラメータによって周波数にかかる係数のテーブルです。2で割って使用します。
    public static final long[] MultTable2 = {
            1, 1 * 2, 2 * 2, 3 * 2, 4 * 2, 5 * 2, 6 * 2, 7 * 2, 8 * 2, 9 * 2, 10 * 2, 10 * 2, 12 * 2, 12 * 2, 15 * 2, 15 * 2
    };

    // KSLTable は、KSLパラメータによる振幅の減衰量のテーブルです。
    // 添字は順に KSL, BLOCK, FNUM上位5bit です。
    public static final double[][][] KSLTable = new double[4][8][32];

    // WaveformLen は、波形テーブルの長さです。
    public static final int WaveformLen = 1024;

    // WaveformLenBits は、波形テーブルのインデックスに必要なビット数です。
    // 2 の WaveformLenBits 乗が WaveformLen になります。
    public static final int WaveformLenBits = 10;

    // WaveformIndexShift は、2の64乗を1周とする値から波形テーブルの
    // インデックスに変換する際、右シフトするビット数です。
    public static final int WaveformIndexShift = 64 - WaveformLenBits;

    // Waveforms は、波形テーブルです。
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

			  波形は完全に上位互換

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

            // ==================================================
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

            // ==================================================
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

            // ==================================================
            // exponential
            for (var i = 0; i < 512; i++) {
                var x = (double) (i) * 16.0 / 256.0;
                Waveforms[7][i] = Math.pow(2.0, -x);
                Waveforms[7][1023 - i] = -Math.pow(2.0, -(x + 1.0 / 16.0));
            }

            // ==================================================
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

            // ==================================================
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

            // ==================================================
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
