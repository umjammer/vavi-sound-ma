/*
 * https://github.com/but80/fmfm.core
 */

package vavi.sound.yamaha.ma.cmd.cli;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import org.klab.commons.cli.Binder.Context;
import org.klab.commons.cli.Option;
import org.klab.commons.cli.Options;
import vavi.sound.yamaha.ma.cmd.cli.internal.player.Limiter;
import vavi.sound.yamaha.ma.cmd.cli.internal.player.Renderer;
import vavi.sound.yamaha.ma.cmd.cli.internal.player.Sequencer;
import vavi.sound.yamaha.ma.fmfm.Controller.ControllerOpts;
import vavi.sound.yamaha.ma.sim.Chip;
import vavi.sound.yamaha.ma.sim.Registers;
import vavi.sound.yamaha.smaf.voice.VM5VoiceLib;


/**
 * YAMAHA MA-5/YMF825 clone synthesizer
 *
 * @author <a href="mailto:mersenne.sister@gmail.com">but80</a>
 */
@Options()
public class Main {

    static String version;

    void init() {
        if (version.isEmpty()) {
            version = "unknown";
        }
    }

    @Option(argName = "list", option = "l", description = "List MIDI devices")
    String listCmd;

    Consumer<Context> x = (ctx) -> {
        devices = player.ListMIDIDeivces();
        for (var dev : devices) {
            System.err.printf(dev);
        }
    };

    @Option(argName = "midi", option = "m", description = "Listen MIDI events", usage = "[<Input MIDI device>]")
    String midiCmd;

    @Option(argName = "mono", option = "m", description = "Force mono mode in all MIDI channels except drum PC")
    boolean mono;

    @Option(argName = "mute-nopc", option = "z", description = "Mute if program change is not found")
    boolean muteNoPc;

    @Option(argName = "level", option = "l", description = "Total level in dB")
    float level= -12.0f;
    @Option(argName = "limiter", option = "c", description = "player.Limiter threshold in dB")
    double limiter = -6.0;
    @Option(argName = "ignore", option = "n", description = "Ignore specified MIDI channel")
    int ignore;
    @Option(argName = "solo", option = "s",description = "Accept only specified MIDI sim.channel")
    int solo;
    @Option(argName = "dump", option = "d", description = "Dump MIDI sim.channel")
    int dump;
    @Option(argName = "print", option = "p",description = "Print status")
    boolean print;

    Consumer<Context> y = (ctx) -> {
        var args = ctx.Args();
        var midiDevice = "";
        if (1 <= ctx.NArg()) {
            midiDevice = args[0];
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
        var opts = new ControllerOpts() {{
            registers = regs;
            library = lib.get().programs.get(0);
            muteIfPCNotFound = muteNoPc;
            forceMono = mono;
            printStatus = print;
            ignoreMIDIChannels = new ArrayList<>();
            soloMIDIChannel = dumpMIDIChannel;
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
        try (var seq = new Sequencer(midiDevice, opts)) {
            renderer.start(chip.next(), seq.flushMIDIMessages);
            try { Thread.sleep(24 * 60 * 60 * 1000); } catch (InterruptedException ignore) {}
        }
    };

    public static void main(String[] args) {
        var app = new Main();
        Options.Util.bind(args, app);
//        app.Name = "fmfm-cli";
//        app.HelpName = "fmfm-cli";
//        app.Commands = new Command[] { midiCmd, listCmd };
//        app.Action = (cli.Context ctx) -> {
//            cli.ShowAppHelp(ctx);
//        };
//        app.Run(os.Args);
    }
}
