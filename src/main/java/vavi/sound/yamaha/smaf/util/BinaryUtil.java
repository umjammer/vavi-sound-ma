/*
 * https://github.com/but80/smaf825
 */

package vavi.sound.yamaha.smaf.util;

import java.io.DataInputStream;
import java.io.IOException;


public class BinaryUtil {

    public static int readVariableInt(boolean allow3bytes, DataInputStream rdr, int[] rest) throws IOException {
        var result = 0;
        var i = 0;
        while (true) {
            byte b = rdr.readByte();
            rest[0]--;
            if (!allow3bytes && i == 1) {
                return result + 0x80;
            }
            result |= b & 0x7F;
            if ((b & 0x80) == 0) {
                break;
            }
            result <<= 7;
            i++;
        }
        return result;
    }

    public static byte boolToByte(boolean b, byte v) {
        if (b) {
            return v;
        }
        return 0;
    }

    public static int[] bytesToInts(byte[] b) {
        var result = new int[b.length];
        for (var i = 0; i < b.length; i++) {
            var v = b[i];
            result[i] = v;
        }
        return result;
    }
}