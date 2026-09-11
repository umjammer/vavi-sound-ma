/*
 * https://github.com/but80/smaf825
 */

package vavi.sound.yamaha.smaf.voice;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import com.google.gson.Gson;
import vavi.sound.yamaha.smaf.enums.Enums.Algorithm;
import vavi.sound.yamaha.smaf.enums.Enums.BasicOctave;
import vavi.sound.yamaha.smaf.enums.Enums.Multiplier;
import vavi.sound.yamaha.smaf.enums.Enums.Panpot;
import vavi.sound.yamaha.smaf.enums.Note;
import vavi.sound.yamaha.smaf.voice.VM35FMVoice.VM35FMOperator;

import static vavi.sound.yamaha.smaf.util.BinaryUtil.boolToByte;
import static vavi.sound.yamaha.smaf.util.BinaryUtil.bytesToInts;
import static vavi.sound.yamaha.smaf.util.TextUtil.hex;
import static vavi.sound.yamaha.smaf.util.TextUtil.indent;


public class VMAFMVoice implements VM35Voice {

    public static class VMAFMOperator {

        /** Operator number */
        //`json:"-"`
        int num;
        /** Multiplier */
        //`json:"mult"`
        Multiplier mult;
        /** Key Scaling Level */
        //`json:"ksl"`
        int ksl;
        /** Total Level */
        //`json:"tl"`
        int tl;
        /** Attack Rate */
        //`json:"ar"`
        int ar;
        /** Decay Rate */
        //`json:"dr"`
        int dr;
        /** Sustain Level */
        //`json:"sl"`
        int sl;
        /** Release Rate */
        //`json:"rr"`
        int rr;
        /** Wave Shape */
        //`json:"ws"`
        int ws;
        /** Depth of Vibrato */
        //`json:"dvb"`
        int dvb;
        /** Depth of AM */
        //`json:"dam"`
        int dam;
        /** Vibrato */
        // `json:"vib"`
        boolean vib;
        //
        // `json:"egt"`
        boolean egt;
        /** Keep sustain rate after KeyOff (unused in YMF825) */
        // `json:"sus"`
        boolean sus;
        /** Key Scaling Rate */
        // `json:"ksr"`
        boolean ksr;
        /** AM */
        // `json:"am"`
        boolean am;

        void read(DataInputStream rdr, int[] rest) throws IOException {
            //    | 7 | 6 | 5 | 4 | 3 | 2 | 1 | 0 |
            // +0 |     MULT      |VIB|EGT|SUS|KSR|
            // +1 |      R R      |      D R      |
            // +2 |      A R      |      S L      |
            // +3 |          T L          |  KSL  |
            // +4 |  DVB  |  DAM  |A M|    W S    |

            var data = new byte[5];
            rdr.readFully(data);
            rest[0] -= data.length;

            this.mult = Multiplier.values()[(data[0] & 0xff) >> 4];
            this.vib = (data[0] & 0x08) != 0;
            this.egt = (data[0] & 0x04) != 0;
            this.sus = (data[0] & 0x02) != 0;
            this.ksr = (data[0] & 0x01) != 0;
            this.rr = ((data[1] & 0xff) >> 4) & 0xff;
            this.dr = (data[1] & 15) & 0xff;
            this.ar = ((data[2] & 0xff) >> 4) & 0xff;
            this.sl = (data[2] & 15) & 0xff;
            this.tl = ((data[3] & 0xff) >> 2) & 0xff;
            this.ksl = (data[3] & 3) & 0xff;
            this.dvb = ((data[4] & 0xff) >> 6 & 3);
            this.dam = ((data[4] & 0xff) >> 4 & 3);
            this.am = (data[4] & 0x08) != 0;
            this.ws = (data[4] & 7) & 0xff;
        }

        byte[] toBytes() {
            return new byte[] {
                    (byte) ((this.mult.ordinal() & 15) << 4 | boolToByte(this.vib, (byte) 0x08) | boolToByte(this.egt, (byte) 0x04) | boolToByte(this.sus, (byte) 0x02) | boolToByte(this.ksr, (byte) 0x01)),
                    (byte) ((this.rr & 15) << 4 | (this.dr & 15)),
                    (byte) ((this.ar & 15) << 4 | (this.sl & 15)),
                    (byte) ((this.tl & 63) << 2 | (this.ksl & 3)),
                    (byte) ((this.dvb & 3) << 6 | (this.dam & 3) << 4 | boolToByte(this.am, (byte) 0x08) | (this.ws & 7)),
            };
        }

        @Override
        public String toString() {
            var t = new ArrayList<>(Arrays.asList(
                    "ADR=%d,%d,%d".formatted(this.ar, this.dr, this.rr),
                    "sl=%d".formatted(this.sl),
                    "tl=%d".formatted(this.tl),
                    "ksl=%d".formatted(this.ksl),
                    "ws=%d".formatted(this.ws)
            ));
            if (this.am) {
                t.add("am=%d".formatted(this.dam));
            }
            if (this.vib) {
                t.add("VB=%d".formatted(this.dvb));
            }
            if (this.egt) {
                t.add("egt");
            }
            if (this.sus) {
                t.add("sus");
            }
            if (this.ksr) {
                t.add("ksr");
            }
            var s = String.join(" ", t.stream().map(x -> x).toArray(String[]::new));
            return "Op #%d: mult=%s\n".formatted(this.num + 1, this.mult) + indent(s, "\t");
        }

        public VM35FMOperator ToVM35(int fb_) {
            var sr_ = this.egt ? 0 : this.rr;
            return new VM35FMOperator() {{
                num = VMAFMOperator.this.num;
                multi = mult;
                DT = 0;
                ar = VMAFMOperator.this.ar;
                dr = VMAFMOperator.this.dr;
                sr = sr_;
                rr = VMAFMOperator.this.rr;
                sl = VMAFMOperator.this.sl;
                tl = VMAFMOperator.this.tl;
                ksl = VMAFMOperator.this.ksl;
                dam = VMAFMOperator.this.dam;
                dvb = VMAFMOperator.this.dvb;
                fb = fb_;
                ws = VMAFMOperator.this.ws;
                xof = false;
                sus = VMAFMOperator.this.sus;
                ksr = VMAFMOperator.this.ksr;
                eam = am;
                evb = vib;
            }};
        }
    }

    //`json:"lfo"`
    int lfo;
    //`json:"fb"`
    int fb;
    //`json:"alg"`
    Algorithm alg;
    //`json:"operators"`
    VMAFMOperator[] operators;

    public VMAFMVoice(byte[] data) throws IOException {
        int[] rest = new int[] {data.length};
        var rdr = new DataInputStream(new ByteArrayInputStream(data));
        this.read(rdr, rest); // without this "alg" and "operators" are still null
        if (0 < rest[0]) {
            this.readUnusedRest(rdr, rest);
        }
        if (rest[0] != 0) {
            throw new IllegalStateException("Wrong size of VMA voice data (want %d, got %d)".formatted(data.length + rest[0], data.length));
        }
    }

    public VMAFMVoice() {
    }

    @Override
    public void read(DataInputStream rdr, int[] rest) throws IOException {
        //    | 7 | 6 | 5 | 4 | 3 | 2 | 1 | 0 |
        // +0 |  LFO  |    F B    |    ALG    |
        // +1 |              01?              |

        var global = new byte[2];
        rdr.readFully(global);
        rest[0] -= 2 /* sizeof(global) */;
        this.lfo = (global[0] >> 6) & 3;
        this.fb = (global[0] >> 3) & 7;
        this.alg = Algorithm.values()[global[0] & 7];
        this.operators = new VMAFMOperator[4];
        var n = this.alg.operatorCount();
        for (var op = 0; op < 4; op++) {
            this.operators[op] = new VMAFMOperator();
            this.operators[op].num = op;
        }
        for (var op = 0; op < n; op++) {
            this.operators[op].read(rdr, rest);
        }
    }

    @Override
    public void readUnusedRest(DataInputStream rdr, int[] rest) throws IOException {
        var n = this.alg.operatorCount();
        for (var op = n; op < 4; op++) {
            this.operators[op] = new VMAFMOperator();
            this.operators[op].num = op;
            this.operators[op].read(rdr, rest);
        }
    }

    public byte[] toBytes(boolean staticLen) {
        var b = new ByteArrayOutputStream();
        b.write(((this.lfo & 3) << 6) | ((this.fb & 7) << 3) | (this.alg.ordinal() & 7));
        b.write(1);
        var n = 4;
        if (!staticLen) {
            n = this.alg.operatorCount();
        }
        for (var op = 0; op < n; op++) {
            b.writeBytes(this.operators[op].toBytes());
        }
        return b.toByteArray();
    }

    //type VMAFMVoiceMarshaller VMAFMVoice
    static class VMAFMVoiceMarshaller {
        //`json: "ymf825_data"`
        int[] YMF825Data;
    }

    private static Gson gson = new Gson().newBuilder().create();

    public byte[] marshalJSON() throws IOException {
        return gson.toJson(new VMAFMVoiceMarshaller() {{
            YMF825Data = bytesToInts(ToVM35().toBytes(true, true));
        }}).getBytes();
    }

    @Override
    public String toString() {
        var s = new ArrayList<String>();
        s.add("lfo=%d fb=%d alg=%s".formatted(this.lfo, this.fb, this.alg));
        for (var op = 0; op < this.alg.operatorCount(); op++) {
            s.add(this.operators[op].toString());
        }
        s.add("Raw=" + hex(this.toBytes(false)));
        return String.join("\n", s);
    }

    public VM35FMVoice ToVM35() {
        var result = new VM35FMVoice() {{
            drumKey = new Note(0);
            panpot = Panpot.Center;
            bo = BasicOctave.Normal;
            lfo = VMAFMVoice.this.lfo;
            pe = false;
            alg = VMAFMVoice.this.alg;
            operators = new ArrayList<>(4);
        }};
        var fb = this.fb;
        for (var op = 0; op < 4; op++) {
            // add, not set: "new ArrayList<>(4)" is a capacity of 4, not a size of
            // 4, so the list is still empty here (go's "make([]T, 4)" is not)
            result.operators.add(this.operators[op].ToVM35(fb));
            fb = 0;
        }
        return result;
    }
}
