/*
 * https://github.com/but80/fmfm.core
 */

package vavi.sound.yamaha.ma.ymf;


public class Register {

    // OpRegister は、オペレータパラメータを保持するレジスタの種類を表す型です。
    public enum OpRegister {
        // EAM は、EAM レジスタです。
        EAM,
        // EVB は、EVB レジスタです。
        EVB,
        // DAM は、DAM レジスタです。
        DAM,
        // DVB は、DVB レジスタです。
        DVB,
        // DT は、DT レジスタです。
        DT,
        // KSL は、KSL レジスタです。
        KSL,
        // KSR は、KSR レジスタです。
        KSR,
        // WS は、WS レジスタです。
        WS,
        // MULT は、MULT レジスタです。
        MULT,
        // FB は、FB レジスタです。
        FB,
        // AR は、AR レジスタです。
        AR,
        // DR は、DR レジスタです。
        DR,
        // SL は、SL レジスタです。
        SL,
        // SR は、SR レジスタです。
        SR,
        // RR は、RR レジスタです。
        RR,
        // TL は、TL レジスタです。
        TL,
        // XOF は、XOF レジスタです。
        XOF
    }

    // ChRegister は、チャンネルパラメータを保持するレジスタの種類を表す型です。
    public enum ChRegister {
        // KON は、KON レジスタです。
        KON,
        // BLOCK は、BLOCK レジスタです。
        BLOCK,
        // FNUM は、FNUM レジスタです。
        FNUM,
        // alg は、alg レジスタです。
        ALG,
        // lfo は、lfo レジスタです。
        LFO,
        // panpot は、panpot レジスタです。
        PANPOT,
        // CHPAN は、CHPAN レジスタです。
        CHPAN,
        // VOLUME は、VOLUME レジスタです。
        VOLUME,
        // EXPRESSION は、EXPRESSION レジスタです。
        EXPRESSION,
        // VELOCITY は、VELOCITY レジスタです。
        VELOCITY,
        // bo は、bo レジスタです。
        BO,
        // RESET は、RESET レジスタです。
        RESET
    }

    // registers は、音源チップのレジスタを抽象化したインタフェースです。
    interface Registers {

        // WriteOperator は、オペレータレジスタに値を書き込みます。
        void writeOperator(int channel, int operatorIndex, OpRegister offset, int v);

        // WriteTL は、TLレジスタに値を書き込みます。
        void writeTL(int channel, int operatorIndex, int tlCarrier, int tlModulator);

        // WriteChannel は、チャンネルレジスタに値を書き込みます。
        void writeChannel(int channel, ChRegister offset, int v);

        // DebugSetMIDIChannel は、チャンネルを使用しているMIDIチャンネル番号をデバッグ用にセットします。
        void debugSetMIDIChannel(int channel, int midiChannel);
    }
}
