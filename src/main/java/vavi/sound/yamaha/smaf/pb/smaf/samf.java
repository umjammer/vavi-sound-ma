/*
 * https://github.com/but80/go-smaf
 */

package vavi.sound.yamaha.smaf.pb.smaf;

import java.util.ArrayList;

import vavi.sound.yamaha.smaf.pb.smaf.pb.VM35FMVoice;
import vavi.sound.yamaha.smaf.pb.smaf.pb.VM35VoicePC;

import static vavi.sound.yamaha.smaf.pb.smaf.pb.VM35FMVoiceVersion.VM35FMVoiceVersionVM5;
import static vavi.sound.yamaha.smaf.pb.smaf.pb.VoiceType.VoiceTypeFM;


/**
 * samf.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 2024-12-19 nsano initial version <br>
 */
public class samf {

    // DefaultPC は、未定義の音色をロードしようとしたときに返されるデフォルト音色です。
    public static VM35VoicePC defaultPC = new VM35VoicePC() {
        {
            Version = VM35FMVoiceVersionVM5;
            Name = "default";
            VoiceType = VoiceTypeFM;
            FmVoice = new VM35FMVoice() {{
                Panpot = 15;
                Bo = 1;
                Alg = 1;
                Operators = new ArrayList<>();
            }};
        }
    };

    static int normalizeint(boolean[] ok, int target, int min, int max) {
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
