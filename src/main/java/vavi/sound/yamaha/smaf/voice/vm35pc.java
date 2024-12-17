/*
 * https://github.com/but80/smaf825
 */

package vavi.sound.yamaha.smaf.voice;

import java.io.DataInputStream;
import java.io.IOException;

import vavi.sound.yamaha.smaf.enums.Note;


interface VM35Voice {

    //fmt.Stringer
    void Read(DataInputStream rdr, int[] rest) throws IOException;

    void ReadUnusedRest(DataInputStream rdr, int[] rest);
}

public class VM35VoicePC {

    //`json:"is_vm5"`
    VM35FMVoiceVersion Version;
    //`json:"name"`
    String Name;
    //`json:"-"` // = 0x24
    int Flag;
    //`json:"bank_msb"`
    int BankMSB;
    //`json:"bank_lsb"`
    int BankLSB;
    //`json:"pc"`
    int PC;
    //`json:"drum_note"`
    Note DrumNote;
    //`json:"-"`
    int Enigma1;
    //`json:"voice_type"`
    VoiceType VoiceType;
    //`json:"voice"`
    VM35Voice Voice;

    static class vm3VoicePCHeaderRawData {

        short Enigma1;
        byte Flag;
        byte BankMSB;
        byte BankLSB;
        byte PC;
        byte DrumNote;
        byte VoiceType;  // bit0: 0=Type=FM 1=Type=PCM
        byte[] Name = new byte[16];
    }

    static class vm5VoicePCHeaderRawData {

        short Enigma1;
        byte[] Name = new byte[16];
        byte Flag;
        byte BankMSB;
        byte BankLSB;
        byte PC;
        byte DrumNote;
        byte VoiceType;// bit0: 0=Type=FM 1=Type=PCM

        void read(DataInputStream dis) throws IOException {
            Enigma1 = dis.readShort();
            dis.readFully(Name);
            Flag = dis.readByte();
            BankMSB = dis.readByte();
            BankLSB = dis.readByte();
            PC = dis.readByte();
            DrumNote = dis.readByte();
            VoiceType = dis.readByte();
        }
    }

    void Read(DataInputStream rdr, int[] rest) throws IOException {
        switch (this.Version) {
            case VM35FMVoiceVersion_VM5 -> {
                vm5VoicePCHeaderRawData data = new vm5VoicePCHeaderRawData();
                data.read(rdr);
                rest[0] -= 2 + 16 + 1 + 1 + 1 + 1 + 1 + 1 /* sizeof(data) */;
                this.Name = util.ZeroPadSliceToString(data.Name);
                this.Flag = data.Flag & 0xff;
                this.BankMSB = data.BankMSB & 0xff;
                this.BankLSB = data.BankLSB & 0xff;
                this.PC = data.PC & 0xff;
                this.DrumNote = enums.Note(data.DrumNote);
                this.Enigma1 = data.Enigma1 & 0xff;
                this.VoiceType = enums.VoiceType(data.VoiceType);
            }
            case VM35FMVoiceVersion_VM3Lib -> {
                vm3VoicePCHeaderRawData data = new vm3VoicePCHeaderRawData();
                rest[0] -= 2 + 1 + 1 + 1 + 1 + 1 + 1 + 16 /* sizeof(data) */;
                this.Name = util.ZeroPadSliceToString(data.Name);
                this.Flag = data.Flag & 0xff;
                this.BankMSB = data.BankMSB & 0xff;
                this.BankLSB = data.BankLSB & 0xff;
                this.PC = data.PC & 0xff;
                this.DrumNote = enums.Note(data.DrumNote);
                this.Enigma1 = data.Enigma1 & 0xff;
                this.VoiceType = enums.VoiceType(data.VoiceType);
            }
        }
        switch (this.VoiceType) {
            case VoiceType_FM:
                this.Voice = new VM35FMVoice();
                this.Voice.Read(rdr, rest);
                this.Voice.ReadUnusedRest(rdr, rest);
            case VoiceType_PCM:
                this.Voice = new VM35PCMVoice();
                this.Voice.Read(rdr, rest);
                //case enums.VoiceType_AL:
            default:
                throw new IllegalArgumentException("contains unsupported type of voice: %s".formatted(this.VoiceType));
        }
    }

    boolean IsForDrum() {
        return this.DrumNote != 0;
    }

    @Override
    public String toString() {
        // Flag: 0x%02X".formatted(v.Flag)
        // Enigma1: 0x%04X".formatted(v.Enigma1)
        // Enigma2: 0x%08X".formatted(v.Enigma2)
        var s = "Bank %d-%d @%d %s".formatted(this.BankMSB, this.BankLSB, this.PC, this.VoiceType);
        if (this.IsForDrum()) {
            s += " DrumNote=%s\n".formatted(this.DrumNote);
        }
        if (!this.Name.equals("")) {
            s += ": [%s]".formatted(this.Name);
        }
        s += "\n";
        return s + util.Indent(this.Voice, "\t");
    }
}