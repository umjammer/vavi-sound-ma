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
import vavi.sound.yamaha.smaf.enums.Enums.Algorithm;
import vavi.sound.yamaha.smaf.enums.Enums.BasicOctave;
import vavi.sound.yamaha.smaf.enums.Enums.Panpot;
import vavi.sound.yamaha.smaf.enums.Enums.VoiceType;
import vavi.sound.yamaha.smaf.enums.Note;
import vavi.sound.yamaha.smaf.voice.VM35FMVoice;
import vavi.sound.yamaha.smaf.voice.VM35VoicePC;
import vavi.sound.yamaha.smaf.voice.VM5VoiceLib;

import static vavi.sound.yamaha.ma.fmfm.Controller.MIDIMessage.MIDIControlChange;
import static vavi.sound.yamaha.ma.fmfm.Controller.MIDIMessage.MIDINoteOff;
import static vavi.sound.yamaha.ma.fmfm.Controller.MIDIMessage.MIDINoteOn;
import static vavi.sound.yamaha.ma.fmfm.Controller.MIDIMessage.MIDIProgramChange;


class ChipTest {

	@FuzzTest
	void testNewChip(FuzzedDataProvider data) throws IOException {
		var sampleRate = 44100.0;

		for (var i = 0; i < 1000; i++) {
			var pc = new VM35VoicePC();
			pc.bankMSB = data.consumeInt();
			pc.bankLSB = data.consumeInt();
			pc.pc = data.consumeInt();
			pc.drumNote = Note.values()[data.consumeInt(0, Note.values().length)];
			pc.voiceType = VoiceType.values()[data.consumeInt(0, VoiceType.values().length)];
			pc.voice = new VM35FMVoice();
			((VM35FMVoice) pc.voice).alg = Algorithm.values()[data.consumeInt(0, Algorithm.values().length)];
			((VM35FMVoice) pc.voice).panpot = Panpot.values()[data.consumeInt(0, Panpot.values().length)];
			((VM35FMVoice) pc.voice).bo = BasicOctave.values()[data.consumeInt(0, BasicOctave.values().length)];
			((VM35FMVoice) pc.voice).lfo = data.consumeInt();

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
