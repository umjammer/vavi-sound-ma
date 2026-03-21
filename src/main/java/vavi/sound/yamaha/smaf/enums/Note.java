/*
 * https://github.com/but80/fmfm.core
 */

package vavi.sound.yamaha.smaf.enums;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;

import static java.lang.System.getLogger;


/**
 *	|C3		|261.6	|4	|357|
 *	|C#3	|277.2	|4	|378|
 *	|D3		|293.7	|4	|401|
 *	|D#3	|311.1	|4	|425|
 *	|E3		|329.6	|4	|450|
 *	|F3		|349.2	|4	|477|
 *	|F#3	|370	|4	|505|
 *	|G3		|392	|4	|535|
 *	|G#3	|415.3	|4	|567|
 *	|A3		|440	|4	|601|
 *	|A#3	|466.2	|4	|637|
 *	|B3		|493.9	|4	|674|
 */
public class Note {

    private static final Logger logger = getLogger(Note.class.getName());

    String[] noteNames = {
            "C",
            "C#",
            "D",
            "D#",
            "E",
            "F",
            "F#",
            "G",
            "G#",
            "A",
            "A#",
            "B"
    };

    public final int note;

    public Note(int note) {
        this.note = note;
    }

    static class NoteFreq {

        int block;
        int fNum;
    }

    public static final int Note_A3 = 9 + 12 * 3;

    @Override
    public String toString() {
        return "%s(%d)".formatted(name(), note);
    }

    public String name() {
        return "%s%d".formatted(noteNames[note % 12], note / 12 - 1);
    }

    static final double fNumK = Math.pow(2.0, 19.0) / 48000.0 / 2.0;

    NoteFreq freq(double delta) {
        var f = 440 * Math.pow(2.0, ((double) (note - Note_A3) + delta) / 12.0);
        var block = note / 12;
        if (block < 0) {
            block = 0;
        } else if (7 < block) {
            block = 7;
        }
        var fnum = 0;
        while (true) {
            fnum = (int) (Math.floor(.5 + f * fNumK / Math.pow(2.0, block)));
            if (fnum < 0) {
                if (0 < block) {
                    block--;
                    continue;
                }
                logger.log(Level.WARNING, "Too low fNum: %s".formatted(this));
                fnum = 0;
            } else if (1024 <= fnum) {
                if (block < 7) {
                    block++;
                    continue;
                }
                logger.log(Level.WARNING, "Too high fNum: %s".formatted(this));
                fnum = 1023;
            }
            break;
        }
        NoteFreq nf = new NoteFreq();
        nf.block = block;
        nf.fNum = fnum;
        return nf;
    }
}