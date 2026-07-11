/*
 * https://github.com/but80/smaf825
 */

package vavi.sound.yamaha.smaf.voice;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.Arrays;

import com.google.gson.Gson;
import java.util.List;
import vavi.sound.yamaha.smaf.enums.Enums.Algorithm;
import vavi.sound.yamaha.smaf.enums.Enums.BasicOctave;
import vavi.sound.yamaha.smaf.enums.Enums.Multiplier;
import vavi.sound.yamaha.smaf.enums.Enums.Panpot;
import vavi.sound.yamaha.smaf.enums.Note;

import static java.lang.System.getLogger;
import static vavi.sound.yamaha.smaf.enums.Enums.BasicOctave.Normal;
import static vavi.sound.yamaha.smaf.enums.Enums.Panpot.Center;
import static vavi.sound.yamaha.smaf.enums.Note.Note_A3;
import static vavi.sound.yamaha.smaf.util.BinaryUtil.boolToByte;
import static vavi.sound.yamaha.smaf.util.BinaryUtil.bytesToInts;
import static vavi.sound.yamaha.smaf.util.TextUtil.hex;
import static vavi.sound.yamaha.smaf.util.TextUtil.indent;


public class VM35FMVoice implements VM35Voice {

    public static class VM35FMOperator {

        private static final Logger logger = getLogger(VM35FMOperator.class.getName());

        /** Operator number */
        //`json:"-"`
        public int num;
        //`json:"-"`
        public VM35FMVoiceVersion version;
        /** Multiplier */
        //`json:"multi"`
        public Multiplier multi;
        /** Detune */
        //`json:"dt"`
        public int DT;
        /** Attack Rate */
        //`json:"ar"`
        public int ar;
        /** Decay Rate */
        //`json:"dr"`
        public int dr;
        /** Sustain Rate */
        //`json:"sr"`
        public int sr;
        /** Release Rate */
        //`json:"rr"`
        public int rr;
        /** Sustain Level */
        //`json:"sl"`
        public int sl;
        /** Total Level */
        //`json:"tl"`
        public int tl;
        /** Key Scaling Level */
        //`json:"ksl"`
        public int ksl;
        /** Depth of am */
        //`json:"dam"`
        public int dam;
        /** Depth of Vibrato */
        //`json:"dvb"`
        public int dvb;
        /** Feedback */
        //`json:"fb"`
        public int fb;
        /** Wave Shape */
        //`json:"ws"`
        public int ws;
        /** Ignore KeyOff */
        //`json:"xof"`
        public boolean xof;
        /** Keep sustain rate after KeyOff (unused in YMF825) */
        //`json:"sus"`
        public boolean sus;
        /** Key Scaling Rate */
        //`json:"ksr"`
        public boolean ksr;
        /** Enable am */
        //`json:"eam"`
        public boolean eam;
        /** Enable Vibrato */
        //`json:"evb"`
        public boolean evb;

        public void read(DataInputStream rdr, int[] rest) throws IOException {
            //    | 7 | 6 | 5 | 4 | 3 | 2 | 1 | 0 |
            // +0 |      S R      |xof| - |sus|ksr|
            // +1 |      R R      |      D R      |
            // +2 |      A R      |      S L      |
            // +3 |          T L          |  ksl  |
            // +4 | - |  dam  |eam| - |  dvb  |evb|
            // +5 |     multi     | - |    D T    |
            // +6 |        W S        |    F B    |

            var data = new byte[7];
            rdr.readFully(data);
            rest[0] -= data.length;

            this.sr = ((data[0] & 0xff) >>> 4);
            this.xof = (data[0] & 0x08) != 0;
            this.sus = (data[0] & 0x02) != 0;
            this.ksr = (data[0] & 0x01) != 0;
            this.rr = ((data[1] & 0xff) >>> 4);
            this.dr = (data[1] & 15);
            this.ar = ((data[2] & 0xff) >>> 4);
            this.sl = (data[2] & 15);
            this.tl = ((data[3] & 0xff) >>> 2);
            this.ksl = (data[3] & 3);
            this.dam = ((data[4] & 0xff) >>> 5 & 3);
            this.eam = (data[4] & 0x10) != 0;
            this.dvb = ((data[4] & 0xff) >>> 1 & 3);
            this.evb = (data[4] & 0x01) != 0;
            this.multi = Multiplier.values()[(data[5] & 0xff) >>> 4];
            this.DT = (data[5] & 7);
            this.ws = ((data[6] & 0xff) >>> 3);
            this.fb = (data[6] & 7);
        }

        public byte[] getBytes(boolean forYMF825) {
            var sus = this.sus;
            var ws = this.ws & 31;
            if (forYMF825) {
                sus = false;
                if (ws < 0 || ws == 15 || ws == 23 || 31 <= ws) {
                    logger.log(Level.WARNING, "Invalid wave shape %d".formatted(ws));
                }
            }
            return new byte[] {
                    (byte) ((this.sr & 15) << 4 | boolToByte(this.xof, (byte) 0x08) | boolToByte(sus, (byte) 0x02) | boolToByte(this.ksr, (byte) 0x01)),
                    (byte) ((this.rr & 15) << 4 | (this.dr & 15)),
                    (byte) ((this.ar & 15) << 4 | (this.sl & 15)),
                    (byte) ((this.tl & 63) << 2 | (this.ksl & 3)),
                    (byte) ((this.dam & 3) << 5 | boolToByte(this.eam, (byte) 0x10) |
                            (this.dvb & 3) << 1 | boolToByte(this.evb, (byte) 0x01)),
                    (byte) ((this.multi.ordinal() & 15) << 4 | (this.DT & 7)),
                    (byte) ((ws) << 3 | (this.fb & 7)),
            };
        }

        @Override
        public String toString() {
            var t =  new ArrayList<String>();
            t.add("ADSR=%d,%d,%d,%d".formatted(this.ar, this.dr, this.sr, this.rr));
            t.add("sl=%d".formatted(this.sl));
            t.add("tl=%d".formatted(this.tl));
            t.add("ksl=%d".formatted(this.ksl));
            t.add("fb=%d".formatted(this.fb));
            t.add("ws=%d".formatted(this.ws));
            if (this.eam) {
                t.add("am=%d".formatted(this.dam));
            }
            if (this.evb) {
                t.add("VB=%d".formatted(this.dvb));
            }
            if (this.xof) {
                t.add("xof");
            }
            if (this.sus) {
                t.add("sus");
            }
            if (this.ksr) {
                t.add("ksr");
            }
            var s = String.join(" ", t);
            return "Op #%d: multi=%s DT=%d\n".formatted(this.num + 1, this.multi, this.DT) + indent(s, "\t");
        }

        /**
         * Normalize removes outliers from the timbre data and normalizes it.
         * Returns true if the tone was normal to begin with.
         */
        public boolean normalize() {
            var ok = new boolean[] {true};
            VM35Voice.normalizeInt(ok, this.multi.ordinal(), 0, 15);
            VM35Voice.normalizeInt(ok, this.DT, 0, 7);
            VM35Voice.normalizeInt(ok, this.ar, 0, 15);
            VM35Voice.normalizeInt(ok, this.dr, 0, 15);
            VM35Voice.normalizeInt(ok, this.sr, 0, 15);
            VM35Voice.normalizeInt(ok, this.rr, 0, 15);
            VM35Voice.normalizeInt(ok, this.sl, 0, 15);
            VM35Voice.normalizeInt(ok, this.tl, 0, 63);
            VM35Voice.normalizeInt(ok, this.ksl, 0, 3);
            VM35Voice.normalizeInt(ok, this.dam, 0, 3);
            VM35Voice.normalizeInt(ok, this.dvb, 0, 3);
            VM35Voice.normalizeInt(ok, this.fb, 0, 7);
            VM35Voice.normalizeInt(ok, this.ws, 0, 31);
            // TODO User waveform warning
            return ok[0];
        }
    }

    private static final Logger logger = getLogger(VM35FMVoice.class.getName());

    //`json:"-"`
    public VM35FMVoiceVersion version = VM35FMVoiceVersion.VM3Lib;
    //`json:"drum_key"`
    public Note drumKey;
    // Panpot (unused in YMF825)
    //`json:"panpot"`
    public Panpot panpot;
    //`json:"bo"`
    public BasicOctave bo;
    //`json:"lfo"`
    public int lfo;
    // Panpot Enable (unused in YMF825)
    // `json:"pe"`
    public boolean pe;
    //`json:"alg"`
    public Algorithm alg;

    //`json:"operators"`
    public List<VM35FMOperator> operators = new ArrayList<>(4);

    public VM35FMVoice() {}

    public VM35FMVoice(byte[] data, VM35FMVoiceVersion version) throws IOException {
        this.version = version;
        var rest = new int[] {data.length};
        var rdr = new DataInputStream(new ByteArrayInputStream(data));
        try {
            this.read(rdr, rest);
        } catch (Exception e) {
            logger.log(Level.ERROR, "NewVM35FMVoice invalid data: %s (want %d, got %d bytes)".formatted(hex(data), data.length - rest[0], data.length));
        }
        switch (version) {
            case VM3Lib, VM3Exclusive:
                this.readUnusedRest(rdr, rest);
        }
        if (rest[0] != 0) {
            logger.log(Level.ERROR, "Wrong size of VM3/VM5 voice data (want %d, got %d bytes): %s".formatted(data.length - rest[0], data.length, hex(data)));
        }
    }

    @Override
    public void read(DataInputStream rdr, int[] rest) throws IOException {
        switch (this.version) {
            case VM3Exclusive:
                //    | 7 | 6 | 5 | 4 | 3 | 2 | 1 | 0 |
                // ------------------------------------ Global
                // +0 |       |PN4|LF1|SR3|RR3|AR3|TL5|  // Bits 0-3 affect the next Op
                // +1 |                               |  // Drumkey?
                // +2 | - |   PAN0123     |       | ? |
                // +3 | - |LF0|P E|       |    alg    |
                // ------------------------------------ Op0
                // +4 | - |   SR012   |xof| - |sus|ksr|
                // +5 | - |   RR012   |      D R      |
                // +6 | - |   AR012   |      S L      |
                // +7 | - |      TL01234      |  ksl  |
                // +8 |   -   |ML3|WS4|SR3|RR3|AR3|TL5|  // Bits 0-3 affect the next Op
                // +9 | - |  dam  |eam| - |  dvb  |evb|
                // +A | - |  MUL012   | - |    DT     |
                // +B | - |     WS0123    |    fb     |
                // ------------------------------------ Op1
                // ...
                var raw = new byte[4 + 8 * 4];
                rdr.readFully(raw);
                rest[0] -= raw.length;
                raw[2] |= (byte) ((raw[0] << 2) & 0x80);
                raw[3] |= (byte) ((raw[0] << 3) & 0x80);
                for (var op = 0; op < 4; op++) {
                    raw[4 + op * 8] |= (byte) ((raw[op * 8] << 4) & 0x80);
                    raw[5 + op * 8] |= (byte) ((raw[op * 8] << 5) & 0x80);
                    raw[6 + op * 8] |= (byte) ((raw[op * 8] << 6) & 0x80);
                    raw[7 + op * 8] |= (byte) ((raw[op * 8] << 7) & 0x80);
                    raw[10 + op * 8] |= (byte) ((raw[8 + op * 8] << 2) & 0x80);
                    raw[11 + op * 8] |= (byte) ((raw[8 + op * 8] << 3) & 0x80);
                }
                //    | 7 | 6 | 5 | 4 | 3 | 2 | 1 | 0 |
                // ------------------------------------ Global
                // +0 |                               |
                // +1 |                               |  // Drumkey?
                // +2 |      panpot       |       | ? |
                // +3 |  lfo  |P E|       |    alg    |
                // ------------------------------------ Op0
                // +4 |      S R      |xof| - |sus|ksr|
                // +5 |      R R      |      D R      |
                // +6 |      A R      |      S L      |
                // +7 |         T L           |  ksl  |
                // +8 |                               |
                // +9 | - |  dam  |eam| - |  dvb  |evb|
                // +A |      MUL      | - |    DT     |
                // +B |        W S        |    fb     |
                // ------------------------------------ Op1
                // ...
                ByteArrayOutputStream fixed = new ByteArrayOutputStream();
                fixed.write(Arrays.copyOfRange(raw, 1, 4));
                for (var op = 0; op < 4; op++) {
                    fixed.write(Arrays.copyOfRange(raw, 4 + op * 8, 8 + op * 8));
                    fixed.write(Arrays.copyOfRange(raw, 9 + op * 8, 12 + op * 8));
                }
                rdr = new DataInputStream(new ByteArrayInputStream(fixed.toByteArray()));
                var rest_ = fixed.size();
                rest[0] = rest_;
        }

        //          | 7 | 6 | 5 | 4 | 3 | 2 | 1 | 0 |
        // Global+0 |            drumKey            |
        // Global+1 |       panpot      | - |  B O  |
        // Global+2 |  lfo  |pe |   -   |    alg    |

        var global = new byte[3];
        rdr.readFully(global);
        rest[0] -= global.length;
        this.drumKey = new Note(global[0] & 0xff);
        this.panpot = Panpot.values()[(global[1] & 0xff) >> 3];
        this.bo = BasicOctave.values()[global[1] & 3];
        this.lfo = (global[2] & 0xff) >> 6 & 3;
        this.pe = (global[2] & 0x20) != 0;
        this.alg = Algorithm.values()[global[2] & 7];
        this.operators = new ArrayList<>(4);
        var n = this.alg.operatorCount();
        for (var op = 0; op < 4; op++) {
            this.operators.add(new VM35FMOperator());
            this.operators.get(op).version = this.version;
            this.operators.get(op).num = op;
        }
        for (var op = 0; op < n; op++) {
            this.operators.get(op).read(rdr, rest);
        }
    }

    @Override
    public void readUnusedRest(DataInputStream rdr, int[] rest) throws IOException {
        var n = this.alg.operatorCount();
        for (var op = n; op < 4; op++) {
            this.operators.set(op, new VM35FMOperator());
            this.operators.get(op).num = op;
            this.operators.get(op).read(rdr, rest);
        }
    }

    public byte[] toBytes(boolean staticLen, boolean forYMF825) {
        var pan = this.panpot;
        var pe = this.pe;
        if (forYMF825) {
            pan = Panpot.values()[0];
            pe = false;
        }
        var b = new ByteArrayOutputStream();
        b.write((pan.ordinal() & 31) << 3 | (this.bo.ordinal() & 3));
        b.write((this.lfo & 3) << 6 | boolToByte(pe, (byte) 0x20) | (this.alg.ordinal() & 7));
        var n = 4;
        if (!staticLen) {
            n = this.alg.operatorCount();
        }
        for (var op = 0; op < n; op++) {
            b.writeBytes(this.operators.get(op).getBytes(forYMF825));
        }
        return b.toByteArray();
    }

    //type VM35FMVoiceMarshaler VM35FMVoice
    private static class VM35FMVoiceMarshaler {
        int[] YMF825Data; // `json:"ymf825_data"`;
    }

    private static Gson gson = new Gson().newBuilder().create();

    public byte[] marshalJSON() {
        return gson.toJson(new VM35FMVoiceMarshaler() {{
            YMF825Data = bytesToInts(toBytes(true, true));
        }}).getBytes();
    }

    @Override
    public String toString() {
        var s = new ArrayList<String>();
        //s.add("flag: 0x%02X".formatted(this.flag));
        s.add("drumKey=%s panpot=%s lfo=%d pe=%s alg=%s".formatted(this.drumKey.name(), this.panpot, this.lfo, this.pe, this.alg));
        //s.add(enigma1: 0x%04X".formatted(this.enigma1));
        //s.add(Enigma2: 0x%08X".formatted(this.Enigma2));
        for (var op = 0; op < this.alg.operatorCount(); op++) {
            s.add(this.operators.get(op).toString());
        }
        s.add("Raw=" + hex(this.toBytes(false, false)));
        return String.join("\n", s);
    }

    public VM35FMVoice demoVM35FMVoice() {
        var v = new VM35FMVoice() {{
            version = VM35FMVoiceVersion.VM5;
            drumKey = new Note(Note_A3);
            panpot = Center;
            bo = Normal;
            alg = Algorithm.values()[0];
            operators = new ArrayList<>(4);
        }};
        for (var i = 0; i < 4; i++) {
            this.operators.set(i, new VM35FMOperator());
        }
        var op1 = new VM35FMOperator() {{
            multi = Multiplier.values()[1];
            ar = 14;
            dr = 2;
            sr = 8;
            sl = 8;
            rr = 8;
        }};
        this.operators.set(1, op1);
        return v;
    }

    // Normalize removes outliers from the timbre data and normalizes it.
    // Returns true if the tone was normal to begin with. */
    public boolean /* voice *VM35FMVoice */ normalize() {
        var ok = new boolean[] {true};
        VM35Voice.normalizeInt(ok, this.drumKey.note, 0, 127);
        VM35Voice.normalizeInt(ok, this.panpot.ordinal(), 0, 31);
        VM35Voice.normalizeInt(ok, this.bo.ordinal(), 0, 3);
        VM35Voice.normalizeInt(ok, this.lfo, 0, 3);
        VM35Voice.normalizeInt(ok, this.alg.operatorCount(), 0, 7);
        var ops = 4;
        if (this.alg.ordinal() < 2) {
            ops = 2;
        }
        while (this.operators.size() < ops) {
            this.operators.add(new VM35FMOperator());
            ok[0] = false;
        }
        if (ops < this.operators.size()) {
            this.operators = this.operators.subList(0, ops);
            ok[0] = false;
        }
        for (var i = 0; i < this.operators.size(); i++) {
            var op = this.operators.get(i);
            if (op == null) {
                op = new VM35FMOperator();
                this.operators.set(i, op);
                ok[0] = false;
            }
            if (!op.normalize()) {
                ok[0] = false;
            }
        }
        return ok[0];
    }
}