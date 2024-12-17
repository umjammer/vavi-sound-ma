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
import vavi.sound.yamaha.smaf.voice.VM35FMOperator;
import vavi.sound.yamaha.smaf.voice.VM35FMVoice;
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
import static vavi.sound.yamaha.ma.ymf.ymfdata.Data.ChannelCount;
import static vavi.sound.yamaha.smaf.enums.Enums.VoiceType.VoiceType_FM;
import static vavi.sound.yamaha.smaf.voice.VM35FMVoiceVersion.VM35FMVoiceVersion_VM5;


public class Controller {

    VM35VoicePC defaultPC = new VM35VoicePC() {{
        var Version = VM35FMVoiceVersion_VM5;
        var Name = "default";
        var VoiceType = VoiceType_FM;
        var FmVoice = new VM35FMVoice() {{
            Panpot = 15;
            Bo = 1;
            Alg = 0;
            Lfo = 2;
            Operators = {
                    new VM35FMOperator() {{
                        Multi = 1;
                        Ar = 15;
                        Dr = 4;
                        Sl = 15;
                        Rr = 12;
                        Tl = 12;
                        Ksl = 2;
                        Dvb = 3;
                    }},
                    new VM35FMOperator() {{
                        Multi = 1;
                        Ar = 15;
                        Rr = 12;
                        Dvb = 3;
                    }},
            };
        }};
    }};

    // MIDIMessage は、MIDIメッセージの種類を表す列挙子型です。
    public enum MIDIMessage {
        // MIDINoteOn は、MIDIメッセージの種類 NoteOn を表す列挙子です。
        MIDINoteOn(1),
        // MIDINoteOff は、MIDIメッセージの種類 NoteOff を表す列挙子です。
        MIDINoteOff(2),
        // MIDIControlChange は、MIDIメッセージの種類 ControlChange を表す列挙子です。
        MIDIControlChange(3),
        // MIDIProgramChange は、MIDIメッセージの種類 ProgramChange を表す列挙子です。
        MIDIProgramChange(4),
        // MIDIPitchBend は、MIDIメッセージの種類 PitchBend を表す列挙子です。
        MIDIPitchBend(5);
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

    //    type flag int
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
    //	static final int ccSoftPedal = 67;
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

    // ControllerOpts は、 NewController のオプションです。
    public static class ControllerOpts {

        Registers Registers;
        VM5VoiceLib Library;
        boolean MuteIfPCNotFound;
        boolean ForceMono;
        boolean PrintStatus;
        int[] IgnoreMIDIChannels;
        int SoloMIDIChannel;
    }

    // Controller は、MIDIに類似するインタフェースで Chip のレジスタをコントロールします。
    Object mutex;
    Registers registers;
    VM5VoiceLib library;
    boolean muteIfPCNotFound;
    boolean forceMono;
    boolean debugPrintStatus;
    Map<Integer, Object> ignoreMIDIChannels;
    int soloMIDIChannel;
    List<midiMessage> midiMessages;

    midiChannelState[] midiChannelStates = new midiChannelState[16];
    chipChannelState[] chipChannelStates = new chipChannelState[ChannelCount];

    // NewController は、新しい Controller を作成します。
    public Controller(Controller.ControllerOpts opts) {
        this.registers = opts.Registers;
        this.library = opts.Library;
        this.muteIfPCNotFound = opts.MuteIfPCNotFound;
        this.forceMono = opts.ForceMono;
        this.debugPrintStatus = opts.PrintStatus;

        Map<Integer, Object> ignoreMIDIChannels = new HashMap<>();
        soloMIDIChannel = opts.SoloMIDIChannel;
        this.midiMessages = new ArrayList<>();
        for (var ch : opts.IgnoreMIDIChannels) {
            this.ignoreMIDIChannels.put(ch, null);
        }
        for (var i = 0; i < this.chipChannelStates.length; i++) {
            this.chipChannelStates[i] = new chipChannelState();
        }
        for (var i = 0; i < this.midiChannelStates.length; i++) {
            this.midiChannelStates[i] = new midiChannelState();
        }
        this.Reset();
    }

    // PushMIDIMessage は、処理すべきMIDIメッセージを追加します。
    public synchronized void PushMIDIMessage(Controller.MIDIMessage typ, int timestamp, int midich, int data1, int data2) {

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
                this.midiMessages.add(this.midiMessages, msg);
            } else {
                this.midiMessages.add(this.midiMessages[:i + 1],this.midiMessages[i:]...);
                this.midiMessages[i] = msg;
            }
            return;
        }
        this.midiMessages.add(new midiMessage[] { msg }, this.midiMessages...);
    }

    Instant lastPrintedAt = Instant.now();

    // FlushMIDIMessages は、蓄積されたMIDIメッセージを処理します。
    public synchronized void FlushMIDIMessages(int until) {

        List<Controller.midiMessage> rest;
        for (var i = 0; i < this.midiMessages.size(); i++) {
            var msg = this.midiMessages.get(i);
            if (until < msg.timestamp) {
                rest = this.midiMessages[i:];
                break;
            }
            // System.out.printf("%02d: %d\n", msg.midiChannel, until - msg.timestamp)
            switch (msg.typ) {
                case MIDINoteOn:
                    this.noteOn(msg.midiChannel, msg.data1, msg.data2);
                case MIDINoteOff:
                    this.noteOff(msg.midiChannel, msg.data1);
                case MIDIControlChange:
                    this.controlChange(msg.midiChannel, msg.data1, msg.data2);
                case MIDIProgramChange:
                    this.programChange(msg.midiChannel, msg.data1);
                case MIDIPitchBend:
                    this.pitchBend(msg.midiChannel, msg.data1, msg.data2);
            }
        }
        this.midiMessages = rest;

        if (this.debugPrintStatus) {
            var now = Instant.now();
            if (Duration.ofMillis(10) <= now.Sub(lastPrintedAt)) {
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
            var cs = chipChannelState;
            var voices = 0;
            var lastTime = Instant.now();
            for (var s : this.chipChannelStates) {
                if (s.midiChannel == i) {
                    voices++;
                    if (s.time.After(lastTime)) {
                        cs = s;
                        lastTime = s.time;
                    }
                }
            }

            monopoly = "-";
            note = "";
            instr = ms.debugLastInstrument;
            pc = "-----------";
            if (instr == null || instr == smaf.DefaultPC) {
                instr = smaf.VM35VoicePC()
            } else {
                if (ms.mono) {
                    monopoly = "M";
                } else {
                    monopoly = "P";
                }
                pc = fmt.Sprintf("%03d-%03d-%03d", ms.bankMSB, ms.bankLSB, ms.pc);
                note = fmt.Sprintf("%s%d", notes[cs.note % 12], cs.note / 12 - 2);
            }
            if (voices == 0) {
                note = "";
            }
            System.out.printf("%2d %s %-16s %s %3d %3d %3d %2d %-4s\n",
                    i + 1,
                    pc,
                    instr.Name,
                    monopoly,
                    ms.volume,
                    ms.expression,
                    ms.pan,
                    voices,
                    note
            );
        }
    }

    // noteOn は、MIDIノートオン受信時の音源の振る舞いを再現します。
    void noteOn(int midich, int note, int velocity) {
        if (velocity == 0) {
            this.noteOff(midich, note);
            return;
        }
        if (this.ignoreMIDIChannels[midich]) {
            return;
        }

        var instr = this.getInstrument(midich, note);

        if (instr.VoiceType != VoiceType_FM) {
            System.out.printf("unsupported voice type: @%d-%d-%d note=%d type=%s\n", instr.BankMsb, instr.BankLsb, instr.Pc, note, instr.VoiceType);
            return;
        }

        var chipch = -1;
        if (this.midiChannelStates[midich].mono || this.forceMono && DrumNote == 0) {
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

    // noteOff は、MIDIノートオフ受信時の音源の振る舞いを再現します。
    void noteOff(int midich, int note) {
        if (this.ignoreMIDIChannels[midich]) {
            return;
        }

        var sus = this.midiChannelStates[midich].sustain;
        for (chipch, state = range this.chipChannelStates) {
            if (state.midiChannel == midich && state.note == note) {
                if (sus < 0x40) {
                    this.keyOff(chipch);
                } else {
                    state.flags |= flagSustain;
                }
            }
        }
    }

    // controlChange は、MIDIコントロールチェンジ受信時の音源の振る舞いを再現します。
    void controlChange(int midich, int cc, int value) {
        if (this.ignoreMIDIChannels[midich]) {
            return;
        }
        var channel = this.midiChannelStates[midich];

        switch (cc) {
            case ccBankMSB:
                channel.bankMSB = (byte) value
            case ccBankLSB:
                channel.bankLSB = (byte) (value);
            case ccModulation:
                channel.modulation = (byte) (value);
                for (i, state = range this.chipChannelStates) {
                    if (state.midiChannel == midich) {
                        flags = state.flags;
                        if (modThresh <= value) {
                            state.flags |= flagVibrato;
                            if (state.flags != flags) {
                                this.writeModulation(i, state.instrument, true);
                            }
                        } else {
                            state.flags &= ^ flagVibrato;
                            if (state.flags != flags) {
                                this.writeModulation(i, state.instrument, false);
                            }
                        }
                    }
                }

            case ccVolume: // change volume
                channel.volume = (byte) (value);
                if (this.soloMIDIChannel < 0 || midich == this.soloMIDIChannel) {
                    this.writeChannelsUsingMIDIChannel(midich, ymf.VOLUME, value);
                }

            case ccExpression: // change expression
                channel.expression = (byte) (value);
                this.writeChannelsUsingMIDIChannel(midich, ymf.EXPRESSION, value);

            case ccPan: // change pan (balance)
                channel.pan = (byte) (value);
                this.writeChannelsUsingMIDIChannel(midich, ymf.CHPAN, value);

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
                for (var i, state = range this.chipChannelStates) {
                    if (state.midiChannel == midich) {
                        if (channel.sustain < 0x40) {
                            this.keyOff(i);
                        } else {
                            state.flags |= flagSustain;
                        }
                    }
                }

            case ccSoundsOff: // release all notes for this channel
                for (var i, state = range this.chipChannelStates) {
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

    // programChange は、MIDIプログラムチェンジ受信時の音源の振る舞いを再現します。
    void programChange(int midich, int pc) {
        if (this.ignoreMIDIChannels[midich]) {
            return;
        }
        this.midiChannelStates[midich].pc = (byte) (pc);
    }

    // pitchBend は、MIDIピッチベンド受信時の音源の振る舞いを再現します。
    void pitchBend(int midich, int l, int h) {
        if (this.ignoreMIDIChannels[midich]) {
            return;
        }

        var pitch = h * 128 + l - 8192;
        pitch = (int) ((double) (pitch) * (double) (this.midiChannelStates[midich].pitchSens) / (200 * 128) + 64);
        this.midiChannelStates[midich].pitch = (byte) (pitch);
        for (var i, state = range this.chipChannelStates) {
            if (state.midiChannel == midich) {
                state.pitch = state.finetune + pitch;
                this.writeFrequency(i, state.realnote, state.pitch);
            }
        }
    }

    // Reset は、音源の状態をリセットします。
    synchronized void Reset() {
        for (var i = range this.chipChannelStates) {
            this.resetChipChannel(i);
        }
        for (var i = range this.midiChannelStates) {
            this.resetMIDIChannel(i);
        }
    }

    void writeModulation(int chipch, VM35VoicePC instr, boolean state) {
        // TODO: モジュレータではevbだけを見る(stateは無視)？
        for (var i, o = range instr.FmVoice.Operators) {
            this.registers.WriteOperator(chipch, i, ymf.EVB, boolean2int(o.Evb || state));
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
        chipState.time = time.Now();

        chipState.finetune = 0;
        if (instr.DrumNote != 0) {
            note = (int) (instr.FmVoice.DrumKey);
        }
        chipState.pitch = chipState.finetune + (int) (midiState.pitch);
        chipState.instrument = instr;
        midiState.debugLastInstrument = instr;
        chipState.realnote = note;

        chipState.minRR = 15;
        for (var i, op = range instr.FmVoice.Operators) {
            isCarrier = ymfdata.CarrierMatrix[instr.FmVoice.Alg][i];
            if (isCarrier && (int) (op.Rr) < chipState.minRR) {
                chipState.minRR = (int) (op.Rr);
            }
        }

        this.writeInstrument(chipch, instr);
        this.writeModulation(chipch, instr, chipState.flags & flagVibrato != 0);
        this.registers.WriteChannel(chipch, CHPAN, (int) (this.midiChannelStates[midich].pan));
        if (this.soloMIDIChannel < 0 || midich == this.soloMIDIChannel) {
            this.registers.WriteChannel(chipch, VOLUME, (int) (this.midiChannelStates[midich].volume));
        } else {
            this.registers.WriteChannel(chipch, VOLUME, 0);
        }
        this.registers.WriteChannel(chipch, EXPRESSION, (int) (this.midiChannelStates[midich].expression));
        this.registers.WriteChannel(chipch, VELOCITY, velocity);
        this.writeFrequency(chipch, note, chipState.pitch);
        this.keyOn(chipch, midich);
    }

    void resetChipChannel(int chipch) {
        var state = this.chipChannelStates[chipch];
        state.time = time.Time;
        state.flags = flagReleased | flagFree;
        state.minRR = 15;
        state.instrument = null;
        state.midiChannel = -1;
        // state.note = 0
        // state.realnote = 0
        // state.finetune = 0
        // state.pitch = 0
        this.registers.WriteChannel(chipch, RESET, 1);
    }

    void releaseSustain(int midich) {
        for (var i, state :this.chipChannelStates){
            if (state.midiChannel == midich && state.flags & flagSustain != 0) {
                this.keyOff(i);
            }
        }
    }

    // findLastUsedChipChannel は、指定MIDIチャンネルの指定ノートを発音するとき、
// MONOモード時に収容先となるチップのチャンネルを選択します。
    int findLastUsedChipChannel(int midich, int note) {
        var now = time.Now();
        var found = -1;
        var minDelta = Long.MIN_VALUE;
        for (i, state :this.chipChannelStates){
            if (state.midiChannel != midich) {
                continue;
            }
            if (state.note == note) {
                return i;
            }
            delta = int(now.Sub(state.time)) * state.minRR;
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

    // findLastUsedChipChannel は、指定MIDIチャンネルの指定ノートを発音するとき、
    // POLYモード時に収容先となるチップのチャンネルを選択します。
    int findFreeChipChannel(int midich, int note) {
        // // 同じノートで発音済みのチャンネルがあれば最優先で選択
        // for i, state := range this.chipChannelStates {
        // 	if state.midiChannel == midich && state.note == note {
        // 		return i
        // 	}
        // }

        // 無音のチャンネルがあれば選択
        for (var i, state = range this.chipChannelStates) {
            if (state.flags & flagFree != 0) {
                return i;
                ;
            }
        }

        var now = time.Now();
        var foundTotal = -1;
        var foundReleased = -1;
        var maxDeltaTotal = -1;
        var maxAttenuationReleased = -1;
        for (i, state = range this.chipChannelStates) {
            var delta = (int) (now.Sub(state.time))
            if (maxDeltaTotal < delta) {
                maxDeltaTotal = delta;
                foundTotal = i;
            }
            // -dB ∝ 2^RR * time
            var attenuation = delta * (int) (1 << uint(state.minRR));
            if (maxAttenuationReleased < attenuation && state.flags & flagReleased != 0) {
                maxAttenuationReleased = attenuation;
                foundReleased = i;
            }
        }

        // リリース後に最も減衰していると思われるチャンネルを選択
        if (0 <= foundReleased) {
            this.resetChipChannel(foundReleased);
            return foundReleased;
        }
        // 未リリースだが最も古くなったと思われるチャンネルを選択
        if (0 <= foundTotal) {
            this.resetChipChannel(foundTotal);
            return foundTotal;
        }

        // 収容先がない
        return -1;
    }

	(*smaf.VM35VoicePC,boolean)

    getInstrument(int midich, int note) {
        var s = this.midiChannelStates[midich];
        var result, ok = this.library.Get(int(s.bankMSB), int(s.bankLSB), int(s.pc), note);
        if (!ok && !this.muteIfPCNotFound) {
            result = defaultPC;
        }
        return result,ok;
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
        for (var i, state = range this.chipChannelStates) {
            if (state.midiChannel == midich) {
                this.registers.WriteChannel(i, regbase, value);
            }
        }
    }

    void writeAllOperators(int chipch, OpRegister regbase, int value) {
        this.registers.WriteOperator(chipch, 0, regbase, value);
        this.registers.WriteOperator(chipch, 1, regbase, value);
        this.registers.WriteOperator(chipch, 2, regbase, value);
        this.registers.WriteOperator(chipch, 3, regbase, value);
    }

    void writeFrequency(int chipch, int note, int pitch) {
        var n = double(note - ymfdata.A3Note) + double(pitch - 64) / 32.0;
        var freq = ymfdata.A3Freq * math.Pow(2.0, n / 12.0);

        var block = (note + 3 - 12) / 12;
        if (block < 0) {
            block = 0;
        } else if (7 < block) {
            block = 7;
        }

        var fnumF64 = freq * ymfdata.FNUMCoef;
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

        this.registers.WriteChannel(chipch, FNUM, fnum);
        this.registers.WriteChannel(chipch, BLOCK, block);
    }

    void keyOn(int chipch, int midich) {
        this.registers.DebugSetMIDIChannel(chipch, midich);
        this.registers.WriteChannel(chipch, KON, 1);
    }

    void keyOff(int chipch) {
        var state = this.chipChannelStates[chipch];
        state.time = time.Now();
        state.flags = flagReleased;
        this.registers.WriteChannel(chipch, KON, 0);
    }

    static int boolean2int(boolean b) {
        if (b) {
            return 1;
        }
        return 0;
    }

    void writeInstrument(int chipch, VM35VoicePC instr) {
        this.writeAllOperators(chipch, TL, 0x3f); // no volume

        for (var i = 0; i < Operators) {
            this.registers.WriteOperator(chipch, i, EAM, boolean2int(Eam));
            this.registers.WriteOperator(chipch, i, EVB, boolean2int(Evb));
            this.registers.WriteOperator(chipch, i, DAM, (int) (Dam));
            this.registers.WriteOperator(chipch, i, DVB, (int) (Dvb));
            this.registers.WriteOperator(chipch, i, DT, (int) (Dt));
            this.registers.WriteOperator(chipch, i, KSL, (int) (Ksl));
            this.registers.WriteOperator(chipch, i, KSR, boolean2int(Ksr));
            this.registers.WriteOperator(chipch, i, WS, (int) (Ws));
            this.registers.WriteOperator(chipch, i, MULT, (int) (Multi));
            this.registers.WriteOperator(chipch, i, FB, (int) (op.Fb));
            this.registers.WriteOperator(chipch, i, AR, (int) (op.Ar));
            this.registers.WriteOperator(chipch, i, DR, (int) (op.Dr));
            this.registers.WriteOperator(chipch, i, SL, (int) (op.Sl));
            this.registers.WriteOperator(chipch, i, SR, (int) (op.Sr));
            this.registers.WriteOperator(chipch, i, RR, (int) (op.Rr));
            this.registers.WriteOperator(chipch, i, TL, (int) (op.Tl));
            this.registers.WriteOperator(chipch, i, XOF, boolean2int(op.Xof));
        }

        this.registers.WriteChannel(chipch, ALG, (int) (instr.FmVoice.Alg));
        this.registers.WriteChannel(chipch, LFO, (int) (instr.FmVoice.Lfo));
        this.registers.WriteChannel(chipch, PANPOT, (int) (instr.FmVoice.Panpot));
        this.registers.WriteChannel(chipch, BO, (int) (instr.FmVoice.Bo));
    }
}
