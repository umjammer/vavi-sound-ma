/*
 * https://github.com/but80/fmfm.core
 */

package vavi.sound.yamaha.ma.sim;

import vavi.sound.yamaha.ma.ymf.Register.ChRegister;
import vavi.sound.yamaha.ma.ymf.Register.OpRegister;

import static vavi.sound.yamaha.ma.ymf.Register.OpRegister.TL;


// Registers は、全レジスタのコンテナです。
public class Registers {

    Chip chip;

//	var _
//	ymf.Registers =&
//
//	Registers {
//	}

    // NewRegisters は、新しい Registers を作成します。
    public Registers(Chip chip) {
        this.chip = chip;
    }

    // WriteOperator は、オペレータレジスタに値を書き込みます。
    public void WriteOperator(int channel, int operatorIndex, OpRegister offset, int v) {
        synchronized (this.chip.Mutex) {
            switch (offset) {
                case EAM:
                    this.chip.channels[channel].operators[operatorIndex].setEAM(v);
                case EVB:
                    this.chip.channels[channel].operators[operatorIndex].setEVB(v);
                case DAM:
                    this.chip.channels[channel].operators[operatorIndex].setDAM(v);
                case DVB:
                    this.chip.channels[channel].operators[operatorIndex].setDVB(v);
                case DT:
                    this.chip.channels[channel].operators[operatorIndex].setDT(v);
                case KSR:
                    this.chip.channels[channel].operators[operatorIndex].setKSR(v);
                case MULT:
                    this.chip.channels[channel].operators[operatorIndex].setMULT(v);
                case KSL:
                    this.chip.channels[channel].operators[operatorIndex].setKSL(v);
                case TL:
                    this.chip.channels[channel].operators[operatorIndex].setTL(v);
                case AR:
                    this.chip.channels[channel].operators[operatorIndex].setAR(v);
                case DR:
                    this.chip.channels[channel].operators[operatorIndex].setDR(v);
                case SL:
                    this.chip.channels[channel].operators[operatorIndex].setSL(v);
                case SR:
                    this.chip.channels[channel].operators[operatorIndex].setSR(v);
                case RR:
                    this.chip.channels[channel].operators[operatorIndex].setRR(v);
                case XOF:
                    this.chip.channels[channel].operators[operatorIndex].setXOF(v);
                case WS:
                    this.chip.channels[channel].operators[operatorIndex].setWS(v);
                case FB:
                    this.chip.channels[channel].operators[operatorIndex].setFB(v);
            }
        }
    }

    // WriteTL は、TLレジスタに値を書き込みます。
    void WriteTL(int channel, int operatorIndex, int tlCarrier, int tlModulator) {
        synchronized (this.chip.Mutex) {
            if (this.chip.channels[channel].operators[operatorIndex].isModulator) {
                this.WriteOperator(channel, operatorIndex, TL, tlModulator);
            } else {
                this.WriteOperator(channel, operatorIndex, TL, tlCarrier);
            }
        }
    }

    // DebugSetMIDIChannel は、チャンネルを使用しているMIDIチャンネル番号をデバッグ用にセットします。
    public void DebugSetMIDIChannel(int channel, int midiChannel) {
        synchronized (this.chip.Mutex) {
            this.chip.channels[channel].midiChannelID = midiChannel;
        }
    }

    // WriteChannel は、チャンネルレジスタに値を書き込みます。
    public void WriteChannel(int channel, ChRegister offset, int v) {
        synchronized (this.chip.Mutex) {
            switch (offset) {
                case KON:
                    this.chip.channels[channel].setKON(v);
                case BLOCK:
                    this.chip.channels[channel].setBLOCK(v);
                case FNUM:
                    this.chip.channels[channel].setFNUM(v);
                case ALG:
                    this.chip.channels[channel].setALG(v);
                case LFO:
                    this.chip.channels[channel].setLFO(v);
                case PANPOT:
                    this.chip.channels[channel].setPANPOT(v);
                case CHPAN:
                    this.chip.channels[channel].setCHPAN(v);
                case VOLUME:
                    this.chip.channels[channel].setVOLUME(v);
                case EXPRESSION:
                    this.chip.channels[channel].setEXPRESSION(v);
                case VELOCITY:
                    this.chip.channels[channel].setVELOCITY(v);
                case BO:
                    this.chip.channels[channel].setBO(v);
                case RESET:
                    if (v != 0) {
                        this.chip.channels[channel].resetAll();
                    }
            }
        }
    }
}
