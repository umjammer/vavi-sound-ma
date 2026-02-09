/*
 * Copyright (c) 2025 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.sound.yamaha.ma;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.zip.GZIPInputStream;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import vavi.sound.yamaha.smaf.pb.smaf.Smaf.VM5VoiceLib;
import vavi.util.Debug;


/**
 * TestCase.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 2025-03-16 nsano initial version <br>
 */
class TestCase {

    @Test
    @Disabled
    @DisplayName("protobuf")
    void test1() throws Exception {
        InputStream is = new GZIPInputStream(new ByteArrayInputStream(fileDescriptor_smaf_4f8a53039970ce01));
        Path path = Path.of("tmp/out.dat");
        Files.copy(is, path, StandardCopyOption.REPLACE_EXISTING);
        var list = VM5VoiceLib.parser().parseDelimitedFrom(Files.newInputStream(path)).getProgramsList();
        list.forEach(Debug::println);
    }

    static final byte[] fileDescriptor_smaf_4f8a53039970ce01 = {
            (byte) 0x1f, (byte) 0x8b, (byte) 0x08, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0xff, (byte) 0x13, (byte) 0xe4, (byte) 0xe2, (byte) 0xe7, (byte) 0x60, (byte) 0x12, 
            (byte) 0xe2, (byte) 0x0c, (byte) 0x49, (byte) 0x2d, (byte) 0x2e, (byte) 0x09, (byte) 0xcb, (byte) 0xcf, (byte) 0x4c, (byte) 0x4e, (byte) 0xd5, (byte) 0x60, (byte) 0x04, (byte) 0x00, (byte) 0x31, (byte) 0x06, 
            (byte) 0xf4, (byte) 0x03, (byte) 0x12, (byte) 0x00, (byte) 0x00, (byte) 0x00, 
    };
}