/*
 * https://github.com/but80/fmfm.core
 */

package vavi.sound.yamaha.ma.sim;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Executors;

import com.code_intelligence.jazzer.api.FuzzedDataProvider;
import com.code_intelligence.jazzer.junit.FuzzTest;
import vavi.sound.yamaha.ma.fmfm.Controller;
import vavi.sound.yamaha.ma.fmfm.Controller.ControllerOpts;
import vavi.sound.yamaha.smaf.pb.smaf.Smaf.VM35VoicePC;
import vavi.sound.yamaha.smaf.pb.smaf.pb.VM5VoiceLib;

import static vavi.sound.yamaha.ma.fmfm.Controller.MIDIMessage.MIDIControlChange;
import static vavi.sound.yamaha.ma.fmfm.Controller.MIDIMessage.MIDINoteOff;
import static vavi.sound.yamaha.ma.fmfm.Controller.MIDIMessage.MIDINoteOn;
import static vavi.sound.yamaha.ma.fmfm.Controller.MIDIMessage.MIDIProgramChange;


class ChipTest {

	@FuzzTest
	void testNewChip(FuzzedDataProvider data) throws IOException {
		var sampleRate = 44100.0;

		for (var i = 0; i < 1000; i++) {
			var pc = VM35VoicePC.getDefaultInstance();
			f.Fuzz(pc);
			pc.BankMsb = 0;
			pc.BankLsb = 0;
			pc.Pc = 0;
			pc.DrumNote = 0;
			pc.VoiceType = VoiceType_FM;
			pc.FmVoice = new VM35FMVoice();
			f.Fuzz(pc.FmVoice);

			var lib = new VM5VoiceLib() {{
				programs = new ArrayList<>();
			}};
			lib.Normalize();

			Executors.newSingleThreadExecutor().submit(() -> {
				var chip = new Chip((int) sampleRate, -15.0, -1);
				var regs = new Registers(chip);
				var opts = new ControllerOpts() {{
					registers = regs;
					library = lib;
				}};
				var seq = new Controller(opts);
				chip.next();

				seq.PushMIDIMessage(MIDIControlChange, 1, 0, 0, 0);
				seq.PushMIDIMessage(MIDIControlChange, 1, 0, 32, 0);
				seq.PushMIDIMessage(MIDIProgramChange, 1, 0, 0, 0);
				seq.FlushMIDIMessages(2);
				chip.next();

				seq.PushMIDIMessage(MIDINoteOn, 3, 0, 60, 127);
				seq.FlushMIDIMessages(4);
				for (var j = 0; j < 100; j++) {
					chip.next();
				}

				seq.PushMIDIMessage(MIDINoteOff, 5, 0, 60, 0);
				seq.FlushMIDIMessages(6);
				for (var j = 0; j < 100; j++) {
					chip.next();
				}
			});
		}
	}
}
