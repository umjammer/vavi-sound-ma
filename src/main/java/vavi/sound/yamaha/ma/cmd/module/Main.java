/*
 * https://github.com/but80/fmfm.core
 */

package vavi.sound.yamaha.ma.cmd.module;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import vavi.sound.yamaha.ma.fmfm.Controller;
import vavi.sound.yamaha.ma.fmfm.Controller.ControllerOpts;
import vavi.sound.yamaha.ma.sim.Chip;
import vavi.sound.yamaha.ma.sim.Registers;
import vavi.sound.yamaha.smaf.pb.smaf.Smaf.VM35VoicePC;
import vavi.sound.yamaha.smaf.voice.VM5VoiceLib;

import static vavi.sound.yamaha.ma.cmd.module.Helper.collectInts;
import static vavi.sound.yamaha.ma.cmd.module.Helper.writeBytes;
import static vavi.sound.yamaha.ma.cmd.module.Helper.writeInts;
import static vavi.sound.yamaha.ma.fmfm.Controller.MIDIMessage.ControlChange;
import static vavi.sound.yamaha.ma.fmfm.Controller.MIDIMessage.NoteOff;
import static vavi.sound.yamaha.ma.fmfm.Controller.MIDIMessage.NoteOn;
import static vavi.sound.yamaha.ma.fmfm.Controller.MIDIMessage.PitchBend;
import static vavi.sound.yamaha.ma.fmfm.Controller.MIDIMessage.ProgramChange;


public class Main {

    VM5VoiceLib lib;
    Chip chip;
    Controller ctrl;

    /** Loads a library. */
    int loadLibrary(String voicePath) throws IOException {
        var voicePathGo = Path.of(voicePath);
        try (var s = Files.list(voicePathGo)) {
            AtomicInteger i = new AtomicInteger();
			s.forEach(p -> {
				if (!Files.isDirectory(p) && p.getFileName().toString().endsWith(".vm5.pb")) {
                    try {
                        lib.loadFile(voicePathGo.resolve(p.getFileName()).toString());
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                }
			});
        }
        return 1;
    }

    /** Initializes the sound source. */
    int init(double sampleRate) {
        AtomicInteger result = new AtomicInteger();
        Executors.newSingleThreadExecutor().submit(() -> {
            var chip = new Chip((int) sampleRate, -15.0, -1);
            var regs = new Registers(chip);
            var opts = new ControllerOpts();
            opts.registers = regs;
            opts.library = Main.this.lib;
            ctrl = new Controller(opts);
            result.set(1);
        });
        return result.get();
    }

    /** Processes accumulated MIDI messages. */
    void flushMIDIMessages(long until) {
        ctrl.FlushMIDIMessages((int) (until));
    }

    /** Reproduces the behavior of a sound source when receiving a MIDI note-on. */
    void noteOn(long timestamp, long ch, long note, long velocity) {
        ctrl.pushMIDIMessage(NoteOn, (int) timestamp, (int) ch, (int) note, (int) velocity);
    }

    /** Reproduces the behavior of a sound source when receiving a MIDI note-off signal. */
    void noteOff(long timestamp, long ch, long note) {
        ctrl.pushMIDIMessage(NoteOff, (int) timestamp, (int) ch, (int) note, 0);
    }

    /** Reproduces the behavior of a sound source when receiving a MIDI control change. */
    void controlChange(long timestamp, long ch, long cc, long value) {
        ctrl.pushMIDIMessage(ControlChange, (int) timestamp, (int) ch, (int) cc, (int) value);
    }

    /** Reproduces the behavior of a sound source when receiving a MIDI Program Change. */
    void programChange(long timestamp, long ch, long value) {
        ctrl.pushMIDIMessage(ProgramChange, (int) timestamp, (int) ch, (int) value, 0);
    }

    /** Reproduces the behavior of a sound source when receiving MIDI pitch bend. */
    void pitchBend(long timestamp, long ch, long l, long h) {
        ctrl.pushMIDIMessage(PitchBend, (int) timestamp, (int) ch, (int) l, (int) h);
    }

    /** Returns a list of selectable MSBs for the registered tones. */
    public long listBankMSB(long[] out) {
        return writeInts(out, collectInts(() -> {
            var ch = new ArrayList<Integer>();
            for (var p : lib.programs) {
                ch.add(p.bankMSB);
            }
            return ch;
        }));
    }

    /** Returns a list of selectable LSBs for the registered tones. */
    public long listBankLSB(long[] out, long msb) {
        return writeInts(out, collectInts(() -> {
            var ch = new ArrayList<Integer>();
            for (var p : lib.programs) {
                if (p.bankMSB == msb) {
                    ch.add(p.bankLSB);
                }
            }
            return ch;
        }));
    }

    /** Returns a list of selectable program changes for the registered tones. */
    long listPC(long[] out, long msb, long lsb) {
        return writeInts(out, collectInts(() -> {
            var ch = new ArrayList<Integer>();
            for (var p : lib.programs) {
                if (p.bankMSB == msb && p.bankLSB == lsb) {
                    ch.add(p.pc);
                }
            }
            return ch;
        }));
    }

    /** Returns a list of selectable drum notes for the registered sounds. */
    long listDrumNote(long[] out, long msb, long lsb, long pc) {
        return writeInts(out, collectInts(() -> {
            var ch = new ArrayList<Integer>();
            for (var p : lib.programs) {
                if (p.bankMSB == msb && p.bankLSB == lsb && p.pc == pc) {
                    ch.add(p.drumNote.note);
                }
            }
            return ch;
        }));
    }

    /** Returns voice data encoded in Protocol Buffers format. */
    long getVoice(byte[] out, long msb, long lsb, long pc, long drumNote) {
        // TODO: implement
        for (var p : lib.programs) {
            var ch = new ArrayList<Integer>();
            if (p.bankMSB == msb && p.bankLSB == lsb && p.pc == pc) {
                var data = VM35VoicePC.getDefaultInstance().toByteArray();
                return writeBytes(out, data);
            }
        }
        return 0;
    }

    /** Generates and retrieves the next sample. */
    double[] next() {
        return chip.next();
    }
}
