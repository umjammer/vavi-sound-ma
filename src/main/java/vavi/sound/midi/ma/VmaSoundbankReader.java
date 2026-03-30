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
import vavi.sound.midi.ma.VmaSoundbank.VmaInstrument;
import vavi.sound.yamaha.smaf.voice.VM5VoiceLib;
import vavi.sound.yamaha.smaf.voice.VMAVoiceLib;


/**
 * VmaSoundbankReader.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2026/03/29 umjammer initial version <br>
 */
class VmaSoundbankReader extends MaSoundbankReader {

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
        VmaSoundbank soundbank = new VmaSoundbank();
        VMAVoiceLib voiceLib = new VMAVoiceLib(is);
        voiceLib.programs.forEach(voice -> soundbank.instruments.add(new VmaInstrument(voice.bank, voice.pc, false, voice)));
        return soundbank;
    }
}
