/*
 * https://github.com/but80/fmfm.core
 */

package vavi.sound.yamaha.smaf.enums;


import java.lang.System.Logger;
import java.lang.System.Logger.Level;

import static java.lang.System.getLogger;


//	|C3		|261.6	|4	|357|
//	|C#3	|277.2	|4	|378|
//	|D3		|293.7	|4	|401|
//	|D#3	|311.1	|4	|425|
//	|E3		|329.6	|4	|450|
//	|F3		|349.2	|4	|477|
//	|F#3	|370	|4	|505|
//	|G3		|392	|4	|535|
//	|G#3	|415.3	|4	|567|
//	|A3		|440	|4	|601|
//	|A#3	|466.2	|4	|637|
//	|B3		|493.9	|4	|674|
public enum Note {
    C("C"),
    C_Sherp("C#"),
    D("D"),
    D_Sherp("D#"),
    E("E"),
    F("F"),
    F_Sherp("F#"),
    G("G"),
    G_Sherp("G#"),
    A("A"),
    A_Sherp("A#"),
    B("B");

    private static final Logger logger = getLogger(Note.class.getName());

    final String s;

    Note(String s) {
        this.s = s;
    }

    static class NoteFreq {

        int Block;
        int Fnum;
    }

    public static final int Note_A3 = 9 + 12 * 3;

    public String toString() {
        return "%s(%d)".formatted(name(), ordinal());
    }

    String Name() {
        var i = ordinal();
        return "%s%d".formatted(name(), i / 12 - 1);
    }

    static final double fnumK = Math.pow(2.0, 19.0) / 48000.0 / 2.0;

    NoteFreq Freq(double delta) {
        var f = 440 * Math.pow(2.0, ((double) (ordinal() - Note_A3) + delta) / 12.0);
        var block = ordinal() / 12;
        if (block < 0) {
            block = 0;
        } else if (7 < block) {
            block = 7;
        }
        var fnum = 0;
        while (true) {
            fnum = (int) (Math.floor(.5 + f * fnumK / Math.pow(2.0, block)));
            if (fnum < 0) {
                if (0 < block) {
                    block--;
                    continue;
                }
                logger.log(Level.WARNING, "Too low fnum: %s".formatted(this));
                fnum = 0;
            } else if (1024 <= fnum) {
                if (block < 7) {
                    block++;
                    continue;
                }
                logger.log(Level.WARNING, "Too high fnum: %s".formatted(this));
                fnum = 1023;
            }
            break;
        }
        NoteFreq nf = new NoteFreq();
        nf.Block = block;
        nf.Fnum = fnum;
        return nf;
    }
}