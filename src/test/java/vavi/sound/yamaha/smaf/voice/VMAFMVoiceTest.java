/*
 * Copyright (c) 2026 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.sound.yamaha.smaf.voice;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


/**
 * VMAFMVoiceTest.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 2026-09-11 nsano initial version <br>
 */
class VMAFMVoiceTest {

    /**
     * The 4 operator voice of "AroundTheWorld.mmf", the body of a
     * {@code 43 03 nn ll pc ... f7} SMAF voice exclusive.
     */
    private static final byte[] VOICE_4OP = {
        (byte) 0x75, (byte) 0x01,
        (byte) 0x15, (byte) 0x32, (byte) 0xfc, (byte) 0x5a, (byte) 0xa0,
        (byte) 0x25, (byte) 0x83, (byte) 0xf2, (byte) 0x00, (byte) 0xa0,
        (byte) 0x10, (byte) 0x71, (byte) 0xfd, (byte) 0x43, (byte) 0xa0,
        (byte) 0x10, (byte) 0x81, (byte) 0xff, (byte) 0x00, (byte) 0xa0,
    };

    /** 2 global bytes and 5 per operator, so a 2 operator voice is 12 bytes */
    private static final byte[] VOICE_2OP = {
        (byte) 0x68, (byte) 0x01,
        (byte) 0x90, (byte) 0x88, (byte) 0xd0, (byte) 0x00, (byte) 0xa0,
        (byte) 0x11, (byte) 0x78, (byte) 0xa6, (byte) 0x00, (byte) 0xa2,
    };

    /** the constructor has to read the voice, not only the operators ALG leaves over */
    @Test
    void readsFourOperators() throws Exception {
        VMAFMVoice voice = new VMAFMVoice(VOICE_4OP);

        assertNotNull(voice.alg);
        assertEquals(4, voice.alg.operatorCount());
        assertNotNull(voice.operators[3]);
    }

    /** */
    @Test
    void readsTwoOperators() throws Exception {
        VMAFMVoice voice = new VMAFMVoice(VOICE_2OP);

        assertNotNull(voice.alg);
        assertEquals(2, voice.alg.operatorCount());
    }

    /**
     * The conversion must fill the operator list. {@code new ArrayList<>(4)} is a
     * capacity of 4 and a size of 0, unlike go's {@code make([]T, 4)} this is a port
     * of, so filling it with {@code List#set} threw and left it empty - which a
     * consumer then sees as a voice with no operator at all.
     */
    @Test
    void convertsToVM35WithItsOperators() throws Exception {
        VM35FMVoice converted = new VMAFMVoice(VOICE_4OP).ToVM35();

        assertEquals(4, converted.operators.size());
        for (int op = 0; op < 4; op++) {
            assertNotNull(converted.operators.get(op), "operator " + op);
        }
        // the feedback of the voice goes to the first operator only
        assertEquals(6, converted.operators.get(0).fb);
        assertEquals(0, converted.operators.get(1).fb);
    }
}
