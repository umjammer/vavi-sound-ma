/*
 * https://github.com/but80/fmfm.core
 */

package vavi.sound.yamaha.ma.cmd.module;

import java.io.IOException;
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
import vavi.sound.yamaha.smaf.pb.smaf.pb.VM5VoiceLib;

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

    // FMFMLoadLibrary は、ライブラリをロードします。
    //export FMFMLoadLibrary
    int FMFMLoadLibrary(String voicePath) throws IOException {
        var voicePathGo = Path.of(voicePath);
        try (var s = Files.list(voicePathGo)) {
            AtomicInteger i = new AtomicInteger();
			s.forEach(p -> {
				if (!Files.isDirectory(p) && p.getFileName().toString().endsWith(".vm5.pb")) {
					lib.LoadFile(voicePathGo.resolve(p.getFileName()).toString());
				}
			});
        }
        return 1;
    }

    // FMFMInit は、音源を初期化します。
    int FMFMInit(double sampleRate) {
        AtomicInteger result = new AtomicInteger();
        Executors.newSingleThreadExecutor().submit(() -> {
            var chip = new Chip((int) sampleRate, -15.0, -1);
            var regs = new Registers(chip);
            var opts = new ControllerOpts() {{
                registers = regs;
                library = Main.this.lib;
            }};
            ctrl = new Controller(opts);
            result.set(1);
        });
        return result.get();
    }

    // FMFMFlushMIDIMessages は、蓄積されたMIDIメッセージを処理します。
    void FMFMFlushMIDIMessages(long until) {
        ctrl.FlushMIDIMessages((int) (until));
    }

    // FMFMNoteOn は、MIDIノートオン受信時の音源の振る舞いを再現します。
    void FMFMNoteOn(long timestamp, long ch, long note, long velocity) {
        ctrl.PushMIDIMessage(MIDINoteOn, (int) timestamp, (int) ch, (int) note, (int) velocity);
    }

    // FMFMNoteOff は、MIDIノートオフ受信時の音源の振る舞いを再現します。
    void FMFMNoteOff(long timestamp, long ch, long note) {
        ctrl.PushMIDIMessage(MIDINoteOff, (int) timestamp, (int) ch, (int) note, 0);
    }

    // FMFMControlChange は、MIDIコントロールチェンジ受信時の音源の振る舞いを再現します。
    void FMFMControlChange(long timestamp, long ch, long cc, long value) {
        ctrl.PushMIDIMessage(MIDIControlChange, (int) timestamp, (int) ch, (int) cc, (int) value);
    }

    // FMFMProgramChange は、MIDIプログラムチェンジ受信時の音源の振る舞いを再現します。
    void FMFMProgramChange(long timestamp, long ch, long value) {
        ctrl.PushMIDIMessage(MIDIProgramChange, (int) timestamp, (int) ch, (int) value, 0);
    }

    // FMFMPitchBend は、MIDIピッチベンド受信時の音源の振る舞いを再現します。
    void FMFMPitchBend(long timestamp, long ch, long l, long h) {
        ctrl.PushMIDIMessage(MIDIPitchBend, (int) timestamp, (int) ch, (int) l, (int) h);
    }

    // FMFMListBankMSB は、登録されている音色の選択可能なMSBの一覧を返します。
    public long FMFMListBankMSB(long[] out) {
        return writeInts(out, collectInts(() -> {
            var ch = new ArrayList<Integer>();
            for (var p : lib.Programs) {
                ch.add(p.BankMsb);
            }
            return ch;
        }));
    }

    // FMFMListBankLSB は、登録されている音色の選択可能なLSBの一覧を返します。
    public long FMFMListBankLSB(long[] out, long msb) {
        return writeInts(out, collectInts(() -> {
            var ch = new ArrayList<Integer>();
            for (var p : lib.Programs) {
                if (p.BankMsb == msb) {
                    ch.add(p.BankLsb);
                }
            }
            return ch;
        }));
    }

    // FMFMListPC は、登録されている音色の選択可能なプログラムチェンジの一覧を返します。
    long FMFMListPC(long[] out, long msb, long lsb) {
        return writeInts(out, collectInts(() -> {
            var ch = new ArrayList<Integer>();
            for (var p : lib.Programs) {
                if (p.BankMsb == (int) (msb) && p.BankLsb == (int) (lsb)) {
                    ch.add(p.Pc);
                }
            }
            return ch;
        }));
    }

    // FMFMListDrumNote は、登録されている音色の選択可能なドラムノートの一覧を返します。
    long FMFMListDrumNote(long[] out, long msb, long lsb, long pc) {
        return writeInts(out, collectInts(() -> {
            var ch = new ArrayList<Integer>();
            for (var p : lib.Programs) {
                if (p.BankMsb == (int) (msb) && p.BankLsb == (int) (lsb) && p.Pc == (int) (pc)) {
                    ch.add(p.DrumNote);
                }
            }
            return ch;
        }));
    }

    // FMFMGetVoice は、音色データを Protocol Buffers 形式にエンコードして返します。
    long FMFMGetVoice(byte[] out, long msb, long lsb, long pc, long drumNote) {
        // TODO: implement
        for (var p : lib.Programs) {
            var ch = new ArrayList<Integer>();
            if (p.BankMsb == (int) (msb) && p.BankLsb == (int) (lsb) && p.Pc == (int) (pc)) {
                var data = VM35VoicePC.newBuilder(p).build();
                return writeBytes(out, data);
            }
        }
        return 0;
    }

    // FMFMNext は、次のサンプルを生成・取得します。
    double[] FMFMNext() {
        return chip.next();
    }
}
