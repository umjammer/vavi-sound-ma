/*
 * Copyright (c) 2026 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.sound.midi.ma;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.Soundbank;

import vavi.sound.midi.ma.Vm35Soundbank.Vm35Instrument;
import vavi.sound.yamaha.smaf.voice.VM5VoiceLib;


/**
 * Vm5SoundbankReader.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2026/03/29 umjammer initial version <br>
 */
class Vm5SoundbankReader extends MaSoundbankReader {

    @Override
    public Soundbank getSoundbank(URL url) throws InvalidMidiDataException, IOException {
        return getSoundbank(url.openStream());
    }

    @Override
    public Soundbank getSoundbank(InputStream stream) throws InvalidMidiDataException, IOException {
        return getSoundbankInternal(stream);
    }

    @Override
    public Soundbank getSoundbank(File file) throws InvalidMidiDataException, IOException {
        return getSoundbank(new FileInputStream(file));
    }

    static Soundbank getSoundbankInternal(InputStream is) throws IOException {
        Vm35Soundbank soundbank = new Vm35Soundbank();
        VM5VoiceLib voiceLib = new VM5VoiceLib(is);
        voiceLib.programs.forEach(voice -> soundbank.instruments.add(new Vm35Instrument(voice.bankMSB, voice.pc, false, voice)));
        return soundbank;
    }
}
