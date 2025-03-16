/*
 * https://github.com/but80/fmfm.core
 */

package vavi.sound.yamaha.ma.cmd.cli.internal.player;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiDevice.Info;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequence;

import vavi.sound.yamaha.ma.fmfm.Controller;
import vavi.sound.yamaha.ma.fmfm.Controller.ControllerOpts;
import vavi.sound.yamaha.ma.fmfm.Controller.MIDIMessage;

import static java.lang.System.getLogger;
import static vavi.sound.yamaha.ma.fmfm.Controller.MIDIMessage.MIDIControlChange;
import static vavi.sound.yamaha.ma.fmfm.Controller.MIDIMessage.MIDINoteOff;
import static vavi.sound.yamaha.ma.fmfm.Controller.MIDIMessage.MIDINoteOn;
import static vavi.sound.yamaha.ma.fmfm.Controller.MIDIMessage.MIDIPitchBend;
import static vavi.sound.yamaha.ma.fmfm.Controller.MIDIMessage.MIDIProgramChange;


public class Sequencer implements AutoCloseable {

    private static final Logger logger = getLogger(Sequencer.class.getName());

    static final String defaultMIDIDeviceName = "IAC YAMAHA Virtual MIDI Device 0";

    // The Sequencer receives MIDI messages via PortMIDI and controls the Chip's registers.
    // TODO: rename
    Controller fmfm;
    Sequence input;

    Sequencer() {}

    // NewSequencer creates a new Sequencer.
    public Sequencer(String midiDevice, ControllerOpts opts) throws MidiUnavailableException, InvalidMidiDataException {
        if (midiDevice.equals("@")) {
            midiDevice = defaultMIDIDeviceName;
        }

        Info /* portmidi.DeviceID */ selectedMIDIDeviceID = null;

        if (midiDevice.isEmpty()) {
            boolean found;
            try {
                selectedMIDIDeviceID = MidiSystem.getSynthesizer().getDeviceInfo();
            } catch (MidiUnavailableException e) {
                throw new IllegalStateException("No default MIDI device found");
            }
        } else {
            boolean found = false;
            Info[] infos = MidiSystem.getMidiDeviceInfo();
            for (Info info : infos) {
                MidiDevice device;
                try {
                    device = MidiSystem.getMidiDevice(info);
                    if (device.getMaxTransmitters() == 0) {
                        continue;
                    }
                    if (device.isOpen() && info.getName().equals(midiDevice)) {
                        selectedMIDIDeviceID = device.getDeviceInfo();
                        found = true;
                        break;
                    }
                } catch (MidiUnavailableException e) {
logger.log(Level.ERROR, e.getMessage(), e);
                }
            }

            for (var info : infos) {
                var deviceID = MidiSystem.getMidiDevice(info);
            }
            if (!found) {
                throw new IllegalStateException("No such MIDI device found: " + midiDevice);
            }
        }

        var info = MidiSystem.getMidiDevice(selectedMIDIDeviceID);
        System.err.printf("MIDI device: %s > %s\n", info.getReceivers(), info.getDeviceInfo().getName());

        var input = new Sequence(/* selectedMIDIDeviceID */ Sequence.PPQ, 512, 1);

        var seq = new Sequencer() {{
            fmfm = new Controller(opts);
            this.input = input;
        }};

        Executors.newSingleThreadExecutor().submit(() -> {
            for (int i = 0; i < this.input.getTracks()[0].size(); i++) {
                var e = this.input.getTracks()[0].get(i);
                if (e.getTick() < 0) {
                    continue;
                }
                var msg = e.getMessage();
                var status = msg.getStatus();
                var channel = status & 15;
                MIDIMessage typ = null;
                switch (status & 0xf0) {
                    case 0x90:
                        typ = MIDINoteOn;
                    case 0x80:
                        typ = MIDINoteOff;
                    case 0xb0:
                        typ = MIDIControlChange;
                    case 0xc0:
                        typ = MIDIProgramChange;
                    case 0xe0:
                        typ = MIDIPitchBend;
                }
                seq.fmfm.PushMIDIMessage(typ, (int) (e.getTick()), channel, msg.getMessage()[0], msg.getMessage()[1]);
            }
        });
    }

    // Close ends reception of MIDI messages.
    @Override
    public void close() {
    }

    // ListMIDIDevices gets a list of MIDI devices that can be selected as input.
    String[] ListMIDIDeivces() throws MidiUnavailableException {
        List<String> result = new ArrayList<>();
        Info[] infos = MidiSystem.getMidiDeviceInfo();
        for (Info info : infos) {
            var device = MidiSystem.getMidiDevice(info);
            if (device.isOpen() && !info.getName().isEmpty()) {
                result.add(info.getName());
            }
        }
        return result.toArray(String[]::new);
    }
}
