/*
 * https://github.com/but80/smaf825
 */

package vavi.sound.yamaha.smaf.voice;

import java.io.DataInputStream;
import java.io.IOException;


/**
 * <pre>
 *      | 7 | 6 | 5 | 4 | 3 | 2 | 1 | 0 |
 *  + 0 |             Fs(H)             |
 *  + 1 |             Fs(L)             |
 *  + 2 |      PANPOT       |   ?   |P E|
 *  + 3 |  LFO  |           ?           |
 *  + 4 |      S R      |XOF|   |SUS|   |
 *  + 5 |      R R      |      D R      |
 *  + 6 |      A R      |      S L      |
 *  + 7 |          T L          |   ?   |
 *  + 8 | ? |  DAM  |EAM| ? |  DVB  |EVB|
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
class VM35PCMVoice implements VM35Voice {

    //`json:"raw_data"`
    byte[] RawData = new byte[19];

    @Override
    public void Read(DataInputStream rdr, int[] rest) throws IOException {
        rdr.readFully(this.RawData);
        rest[0] -= this.RawData.length;
    }

    @Override
    public void ReadUnusedRest(DataInputStream rdr, int[] rest) {
    }

    public String toString() {
        return util.Hex(this.RawData);
    }
}