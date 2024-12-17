/*
 * https://github.com/but80/smaf825
 */

package vavi.sound.yamaha.smaf.voice;


import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import vavi.sound.yamaha.smaf.enums.Enums.Algorithm;
import vavi.sound.yamaha.smaf.enums.Enums.Multiplier;


class VMAFMOperator {

    // Operator number
    //`json:"-"`
    int Num;
    // Multiplier
    //`json:"mult"`
    Multiplier MULT;
    // Key Scaling Level
    //`json:"ksl"`
    int KSL;
    // Total Level
    //`json:"tl"`
    int TL;
    // Attack Rate
    //`json:"ar"`
    int AR;
    // Decay Rate
    //`json:"dr"`
    int DR;
    // Sustain Level
    //`json:"sl"`
    int SL;
    // Release Rate
    //`json:"rr"`
    int RR;
    // Wave Shape
    //`json:"ws"`
    int WS;
    // Depth of Vibrato
    //`json:"dvb"`
    int DVB;
    // Depth of AM
    //`json:"dam"`
    int DAM;
    // Vibrato
    // `json:"vib"`
    boolean VIB;
    //
    // `json:"egt"`
    boolean EGT;
    // Keep sustain rate after KeyOff (unused in YMF825)
    // `json:"sus"`
    boolean SUS;
    // Key Scaling Rate
    // `json:"ksr"`
    boolean KSR;
    // AM
    // `json:"am"`
    boolean AM;

    void Read(DataInputStream rdr, int[] rest) throws IOException {
        //    | 7 | 6 | 5 | 4 | 3 | 2 | 1 | 0 |
        // +0 |     MULT      |VIB|EGT|SUS|KSR|
        // +1 |      R R      |      D R      |
        // +2 |      A R      |      S L      |
        // +3 |          T L          |  KSL  |
        // +4 |  DVB  |  DAM  |A M|    W S    |

        var data = new byte[5];
        rdr.readFully(data);
        rest[0] -= data.length;

        this.MULT = Multiplier(data[0] >> 4);
        this.VIB = (data[0] & 0x08) != 0;
        this.EGT = (data[0] & 0x04) != 0;
        this.SUS = (data[0] & 0x02) != 0;
        this.KSR = (data[0] & 0x01) != 0;
        this.RR = (data[1] >> 4) & 0xff;
        this.DR = (data[1] & 15) & 0xff;
        this.AR = (data[2] >> 4) & 0xff;
        this.SL = (data[2] & 15) & 0xff;
        this.TL = (data[3] >> 2) & 0xff;
        this.KSL = (data[3] & 3) & 0xff;
        this.DVB = (data[4] >> 6 & 3);
        this.DAM = (data[4] >> 4 & 3);
        this.AM = (data[4] & 0x08) != 0;
        this.WS = (data[4] & 7) & 0xff;
    }

    byte[] Bytes() {
        return {
                (byte) (this.MULT & 15) << 4 | util.BoolToByte(this.VIB, 0x08) | util.BoolToByte(this.EGT, 0x04) | util.BoolToByte(this.SUS, 0x02) | util.BoolToByte(this.KSR, 0x01),
                (byte) ((this.RR & 15) << 4 | (this.DR & 15)),
                (byte) ((this.AR & 15) << 4 | (this.SL & 15)),
                (byte) ((this.TL & 63) << 2 | (this.KSL & 3)),
                (byte) ((this.DVB & 3) << 6 | (this.DAM & 3) << 4 | util.BoolToByte(this.AM, 0x08) | byte(this.WS & 7)),
        } ;
    }

    @Override
    public String toString() {
        var t = Arrays.asList(
            "ADR=%d,%d,%d".formatted(this.AR, this.DR, this.RR),
            "SL=%d".formatted(this.SL),
            "TL=%d".formatted(this.TL),
            "KSL=%d".formatted(this.KSL),
            "WS=%d".formatted(this.WS)
        );
        if (this.AM) {
            t.add("AM=%d".formatted(this.DAM));
        }
        if (this.VIB) {
            t.add("VB=%d".formatted(this.DVB));
        }
        if (this.EGT) {
            t.add("EGT");
        }
        if (this.SUS) {
            t.add("SUS");
        }
        if (this.KSR) {
            t.add("KSR");
        }
        var s = String.join(" ", t.stream().map(x -> x).toArray(String[]::new));
        return "Op #%d: MULT=%s\n".formatted(this.Num + 1, this.MULT) + util.Indent(s, "\t");
    }

    VM35FMOperator ToVM35(int fb ) {
        var sr = this.EGT? 0 : this.RR;
        return new VM35FMOperator() {{
            Num = this.Num;
            MULTI = MULT;
            DT = 0;
            AR = this.AR;
            DR = this.DR;
            SR = sr;
            RR = this.RR;
            SL = this.SL;
            TL = this.TL;
            KSL = this.KSL;
            DAM = this.DAM;
            DVB = this.DVB;
            FB = fb;
            WS = this.WS;
            XOF = false;
            SUS = this.SUS;
            KSR = this.KSR;
            EAM = AM;
            EVB = VIB;
        }};
    }

    static class VMAFMVoice implements VM35Voice {

        //`json:"lfo"`
        int LFO;
        //`json:"fb"`
        int FB;
        //`json:"alg"`
        Algorithm ALG;
        //`json:"operators"`
        VMAFMOperator[] Operators;

        VMAFMVoice(byte[] data) {
            int[] rest = new int[] {data.length};
            var rdr = new DataInputStream(new ByteArrayInputStream(data));
            if (0 < rest[0]) {
                this.ReadUnusedRest(rdr, rest);
            }
            if (rest[0] != 0) {
                throw new IllegalStateException("Wrong size of VMA voice data (want %d, got %d)".formatted(data.length + rest[0], data.length));
            }
        }

        public VMAFMVoice() {

        }

        @Override
        public void Read(DataInputStream rdr, int[] rest) throws IOException {
            //    | 7 | 6 | 5 | 4 | 3 | 2 | 1 | 0 |
            // +0 |  LFO  |    F B    |    ALG    |
            // +1 |              01?              |

            var global = new byte[2];
            rdr.readFully(global);
            rest[0] -= 2 /* sizeof(global) */;
            this.LFO = (global[0] >> 6) & 3;
            this.FB = (global[0] >> 3) & 7;
            this.ALG = Algorithm(global[0] & 7);
            this.Operators = new VMAFMOperator[4];
            var n = this.ALG.OperatorCount();
            for (var op = 0; op < 4; op++) {
                this.Operators[op] = new VMAFMOperator() {{
                    Num = op;
                }};
            }
            for (var op = 0; op < n; op++) {
                err = this.Operators[op].Read(rdr, rest);
                if (err != null) {
                    return errors.WithStack(err);
                }
            }
        }

        @Override
        public void ReadUnusedRest(DataInputStream rdr, int[] rest) {
            var n = this.ALG.OperatorCount();
            for (var op = n; op < 4; op++) {
                this.Operators[op] = new VMAFMOperator() {{
                    Num = op;
                }};
                err = this.Operators[op].Read(rdr, rest);
                if (err != null) {
                    return errors.WithStack(err);
                }
            }
        }

        byte[] Bytes(boolean staticLen) {
            var b = {
                    (byte) (this.LFO & 3) << 6 | (byte) (this.FB & 7) << 3 | (byte) (this.ALG & 7),
                    1,
            };
            var n = 4;
            if (!staticLen) {
                n = this.ALG.OperatorCount();
            }
            for (var op = 0; op < n; op++) {
                var b = append(b, this.Operators[op].Bytes()...);
            }
            return b;
        }

        //type vmaFMVoiceMarshaler VMAFMVoice
        static class X {
            vmaFMVoiceMarshaler vmaFMVoiceMarshaler;
            int[] YMF825Data;  //`json: "ymf825_data"`
        }

        byte[] MarshalJSON() {
            return json.Marshal(new X() {{
                    vmaFMVoiceMarshaler = vmaFMVoiceMarshaler(v);
                    YMF825Data = util.BytesToInts(this.ToVM35().Bytes(true, true));
            }});
        }

        @Override
        public String toString() {
            var s = new ArrayList<String>();
            s.add("LFO=%d FB=%d ALG=%s".formatted(this.LFO, this.FB, this.ALG));
            for (var op = 0; op < this.ALG.OperatorCount(); op++) {
                s.add(this.Operators[op].toString());
            }
            s.add("Raw=" + util.Hex(this.Bytes(false)));
            return String.join("\n", s);
        }

        VM35FMVoice ToVM35() {
            var result = new VM35FMVoice() {{
                DrumKey = Note(0);
                PANPOT = Panpot_Center;
                BO = BasicOctave_Normal;
                LFO = this.LFO;
                PE = false;
                ALG = this.ALG;
                Operators = new VM35FMOperator[4];
            }};
            var fb = this.FB;
            for (var op = 0; op < 4; op++) {
                result.Operators[op] = this.Operators[op].ToVM35(fb);
                fb = 0;
            }
            return result;
        }
    }
}