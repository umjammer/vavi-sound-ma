/*
 * Copyright (c) 2026 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.sound.midi.ma;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.List;
import javax.sound.midi.Instrument;
import javax.sound.midi.Patch;
import javax.sound.midi.Soundbank;
import javax.sound.midi.SoundbankResource;
import com.sun.media.sound.ModelPatch;
import com.sun.media.sound.SimpleInstrument;

import vavi.sound.yamaha.smaf.voice.VM35VoicePC;

import static java.lang.System.getLogger;


/**
 * Vm3Soundbank.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2026/03/28 umjammer initial version <br>
 */
public class Vm35Soundbank implements Soundbank {

    private static final Logger logger = getLogger(Vm35Soundbank.class.getName());

    /** */
    final List<Instrument> instruments = new ArrayList<>();

    public Vm35Soundbank() {
    }

    @Override
    public String getName() {
        return "Vm3Soundbank";
    }

    @Override
    public String getVersion() {
        return Vm3SoundbankReader.version;
    }

    @Override
    public String getVendor() {
        return "vavi";
    }

    @Override
    public String getDescription() {
        return "Soundbank for YAMAHA MA3 MA5 compatible synthesizer";
    }

    @Override
    public SoundbankResource[] getResources() {
        return getInstruments();
    }

    @Override
    public Instrument[] getInstruments() {
        return instruments.toArray(Instrument[]::new);
    }

    @Override
    public Instrument getInstrument(Patch patch) {
        for (Instrument instrument : instruments) {
            if (instrument.getPatch().getProgram() == patch.getProgram() &&
                    instrument.getPatch().getBank() == patch.getBank()) {
logger.log(Level.DEBUG, "request for: " + patch);
                return instrument;
            }
        }
logger.log(Level.DEBUG, "no instrument for: " + patch);
        return null;
    }

    /** */
    public static class Vm35Instrument extends SimpleInstrument {
        final VM35VoicePC data;
        protected Vm35Instrument(int bank, int program, boolean percussion, VM35VoicePC instrument) {
            setPatch(new ModelPatch(bank, program, percussion));
            this.name = (percussion ?  "p." : "") + bank + "." + program;
            this.data = instrument;
        }

        @Override
        public Class<VM35VoicePC> getDataClass() {
            return VM35VoicePC.class;
        }

        @Override
        public VM35VoicePC getData() {
            return data;
        }
    }
}
