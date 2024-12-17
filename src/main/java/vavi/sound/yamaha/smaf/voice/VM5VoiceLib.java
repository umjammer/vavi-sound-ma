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
import static vavi.sound.yamaha.smaf.voice.VM35FMVoiceVersion.VM35FMVoiceVersion_VM5;


public class VM5VoiceLib {

    private static final Logger logger = getLogger(VM5VoiceLib.class.getName());

    // `json:"programs"`
    List<VM35VoicePC> Programs = new ArrayList<>();

    void Read(DataInputStream rdr , int[] rest) throws IOException {
        for (var pc = 0; pc < 128 && 0 < rest[0]; pc++) {
            var voice = new VM35VoicePC() {{
                Version = VM35FMVoiceVersion_VM5;
            }};
            voice.Read(rdr, rest);
            this.Programs.add(voice);
        }
    }

     @Override public String toString() {
         return String.join("\n", this.Programs.stream().map(VM35VoicePC::toString).toArray(String[]::new));
    }

    VM5VoiceLib(String file) throws IOException {
        try (var fh = new DataInputStream(Files.newInputStream(Path.of(file)))) {

            chunkHeader hdr = new chunkHeader();
            hdr.read(fh);
            if (hdr.Signature != ('V' << 24 | 'O' << 16 | 'M' << 8 | '5')) {
                throw new IllegalArgumentException("Header signature must be \"VOM5\"");
            }

            var total = hdr.Size + 8 /* sizeof(hdr) */;
            var rest = new int[] {hdr.Size};
            try {
                this.Read(fh, rest);
            } catch (IOException e) {
                logger.log(Level.WARNING, "at 0x%X bytes".formatted(total - rest[0]));
            }
        }
    }
}