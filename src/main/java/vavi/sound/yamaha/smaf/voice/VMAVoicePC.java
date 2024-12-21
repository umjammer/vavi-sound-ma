/*
 * https://github.com/but80/smaf825
 */

package vavi.sound.yamaha.smaf.voice;

import java.io.DataInputStream;
import java.io.IOException;

import vavi.sound.yamaha.smaf.enums.Enums.VoiceType;
import vavi.sound.yamaha.smaf.enums.Note;

import static vavi.sound.yamaha.smaf.util.TextUtil.indent;


public class VMAVoicePC {

    //`json:"name"`
    String name;
    //`json:"bank"`
    int bank;
    //`json:"pc"`
    int pc;
    //`json:"voice"`
    VMAFMVoice voice;

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
            drumNote = Note.values()[0];
            enigma1 = 0;
            voiceType = VoiceType.VoiceType_FM;
            voice = VMAVoicePC.this.voice.ToVM35();
        }};
    }
}