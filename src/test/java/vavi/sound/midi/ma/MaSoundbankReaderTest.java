/*
 * Copyright (c) 2026 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.sound.midi.ma;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import vavi.sound.yamaha.smaf.voice.VM3VoiceLib;
import vavi.sound.yamaha.smaf.voice.VM5VoiceLib;
import vavi.sound.yamaha.smaf.voice.VMAVoiceLib;
import vavi.util.Debug;
import vavi.util.properties.annotation.Property;
import vavi.util.properties.annotation.PropsEntity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


/**
 * MaSoundbankReaderTest.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 2026-03-29 nsano initial version <br>
 */
@PropsEntity(url = "file:local.properties")
class MaSoundbankReaderTest {

    static boolean localPropertiesExists() {
        return Files.exists(Paths.get("local.properties"));
    }

    @Property
    String vm3 = "src/test/resources/test.vm3";

    @Property
    String vm5 = "src/test/resources/test.vm5";

    @Property
    String vma = "src/test/resources/test.vma";

    @BeforeEach
    void setup() throws Exception {
        if (localPropertiesExists()) {
            PropsEntity.Util.bind(this);
        }
    }

    @Test
    @DisplayName("via api .vm3")
    void test1() throws Exception {
        VM3VoiceLib voices = new VM3VoiceLib(Files.newInputStream(Path.of(vm3)));
voices.programs.forEach(System.err::println);
    }

    @Test
    @Disabled(".vm5 not found")
    @DisplayName("via api .vm5")
    void test2() throws Exception {
        VM5VoiceLib voices = new VM5VoiceLib(Files.newInputStream(Path.of(vm5)));
voices.programs.forEach(System.err::println);
    }

    @Test
    @DisplayName("via api .vma")
    void test3() throws Exception {
        VMAVoiceLib voices = new VMAVoiceLib(Files.newInputStream(Path.of(vma)));
voices.programs.forEach(System.err::println);
    }
}
