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
import static vavi.sound.yamaha.smaf.voice.VM35Voice.VM35FMVoiceVersion.VM35FMVoiceVersion_VM3Lib;


/**
 * Represents .vm3 voice lib file.
 */
public class VM3VoiceLib implements VoiceLib {

    private static final Logger logger = getLogger(VM3VoiceLib.class.getName());

    List<VM35VoicePC> programs = new ArrayList<>(); // `json:"programs"`

    void read(DataInputStream rdr, int[] rest) throws IOException {
        for (var pc = 0; pc < 128 && 0 < rest[0]; pc++) {
            var voice = new VM35VoicePC() {{
                version = VM35FMVoiceVersion_VM3Lib;
            }};
            voice.read(rdr, rest);
            this.programs.add(voice);
        }
    }

    @Override
    public String toString() {
        return String.join("\n", this.programs.stream().map(VM35VoicePC::toString).toArray(String[]::new));
    }

    VM3VoiceLib(String file) throws IOException {
        try (DataInputStream fh = new DataInputStream(Files.newInputStream(Path.of(file)))) {
            ChunkHeader hdr = new ChunkHeader();
            hdr.read(fh);
            if (hdr.signature != ('F' << 24 | 'M' << 16 | 'M' << 8 | '3')) {
                throw new IllegalArgumentException("Header signature must be \"FMM3\"");
            }

            var total = hdr.size + 8 /* sizeof(hdr) */;
            int[] rest = new int[] {hdr.size};
            try {
                this.read(fh, rest);
            } catch (IOException e) {
                logger.log(Level.ERROR, "at 0x%X bytes".formatted(total - rest[0]), e);
                throw e;
            }
        }
    }
}