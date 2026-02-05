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
import static vavi.sound.yamaha.smaf.util.TextUtil.zeroPadSliceToString;
import static vavi.sound.yamaha.smaf.voice.VM35Voice.normalizeString;
import static vavi.sound.yamaha.smaf.voice.VM35Voice.normalizeint;


public class VM35VoicePC {

    //`json:"is_vm5"`
    public VM35FMVoiceVersion version;
    //`json:"name"`
    public String name;
    // = 0x24
    //`json:"-"`
    public int flag;
    //`json:"bank_msb"`
    public int bankMSB;
    //`json:"bank_lsb"`
    public int bankLSB;
    //`json:"pc"`
    public int pc;
    //`json:"drum_note"`
    public Note drumNote;
    //`json:"-"`
    int enigma1;
    //`json:"voice_type"`
    public VoiceType voiceType;
    //`json:"voice"`
    public VM35Voice voice;

    static class VM3VoicePCHeaderRawData {

        short enigma1;
        byte flag;
        byte bankMSB;
        byte bankLSB;
        byte pc;
        byte drumNote;
        // bit0: 0=Type=FM 1=Type=PCM
        byte voiceType;
        byte[] name = new byte[16];
    }

    static class VM5VoicePCHeaderRawData {

        short enigma1;
        byte[] name = new byte[16];
        byte flag;
        byte bankMSB;
        byte bankLSB;
        byte pc;
        byte drumNote;
        // bit0: 0=Type=FM 1=Type=PCM
        byte voiceType;

        void read(DataInputStream dis) throws IOException {
            enigma1 = dis.readShort();
            dis.readFully(name);
            flag = dis.readByte();
            bankMSB = dis.readByte();
            bankLSB = dis.readByte();
            pc = dis.readByte();
            drumNote = dis.readByte();
            voiceType = dis.readByte();
        }
    }

    void read(DataInputStream rdr, int[] rest) throws IOException {
        switch (this.version) {
            case VM35FMVoiceVersion_VM5 -> {
                VM5VoicePCHeaderRawData data = new VM5VoicePCHeaderRawData();
                data.read(rdr);
                rest[0] -= 2 + 16 + 1 + 1 + 1 + 1 + 1 + 1 /* sizeof(data) */;
                this.name = zeroPadSliceToString(data.name);
                this.flag = data.flag & 0xff;
                this.bankMSB = data.bankMSB & 0xff;
                this.bankLSB = data.bankLSB & 0xff;
                this.pc = data.pc & 0xff;
                this.drumNote = Note.values()[data.drumNote];
                this.enigma1 = data.enigma1 & 0xff;
                this.voiceType = VoiceType.values()[data.voiceType];
            }
            case VM35FMVoiceVersion_VM3Lib -> {
                VM3VoicePCHeaderRawData data = new VM3VoicePCHeaderRawData();
                rest[0] -= 2 + 1 + 1 + 1 + 1 + 1 + 1 + 16 /* sizeof(data) */;
                this.name = zeroPadSliceToString(data.name);
                this.flag = data.flag & 0xff;
                this.bankMSB = data.bankMSB & 0xff;
                this.bankLSB = data.bankLSB & 0xff;
                this.pc = data.pc & 0xff;
                this.drumNote = Note.values()[data.drumNote];
                this.enigma1 = data.enigma1 & 0xff;
                this.voiceType = VoiceType.values()[data.voiceType];
            }
        }
        switch (this.voiceType) {
            case VoiceType_FM:
                this.voice = new VM35FMVoice();
                this.voice.read(rdr, rest);
                this.voice.readUnusedRest(rdr, rest);
            case VoiceType_PCM:
                this.voice = new VM35PCMVoice();
                this.voice.read(rdr, rest);
                //case enums.VoiceType_AL:
            default:
                throw new IllegalArgumentException("contains unsupported type of voice: %s".formatted(this.voiceType));
        }
    }

    public boolean isForDrum() {
        return this.drumNote != null && this.drumNote.ordinal() != 0;
    }

    @Override
    public String toString() {
        // flag: 0x%02X".formatted(v.flag)
        // enigma1: 0x%04X".formatted(v.enigma1)
        // Enigma2: 0x%08X".formatted(v.Enigma2)
        var s = "bank %d-%d @%d %s".formatted(this.bankMSB, this.bankLSB, this.pc, this.voiceType);
        if (this.isForDrum()) {
            s += " drumNote=%s\n".formatted(this.drumNote);
        }
        if (!this.name.isEmpty()) {
            s += ": [%s]".formatted(this.name);
        }
        s += "\n";
        return s + indent(this.voice.toString(), "\t");
    }

    // Normalize removes outliers from the timbre data and normalizes it.
    // Returns true if the tone was normal to begin with.
    boolean normalize() {
        var ok = new boolean[] {true};

        if (VM35FMVoiceVersion.values().length < this.version.ordinal()) {
            this.version = VM35FMVoiceVersion.VM35FMVoiceVersion_VM5;
            ok[0] = false;
        }
        this.name = normalizeString(ok, this.name, "(undefined)");
        this.bankMSB = normalizeint(ok, this.bankMSB, 0, 127);
        this.bankLSB = normalizeint(ok, this.bankLSB, 0, 127);
        this.pc = normalizeint(ok, this.pc, 0, 127);
        this.drumNote = Note.values()[normalizeint(ok, this.drumNote.ordinal(), 0, 127)];
        if (VoiceType.values().length < this.voiceType.ordinal()) {
            this.voiceType = VoiceType.VoiceType_FM;
            ok[0] = false;
        }
        switch (this.voiceType) {
            case VoiceType_FM:
                if (this.voice == null) {
                    this.voice = new VM35FMVoice();
                    ok[0] = false;
                }
                if (!((VM35FMVoice) this.voice).normalize()) {
                    ok[0] = false;
                }
                break;
            case VoiceType_PCM:
                if (this.voice == null) {
                    this.voice = new VM35PCMVoice();
                    ok[0] = false;
                }
                if (!((VM35PCMVoice) this.voice).normalize()) {
                    ok[0] = false;
                }
                break;
            case VoiceType_AL:
                this.voiceType = VoiceType.VoiceType_FM;
                this.voice = new VM35FMVoice();
                ok[0] = false;
                break;
        }
        return ok[0];
    }
}