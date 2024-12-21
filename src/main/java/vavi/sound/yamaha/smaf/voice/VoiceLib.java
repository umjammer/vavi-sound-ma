/*
 * https://github.com/but80/smaf825
 */

package vavi.sound.yamaha.smaf.voice;

import java.io.DataInputStream;
import java.io.IOException;


public interface VoiceLib {

//    fmt.Stringer;

    class ChunkHeader {

        int signature;
        int size;

        void read(DataInputStream dis) throws IOException {
            signature = dis.readInt();
            size = dis.readInt();
        }
    }
}
