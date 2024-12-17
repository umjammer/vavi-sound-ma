/*
 * https://github.com/but80/fmfm.core
 */

package vavi.sound.yamaha.smaf.enums;


public class Enums {

    public enum VoiceType {

        VoiceType_FM("FM"),
        VoiceType_PCM("PCM"),
        VoiceType_AL("AL");
        // "unknown"

        final String t;

        VoiceType(String t) {
            this.t = t;
        }

        public String toString() {
            return "%s(%d)".formatted(name(), ordinal());
        }

        byte[] MarshalJSON() {
            return gson.Marshal(toString());
        }
    }

    public enum Algorithm {
        A0("FB(1)->2"),
        A1("FB(1) + 2"),
        A2("FB(1) + 2 + FB(3) + 4"),
        A3("(FB(1) + 2->3) -> 4"),
        A4("FB(1)->2->3->4"),
        A5("FB(1)->2 + FB(3)->4"),
        A6("FB(1) + 2->3->4"),
        A7("FB(1) + 2->3 + 4");

        final String s;

        Algorithm(String s) {
            this.s = s;
        }

        public String toString() {
            return "%d[ %s ]".formatted(this, s);
        }

        int OperatorCount() {
            if (this.ordinal() < 2) {
                return 2;
            } else {
                return 4;
            }
        }
    }

    public enum BasicOctave {
        BasicOctave_Normal(1),
        BasicOctave0(0),
        BasicOctaveMinus1(-1),
        BasicOctaveMinus2(-2);
        // "undefined"
        final int v;
        BasicOctave(int v) { this.v = v; }

        Note NoteDiff( BasicOctave o) {
            return switch (o.v) {
                case 0 -> Note.values()[1 * 12];
                case 2 -> Note.values()[-1 * 12];
                case 3 -> Note.values()[-2 * 12];
                default -> Note.values()[0 * 12];
            };
        }
    }

    public enum Panpot {
        Panpot0,
        Panpot1,
        Panpot2,
        Panpot3,
        Panpot4,
        Panpot5,
        Panpot6,
        Panpot7,
        Panpot8,
        Panpot9,
        Panpot10,
        Panpot11,
        Panpot12,
        Panpot13,
        Panpot14,
        Panpot_Center,
        Panpot15,
        Panpot16,
        Panpot17,
        Panpot18,
        Panpot19,
        Panpot20,
        Panpot21,
        Panpot22,
        Panpot23,
        Panpot24,
        Panpot25,
        Panpot26,
        Panpot27,
        Panpot28,
        Panpot29,
        Panpot30,
        Panpot31;

        public String toString() {
            var v = ordinal();
            if (v == 15) {
                return "C";
            } else if (0 <= v && v < 15) {
                return "L%d".formatted(15 - v);
            } else if (15 < v && v < 32) {
                return "R%d".formatted(v - 15);
            } else{
                return "undefined";
            }
        }
    }

    public enum Multiplier {

        int        func(m Multiplier) String()

        string {
            if m == 0 {
                return "1/2"
            } else{
                return fmt.Sprintf("%d", m)
            }
        }
    }
}
