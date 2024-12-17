/*
 * https://github.com/but80/fmfm.core
 */

package vavi.sound.yamaha.ma.fmfm;

import java.util.HashMap;

import org.junit.jupiter.api.Test;
import vavi.sound.yamaha.ma.fmfm.Controller.ControllerOpts;
import vavi.sound.yamaha.ma.sim.Registers;
import vavi.sound.yamaha.ma.ymf.Register.ChRegister;
import vavi.sound.yamaha.ma.ymf.Register.OpRegister;

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

		int[][] channels;
		int[][][] operators;
		int[][] midiChannels;

		registers() {
			for (var i = 0; i < ChannelCount; i++) {
				var m = new HashMap<ChRegister, Integer>();
				m.put(PANPOT, 15);
				m.put(CHPAN, 64);
				m.put(VOLUME, 100);
				m.put(EXPRESSION, 127);
				m.put(BO, 1);
				this.channels.put(i, m);
				this.midiChannels.put(i, -1);
				for (var j = 0; j < 4; j++) {
					var m2 = new HashMap<OpRegister, Integer>();
					m2.put(MULT, 1);
					m2.put(AR, 15);
					m2.put(RR, 15);
					this.operators[i][j] = m2;
				}
			}
		}

		// WriteOperator は、オペレータレジスタに値を書き込みます。
		void WriteOperator(int channel, int operatorIndex, OpRegister offset, int v) {
			this.operators[channel][operatorIndex][offset.ordinal()] = v;
		}

		// WriteTL は、TLレジスタに値を書き込みます。
		void WriteTL(int channel, int operatorIndex, int tlCarrier, int tlModulator) {
			var alg = this.channels[channel][ALG.ordinal()];
			for (var i = 0; i < 4; i++) {
				var v = 31;
				if (CarrierMatrix[alg][i]) {
					v = tlCarrier;
				} else if (ModulatorMatrix[alg][i]) {
					v = tlModulator;
				}
				this.operators[channel][operatorIndex][TL.ordinal()] = v;
			}
		}

		// WriteChannel は、チャンネルレジスタに値を書き込みます。
		void WriteChannel(int channel, ChRegister offset, int v) {
			this.channels[channel][offset.ordinal()] = v;
		}

		// DebugSetMIDIChannel は、チャンネルを使用しているMIDIチャンネル番号をデバッグ用にセットします。
		void DebugSetMIDIChannel(int channel, int midiChannel) {
			this.midiChannels.put(channel, midiChannel);
		}
	}

	@Test
	void TestController_writeFrequency() {
		var regs = new Registers();
		var ctrl = new Controller(new ControllerOpts() {{
			Registers = regs;
		}});
		var fnumPrev = 300;
		for (var i = 0; i < 12; i++) {
			var n = A3Note + i;
			ctrl.noteOn(0, n, 127);
			var ch = this.channels[0];
			var fnum = ch[FNUM];
			if (i == 0) {
				assertEquals(300, fnum);
			} else {
				assertTrue(fnumPrev < fnum);
			}
			fnumPrev = fnum;
			// t.Errorf("%d: block=%d bo=%d fnum=%d", n, ch[ymf.BLOCK], ch[ymf.BO], ch[ymf.FNUM])
			ctrl.noteOff(0, n);
			ctrl.resetChipChannel(0);
		}
	}
}
