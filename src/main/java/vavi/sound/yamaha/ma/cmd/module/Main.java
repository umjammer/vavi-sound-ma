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
import static vavi.sound.yamaha.ma.fmfm.Controller.MIDIMessage.MIDIControlChange;
import static vavi.sound.yamaha.ma.fmfm.Controller.MIDIMessage.MIDINoteOff;
import static vavi.sound.yamaha.ma.fmfm.Controller.MIDIMessage.MIDINoteOn;
import static vavi.sound.yamaha.ma.fmfm.Controller.MIDIMessage.MIDIPitchBend;
import static vavi.sound.yamaha.ma.fmfm.Controller.MIDIMessage.MIDIProgramChange;


public class Main {

    VM5VoiceLib lib;
    Chip chip;
    Controller ctrl;

    // FMFMLoadLibrary loads a library.
    //export FMFMLoadLibrary
    int FMFMLoadLibrary(String voicePath) throws IOException {
        var voicePathGo = Path.of(voicePath);
        try (var s = Files.list(voicePathGo)) {
            AtomicInteger i = new AtomicInteger();
			s.forEach(p -> {
				if (!Files.isDirectory(p) && p.getFileName().toString().endsWith(".vm5.pb")) {
                    try {
                        lib.LoadFile(voicePathGo.resolve(p.getFileName()).toString());
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                }
			});
        }
        return 1;
    }

    // FMFMInit initializes the sound source.
    int FMFMInit(double sampleRate) {
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

    // FMFMFlushMIDIMessages processes accumulated MIDI messages.
    void FMFMFlushMIDIMessages(long until) {
        ctrl.FlushMIDIMessages((int) (until));
    }

    // FMFMNoteOn reproduces the behavior of a sound source when receiving a MIDI note-on.
    void FMFMNoteOn(long timestamp, long ch, long note, long velocity) {
        ctrl.PushMIDIMessage(MIDINoteOn, (int) timestamp, (int) ch, (int) note, (int) velocity);
    }

    // FMFMNoteOff reproduces the behavior of a sound source when receiving a MIDI note-off signal.
    void FMFMNoteOff(long timestamp, long ch, long note) {
        ctrl.PushMIDIMessage(MIDINoteOff, (int) timestamp, (int) ch, (int) note, 0);
    }

    // FMFMControlChange reproduces the behavior of a sound source when receiving a MIDI control change.
    void FMFMControlChange(long timestamp, long ch, long cc, long value) {
        ctrl.PushMIDIMessage(MIDIControlChange, (int) timestamp, (int) ch, (int) cc, (int) value);
    }

    // FMFMProgramChange reproduces the behavior of a sound source when receiving a MIDI Program Change.
    void FMFMProgramChange(long timestamp, long ch, long value) {
        ctrl.PushMIDIMessage(MIDIProgramChange, (int) timestamp, (int) ch, (int) value, 0);
    }

    // FMFMPitchBend reproduces the behavior of a sound source when receiving MIDI pitch bend.
    void FMFMPitchBend(long timestamp, long ch, long l, long h) {
        ctrl.PushMIDIMessage(MIDIPitchBend, (int) timestamp, (int) ch, (int) l, (int) h);
    }

    // FMFMListBankMSB returns a list of selectable MSBs for the registered tones.
    public long FMFMListBankMSB(long[] out) {
        return writeInts(out, collectInts(() -> {
            var ch = new ArrayList<Integer>();
            for (var p : lib.programs) {
                ch.add(p.bankMSB);
            }
            return ch;
        }));
    }

    // FMFMListBankLSB returns a list of selectable LSBs for the registered tones.
    public long FMFMListBankLSB(long[] out, long msb) {
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

    // FMFMListPC returns a list of selectable program changes for the registered tones.
    long FMFMListPC(long[] out, long msb, long lsb) {
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

    // FMFMListDrumNote returns a list of selectable drum notes for the registered sounds.
    long FMFMListDrumNote(long[] out, long msb, long lsb, long pc) {
        return writeInts(out, collectInts(() -> {
            var ch = new ArrayList<Integer>();
            for (var p : lib.programs) {
                if (p.bankMSB == msb && p.bankLSB == lsb && p.pc == pc) {
                    ch.add(p.drumNote.ordinal());
                }
            }
            return ch;
        }));
    }

    // FMFMGetVoice returns voice data encoded in Protocol Buffers format.
    long FMFMGetVoice(byte[] out, long msb, long lsb, long pc, long drumNote) {
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

    // FMFMNext generates and retrieves the next sample.
    double[] FMFMNext() {
        return chip.next();
    }
}
