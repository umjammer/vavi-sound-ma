/*
 * Copyright (c) 2026 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.sound.midi.ma;

import java.io.InputStream;
import java.util.Properties;
import javax.sound.midi.spi.SoundbankReader;


/**
 * MaSoundbankReader.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2026/03/29 umjammer initial version <br>
 */
abstract class MaSoundbankReader extends SoundbankReader {

    static {
        try {
            try (InputStream is = MaSoundbankReader.class.getResourceAsStream("/META-INF/maven/vavi/vavi-sound-ma/pom.properties")) {
                if (is != null) {
                    Properties props = new Properties();
                    props.load(is);
                    version = props.getProperty("version", "undefined in pom.properties");
                } else {
                    version = System.getProperty("vavi.test.version", "undefined");
                }
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    static final String version;
}
