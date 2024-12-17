/*
 * https://github.com/but80/fmfm.core
 */

package vavi.sound.yamaha.ma.cmd.cli.internal.player;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

import vavi.sound.yamaha.ma.fmfm.Controller;
import vavi.sound.yamaha.ma.fmfm.Controller.ControllerOpts;
import vavi.sound.yamaha.ma.fmfm.Controller.MIDIMessage;

import static vavi.sound.yamaha.ma.fmfm.Controller.MIDIMessage.MIDIControlChange;
import static vavi.sound.yamaha.ma.fmfm.Controller.MIDIMessage.MIDINoteOff;
import static vavi.sound.yamaha.ma.fmfm.Controller.MIDIMessage.MIDINoteOn;
import static vavi.sound.yamaha.ma.fmfm.Controller.MIDIMessage.MIDIPitchBend;
import static vavi.sound.yamaha.ma.fmfm.Controller.MIDIMessage.MIDIProgramChange;


public class Sequencer {

    static final String defaultMIDIDeviceName = "IAC YAMAHA Virtual MIDI Device 0";

    // Sequencer は、PortMIDI により MIDIメッセージを受信して Chip のレジスタをコントロールします。
    // TODO: rename
    Controller fmfm;
    Stream input;

    var newSequencerOnce = sync.Once;

    // NewSequencer は、新しい Sequencer を作成します。
    Sequencer(String midiDevice, ControllerOpts opts) {
        if (midiDevice.equals("@")) {
            midiDevice = defaultMIDIDeviceName;
        }

        newSequencerOnce.Do(() -> {
            portmidi.Initialize();
            if (portmidi.CountDevices() < 1) {
                panic("no midi device");
            }
        });

        portmidi.DeviceID selectedMIDIDeviceID;

        if (midiDevice.isEmpty()) {
            boolean found;
            selectedMIDIDeviceID, found = portmidi.DefaultInputDeviceID();
            if (!found) {
                throw new IllegalStateException("No default MIDI device found");
            }
        } else {
            boolean found = false;
            for (var i = 0; i < portmidi.CountDevices(); i++) {
                var deviceID = portmidi.DeviceID(i);
                var info = portmidi.GetDeviceInfo(deviceID);
                if (info.IsInputAvailable && info.Name == midiDevice) {
                    selectedMIDIDeviceID = deviceID;
                    found = true;
                    break;
                }
            }
            if (!found) {
                throw new IllegalStateException("No such MIDI device found: " + midiDevice);
            }
        }

        var info = portmidi.GetDeviceInfo(selectedMIDIDeviceID);
        System.err.printf("MIDI device: %s > %s\n", info.Interface, info.Name);

        var input = portmidi.NewInputStream(selectedMIDIDeviceID, 512, 0);

        var seq = new Sequencer() {{
            Controller = new Controller(opts);
            input = input;
        }};

        try (Executors.newSingleThreadExecutor().submit(() -> {
            for (var e : this.input.Source()) {
                if (e.Timestamp < 0) {
                    continue;
                }
                var msg = portmidi.Message(e.Message);
                var status = (int) (msg.Status());
                var channel = (int) (status & 15);
                MIDIMessage typ;
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
                this.PushMIDIMessage(typ, (int) (e.Timestamp), channel, (int) (msg.Data1()), (int) (msg.Data2()));
            }
        })) {
        }
    }

    // Close は、MIDIメッセージの受信を終了します。
    void Close() {
        this.input.Close();
    }

    // ListMIDIDeivces は、入力として選択可能なMIDIデバイスの一覧を取得します。
    String[] ListMIDIDeivces() {
        List<String> result = new ArrayList<>();
        for (var i = 0; i < portmidi.CountDevices(); i++) {
            var deviceID = portmidi.DeviceID(i);
            var info = portmidi.GetDeviceInfo(deviceID);
            if (info.IsInputAvailable && info.Name != "") {
                result.add(info.Name);
            }
        }
        return result.toArray(String[]::new);
    }
}
