/*
 * https://github.com/but80/smaf825
 */

package vavi.sound.yamaha.smaf.voice;

import java.io.DataInputStream;
import java.io.IOException;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static java.lang.System.getLogger;


class VMAVoiceLib {

    private static final Logger logger = getLogger(VMAVoiceLib.class.getName());

    //`json:"programs"`
    List<VMAVoicePC> Programs = new ArrayList<>();

    void Read(DataInputStream rdr, int[] rest) throws IOException {
        for (var pc = 0; pc < 128 && 0 < rest[0]; pc++) {
            var voice = new VMAVoicePC();
            var name = new byte[16];
            rdr.readFully(name);
            rest[0] -= name.length;
            voice.Name = util.ZeroPadSliceToString(name);
            this.Programs.add(voice);
        }
        for (var pc = 0; pc < 128 && 0 < rest[0]; pc++) {
            var voice = this.Programs.get(pc);
            voice.Read(rdr, rest);
            this.Programs.add(voice);
        }
    }

    @Override
    public String toString() {
        return String.join("\n", this.Programs.stream().map(VMAVoicePC::toString).toArray(String[]::new));
    }

    VMAVoiceLib(String file) throws IOException {
        try (var fh = new DataInputStream(Files.newInputStream(Path.of(file)))) {

            chunkHeader hdr = new chunkHeader();
            hdr.read(fh);
            if (hdr.Signature != ('F' << 24 | 'M' << 16 | ' ' << 8 | ' ')) {
                throw new IllegalArgumentException("Header signature must be \"FM  \"");
            }

            var total = hdr.Size + 8 /* sizeof(hdr) */;
            int[] rest = new int[] {hdr.Size};
            try {
                this.Read(fh, rest);
            } catch (IOException e) {
                logger.log(Level.WARNING, "at 0x%X bytes".formatted(total - rest[0]));
            }
        }
    }
}