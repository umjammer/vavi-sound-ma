/*
 * https://github.com/but80/fmfm.core
 */

package vavi.sound.yamaha.ma.ymf;


public class Register {

    // OpRegister is a type that represents the kind of register that holds operator parameters.
    public enum OpRegister {
        // EAM is the EAM register.
        EAM,
        // EVB is the EVB register.
        EVB,
        // DAM is the DAM register.
        DAM,
        // DVB is the DVB register.
        DVB,
        // DT is the DT register.
        DT,
        // KSL is the KSL register.
        KSL,
        // KSR is the KSR register.
        KSR,
        // WS is the WS register.
        WS,
        // MULT is the MULT register.
        MULT,
        // FB is the FB register.
        FB,
        // AR is the AR register.
        AR,
        // DR is the DR register.
        DR,
        // SL is the SL register.
        SL,
        // SR is the SR register.
        SR,
        // RR is the RR register.
        RR,
        // TL is the TL register.
        TL,
        // XOF is the XOF register.
        XOF
    }

    // ChRegister is a type that represents the type of register that holds channel parameters.
    public enum ChRegister {
        // KON is the KON register.
        KON,
        // BLOCK is the BLOCK register.
        BLOCK,
        // FNUM is the FNUM register.
        FNUM,
        // alg is the alg register.
        ALG,
        // lfo is the lfo register.
        LFO,
        // panpot is the panpot register.
        PANPOT,
        // CHPAN is the CHPAN register.
        CHPAN,
        // VOLUME is the VOLUME register.
        VOLUME,
        // EXPRESSION is the EXPRESSION register.
        EXPRESSION,
        // VELOCITY is the VELOCITY register.
        VELOCITY,
        // bo is the bo register.
        BO,
        // RESET is the RESET register.
        RESET
    }

    // registers is an interface that abstracts the registers of the sound chip.
    interface Registers {

        // WriteOperator writes a value to an operator register.
        void writeOperator(int channel, int operatorIndex, OpRegister offset, int v);

        // WriteTL writes a value to the TL register.
        void writeTL(int channel, int operatorIndex, int tlCarrier, int tlModulator);

        // WriteChannel writes a value to a channel register.
        void writeChannel(int channel, ChRegister offset, int v);

        // DebugSetMIDIChannel sets the MIDI channel number used for debugging purposes.
        void debugSetMIDIChannel(int channel, int midiChannel);
    }
}
