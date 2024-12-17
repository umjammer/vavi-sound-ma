/*
 * https://github.com/but80/smaf825
 */

package vavi.sound.yamaha.smaf.voice;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.Arrays;

import javax.sound.sampled.AudioInputStream;

import vavi.sound.yamaha.smaf.enums.Enums.Algorithm;
import vavi.sound.yamaha.smaf.enums.Enums.BasicOctave;
import vavi.sound.yamaha.smaf.enums.Enums.Multiplier;
import vavi.sound.yamaha.smaf.enums.Enums.Panpot;
import vavi.sound.yamaha.smaf.enums.Note;

import static java.lang.System.getLogger;
import static javassist.compiler.ast.ASTList.append;
import static vavi.sound.yamaha.smaf.enums.Enums.BasicOctave.BasicOctave_Normal;
import static vavi.sound.yamaha.smaf.enums.Enums.Panpot.Panpot_Center;
import static vavi.sound.yamaha.smaf.enums.Note.Note_A3;


enum VM35FMVoiceVersion {
    VM35FMVoiceVersion_VM3Lib,
    VM35FMVoiceVersion_VM3Exclusive,
    VM35FMVoiceVersion_VM5,
}

public class VM35FMOperator {

    private static final Logger logger = getLogger(VM35FMOperator.class.getName());

    //`json:"-"`
    int Num;      // Operator number
    // `json:"-"`
    VM35FMVoiceVersion Version;
    //`json:"multi"`
    Multiplier MULTI;  // Multiplier
    //`json:"dt"`     // Detune
    int DT;
    //`json:"ar"`    // Attack Rate
    int AR;
    //`json:"dr"`    // Decay Rate
    int DR;
    //`json:"sr"`    // Sustain Rate
    int SR;
    //`json:"rr"`    // Release Rate
    int RR;
    //`json:"sl"`    // Sustain Level
    int SL;
    int TL; //`json:"tl"`    // Total Level
    int KSL; //`json:"ksl"`   // Key Scaling Level
    int DAM; //`json:"dam"`   // Depth of AM
    int DVB; //`json:"dvb"`   // Depth of Vibrato
    int FB; //`json:"fb"`    // Feedback
    int WS; //`json:"ws"`    // Wave Shape
    boolean XOF; //`json:"xof"`   // Ignore KeyOff
    boolean SUS; //`json:"sus"`   // Keep sustain rate after KeyOff (unused in YMF825)
    boolean KSR; //`json:"ksr"`   // Key Scaling Rate
    boolean EAM; //`json:"eam"`   // Enable AM
    boolean EVB; //`json:"evb"`   // Enable Vibrato

    void Read(DataInputStream rdr, int[] rest) throws IOException {
        //    | 7 | 6 | 5 | 4 | 3 | 2 | 1 | 0 |
        // +0 |      S R      |XOF| - |SUS|KSR|
        // +1 |      R R      |      D R      |
        // +2 |      A R      |      S L      |
        // +3 |          T L          |  KSL  |
        // +4 | - |  DAM  |EAM| - |  DVB  |EVB|
        // +5 |     MULTI     | - |    D T    |
        // +6 |        W S        |    F B    |

        var data = new byte[7];
        rdr.readFully(data);
        rest[0] -= data.length;

        this.SR = (data[0] >> 4);
        this.XOF = (data[0] & 0x08) != 0;
        this.SUS = (data[0] & 0x02) != 0;
        this.KSR = (data[0] & 0x01) != 0;
        this.RR = (data[1] >> 4);
        this.DR = (data[1] & 15);
        this.AR = (data[2] >> 4);
        this.SL = (data[2] & 15);
        this.TL = (data[3] >> 2);
        this.KSL = (data[3] & 3);
        this.DAM = (data[4] >> 5 & 3);
        this.EAM = (data[4] & 0x10) != 0;
        this.DVB = (data[4] >> 1 & 3);
        this.EVB = (data[4] & 0x01) != 0;
        this.MULTI = Multiplier.values()[data[5] >> 4];
        this.DT = (data[5] & 7);
        this.WS = (data[6] >> 3);
        this.FB = (data[6] & 7);
    }

    byte[] Bytes(boolean forYMF825) {
        var sus = this.SUS;
        var ws = this.WS & 31;
        if (forYMF825) {
            sus = false;
            if (ws < 0 || ws == 15 || ws == 23 || 31 <= ws) {
                logger.log(Level.WARNING, "Invalid wave shape %d".formatted(ws));
            }
        }
        return new byte[] {
                (byte) (this.SR & 15) << 4 | util.BoolToByte(this.XOF, 0x08) | util.BoolToByte(sus, 0x02) | util.BoolToByte(this.KSR, 0x01),
                (byte) ((this.RR & 15) << 4 | (this.DR & 15)),
                (byte) ((this.AR & 15) << 4 | (this.SL & 15)),
                (byte) ((this.TL & 63) << 2 | (this.KSL & 3)),
                (byte) ((this.DAM & 3) << 5 | util.BoolToByte(this.EAM, 0x10) |
                        (this.DVB & 3) << 1 | util.BoolToByte(this.EVB, 0x01)),
                (byte) ((this.MULTI.ordinal() & 15) << 4 | (this.DT & 7)),
                (byte) ((ws) << 3 | (this.FB & 7)),
        };
    }

    @Override
    public String toString() {
        var t =  new ArrayList<String>();
        t.add("ADSR=%d,%d,%d,%d".formatted(this.AR, this.DR, this.SR, this.RR));
        t.add("SL=%d".formatted(this.SL));
        t.add("TL=%d".formatted(this.TL));
        t.add("KSL=%d".formatted(this.KSL));
        t.add("FB=%d".formatted(this.FB));
        t.add("WS=%d".formatted(this.WS));
        if (this.EAM) {
            t.add("AM=%d".formatted(this.DAM));
        }
        if (this.EVB) {
            t.add("VB=%d".formatted(this.DVB));
        }
        if (this.XOF) {
            t.add("XOF");
        }
        if (this.SUS) {
            t.add("SUS");
        }
        if (this.KSR) {
            t.add("KSR");
        }
        var s = String.join(" ", t);
        return "Op #%d: MULTI=%s DT=%d\n".formatted(this.Num + 1, this.MULTI, this.DT) + util.Indent(s, "\t");
    }
}

public class VM35FMVoice {

    private static final Logger logger = getLogger(VM35FMVoice.class.getName());

    //`json:"-"`
    VM35FMVoiceVersion Version;
    //`json:"drum_key"`
    Note DrumKey;
    // Panpot (unused in YMF825)
    //`json:"panpot"`
    Panpot PANPOT;
    //`json:"bo"`
    BasicOctave BO;
    //`json:"lfo"`
    int LFO;
    // Panpot Enable (unused in YMF825)
    // `json:"pe"`
    boolean PE;
    //`json:"alg"`
    Algorithm[] ALG = new Algorithm[4];

    //`json:"operators"`
    VM35FMOperator[] Operators;

    VM35FMVoice(byte[] data, VM35FMVoiceVersion version) throws IOException {
        this.Version = version;
        var rest = new int[] {data.length};
        var rdr = new DataInputStream(new ByteArrayInputStream(data));
        try {
            this.Read(rdr, rest);
        } catch (Exception e) {
            logger.log(Level.ERROR, "NewVM35FMVoice invalid data: %s (want %d, got %d bytes)".formatted(util.Hex(data), data.length - rest[0], data.length)));
        }
        switch (version) {
            case VM35FMVoiceVersion_VM3Lib, VM35FMVoiceVersion_VM3Exclusive:
                this.ReadUnusedRest(rdr, rest);
        }
        if (rest[0] != 0) {
            logger.log(Level.ERROR, "Wrong size of VM3/VM5 voice data (want %d, got %d bytes): %s".formatted(data.length - rest[0], data.length, util.Hex(data)));
        }
    }

    void Read(DataInputStream rdr, int[] rest) throws IOException {
        switch (this.Version) {
            case VM35FMVoiceVersion_VM3Exclusive:
                //    | 7 | 6 | 5 | 4 | 3 | 2 | 1 | 0 |
                // ------------------------------------ Global
                // +0 |       |PN4|LF1|SR3|RR3|AR3|TL5|  // bit0-3は1つ次のOpに作用
                // +1 |                               |  // Drumkey?
                // +2 | - |   PAN0123     |       | ? |
                // +3 | - |LF0|P E|       |    ALG    |
                // ------------------------------------ Op0
                // +4 | - |   SR012   |XOF| - |SUS|KSR|
                // +5 | - |   RR012   |      D R      |
                // +6 | - |   AR012   |      S L      |
                // +7 | - |      TL01234      |  KSL  |
                // +8 |   -   |ML3|WS4|SR3|RR3|AR3|TL5|  // bit0-3は1つ次のOpに作用
                // +9 | - |  DAM  |EAM| - |  DVB  |EVB|
                // +A | - |  MUL012   | - |    DT     |
                // +B | - |     WS0123    |    FB     |
                // ------------------------------------ Op1
                // ...
                var raw = new byte[4 + 8 * 4];
                rdr.readFully(raw);
                rest[0] -= raw.length;
                raw[2] |= raw[0] << 2 & 0x80;
                raw[3] |= raw[0] << 3 & 0x80;
                for (var op = 0; op < 4; op++) {
                    raw[4 + op * 8] |= raw[op * 8] << 4 & 0x80;
                    raw[5 + op * 8] |= raw[op * 8] << 5 & 0x80;
                    raw[6 + op * 8] |= raw[op * 8] << 6 & 0x80;
                    raw[7 + op * 8] |= raw[op * 8] << 7 & 0x80;
                    raw[10 + op * 8] |= raw[8 + op * 8] << 2 & 0x80;
                    raw[11 + op * 8] |= raw[8 + op * 8] << 3 & 0x80;
                }
                //    | 7 | 6 | 5 | 4 | 3 | 2 | 1 | 0 |
                // ------------------------------------ Global
                // +0 |                               |
                // +1 |                               |  // Drumkey?
                // +2 |      PANPOT       |       | ? |
                // +3 |  LFO  |P E|       |    ALG    |
                // ------------------------------------ Op0
                // +4 |      S R      |XOF| - |SUS|KSR|
                // +5 |      R R      |      D R      |
                // +6 |      A R      |      S L      |
                // +7 |         T L           |  KSL  |
                // +8 |                               |
                // +9 | - |  DAM  |EAM| - |  DVB  |EVB|
                // +A |      MUL      | - |    DT     |
                // +B |        W S        |    FB     |
                // ------------------------------------ Op1
                // ...
                var fixed = Arrays.copyOfRange(raw, 1, 4);
                for (var op = 0; op < 4; op++) {
                    fixed = append(fixed, raw[4 + op * 8:8 + op * 8]...);
                    fixed = append(fixed, raw[9 + op * 8:12 + op * 8]...);
                }
                rdr = new DataInputStream(new ByteArrayInputStream(fixed));
                var rest_ = fixed.length;
                rest[0] = rest_;
        }

        //          | 7 | 6 | 5 | 4 | 3 | 2 | 1 | 0 |
        // Global+0 |            DrumKey            |
        // Global+1 |       PANPOT      | - |  B O  |
        // Global+2 |  LFO  |PE |   -   |    ALG    |

        var global = new byte[3];
        rdr.readFully(global);
        rest[0] -= global.length;
        this.DrumKey = Note.values()[global[0] & 0xff];
        this.PANPOT = Panpot.values()[global[1] >> 3];
        this.BO = BasicOctave.values()[global[1] & 3];
        this.LFO = global[2] >> 6 & 3;
        this.PE = (global[2] & 0x20) != 0;
        this.ALG = Algorithm.values()[global[2] & 7];
        this.Operators = new VM35FMOperator[4];
        var n = this.ALG.OperatorCount();
        for (var op = 0; op < 4; op++) {
            this.Operators[op] = new VM35FMOperator() {{
                Version = this.Version;
                Num = op;
            }};
        }
        for (var op = 0; op < n; op++) {
            err = this.Operators[op].Read(rdr, rest);
        }
    }

    void ReadUnusedRest(DataInputStream rdr, int[] rest) {
        var n = this.ALG.OperatorCount();
        for (var op = n; op < 4; op++) {
            this.Operators[op] = new VM35FMOperator() {{
                Num = op;
            }};
            rest = this.Operators[op].Read(rdr, );
        }
    }

    byte[] Bytes(boolean staticLen, boolean forYMF825) {
        var pan = this.PANPOT;
        var pe = this.PE;
        if (forYMF825) {
            pan = Panpot.values()[0];
            pe = false;
        }
        var b = new byte[] {
                (byte) ((pan.ordinal() & 31) << 3 | (this.BO.ordinal() & 3)),
                (byte) ((this.LFO & 3) << 6 | util.BoolToByte(pe, 0x20) | (this.ALG & 7)),
        };
        var n = 4;
        if (!staticLen) {
            n = this.ALG.OperatorCount();
        }
        for (var op = 0; op < n; op++) {
            b = append(b, this.Operators[op].Bytes(forYMF825)...);
        }
        return b;
    }

    //type vm35FMVoiceMarshaler VM35FMVoice
    static class X {
        vm35FMVoiceMarshaler vm35FMVoiceMarshaler;
        int[] YMF825Data; // `json:"ymf825_data"`;
    }

    byte[] MarshalJSON() {
        return json.Marshal(new X() {{
            vm35FMVoiceMarshaler = vm35FMVoiceMarshaler(this);
            YMF825Data = util.BytesToInts(this.Bytes(true, true));
        }});
    }

    @Override
    public String toString() {
        var s = new ArrayList<String>();
        //s.add("Flag: 0x%02X".formatted(this.Flag));
        s.add("DrumKey=%s PANPOT=%s LFO=%d PE=%v ALG=%s".formatted(this.DrumKey.name(), this.PANPOT, this.LFO, this.PE, this.ALG));
        //s.add(Enigma1: 0x%04X".formatted(this.Enigma1));
        //s.add(Enigma2: 0x%08X".formatted(this.Enigma2));
        for (var op = 0; op < this.ALG.OperatorCount(); op++) {
            s.add(this.Operators[op].toString());
        }
        s.add("Raw=" + util.Hex(this.Bytes(false, false)));
        return String.join("\n", s);
    }

    VM35FMVoice DemoVM35FMVoice() {
        var v = new VM35FMVoice() {{
            Version = VM35FMVoiceVersion.VM35FMVoiceVersion_VM5;
            DrumKey = Note_A3;
            PANPOT = Panpot_Center;
            BO = BasicOctave_Normal;
            ALG = Algorithm.values()[0];
            Operators = new VM35FMOperator[4];
        }};
        for (var i = 0; i < 4; i++) {
            this.Operators[i] = new VM35FMOperator();
        }
        var op1 = new VM35FMOperator() {{
            MULTI = 1;
            AR = 14;
            DR = 2;
            SR = 8;
            SL = 8;
            RR = 8;
        }};
        this.Operators[1] = op1;
        return v;
    }
}