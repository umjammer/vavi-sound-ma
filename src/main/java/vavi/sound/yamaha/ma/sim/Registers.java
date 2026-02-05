/*
 * https://github.com/but80/fmfm.core
 */

package vavi.sound.yamaha.ma.sim;

import vavi.sound.yamaha.ma.ymf.Register.ChRegister;
import vavi.sound.yamaha.ma.ymf.Register.OpRegister;

import static vavi.sound.yamaha.ma.ymf.Register.OpRegister.TL;


/** registers is a container for all registers. */
public class Registers {

    Chip chip;

    /** NewRegisters creates new registers. */
    public Registers(Chip chip) {
        this.chip = chip;
    }

    public Chip getChip() {
        return chip;
    }

    /** WriteOperator writes a value to an operator register. */
    public synchronized void writeOperator(int channel, int operatorIndex, OpRegister offset, int v) {
        switch (offset) {
            case EAM:
                this.chip.channels[channel].operators[operatorIndex].setEAM(v);
                break;
            case EVB:
                this.chip.channels[channel].operators[operatorIndex].setEVB(v);
                break;
            case DAM:
                this.chip.channels[channel].operators[operatorIndex].setDAM(v);
                break;
            case DVB:
                this.chip.channels[channel].operators[operatorIndex].setDVB(v);
                break;
            case DT:
                this.chip.channels[channel].operators[operatorIndex].setDT(v);
                break;
            case KSR:
                this.chip.channels[channel].operators[operatorIndex].setKSR(v);
                break;
            case MULT:
                this.chip.channels[channel].operators[operatorIndex].setMULT(v);
                break;
            case KSL:
                this.chip.channels[channel].operators[operatorIndex].setKSL(v);
                break;
            case TL:
                this.chip.channels[channel].operators[operatorIndex].setTL(v);
                break;
            case AR:
                this.chip.channels[channel].operators[operatorIndex].setAR(v);
                break;
            case DR:
                this.chip.channels[channel].operators[operatorIndex].setDR(v);
                break;
            case SL:
                this.chip.channels[channel].operators[operatorIndex].setSL(v);
                break;
            case SR:
                this.chip.channels[channel].operators[operatorIndex].setSR(v);
                break;
            case RR:
                this.chip.channels[channel].operators[operatorIndex].setRR(v);
                break;
            case XOF:
                this.chip.channels[channel].operators[operatorIndex].setXOF(v);
                break;
            case WS:
                this.chip.channels[channel].operators[operatorIndex].setWS(v);
                break;
            case FB:
                this.chip.channels[channel].operators[operatorIndex].setFB(v);
                break;
        }
    }

    /** WriteTL writes a value to the TL register. */
    synchronized void writeTL(int channel, int operatorIndex, int tlCarrier, int tlModulator) {
        if (this.chip.channels[channel].operators[operatorIndex].isModulator) {
            this.writeOperator(channel, operatorIndex, TL, tlModulator);
        } else {
            this.writeOperator(channel, operatorIndex, TL, tlCarrier);
        }
    }

    /** DebugSetMIDIChannel sets the MIDI channel number used for debugging purposes. */
    public synchronized void debugSetMIDIChannel(int channel, int midiChannel) {
        this.chip.channels[channel].midiChannelID = midiChannel;
    }

    /** WriteChannel writes a value to a channel register. */
    public synchronized void writeChannel(int channel, ChRegister offset, int v) {
        switch (offset) {
            case KON:
                this.chip.channels[channel].setKON(v);
                break;
            case BLOCK:
                this.chip.channels[channel].setBLOCK(v);
                break;
            case FNUM:
                this.chip.channels[channel].setFNUM(v);
                break;
            case ALG:
                this.chip.channels[channel].setALG(v);
                break;
            case LFO:
                this.chip.channels[channel].setLFO(v);
                break;
            case PANPOT:
                this.chip.channels[channel].setPANPOT(v);
                break;
            case CHPAN:
                this.chip.channels[channel].setCHPAN(v);
                break;
            case VOLUME:
                this.chip.channels[channel].setVOLUME(v);
                break;
            case EXPRESSION:
                this.chip.channels[channel].setEXPRESSION(v);
                break;
            case VELOCITY:
                this.chip.channels[channel].setVELOCITY(v);
                break;
            case BO:
                this.chip.channels[channel].setBO(v);
                break;
            case RESET:
                if (v != 0) {
                    this.chip.channels[channel].resetAll();
                }
                break;
        }
    }
}
