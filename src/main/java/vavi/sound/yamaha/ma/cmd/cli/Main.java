/*
 * https://github.com/but80/fmfm.core
 */

package vavi.sound.yamaha.ma.cmd.cli;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;
import org.klab.commons.cli.HelpOption;
import org.klab.commons.cli.Option;
import org.klab.commons.cli.Options;
import vavi.sound.yamaha.ma.cmd.cli.internal.player.Limiter;
import vavi.sound.yamaha.ma.cmd.cli.internal.player.Renderer;
import vavi.sound.yamaha.ma.cmd.cli.internal.player.Sequencer;
import vavi.sound.yamaha.ma.fmfm.Controller;
import vavi.sound.yamaha.ma.fmfm.Controller.ControllerOpts;
import vavi.sound.yamaha.ma.sim.Chip;
import vavi.sound.yamaha.ma.sim.Registers;
import vavi.sound.yamaha.smaf.voice.VM5VoiceLib;

import static java.lang.System.getLogger;


/**
 * YAMAHA MA-5/YMF825 clone synthesizer
 *
 * @author <a href="mailto:mersenne.sister@gmail.com">but80</a>
 */
@Options()
@HelpOption(option = "?")
public class Main {

    private static final Logger logger = getLogger(Main.class.getName());

    static String version = "unknown";

    @Option(argName = "list", option = "l", description = "List MIDI devices")
    boolean listCmd;

    void list() {
        try {
            MidiDevice.Info[] infos = MidiSystem.getMidiDeviceInfo();
            for (MidiDevice.Info info : infos) {
                MidiDevice device = MidiSystem.getMidiDevice(info);
                if (device.getMaxTransmitters() == 0)
                    System.out.println(device.getDeviceInfo().getName() + ":" + device.getDeviceInfo().getVendor() + ":" + device.getDeviceInfo().getDescription() + ", T: " + device.getMaxTransmitters() + ", R: " + device.getMaxReceivers());
            }
        } catch (Exception e) {
            logger.log(Level.ERROR, e.getMessage(), e);
        }
    };

    @Option(argName = "midi", option = "m", args = 1, description = "Listen MIDI events" /*, usage = "[<Input MIDI device>]" */)
    String midiCmd;

    @Option(argName = "mono", option = "m", description = "Force mono mode in all MIDI channels except drum PC")
    boolean mono;

    @Option(argName = "mute-nopc", option = "z", description = "Mute if program change is not found")
    boolean muteNoPc;

    @Option(argName = "level", option = "v", args = 1, description = "Total level in dB")
    float level= -12.0f;
    @Option(argName = "limiter", option = "c", args = 1, description = "player.Limiter threshold in dB")
    double limiter = -6.0;
    @Option(argName = "ignore", option = "n", args = 1, description = "Ignore specified MIDI channel")
    int ignore;
    @Option(argName = "solo", option = "s", args = 1, description = "Accept only specified MIDI sim.channel")
    int solo;
    @Option(argName = "dump", option = "d", args = 1, description = "Dump MIDI sim.channel")
    int dump;
    @Option(argName = "print", option = "p",description = "Print status")
    boolean print;

    void midi() {
        try {
            var midiDevice = "";
            if (midiCmd != null) {
                midiDevice = midiCmd;
            }

            var info = Files.list(Path.of("voice"));
            AtomicReference<VM5VoiceLib> lib = new AtomicReference<>();
            info.forEach((i) -> {
                if (Files.isDirectory(i) || !i.getFileName().toString().endsWith(".vm5.pb")) {
                    return;
                }
                try {
                    lib.set(new VM5VoiceLib(Path.of("voice/").resolve(i.getFileName()).toString()));
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            });

            var dumpMIDIChannel = -1;
            if (0 < dump) {
                dumpMIDIChannel = dump - 1;
            }

            var renderer = new Renderer();
            var limiter = new Limiter(renderer.Parameters.getSampleRate());
            limiter.SetThreshold(this.limiter);
            renderer.Insert(limiter);
            var chip = new Chip((int) renderer.Parameters.getSampleRate(),
                    level,
                    dumpMIDIChannel
            );
            var regs = new Registers(chip);
            int _dumpMIDIChannel = dumpMIDIChannel;
            var opts = new ControllerOpts() {{
                registers = regs;
                library = lib.get();
                muteIfPCNotFound = muteNoPc;
                forceMono = mono;
                printStatus = print;
                ignoreMIDIChannels = new ArrayList<>();
                soloMIDIChannel = _dumpMIDIChannel;
            }};
            if (0 < ignore) {
                opts.ignoreMIDIChannels.add(ignore - 1);
            }
            if (0 < solo) {
                for (var i = 0; i < 16; i++) {
                    if (i == solo - 1) {
                        continue;
                    }
                    opts.ignoreMIDIChannels.add(i);
                }
            }
            Controller controller = new Controller();
            try (var seq = new Sequencer(midiDevice, opts)) {
                renderer.Start(chip::next, controller::FlushMIDIMessages);
                Thread.sleep(24 * 60 * 60 * 1000);
            }
        } catch (Exception e) {
            logger.log(Level.ERROR, e.getMessage(), e);
        }
    };

    public static void main(String[] args) {
        var app = new Main();
        Options.Util.bind(args, app);
        if (app.listCmd) {
            app.list();
        } else if (app.midiCmd != null) {
            app.midi();
        }
    }
}
