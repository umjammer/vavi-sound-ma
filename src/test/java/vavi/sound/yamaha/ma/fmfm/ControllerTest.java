/*
 * https://github.com/but80/fmfm.core
 */

package vavi.sound.yamaha.ma.fmfm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import vavi.sound.yamaha.ma.fmfm.Controller.ControllerOpts;
import vavi.sound.yamaha.ma.sim.Chip;
import vavi.sound.yamaha.ma.sim.Registers;
import vavi.sound.yamaha.ma.ymf.Register.ChRegister;
import vavi.sound.yamaha.ma.ymf.Register.OpRegister;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static vavi.sound.yamaha.ma.ymf.Register.ChRegister.ALG;
import static vavi.sound.yamaha.ma.ymf.Register.ChRegister.BO;
import static vavi.sound.yamaha.ma.ymf.Register.ChRegister.CHPAN;
import static vavi.sound.yamaha.ma.ymf.Register.ChRegister.EXPRESSION;
import static vavi.sound.yamaha.ma.ymf.Register.ChRegister.FNUM;
import static vavi.sound.yamaha.ma.ymf.Register.ChRegister.PANPOT;
import static vavi.sound.yamaha.ma.ymf.Register.ChRegister.VOLUME;
import static vavi.sound.yamaha.ma.ymf.Register.OpRegister.AR;
import static vavi.sound.yamaha.ma.ymf.Register.OpRegister.MULT;
import static vavi.sound.yamaha.ma.ymf.Register.OpRegister.RR;
import static vavi.sound.yamaha.ma.ymf.Register.OpRegister.TL;
import static vavi.sound.yamaha.ma.ymf.ymfdata.Data.A3Note;
import static vavi.sound.yamaha.ma.ymf.ymfdata.Data.CarrierMatrix;
import static vavi.sound.yamaha.ma.ymf.ymfdata.Data.ChannelCount;
import static vavi.sound.yamaha.ma.ymf.ymfdata.Data.ModulatorMatrix;


public class ControllerTest {

	static class registers {

		List<Map<ChRegister, Integer>> channels = new ArrayList<>();
		Map<int[], Map<OpRegister, Integer>> operators = new HashMap<>();
		int[] midiChannels = new int[ChannelCount];

		registers() {
			for (var i = 0; i < ChannelCount; i++) {
				var m = new HashMap<ChRegister, Integer>();
				m.put(PANPOT, 15);
				m.put(CHPAN, 64);
				m.put(VOLUME, 100);
				m.put(EXPRESSION, 127);
				m.put(BO, 1);
				this.channels.add(m);
				this.midiChannels[i] = -1;
				for (var j = 0; j < 4; j++) {
					var m2 = new HashMap<OpRegister, Integer>();
					m2.put(MULT, 1);
					m2.put(AR, 15);
					m2.put(RR, 15);
					this.operators.put(new int[] {i, j}, m2);
				}
			}
		}

		// WriteOperator writes a value to an operator register.
		void WriteOperator(int channel, int operatorIndex, OpRegister offset, int v) {
			this.operators.get(new int[] {channel, operatorIndex}).put(offset, v);
		}

		// WriteTL writes a value to the TL register.
		void WriteTL(int channel, int operatorIndex, int tlCarrier, int tlModulator) {
			var alg = this.channels.get(channel).get(ALG);
			for (var i = 0; i < 4; i++) {
				var v = 31;
				if (CarrierMatrix[alg][i]) {
					v = tlCarrier;
				} else if (ModulatorMatrix[alg][i]) {
					v = tlModulator;
				}
				this.operators.get(new int[] {channel, operatorIndex}).put(TL, v);
			}
		}

		// WriteChannel writes a value to a channel register.
		void WriteChannel(int channel, ChRegister offset, int v) {
			this.channels.get(channel).put(offset, v);
		}

		// DebugSetMIDIChannel sets the MIDI channel number used for debugging purposes.
		void DebugSetMIDIChannel(int channel, int midiChannel) {
			this.midiChannels[channel] = midiChannel;
		}
	}

	@Test
	void TestController_writeFrequency() {
		var regs = new Registers(new Chip(44100, 0, 0));
		var ctrl = new Controller(new ControllerOpts() {{
			registers = regs;
		}});
		var fnumPrev = 300;
		for (var i = 0; i < 12; i++) {
			var n = A3Note + i;
			ctrl.noteOn(0, n, 127);
			registers r = new registers();
			var ch = r.channels.get(0);
			var fnum = ch.get(FNUM);
			if (i == 0) {
				assertEquals(300, fnum);
			} else {
				assertTrue(fnumPrev < fnum);
			}
			fnumPrev = fnum;
			// t.Errorf("%d: block=%d bo=%d fnum=%d", n, ch[ymf.BLOCK], ch[ymf.bo], ch[ymf.FNUM])
			ctrl.noteOff(0, n);
			ctrl.resetChipChannel(0);
		}
	}
}
