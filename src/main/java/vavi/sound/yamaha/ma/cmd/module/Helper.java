/*
 * https://github.com/but80/fmfm.core
 */

package vavi.sound.yamaha.ma.cmd.module;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.function.Supplier;


public class Helper {

    static native void _set_longlong_array(long out[], int i, long v);
    static native void _set_uchar_array(byte[] out, int i, byte v);

    public static int[] collectInts(Supplier<List<Integer>> fn) {
        var found = new HashMap<Integer, Object>();
        var ch = fn.get();
        for (var v : ch) {
            found.put(v, new Object());
        }

        var result = new ArrayList<>(found.keySet());
        Collections.sort(result);
        return result.stream().mapToInt(Integer::intValue).toArray();
    }

    public static long writeInts(long[] out, int[] a) {
        for (var i = 0; i < a.length; i++) {
            var v = a[i];
            _set_longlong_array(out, i, v);
        }
        return a.length;
    }

    public static long writeBytes(byte[] out , byte[] a) {
        for (var i = 0; i < a.length; i++) {
            var v = a[i];
            _set_uchar_array(out, i, v);
        }
        return a.length;
    }
}
