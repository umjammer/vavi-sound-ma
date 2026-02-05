/*
 * https://github.com/but80/smaf825
 */

package vavi.sound.yamaha.smaf.voice;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import vavi.sound.yamaha.smaf.enums.Enums.Algorithm;
import vavi.sound.yamaha.smaf.enums.Enums.BasicOctave;
import vavi.sound.yamaha.smaf.enums.Enums.Panpot;
import vavi.sound.yamaha.smaf.enums.Enums.VoiceType;
import vavi.sound.yamaha.smaf.voice.VM35Voice.VM35FMVoiceVersion;
import vavi.util.serdes.Serdes;

import static java.lang.System.getLogger;
import static vavi.sound.yamaha.smaf.voice.VM35Voice.VM35FMVoiceVersion.VM35FMVoiceVersion_VM5;


/**
 * Represents .vm5 voice lib file.
 */
public class VM5VoiceLib implements VoiceLib {

    private static final Logger logger = getLogger(VM5VoiceLib.class.getName());

    // `json:"programs"`
    public List<VM35VoicePC> programs = new ArrayList<>();

    void read(DataInputStream rdr , int[] rest) throws IOException {
        for (var pc = 0; pc < 128 && 0 < rest[0]; pc++) {
            var voice = new VM35VoicePC() {{
                version = VM35FMVoiceVersion_VM5;
            }};
            voice.read(rdr, rest);
            this.programs.add(voice);
        }
    }

     @Override public String toString() {
         return String.join("\n", this.programs.stream().map(VM35VoicePC::toString).toArray(String[]::new));
    }

    public VM5VoiceLib() {}

    public VM5VoiceLib(String file) throws IOException {
        try (var fh = new DataInputStream(Files.newInputStream(Path.of(file)))) {

            ChunkHeader hdr = new ChunkHeader();
            hdr.read(fh);
            if (hdr.signature != ('V' << 24 | 'O' << 16 | 'M' << 8 | '5')) {
                throw new IllegalArgumentException("Header signature must be \"VOM5\"");
            }

            var total = hdr.size + 8 /* sizeof(hdr) */;
            var rest = new int[] {hdr.size};
            try {
                this.read(fh, rest);
            } catch (IOException e) {
                logger.log(Level.WARNING, "at 0x%X bytes".formatted(total - rest[0]));
            }
        }
    }

    // LoadFile loads a sound library from a file.
    public byte[] LoadFile(String file) throws IOException {
        var b = Files.readAllBytes(Path.of("voice").resolve(file));
        this.LoadBytes(b);
        return b;
    }

    // LoadBytes loads a sound library from a byte sequence.
    void LoadBytes(byte[] b) throws IOException {
        VM5VoiceLib loaded = new VM5VoiceLib();
        Serdes.Util.deserialize(new ByteArrayInputStream(b), loaded);
        this.programs.addAll(loaded.programs);
        var x = this.Normalize();
    }

    // Normalize removes outliers from the timbre data and normalizes it.
    // Returns a list of the tones in which anomalies were detected.
    public VM35VoicePC[] Normalize() {
        if (this.programs == null) {
            this.programs = new ArrayList<>();
        }
        var result = new ArrayList<VM35VoicePC>();
        for (var i = 0; i < this.programs.size(); i++) {
            var pc = this.programs.get(i);
            if (pc == null) {
                pc = new VM35VoicePC();
                this.programs.set(i, pc);
            }
            if (!pc.normalize()) {
                result.add(pc);
            }
        }
        return result.toArray(VM35VoicePC[]::new);
    }

    // Get retrieves tone data.
    public VM35VoicePC get(int msb, int lsb, int pc, int note) {
logger.log(Level.INFO, "programs: " + this.programs.size());
        for (var p : this.programs) {
            if (!(p.pc == pc && p.bankLSB == lsb && p.bankMSB == msb)) {
                continue;
            }
            if (p.drumNote != null && p.drumNote.ordinal() != note) {
                continue;
            }
            return p;
        }
        return null;
    }

    public static final VM35VoicePC defaultPC;

    static {
        defaultPC = new VM35VoicePC();
        defaultPC.version = VM35FMVoiceVersion.VM35FMVoiceVersion_VM5;
        defaultPC.name = "default";
        defaultPC.voiceType = VoiceType.VoiceType_FM;
        defaultPC.voice = new VM35FMVoice();
        ((VM35FMVoice) defaultPC.voice).panpot = Panpot.Panpot15;
        ((VM35FMVoice) defaultPC.voice).bo = BasicOctave.BasicOctave_Normal;
        ((VM35FMVoice) defaultPC.voice).alg = Algorithm.A1;
        ((VM35FMVoice) defaultPC.voice).operators = new ArrayList<>();
    }
}