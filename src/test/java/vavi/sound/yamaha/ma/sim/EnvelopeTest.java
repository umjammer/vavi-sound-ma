/*
 * https://github.com/but80/fmfm.core
 */

package vavi.sound.yamaha.ma.sim;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static vavi.sound.yamaha.ma.ymf.ymfdata.Data.SampleRate;


class EnvelopeTest {

	@Test
	void TestEnvelopeGenerator() {
		var threshDB = -30.0;
		var thresh = Math.pow(10.0, threshDB / 20.0);
		var gen = new EnvelopeGenerator(SampleRate);
		var ar = 15;
		var dr = 15;
		var sl = 0;
		var sr = 0;
		var rr = 4;
		var ksl = 0;
		var result = new double[][];
		for (var ksr = 0; ksr < 2; ksr++) {
			List<Double> r = new ArrayList<>();
			for (var ksn = 0; ksn < 16; ksn++) {
				var fnum = (ksn & 1) * 256;
				var block = ksn >> 1;
				gen.setTotalLevel(0);
				gen.setKeyScalingLevel(fnum, block, 1, ksl);
				gen.setActualAR(ar, ksr, ksn);
				gen.setActualDR(dr, ksr, ksn);
				gen.setActualSustainLevel(sl);
				gen.setActualSR(sr, ksr, ksn);
				gen.setActualRR(rr, ksr, ksn);
				var n = (int) (.1 * SampleRate);
				var i = 0;
				for (; i < (int) (60.0 * SampleRate); i++) {
					if (i == 1) {
						gen.keyOn();
					} else if (i == n) {
						gen.keyOff();
					}
					var v = gen.getEnvelope(0);
					if (n < i && v <= thresh) {
						break;
					}
				}
				i -= n;
				var secPerDb = (double0(i) / SampleRate / (.0 - threshDB);
				var dbPerSec = 1.0 / secPerDb;
				r.add(Math.floor(dbPerSec));
			}
			result.add(r);
		}

		assertEquals(new double[][] {
			{17, 17, 17, 17, 17, 22, 22, 22, 22, 26, 26, 26, 26, 31, 31, 31},
			{17, 22, 22, 31, 31, 44, 44, 62, 62, 89, 89, 125, 125, 179, 179, 250},
		}, result);
	}
}
