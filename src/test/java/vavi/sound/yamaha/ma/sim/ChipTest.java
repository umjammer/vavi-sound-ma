/*
 * https://github.com/but80/fmfm.core
 */

package vavi.sound.yamaha.ma.sim;

import org.junit.jupiter.api.Test;
import vavi.sound.yamaha.ma.fmfm.Controller;
import vavi.sound.yamaha.ma.fmfm.Controller.ControllerOpts;
import vavi.sound.yamaha.smaf.voice.VM35VoicePC;

import static vavi.sound.yamaha.ma.fmfm.Controller.MIDIMessage.MIDIControlChange;
import static vavi.sound.yamaha.ma.fmfm.Controller.MIDIMessage.MIDINoteOff;
import static vavi.sound.yamaha.ma.fmfm.Controller.MIDIMessage.MIDINoteOn;
import static vavi.sound.yamaha.ma.fmfm.Controller.MIDIMessage.MIDIProgramChange;


class ChipTest {

	@Test
	void testNewChip() {
		var sampleRate = 44100.0;
		var f = fuzz.New();

		for (var i = 0; i < 1000; i++) {
			var pc = new VM35VoicePC();
			f.Fuzz(pc);
			pc.BankMsb = 0;
			pc.BankLsb = 0;
			pc.Pc = 0;
			pc.DrumNote = 0;
			pc.VoiceType = smaf.VoiceType_FM;
			pc.FmVoice = smaf.VM35FMVoice;
			f.Fuzz(pc.FmVoice);

			lib = new VM5VoiceLib() {{
				Programs = new VM35VoicePC[];
			}};
			lib.Normalize();

			() -> {
				var chip = new Chip((int) sampleRate, -15.0, -1);
				var regs = new Registers(chip);
				var opts = new ControllerOpts() {{
					Registers = regs;
					Library = lib;
				}};
				var seq = new Controller(opts);
				chip.Next();

				seq.PushMIDIMessage(MIDIControlChange, 1, 0, 0, 0);
				seq.PushMIDIMessage(MIDIControlChange, 1, 0, 32, 0);
				seq.PushMIDIMessage(MIDIProgramChange, 1, 0, 0, 0);
				seq.FlushMIDIMessages(2);
				chip.Next();

				seq.PushMIDIMessage(MIDINoteOn, 3, 0, 60, 127);
				seq.FlushMIDIMessages(4);
				for (var j = 0; j < 100; j++) {
					chip.Next();
				}

				seq.PushMIDIMessage(MIDINoteOff, 5, 0, 60, 0);
				seq.FlushMIDIMessages(6);
				for (var j = 0; j < 100; j++) {
					chip.Next();
				}
			}.run();
		}
	}
}
