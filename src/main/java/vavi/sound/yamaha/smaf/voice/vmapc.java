/*
 * https://github.com/but80/smaf825
 */

package vavi.sound.yamaha.smaf.voice;

import java.io.DataInputStream;
import java.io.IOException;

import vavi.sound.yamaha.smaf.voice.VMAFMOperator.VMAFMVoice;


class VMAVoicePC {

    //`json:"name"`
    String Name;
    //`json:"bank"`
    int Bank;
    //`json:"pc"`
    int PC;
    //`json:"voice"`
    VMAFMVoice Voice;

    static class vmaVoicePCHeaderRawData {

        //    | 7 | 6 | 5 | 4 | 3 | 2 | 1 | 0 |
        // +0 |              00?              |
        // +1 |             Bank              |
        // +2 |              PC               |
        byte Enigma;
        byte Bank;
        byte PC;

        void read(DataInputStream dis) throws IOException {
            Enigma = dis.readByte();
            Bank = dis.readByte();
            PC = dis.readByte();
        }
    }

    void Read(DataInputStream rdr, int[] rest) throws IOException {
        var data = new vmaVoicePCHeaderRawData();
        data.read(rdr);
        rest[0] -= 3 /* sizeof(data) */;
        this.Bank = data.Bank;
        this.PC = data.PC;
        this.Voice = new VMAFMVoice();
        this.Voice.Read(rdr, rest);
        this.Voice.ReadUnusedRest(rdr, rest);
        //
        byte enigma2 = rdr.readByte();
        rest[0]--;
    }

    public String toString() {
        var s = "Bank %d @%d".formatted(this.Bank, this.PC);
        if (!this.Name.isEmpty()) {
            s += ": [%s]".formatted(this.Name);
        }
        s += "\n";
        return s + util.Indent(this.Voice.toString(), "\t");
    }

    VM35VoicePC ToVM35() {
        return new VM35VoicePC() {{
            Name = this.Name;
            Flag = 0x24;
            BankMSB = 0;
            BankLSB = this.Bank;
            PC = this.PC;
            DrumNote = 0;
            Enigma1 = 0;
            VoiceType = VoiceType_FM;
            Voice = this.Voice.ToVM35();
        }};
    }
}