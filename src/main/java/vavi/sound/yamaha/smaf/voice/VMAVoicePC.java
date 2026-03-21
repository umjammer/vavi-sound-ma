/*
 * https://github.com/but80/smaf825
 */

package vavi.sound.yamaha.smaf.voice;

import java.io.DataInputStream;
import java.io.IOException;

import vavi.sound.yamaha.smaf.enums.Enums.VoiceType;
import vavi.sound.yamaha.smaf.enums.Note;
import vavi.sound.yamaha.smaf.voice.VM35Voice.VM35FMVoiceVersion;

import static vavi.sound.yamaha.smaf.util.TextUtil.indent;
import static vavi.sound.yamaha.smaf.voice.VM35Voice.normalizeString;
import static vavi.sound.yamaha.smaf.voice.VM35Voice.normalizeInt;


public class VMAVoicePC {

    //`json:"name"`
    public String name;
    //`json:"bank"`
    public int bank;
    //`json:"pc"`
    public int pc;
    //`json:"voice"`
    public VMAFMVoice voice;

    static class VMAVoicePCHeaderRawData {

        //    | 7 | 6 | 5 | 4 | 3 | 2 | 1 | 0 |
        // +0 |              00?              |
        // +1 |             bank              |
        // +2 |              pc               |
        byte enigma;
        byte bank;
        byte pc;

        void read(DataInputStream dis) throws IOException {
            enigma = dis.readByte();
            bank = dis.readByte();
            pc = dis.readByte();
        }
    }

    void read(DataInputStream rdr, int[] rest) throws IOException {
        var data = new VMAVoicePCHeaderRawData();
        data.read(rdr);
        rest[0] -= 3 /* sizeof(data) */;
        this.bank = data.bank;
        this.pc = data.pc;
        this.voice = new VMAFMVoice();
        this.voice.read(rdr, rest);
        this.voice.readUnusedRest(rdr, rest);
        //
        byte enigma2 = rdr.readByte();
        rest[0]--;
    }

    @Override
    public String toString() {
        var s = "bank %d @%d".formatted(this.bank, this.pc);
        if (!this.name.isEmpty()) {
            s += ": [%s]".formatted(this.name);
        }
        s += "\n";
        return s + indent(this.voice.toString(), "\t");
    }

    VM35VoicePC toVM35() {
        return new VM35VoicePC() {{
            name = this.name;
            flag = 0x24;
            bankMSB = 0;
            bankLSB = bank;
            pc = VMAVoicePC.this.pc;
            drumNote = new Note(0);
            enigma1 = 0;
            voiceType = VoiceType.FM;
            voice = VMAVoicePC.this.voice.ToVM35();
        }};
    }

    /**
     * Normalize removes outliers from the timbre data and normalizes it.
     * Returns true if the tone was normal to begin with.
     */
    boolean normalize() {
        var ok = new boolean[] {true};

        VM35VoicePC voice = this.toVM35();
        if (VM35FMVoiceVersion.values().length < voice.version.ordinal()) {
            voice.version = VM35FMVoiceVersion.VM5;
            ok[0] = false;
        }
        voice.name = normalizeString(ok, voice.name, "(undefined)");
        voice.bankMSB = normalizeInt(ok, voice.bankMSB, 0, 127);
        voice.bankLSB = normalizeInt(ok, voice.bankLSB, 0, 127);
        voice.pc = normalizeInt(ok, voice.pc, 0, 127);
        voice.drumNote = new Note(normalizeInt(ok, voice.drumNote.note, 0, 127));
        if (VoiceType.values().length < voice.voiceType.ordinal()) {
            voice.voiceType = VoiceType.FM;
            ok[0] = false;
        }
        switch (voice.voiceType) {
            case FM:
                if (voice.voice == null) {
                    voice.voice = new VM35FMVoice();
                    ok[0] = false;
                }
                if (!((VM35FMVoice) voice.voice).normalize()) {
                    ok[0] = false;
                }
                break;
            case PCM:
                if (voice.voice == null) {
                    voice.voice = new VM35PCMVoice();
                    ok[0] = false;
                }
                if (!((VM35PCMVoice) voice.voice).normalize()) {
                    ok[0] = false;
                }
                break;
            case AL:
                voice.voiceType = VoiceType.FM;
                voice.voice = new VM35FMVoice();
                ok[0] = false;
                break;
        }
        return ok[0];
    }
}