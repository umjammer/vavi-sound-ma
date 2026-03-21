/*
 * https://github.com/but80/smaf825
 */

package vavi.sound.yamaha.smaf.voice;

import java.io.DataInputStream;
import java.io.IOException;

import static vavi.sound.yamaha.smaf.util.TextUtil.hex;


/**
 * <pre>
 *      | 7 | 6 | 5 | 4 | 3 | 2 | 1 | 0 |
 *  + 0 |             Fs(H)             |
 *  + 1 |             Fs(L)             |
 *  + 2 |      panpot       |   ?   |P E|
 *  + 3 |  lfo  |           ?           |
 *  + 4 |      S R      |xof|   |sus|   |
 *  + 5 |      R R      |      D R      |
 *  + 6 |      A R      |      S L      |
 *  + 7 |          T L          |   ?   |
 *  + 8 | ? |  dam  |eam| ? |  dvb  |evb|
 *  + 9 |               ?               |
 *  +10 |               ?               |
 *  +11 |             LP(H)             |
 *  +12 |             LP(L)             |
 *  +13 |             EP(H)             |
 *  +14 |             EP(L)             |
 *  +15 |R M|         ...WaveID         |
 *  +16 |               ?               |
 *  +17 |               ?               |
 *  +18 |               ?               |
 * </pre>
 */
public class VM35PCMVoice implements VM35Voice {

    //`json:"raw_data"`
    byte[] rawData = new byte[19];

    @Override
    public void read(DataInputStream rdr, int[] rest) throws IOException {
        rdr.readFully(this.rawData);
        rest[0] -= this.rawData.length;
    }

    @Override
    public void readUnusedRest(DataInputStream rdr, int[] rest) {
    }

    @Override
    public String toString() {
        return hex(this.rawData);
    }

    /**
     * Normalize removes outliers from the timbre data and normalizes it.
     * Returns true if the tone was normal to begin with.
     */
    boolean normalize() {
        var ok = true;
        if (this.rawData == null) {
            this.rawData = new byte[0];
            ok = false;
        }
        return ok;
    }
}