/*
 * https://github.com/but80/smaf825
 */

package vavi.sound.yamaha.smaf.voice;

import java.io.DataInputStream;
import java.io.IOException;


interface VoiceLib {

//    fmt.Stringer;
}

class chunkHeader {

    int Signature;
    int Size;

    void read(DataInputStream dis) throws IOException {
        Signature = dis.readInt();
        Size = dis.readInt();
    }
}
