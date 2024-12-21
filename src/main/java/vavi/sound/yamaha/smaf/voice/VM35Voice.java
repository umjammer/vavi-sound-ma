/*
 * Copyright (c) 2024 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.sound.yamaha.smaf.voice;

import java.io.DataInputStream;
import java.io.IOException;


/**
 * VM35Voice.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 2024-12-21 nsano initial version <br>
 */
public interface VM35Voice {

    enum VM35FMVoiceVersion {
        VM35FMVoiceVersion_VM3Lib,
        VM35FMVoiceVersion_VM3Exclusive,
        VM35FMVoiceVersion_VM5,
    }

    //fmt.Stringer

    void read(DataInputStream rdr, int[] rest) throws IOException;

    void readUnusedRest(DataInputStream rdr, int[] rest) throws IOException;
}
