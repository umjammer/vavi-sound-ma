/*
 * https://github.com/but80/fmfm.core
 */

package vavi.sound.yamaha.smaf.enums;

import com.google.gson.Gson;


public class Enums {

    private static final Gson gson = new Gson().newBuilder().create();

    public enum VoiceType {

        FM,
        PCM,
        AL;
        // "unknown"

        public String toString() {
            return "%s(%d)".formatted(name(), ordinal());
        }

        byte[] marshalJSON() {
            return gson.toJson(toString()).getBytes();
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
            return "%s[ %s ]".formatted(name(), s);
        }

        public int operatorCount() {
            if (this.ordinal() < 2) {
                return 2;
            } else {
                return 4;
            }
        }
    }

    public enum BasicOctave {
        Normal(1),
        Zero(0),
        Minus1(-1),
        Minus2(-2);
        // "undefined"
        final int v;
        BasicOctave(int v) { this.v = v; }

        Note diffNote(BasicOctave o) {
            return switch (o.v) {
                case 0 -> new Note(1 * 12);
                case 2 -> new Note(-1 * 12);
                case 3 -> new Note(-2 * 12);
                default -> new Note(0 * 12);
            };
        }
    }

    public enum Panpot {
        _0,
        _1,
        _2,
        _3,
        _4,
        _5,
        _6,
        _7,
        _8,
        _9,
        _10,
        _11,
        _12,
        _13,
        _14,
        Center,
        _15,
        _16,
        _17,
        _18,
        _19,
        _20,
        _21,
        _22,
        _23,
        _24,
        _25,
        _26,
        _27,
        _28,
        _29,
        _30,
        _31;

        @Override public String toString() {
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
        _0,
        _1,
        _2,
        _3,
        _4,
        _5,
        _6,
        _7,
        _8,
        _9,
        _10,
        _11,
        _12,
        _13,
        _14,
        _15;

        @Override public String toString() {
            if (ordinal() == 0) {
                return "1/2";
            } else{
                return "%d".formatted(ordinal());
            }
        }
    }
}
