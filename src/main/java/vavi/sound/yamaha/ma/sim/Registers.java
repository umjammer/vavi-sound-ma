/*
 * https://github.com/but80/fmfm.core
 */

package vavi.sound.yamaha.ma.sim;

import vavi.sound.yamaha.ma.ymf.Register.ChRegister;
import vavi.sound.yamaha.ma.ymf.Register.OpRegister;

import static vavi.sound.yamaha.ma.ymf.Register.OpRegister.TL;


/** registers は、全レジスタのコンテナです。 */
public class Registers {

    Chip chip;

    /** NewRegisters は、新しい registers を作成します。 */
    public Registers(Chip chip) {
        this.chip = chip;
    }

    /** WriteOperator は、オペレータレジスタに値を書き込みます。 */
    public synchronized void writeOperator(int channel, int operatorIndex, OpRegister offset, int v) {
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

    /** WriteTL は、TLレジスタに値を書き込みます。 */
    synchronized void writeTL(int channel, int operatorIndex, int tlCarrier, int tlModulator) {
        if (this.chip.channels[channel].operators[operatorIndex].isModulator) {
            this.writeOperator(channel, operatorIndex, TL, tlModulator);
        } else {
            this.writeOperator(channel, operatorIndex, TL, tlCarrier);
        }
    }

    /** DebugSetMIDIChannel は、チャンネルを使用しているMIDIチャンネル番号をデバッグ用にセットします。 */
    public synchronized void debugSetMIDIChannel(int channel, int midiChannel) {
        this.chip.channels[channel].midiChannelID = midiChannel;
    }

    /** WriteChannel は、チャンネルレジスタに値を書き込みます。 */
    public synchronized void writeChannel(int channel, ChRegister offset, int v) {
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
