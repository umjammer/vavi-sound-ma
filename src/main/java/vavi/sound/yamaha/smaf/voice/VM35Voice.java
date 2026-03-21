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
        VM3Lib,
        VM3Exclusive,
        VM5,
    }

    //fmt.Stringer

    void read(DataInputStream rdr, int[] rest) throws IOException;

    void readUnusedRest(DataInputStream rdr, int[] rest) throws IOException;

    static int normalizeInt(boolean[] ok, int target, int min, int max) {
        int result = target;
        if (target < min) {
            result = min;
            ok[0] = false;
        }
        if (max < target) {
            result = max;
            ok[0] = false;
        }
        return result;
    }

    static String normalizeString(boolean[] ok, String target, String def) {
        String result = target;
        if (target.isEmpty()) {
            result = def;
            ok[0] = false;
        }
        return result;
    }
}
