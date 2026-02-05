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
import vavi.sound.yamaha.smaf.voice.VM5VoiceLib;

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

	@Test
	void TestController_writeFrequency() {
		var regs = new Registers(new Chip(44100, 0, 0));
		var ctrl = new Controller(new ControllerOpts() {{
			registers = regs;
			library = new VM5VoiceLib();
		}});
		var fnumPrev = 300;
		for (var i = 0; i < 12; i++) {
			var n = A3Note + i;
			ctrl.noteOn(0, n, 127);
			var ch = regs.getChip().getChannels()[0];
			var fnum = ch.getFNum();
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
