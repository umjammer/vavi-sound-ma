/*
 * https://github.com/but80/fmfm.core
 */

package vavi.sound.yamaha.ma.fmfm;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import vavi.sound.yamaha.ma.sim.Registers;
import vavi.sound.yamaha.ma.ymf.Register.ChRegister;
import vavi.sound.yamaha.ma.ymf.Register.OpRegister;
import vavi.sound.yamaha.smaf.enums.Enums.Algorithm;
import vavi.sound.yamaha.smaf.enums.Enums.BasicOctave;
import vavi.sound.yamaha.smaf.enums.Enums.Multiplier;
import vavi.sound.yamaha.smaf.enums.Enums.Panpot;
import vavi.sound.yamaha.smaf.enums.Enums.VoiceType;
import vavi.sound.yamaha.smaf.voice.VM35FMVoice;
import vavi.sound.yamaha.smaf.voice.VM35FMVoice.VM35FMOperator;
import vavi.sound.yamaha.smaf.voice.VM35Voice.VM35FMVoiceVersion;
import vavi.sound.yamaha.smaf.voice.VM35VoicePC;
import vavi.sound.yamaha.smaf.voice.VM5VoiceLib;

import static vavi.sound.yamaha.ma.ymf.Register.ChRegister.ALG;
import static vavi.sound.yamaha.ma.ymf.Register.ChRegister.BLOCK;
import static vavi.sound.yamaha.ma.ymf.Register.ChRegister.BO;
import static vavi.sound.yamaha.ma.ymf.Register.ChRegister.CHPAN;
import static vavi.sound.yamaha.ma.ymf.Register.ChRegister.EXPRESSION;
import static vavi.sound.yamaha.ma.ymf.Register.ChRegister.FNUM;
import static vavi.sound.yamaha.ma.ymf.Register.ChRegister.KON;
import static vavi.sound.yamaha.ma.ymf.Register.ChRegister.LFO;
import static vavi.sound.yamaha.ma.ymf.Register.ChRegister.PANPOT;
import static vavi.sound.yamaha.ma.ymf.Register.ChRegister.RESET;
import static vavi.sound.yamaha.ma.ymf.Register.ChRegister.VELOCITY;
import static vavi.sound.yamaha.ma.ymf.Register.ChRegister.VOLUME;
import static vavi.sound.yamaha.ma.ymf.Register.OpRegister.AR;
import static vavi.sound.yamaha.ma.ymf.Register.OpRegister.DAM;
import static vavi.sound.yamaha.ma.ymf.Register.OpRegister.DR;
import static vavi.sound.yamaha.ma.ymf.Register.OpRegister.DT;
import static vavi.sound.yamaha.ma.ymf.Register.OpRegister.DVB;
import static vavi.sound.yamaha.ma.ymf.Register.OpRegister.EAM;
import static vavi.sound.yamaha.ma.ymf.Register.OpRegister.EVB;
import static vavi.sound.yamaha.ma.ymf.Register.OpRegister.FB;
import static vavi.sound.yamaha.ma.ymf.Register.OpRegister.KSL;
import static vavi.sound.yamaha.ma.ymf.Register.OpRegister.KSR;
import static vavi.sound.yamaha.ma.ymf.Register.OpRegister.MULT;
import static vavi.sound.yamaha.ma.ymf.Register.OpRegister.RR;
import static vavi.sound.yamaha.ma.ymf.Register.OpRegister.SL;
import static vavi.sound.yamaha.ma.ymf.Register.OpRegister.SR;
import static vavi.sound.yamaha.ma.ymf.Register.OpRegister.TL;
import static vavi.sound.yamaha.ma.ymf.Register.OpRegister.WS;
import static vavi.sound.yamaha.ma.ymf.Register.OpRegister.XOF;
import static vavi.sound.yamaha.ma.ymf.ymfdata.Data.A3Freq;
import static vavi.sound.yamaha.ma.ymf.ymfdata.Data.A3Note;
import static vavi.sound.yamaha.ma.ymf.ymfdata.Data.CarrierMatrix;
import static vavi.sound.yamaha.ma.ymf.ymfdata.Data.ChannelCount;
import static vavi.sound.yamaha.ma.ymf.ymfdata.Data.FNUMCoef;
import static vavi.sound.yamaha.smaf.enums.Enums.VoiceType.FM;
import static vavi.sound.yamaha.smaf.voice.VM35Voice.VM35FMVoiceVersion.VM5;


public class Controller {

    static final VM35VoicePC defaultPC;

    static {
        defaultPC = new VM35VoicePC();
        defaultPC.version = VM35FMVoiceVersion.values()[VM5.ordinal()];
        defaultPC.name = "default";
        defaultPC.voiceType = VoiceType.values()[FM.ordinal()];
        defaultPC.voice = new VM35FMVoice();
        ((VM35FMVoice) defaultPC.voice).panpot = Panpot._15;
        ((VM35FMVoice) defaultPC.voice).bo = BasicOctave.Normal;
        ((VM35FMVoice) defaultPC.voice).alg = Algorithm.A0;
        ((VM35FMVoice) defaultPC.voice).lfo = 2;
        VM35FMVoice.VM35FMOperator op1 = new VM35FMVoice.VM35FMOperator();
        op1.multi = Multiplier._1;
        op1.ar = 15;
        op1.dr = 4;
        op1.sl = 15;
        op1.rr = 12;
        op1.tl = 12;
        op1.ksl = 2;
        op1.dvb = 3;
        VM35FMOperator op2 = new VM35FMOperator();
        op2.multi = Multiplier._1;
        op2.ar = 15;
        op2.rr = 12;
        op2.dvb = 3;
        ((VM35FMVoice) defaultPC.voice).operators = List.of(op1, op2);
    }

    public Controller() {}

    /** MIDIMessage is an enumeration type that represents a type of MIDI message. */
    public enum MIDIMessage {
        /** NoteOn is an enumerator that represents the MIDI message type NoteOn. */
        NoteOn(1),
        /** NoteOff is an enumerator that represents the MIDI message type NoteOff. */
        NoteOff(2),
        /** ControlChange is an enumerator that represents the MIDI message type ControlChange. */
        ControlChange(3),
        /** ProgramChange is an enumerator that represents the MIDI message type ProgramChange. */
        ProgramChange(4),
        /** PitchBend is an enumerator that represents the MIDI message type PitchBend. */
        PitchBend(5);
        final int v;

        MIDIMessage(int v) {
            this.v = v;
        }
    }

    static class midiMessage {

        MIDIMessage typ;
        int timestamp;
        int midiChannel;
        int data1;
        int data2;
    }

    // type flag int
    static final int flagSustain = 0x02;
    static final int flagVibrato = 0x04;
    static final int flagReleased = 0x40;
    static final int flagFree = 0x80;

    static final int modThresh = 40;

    static final int ccBankMSB = 0;
    static final int ccModulation = 1;
    static final int ccDataEntryHi = 6;
    static final int ccVolume = 7;
    static final int ccPan = 10;

    static final int ccExpression = 11;
    static final int ccBankLSB = 32;
    static final int ccDataEntryLo = 38;
    static final int ccSustainPedal = 64;
//    static final int ccSoftPedal = 67;
//    static final int ccReverb = 91;
//    static final int ccChorus = 93;
    static final int ccNRPNLo = 98;
    static final int ccNRPNHi = 99;

    static final int ccRPNLo = 100;
    static final int ccRPNHi = 101;
    static final int ccSoundsOff = 120;
    static final int ccNotesOff = 123;
    static final int ccMono = 126;
    static final int ccPoly = 127;

    static class chipChannelState {

        int midiChannel;
        int note;
        int realnote;
        int flags;
        int finetune;
        int pitch;
        VM35VoicePC instrument;
        Instant time;
        int minRR;
    }

    static class midiChannelState {

        byte bankLSB;
        byte bankMSB;
        byte pc;
        byte volume;
        byte expression;
        byte pan;
        byte pitch;
        byte sustain;
        byte modulation;
        short pitchSens;
        short rpn;
        boolean mono;
        VM35VoicePC debugLastInstrument;
    }

    /** ControllerOpts are the options for #Controller. */
    public static class ControllerOpts {

        public Registers registers;
        public VM5VoiceLib library;
        protected boolean muteIfPCNotFound;
        protected boolean forceMono;
        protected boolean printStatus;
        public List<Integer> ignoreMIDIChannels = new ArrayList<>();
        protected int soloMIDIChannel;
    }

    // The Controller controls the Chip's registers via a MIDI-like interface.
    Object mutex;
    Registers registers;
    VM5VoiceLib library;
    boolean muteIfPCNotFound;
    boolean forceMono;
    boolean debugPrintStatus;
    Map<Integer, Object> ignoreMIDIChannels = new HashMap<>();
    int soloMIDIChannel;
    List<midiMessage> midiMessages;

    midiChannelState[] midiChannelStates = new midiChannelState[16];
    chipChannelState[] chipChannelStates = new chipChannelState[ChannelCount];

    /** Creates a new Controller. */
    public Controller(ControllerOpts opts) {
        this.registers = opts.registers;
        this.library = opts.library;
        this.muteIfPCNotFound = opts.muteIfPCNotFound;
        this.forceMono = opts.forceMono;
        this.debugPrintStatus = opts.printStatus;

        Map<Integer, Object> ignoreMIDIChannels = new HashMap<>();
        soloMIDIChannel = opts.soloMIDIChannel;
        this.midiMessages = new ArrayList<>();
        for (var ch : opts.ignoreMIDIChannels) {
            this.ignoreMIDIChannels.put(ch, null);
        }
        for (var i = 0; i < this.chipChannelStates.length; i++) {
            this.chipChannelStates[i] = new chipChannelState();
        }
        for (var i = 0; i < this.midiChannelStates.length; i++) {
            this.midiChannelStates[i] = new midiChannelState();
        }
        this.reset();
    }

    /** Adds a MIDI message for processing. */
    public synchronized void pushMIDIMessage(MIDIMessage typ, int timestamp, int midich, int data1, int data2) {

        var msg = new midiMessage() {{
            this.typ = typ;
            this.timestamp = timestamp;
            this.midiChannel = midich;
            this.data1 = data1;
            this.data2 = data2;
        }};

        var n = this.midiMessages.size();
        for (var i = n; 0 < i; i--) {
            if (timestamp < this.midiMessages.get(i - 1).timestamp) {
                continue;
            }
            if (i == n) {
                this.midiMessages.add(msg);
            } else {
                this.midiMessages.add(i, msg);
            }
            return;
        }
        this.midiMessages.add(0, msg);
    }

    Instant lastPrintedAt = Instant.now();

    /** Processes any accumulated MIDI messages. */
    public synchronized void FlushMIDIMessages(int until) {

        List<midiMessage> rest = List.of();
        for (var i = 0; i < this.midiMessages.size(); i++) {
            var msg = this.midiMessages.get(i);
            if (until < msg.timestamp) {
                rest = this.midiMessages.subList(i, this.midiMessages.size() - 1);
                break;
            }
            // System.out.printf("%02d: %d\n", msg.midiChannel, until - msg.timestamp)
            switch (msg.typ) {
                case NoteOn:
                    this.noteOn(msg.midiChannel, msg.data1, msg.data2);
                case NoteOff:
                    this.noteOff(msg.midiChannel, msg.data1);
                case ControlChange:
                    this.controlChange(msg.midiChannel, msg.data1, msg.data2);
                case ProgramChange:
                    this.programChange(msg.midiChannel, msg.data1);
                case PitchBend:
                    this.pitchBend(msg.midiChannel, msg.data1, msg.data2);
            }
        }
        this.midiMessages = rest;

        if (this.debugPrintStatus) {
            var now = Instant.now();
            if (now.minus(Duration.ofMillis(10)).compareTo(lastPrintedAt) < 0) {
                this.printStatus();
                lastPrintedAt = now;
            }
        }
    }

    static final String[] notes = {
            "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"
    };

    void printStatus() {
        System.out.println();
        System.out.println("Ch MSB-LSB-@PC Instrument       P Vol Exp Pan Vo Note");
        for (var i = 0; i<  this.midiChannelStates.length; i++) {
            var ms = this.midiChannelStates[i];
            var cs = new chipChannelState();
            var voices = 0;
            var lastTime = Instant.now();
            for (var s : this.chipChannelStates) {
                if (s.midiChannel == i) {
                    voices++;
                    if (s.time.isAfter(lastTime)) {
                        cs = s;
                        lastTime = s.time;
                    }
                }
            }

            var monopoly = "-";
            var note = "";
            var instr = ms.debugLastInstrument;
            var pc = "-----------";
            if (instr == null || instr == defaultPC) {
                instr = new VM35VoicePC();
            } else {
                if (ms.mono) {
                    monopoly = "M";
                } else {
                    monopoly = "P";
                }
                pc = "%03d-%03d-%03d".formatted(ms.bankMSB, ms.bankLSB, ms.pc);
                note = "%s%d".formatted(notes[cs.note % 12], cs.note / 12 - 2);
            }
            if (voices == 0) {
                note = "";
            }
            System.out.printf("%2d %s %-16s %s %3d %3d %3d %2d %-4s\n",
                    i + 1,
                    pc,
                    instr.name,
                    monopoly,
                    ms.volume,
                    ms.expression,
                    ms.pan,
                    voices,
                    note
            );
        }
    }

    /** Reproduces the behavior of a sound source when receiving a MIDI note-on. */
    void noteOn(int midich, int note, int velocity) {
        if (velocity == 0) {
            this.noteOff(midich, note);
            return;
        }
        if (this.ignoreMIDIChannels.get(midich) != null) {
            return;
        }

        var instr = this.getInstrument(midich, note);
        if (instr == null) {
            return;
        }

        if (instr.voiceType.ordinal() != FM.ordinal()) {
            System.out.printf("unsupported voice type: @%d-%d-%d note=%d type=%s\n", instr.bankMSB, instr.bankLSB, instr.pc, note, instr.voiceType);
            return;
        }

        var chipch = -1;
        if (this.midiChannelStates[midich].mono || this.forceMono && !instr.isForDrum()) {
            chipch = this.findLastUsedChipChannel(midich, note);
        }
        if (chipch < 0) {
            chipch = this.findFreeChipChannel(midich, note);
        }
        if (0 <= chipch) {
            this.occupyChipChannel(chipch, midich, note, velocity, instr);
        } else {
            System.out.printf("no free chip channel for MIDI channel #%d\n", midich);
        }
    }

    /** Reproduces the behavior of a sound source when receiving a MIDI note-off. */
    void noteOff(int midich, int note) {
        if (this.ignoreMIDIChannels.get(midich) != null) {
            return;
        }

        var sus = this.midiChannelStates[midich].sustain;
        for (var chipch = 0; chipch < this.chipChannelStates.length; chipch++) {
            var state = this.chipChannelStates[chipch];
            if (state.midiChannel == midich && state.note == note) {
                if (sus < 0x40) {
                    this.keyOff(chipch);
                } else {
                    state.flags |= flagSustain;
                }
            }
        }
    }

    /** Reproduces the behavior of a sound source when receiving a MIDI control change. */
    void controlChange(int midich, int cc, int value) {
        if (this.ignoreMIDIChannels.get(midich) != null) {
            return;
        }
        var channel = this.midiChannelStates[midich];

        switch (cc) {
            case ccBankMSB:
                channel.bankMSB = (byte) value;
            case ccBankLSB:
                channel.bankLSB = (byte) value;
            case ccModulation:
                channel.modulation = (byte) value;
                for (var i = 0; i < this.chipChannelStates.length; i++) {
                    var state = this.chipChannelStates[i];
                    if (state.midiChannel == midich) {
                        var flags = state.flags;
                        if (modThresh <= value) {
                            state.flags |= flagVibrato;
                            if (state.flags != flags) {
                                this.writeModulation(i, state.instrument, true);
                            }
                        } else {
                            state.flags &= ~flagVibrato;
                            if (state.flags != flags) {
                                this.writeModulation(i, state.instrument, false);
                            }
                        }
                    }
                }

            case ccVolume: // change volume
                channel.volume = (byte) (value);
                if (this.soloMIDIChannel < 0 || midich == this.soloMIDIChannel) {
                    this.writeChannelsUsingMIDIChannel(midich, VOLUME, value);
                }

            case ccExpression: // change expression
                channel.expression = (byte) (value);
                this.writeChannelsUsingMIDIChannel(midich, EXPRESSION, value);

            case ccPan: // change pan (balance)
                channel.pan = (byte) (value);
                this.writeChannelsUsingMIDIChannel(midich, CHPAN, value);

            case ccSustainPedal: // change sustain pedal (hold)
                channel.sustain = (byte) (value);
                if (value < 0x40) {
                    this.releaseSustain(midich);
                }

            case ccMono:
                channel.mono = true;

            case ccPoly:
                channel.mono = false;

            case ccNotesOff: // turn off all notes that are not sustained
                for (var i = 0; i < this.chipChannelStates.length; i++) {
                    var state = this.chipChannelStates[i];
                    if (state.midiChannel == midich) {
                        if (channel.sustain < 0x40) {
                            this.keyOff(i);
                        } else {
                            state.flags |= flagSustain;
                        }
                    }
                }

            case ccSoundsOff: // release all notes for this channel
                for (var i = 0; i < this.chipChannelStates.length; i++) {
                    var state = this.chipChannelStates[i];
                    if (state.midiChannel == midich) {
                        this.keyOff(i);
                    }
                }

            case ccRPNHi:
                channel.rpn = (short) ((channel.rpn & 0x007f) | ((short) (value) << 7));

            case ccRPNLo:
                channel.rpn = (short) ((channel.rpn & 0x3f80) | (short) (value));

            case ccNRPNLo, ccNRPNHi:
                channel.rpn = 0x3fff;

            case ccDataEntryHi:
                if (channel.rpn == 0) {
                    channel.pitchSens = (short) ((value) * 100 + (channel.pitchSens % 100));
                }

            case ccDataEntryLo:
                if (channel.rpn == 0) {
                    channel.pitchSens = (short) ((value) + (short) (channel.pitchSens / 100) * 100);
                }
        }
    }

    /** Reproduces the behavior of a sound source when receiving a MIDI program change. */
    void programChange(int midich, int pc) {
        if (this.ignoreMIDIChannels.get(midich) != null) {
            return;
        }
        this.midiChannelStates[midich].pc = (byte) (pc);
    }

    /** Reproduces the behavior of a sound source when receiving MIDI pitch bend. */
    void pitchBend(int midich, int l, int h) {
        if (this.ignoreMIDIChannels.get(midich) != null) {
            return;
        }

        var pitch = h * 128 + l - 8192;
        pitch = (int) ((double) (pitch) * (double) (this.midiChannelStates[midich].pitchSens) / (200 * 128) + 64);
        this.midiChannelStates[midich].pitch = (byte) (pitch);
        for (var i = 0; i < this.chipChannelStates.length; i++) {
            var state = this.chipChannelStates[i];
            if (state.midiChannel == midich) {
                state.pitch = state.finetune + pitch;
                this.writeFrequency(i, state.realnote, state.pitch);
            }
        }
    }

    /** Resets the state of the sound source. */
    synchronized void reset() {
        for (var i = 0; i < this.chipChannelStates.length; i++) {
            this.resetChipChannel(i);
        }
        for (var i = 0; i < this.midiChannelStates.length; i++) {
            this.resetMIDIChannel(i);
        }
    }

    void writeModulation(int chipch, VM35VoicePC instr, boolean state) {
        // TODO: Does the modulator only look at evb (ignoring state)?
        for (var i = 0; i < ((VM35FMVoice) instr.voice).operators.size(); i++) {
            var o = ((VM35FMVoice) instr.voice).operators.get(i);
            this.registers.writeOperator(chipch, i, EVB, boolean2int(o.evb || state));
        }
    }

    void occupyChipChannel(int chipch, int midich, int note, int velocity, VM35VoicePC instr) {
        var midiState = this.midiChannelStates[midich];
        var chipState = this.chipChannelStates[chipch];
        chipState.midiChannel = midich;
        chipState.note = note;
        chipState.flags = 0;
        if (modThresh <= midiState.modulation) {
            chipState.flags |= flagVibrato;
        }
        chipState.time = Instant.now();

        chipState.finetune = 0;
        if (instr.isForDrum()) {
            note = ((VM35FMVoice) instr.voice).drumKey.note;
        }
        chipState.pitch = chipState.finetune + (int) (midiState.pitch);
        chipState.instrument = instr;
        midiState.debugLastInstrument = instr;
        chipState.realnote = note;

        chipState.minRR = 15;
        for (var i = 0; i < ((VM35FMVoice) instr.voice).operators.size(); i++) {
            var op = ((VM35FMVoice) instr.voice).operators.get(i);
            var isCarrier = CarrierMatrix[((VM35FMVoice) instr.voice).alg.ordinal()][i];
            if (isCarrier && op.rr < chipState.minRR) {
                chipState.minRR = op.rr;
            }
        }

        this.writeInstrument(chipch, instr);
        this.writeModulation(chipch, instr, (chipState.flags & flagVibrato) != 0);
        this.registers.writeChannel(chipch, CHPAN, (int) (this.midiChannelStates[midich].pan));
        if (this.soloMIDIChannel < 0 || midich == this.soloMIDIChannel) {
            this.registers.writeChannel(chipch, VOLUME, (int) (this.midiChannelStates[midich].volume));
        } else {
            this.registers.writeChannel(chipch, VOLUME, 0);
        }
        this.registers.writeChannel(chipch, EXPRESSION, (int) (this.midiChannelStates[midich].expression));
        this.registers.writeChannel(chipch, VELOCITY, velocity);
        this.writeFrequency(chipch, note, chipState.pitch);
        this.keyOn(chipch, midich);
    }

    void resetChipChannel(int chipch) {
        var state = this.chipChannelStates[chipch];
        state.time = Instant.now();
        state.flags = flagReleased | flagFree;
        state.minRR = 15;
        state.instrument = null;
        state.midiChannel = -1;
        // state.note = 0
        // state.realnote = 0
        // state.finetune = 0
        // state.pitch = 0
        this.registers.writeChannel(chipch, RESET, 1);
    }

    void releaseSustain(int midich) {
        for (var i = 0; i < this.chipChannelStates.length; i++) {
            var state = this.chipChannelStates[i];
            if (state.midiChannel == midich && (state.flags & flagSustain) != 0) {
                this.keyOff(i);
            }
        }
    }

    // findLastUsedChipChannel selects the channel of the chip to which the specified note
    // on the specified MIDI channel will be assigned in MONO mode.
    int findLastUsedChipChannel(int midich, int note) {
        var now = Instant.now();
        var found = -1;
        var minDelta = Long.MAX_VALUE;
        for (var i = 0; i < this.chipChannelStates.length; i++){
            var state = this.chipChannelStates[i];
            if (state.midiChannel != midich) {
                continue;
            }
            if (state.note == note) {
                return i;
            }
            var delta = Duration.between(state.time, now).toMillis() * state.minRR;
            if (delta < minDelta) {
                minDelta = delta;
                found = i;
            }
        }
        if (0 <= found) {
            return found;
        }
        return -1;
    }

    // findLastUsedChipChannel selects the channel of the chip to which a specified note
    // on a specified MIDI channel will be assigned in POLY mode.
    int findFreeChipChannel(int midich, int note) {
//        // If there is a channel with the same note already played, it will be selected with the highest priority.
//        for (i, state : this.chipChannelStates) {
//            if state.midiChannel == midich && state.note == note {
//                return i
//            }
//        }

        // Select a silent channel if there is one
        for (var i = 0; i < this.chipChannelStates.length; i++) {
            var state = this.chipChannelStates[i];
            if ((state.flags & flagFree) != 0) {
                return i;
            }
        }

        var now = Instant.now();
        var foundTotal = -1;
        var foundReleased = -1;
        var maxDeltaTotal = -1;
        var maxAttenuationReleased = -1;
        for (var i = 0; i < this.chipChannelStates.length; i++) {
            var state = this.chipChannelStates[i];
            var delta = (int) Duration.between(state.time, now).toMillis();
            if (maxDeltaTotal < delta) {
                maxDeltaTotal = delta;
                foundTotal = i;
            }
            // -dB ∝ 2^RR * time
            var attenuation = delta * (1 << state.minRR);
            if (maxAttenuationReleased < attenuation && (state.flags & flagReleased) != 0) {
                maxAttenuationReleased = attenuation;
                foundReleased = i;
            }
        }

        // Select the channel that is most likely to be attenuated after release
        if (0 <= foundReleased) {
            this.resetChipChannel(foundReleased);
            return foundReleased;
        }
        // Select the channel that is unreleased but is considered the oldest.
        if (0 <= foundTotal) {
            this.resetChipChannel(foundTotal);
            return foundTotal;
        }

        // No place to accommodate
        return -1;
    }

	    VM35VoicePC getInstrument(int midich, int note) {
	        var s = this.midiChannelStates[midich];
	        var result = this.library.get(s.bankMSB, s.bankLSB, s.pc, note);
	        if (result == null && !this.muteIfPCNotFound) {
	            result = defaultPC;
	        }
	        return result;
	    }
    void resetMIDIChannel(int midich) {
        this.midiChannelStates[midich].volume = 100;
        this.midiChannelStates[midich].expression = 127;
        this.midiChannelStates[midich].pan = 64;
        this.midiChannelStates[midich].sustain = 0;
        this.midiChannelStates[midich].pitch = 64;
        this.midiChannelStates[midich].rpn = 0x3fff;
        this.midiChannelStates[midich].pitchSens = 200;
    }

    void writeChannelsUsingMIDIChannel(int midich, ChRegister regbase, int value) {
        for (var i = 0; i < this.chipChannelStates.length; i++) {
            var state = this.chipChannelStates[i];
            if (state.midiChannel == midich) {
                this.registers.writeChannel(i, regbase, value);
            }
        }
    }

    void writeAllOperators(int chipch, OpRegister regbase, int value) {
        this.registers.writeOperator(chipch, 0, regbase, value);
        this.registers.writeOperator(chipch, 1, regbase, value);
        this.registers.writeOperator(chipch, 2, regbase, value);
        this.registers.writeOperator(chipch, 3, regbase, value);
    }

    void writeFrequency(int chipch, int note, int pitch) {
        var n = (double)(note - A3Note) + (double) (pitch - 64) / 32.0;
        var freq = A3Freq * Math.pow(2.0, n / 12.0);

        var block = (note + 3 - 12) / 12;
        if (block < 0) {
            block = 0;
        } else if (7 < block) {
            block = 7;
        }

        var fnumF64 = freq * FNUMCoef;
        var blockUint = (int) (block);
        var fnum = (int) (fnumF64 * 2.0 + (double) ((int) (1) << blockUint >> 1)) >> blockUint;
        if (fnum < 0) {
            fnum = 0;
        } else {
            while (1024 < fnum) {
                block++;
                fnum >>= 1;
            }
        }
        if (block < 0) {
            block = 0;
        } else if (7 < block) {
            block = 7;
        }

        this.registers.writeChannel(chipch, FNUM, fnum);
        this.registers.writeChannel(chipch, BLOCK, block);
    }

    void keyOn(int chipch, int midich) {
        this.registers.debugSetMIDIChannel(chipch, midich);
        this.registers.writeChannel(chipch, KON, 1);
    }

    void keyOff(int chipch) {
        var state = this.chipChannelStates[chipch];
        state.time = Instant.now();
        state.flags = flagReleased;
        this.registers.writeChannel(chipch, KON, 0);
    }

    static int boolean2int(boolean b) {
        if (b) {
            return 1;
        }
        return 0;
    }

    void writeInstrument(int chipch, VM35VoicePC instr) {
        this.writeAllOperators(chipch, TL, 0x3f); // no volume

        for (var i = 0; i < ((VM35FMVoice) instr.voice).operators.size(); i++) {
            var op = ((VM35FMVoice) instr.voice).operators.get(i);
            this.registers.writeOperator(chipch, i, EAM, boolean2int(op.eam));
            this.registers.writeOperator(chipch, i, EVB, boolean2int(op.evb));
            this.registers.writeOperator(chipch, i, DAM, op.dam);
            this.registers.writeOperator(chipch, i, DVB, op.dvb);
            this.registers.writeOperator(chipch, i, DT, op.DT);
            this.registers.writeOperator(chipch, i, KSL, op.ksl);
            this.registers.writeOperator(chipch, i, KSR, boolean2int(op.ksr));
            this.registers.writeOperator(chipch, i, WS, op.ws);
            this.registers.writeOperator(chipch, i, MULT, op.multi.ordinal());
            this.registers.writeOperator(chipch, i, FB, op.fb);
            this.registers.writeOperator(chipch, i, AR, op.ar);
            this.registers.writeOperator(chipch, i, DR, op.dr);
            this.registers.writeOperator(chipch, i, SL, op.sl);
            this.registers.writeOperator(chipch, i, SR, op.sr);
            this.registers.writeOperator(chipch, i, RR, op.rr);
            this.registers.writeOperator(chipch, i, TL, op.tl);
            this.registers.writeOperator(chipch, i, XOF, boolean2int(op.xof));
        }

        this.registers.writeChannel(chipch, ALG, (int) (((VM35FMVoice) instr.voice).alg.ordinal()));
        this.registers.writeChannel(chipch, LFO, (int) (((VM35FMVoice) instr.voice).lfo));
        this.registers.writeChannel(chipch, PANPOT, (int) (((VM35FMVoice) instr.voice).panpot.ordinal()));
        this.registers.writeChannel(chipch, BO, (int) (((VM35FMVoice) instr.voice).bo.ordinal()));
    }
}
