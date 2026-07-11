/*
 * https://github.com/but80/smaf825
 */

package vavi.sound.yamaha.smaf.voice;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static java.lang.System.getLogger;
import static vavi.sound.yamaha.smaf.util.TextUtil.zeroPadSliceToString;


/**
 * Represents .vma voice lib file.
 */
public class VMAVoiceLib implements VoiceLib {

    private static final Logger logger = getLogger(VMAVoiceLib.class.getName());

    //`json:"programs"`
    public List<VMAVoicePC> programs = new ArrayList<>();

    public void read(DataInputStream rdr, int[] rest) throws IOException {
        for (var pc = 0; pc < 128 && 0 < rest[0]; pc++) {
            var voice = new VMAVoicePC();
            var name = new byte[16];
            rdr.readFully(name);
            rest[0] -= name.length;
            voice.name = zeroPadSliceToString(name);
            this.programs.add(voice);
        }
        for (var pc = 0; pc < 128 && 0 < rest[0]; pc++) {
            var voice = this.programs.get(pc);
            voice.read(rdr, rest);
            this.programs.add(voice);
        }
    }

    @Override
    public String toString() {
        return String.join("\n", this.programs.stream().map(VMAVoicePC::toString).toArray(String[]::new));
    }

    public VMAVoiceLib(InputStream is) throws IOException {
        try (var fh = new DataInputStream(is)) {

            ChunkHeader hdr = new ChunkHeader();
            hdr.read(fh);
            if (hdr.signature != ('F' << 24 | 'M' << 16 | ' ' << 8 | ' ')) {
                throw new IllegalArgumentException("Header signature must be \"FM  \"");
            }

            var total = hdr.size + 8 /* sizeof(hdr) */;
            int[] rest = new int[] {hdr.size};
            try {
                this.read(fh, rest);
            } catch (IOException e) {
                logger.log(Level.WARNING, "at 0x%X bytes".formatted(total - rest[0]));
            }
        }
    }
}