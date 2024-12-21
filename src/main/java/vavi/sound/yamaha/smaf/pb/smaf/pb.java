/*
 * https://github.com/but80/go-smaf
 */

package vavi.sound.yamaha.smaf.pb.smaf;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.protobuf.Internal;
import com.google.protobuf.Message;
import vavi.sound.yamaha.smaf.enums.Enums;
import vavi.sound.yamaha.smaf.enums.Enums.VoiceType;
import vavi.sound.yamaha.smaf.voice.VM35VoicePC;
import vavi.sound.yamaha.smaf.voice.VM5VoiceLib;

import static vavi.sound.yamaha.smaf.pb.smaf.pb.VM35FMVoiceVersion.VM35FMVoiceVersionVM35FMVoiceVersionMAX;
import static vavi.sound.yamaha.smaf.pb.smaf.pb.VM35FMVoiceVersion.VM35FMVoiceVersionVM35FMVoiceVersionMIN;
import static vavi.sound.yamaha.smaf.pb.smaf.pb.VM35FMVoiceVersion.VM35FMVoiceVersionVM5;
import static vavi.sound.yamaha.smaf.pb.smaf.pb.VoiceType.VoiceTypeFM;
import static vavi.sound.yamaha.smaf.pb.smaf.pb.VoiceType.VoiceTypeVoiceTypeMIN;
import static vavi.sound.yamaha.smaf.pb.smaf.samf.defaultPC;
import static vavi.sound.yamaha.smaf.pb.smaf.samf.normalizeString;
import static vavi.sound.yamaha.smaf.pb.smaf.samf.normalizeint;


public class pb {

    // Reference imports to suppress errors if they are not otherwise used.
//    var _ = proto.Marshal;
//    var _ = fmt.Errorf;
//    var _ = math.Inf;

    // This is a compile-time assertion to ensure that this generated file
    // is compatible with the proto package it is being compiled against.
    // A compilation error at this line likely means your copy of the
    // proto package needs to be updated.
//    const _ = proto.ProtoPackageIsVersion2; // please upgrade the proto package

    static class Descriptor {

        byte[] bytes;
        int[] ints;

        public Descriptor(byte[] bytes, int[] ints) {
            this.bytes = bytes;
            this.ints = ints;
        }
    }

    public enum VM35FMVoiceVersion {
        VM35FMVoiceVersionVM35FMVoiceVersionMIN(0),
        VM35FMVoiceVersionVM3LIB(0),
        VM35FMVoiceVersionVM3EXCLUSIVE(1),
        VM35FMVoiceVersionVM5(2),
        VM35FMVoiceVersionVM35FMVoiceVersionMAX(2);
        final int n;

        VM35FMVoiceVersion(int n) {
            this.n = n;
        }

        final Map<Integer, String> VM35FMVoiceVersionname = Map.of(
                0, "VM35FMVoiceVersionMIN",
                // Duplicate value: 0: "VM3LIB",
                1, "VM3EXCLUSIVE",
                2, "VM5"
                // Duplicate value: 2: "VM35FMVoiceVersionMAX",
        );
        final Map<String, Integer> VM35FMVoiceVersionvalue = Map.of(
                "VM35FMVoiceVersionMIN", 0,
                "VM3LIB", 0,
                "VM3EXCLUSIVE", 1,
                "VM5", 2,
                "VM35FMVoiceVersionMAX", 2
        );

        public String toString() {
            return EnumName(VM35FMVoiceVersionname, this);
        }

        Descriptor EnumDescriptor() {
            return new Descriptor(fileDescriptor_smaf_4f8a53039970ce01, new int[] {0});
        }
    }

    public enum VoiceType {
        VoiceTypeVoiceTypeMIN(0),
        VoiceTypeFM(0),
        VoiceTypePCM(1),
        VoiceTypeAL(2),

        VoiceTypeVoiceTypeMAX(2);
        final int n;

        VoiceType(int n) {
            this.n = n;
        }

        Map<Integer, String> VoiceTypename = Map.of(
                0, "VoiceTypeMIN", // Duplicate value: 0: "FM",
                1, "PCM",
                2, "AL" // Duplicate value: 2: "VoiceTypeMAX",
        );

        Map<String, Integer> VoiceTypevalue = Map.of(
                "VoiceTypeMIN", 0,
                "FM", 0,
                "PCM", 1,
                "AL", 2,
                "VoiceTypeMAX", 2
        );

        public String toString() {
            return proto.EnumName(VoiceTypename, this);
        }

        Descriptor EnumDescriptor() {
            return new Descriptor(fileDescriptor_smaf_4f8a53039970ce01, new int[] {1});
        }
    }

    public static class VM5VoiceLib {

        vavi.sound.yamaha.smaf.voice.VM5VoiceLib m;
        //`protobuf:"bytes,1,rep,name=programs"
        // json:"programs,omitempty"`
        public List<VM35VoicePC> Programs;
        // `json:"-"`
        Object XXX_NoUnkeyedLiteral;
        //`json:"-"`
        byte[] XXX_unrecognized;
        // `json:"-"`
        int XXX_sizecache;

        void Reset() {
            m = new vavi.sound.yamaha.smaf.voice.VM5VoiceLib();
        }

        public String toString() {
            return Protoproto.CompactTextString(m);
        }

        void ProtoMessage() {
        }

        Descriptor Descriptor() {
            return new Descriptor(fileDescriptor_smaf_4f8a53039970ce01, new int[] {0});
        }

        void XXX_Unmarshal(byte[] b) {
            return xxx_messageInfo_VM5VoiceLib.Unmarshal(this, b);
        }

        byte[] XXX_Marshal(byte[] b, boolean deterministic) {
            return xxx_messageInfo_VM5VoiceLib.Marshal(b, this, deterministic);
        }

        void XXX_Merge(Message src) {
            xxx_messageInfo_VM5VoiceLib.Merge(dst, src);
        }

        int XXX_Size() {
            return xxx_messageInfo_VM5VoiceLib.Size(this);
        }

        void XXX_DiscardUnknown() {
            xxx_messageInfo_VM5VoiceLib.DiscardUnknown(this);
        }

        Internal xxx_messageInfo_VM5VoiceLib;

        List<VM35VoicePC> GetPrograms() {
            return this.Programs;
        }

        // Get は、音色データを取得します。
        public VM35VoicePC Get(int msb, int lsb, int pc, int note) {
            for (var p : this.Programs) {
                if (!(p.Pc == pc && p.BankLsb == lsb && p.BankMsb == msb)) {
                    continue;
                }
                if (p.DrumNote != 0 && p.DrumNote != note) {
                    continue;
                }
                return p;
            }
            return defaultPC;
        }

        // LoadFile は、ファイルから音色ライブラリをロードします。
        public byte[] LoadFile(String file) throws IOException {
            var b = Files.readAllBytes(Path.of("voice").resolve(file));
            this.LoadBytes(b);
            return b;
        }

        // LoadBytes は、バイト列から音色ライブラリをロードします。
        void LoadBytes(byte[] b) {
            VM5VoiceLib loaded;
            Unmarshal(b, loaded);
            this.Programs.addAll(loaded.Programs);
            var x = this.Normalize();
        }

        // Normalize は、音色データから異常な値を排除し、正常化します。
        // 異常が検出された音色の一覧を返します。
        VM35VoicePC[] Normalize() {
            if (this.Programs == null) {
                this.Programs = new ArrayList<>();
            }
            var result = new ArrayList<>();
            for (var i = 0; i < this.Programs.size(); i++) {
                var pc = this.Programs.get(i);
                if (pc == null) {
                    pc = new VM35VoicePC();
                    this.Programs.set(i, pc);
                }
                if (!pc.Normalize()) {
                    result.add(pc);
                }
            }
            return result.toArray(VM35VoicePC[]::new);
        }
    }

    public static class VM35VoicePC {

        vavi.sound.yamaha.smaf.voice.VM35VoicePC m;

        //`protobuf:"varint,1,opt,name=version,enum=smaf.VM35FMVoiceVersion"
        //json:"version,omitempty"`
        public VM35FMVoiceVersion Version;
        //`protobuf:"bytes,2,opt,name=name"
        //json:"name,omitempty"`
        public String Name;
        //`protobuf:"varint,3,opt,name=bank_msb,json=bankMsb"
        //json:"bank_msb,omitempty"`
        public int BankMsb;
        //`protobuf:"varint,4,opt,name=bank_lsb,json=bankLsb"
        // json:"bank_lsb,omitempty"`
        public int BankLsb;
        //`protobuf:"varint,5,opt,name=pc"
        // json:"pc,omitempty"`
        public int Pc;
        //`protobuf:"varint,6,opt,name=drum_note,json=drumNote"
        // json:"drum_note,omitempty"`
        public int DrumNote;
        //`protobuf:"varint,7,opt,name=voice_type,json=voiceType,enum=smaf.VoiceType"
        // json:"voice_type,omitempty"`
        public VoiceType VoiceType;
        //`protobuf:"bytes,8,opt,name=fm_voice,json=fmVoice"
        // json:"fm_voice,omitempty"`
        public VM35FMVoice FmVoice;
        //`protobuf:"bytes,9,opt,name=pcm_voice,json=pcmVoice"
        // json:"pcm_voice,omitempty"`
        VM35PCMVoice PcmVoice;
        //`json:"-"`
        Object XXX_NoUnkeyedLiteral;
        //`json:"-"`
        byte[] XXX_unrecognized;
        //`json:"-"`
        int XXX_sizecache;

        void Reset() {
            m = new vavi.sound.yamaha.smaf.voice.VM35VoicePC();
        }

        public String toString() {
            return CompactTextString(this);
        }

        void /* func(*VM35VoicePC)*/ ProtoMessage() {
        }

        Descriptor Descriptor() {
            return new Descriptor(fileDescriptor_smaf_4f8a53039970ce01, new int[] {1});
        }

        void XXX_Unmarshal(byte[] b) {
            return xxx_messageInfo_VM35VoicePC.Unmarshal(this, b);
        }

        byte[] XXX_Marshal(byte[] b, boolean deterministic) {
            return xxx_messageInfo_VM35VoicePC.Marshal(b, this, deterministic);
        }

        void XXX_Merge(proto.Message src) {
            xxx_messageInfo_VM35VoicePC.Merge(dst, src);
        }

        int XXX_Size() {
            return xxx_messageInfo_VM35VoicePC.Size(this);
        }

        void XXX_DiscardUnknown() {
            xxx_messageInfo_VM35VoicePC.DiscardUnknown(this);
        }

        proto.InternalMessageInfo xxx_messageInfo_VM35VoicePC;

        VM35FMVoiceVersion GetVersion() {
            return this.Version;
        }

        String GetName() {
            return this.Name;
        }

        int GetBankMsb() {
            return this.BankMsb;
        }

        int GetBankLsb() {
            return this.BankLsb;
        }

        int GetPc() {
            return this.Pc;
        }

        int GetDrumNote() {
            return this.DrumNote;
        }

        VoiceType GetVoiceType() {
            return this.VoiceType;
        }

        VM35FMVoice GetFmVoice() {
            return this.FmVoice;
        }

        VM35PCMVoice GetPcmVoice() {
            return this.PcmVoice;
        }

        // Normalize は、音色データから異常な値を排除し、正常化します。
        // 元から正常な音色だったときは true を返します。
        boolean Normalize() {
            var ok = new boolean[] {true};

            if (this.Version.n < VM35FMVoiceVersionVM35FMVoiceVersionMIN.n || VM35FMVoiceVersionVM35FMVoiceVersionMAX.n < this.Version.n) {
                this.Version = VM35FMVoiceVersionVM5;
                ok[0] = false;
            }
            this.Name = normalizeString(ok, this.Name, "(undefined)");
            this.BankMsb = normalizeint(ok, this.BankMsb, 0, 127);
            this.BankLsb = normalizeint(ok, this.BankLsb, 0, 127);
            this.Pc = normalizeint(ok, this.Pc, 0, 127);
            this.DrumNote = normalizeint(ok, this.DrumNote, 0, 127);
            if (this.VoiceType.n < VoiceTypeVoiceTypeMIN.n || pb.VoiceType.VoiceTypeVoiceTypeMAX.n < this.VoiceType.n) {
                this.VoiceType = VoiceTypeFM;
                ok[0] = false;
            }
            switch (this.VoiceType) {
                case VoiceTypeFM:
                    if (this.FmVoice == null) {
                        this.FmVoice = new VM35FMVoice();
                        ok[0] = false;
                    }
                    if (!this.FmVoice.Normalize()) {
                        ok[0] = false;
                    }
                    break;
                case VoiceTypePCM:
                    if (this.PcmVoice == null) {
                        this.PcmVoice = new VM35PCMVoice();
                        ok[0] = false;
                    }
                    if (!this.PcmVoice.Normalize()) {
                        ok[0] = false;
                    }
                    break;
                case VoiceTypeAL:
                    this.VoiceType = VoiceTypeFM;
                    this.FmVoice = new VM35FMVoice();
                    ok[0] = false;
                    break;
            }
            return ok[0];
        }
    }

    public static class VM35FMVoice {

        //`protobuf:"varint,1,opt,name=drum_key,json=drumKey"
        // json:"drum_key,omitempty"`
        public int DrumKey;
        //`protobuf:"varint,2,opt,name=panpot"
        // json:"panpot,omitempty"`
        public int Panpot;
        //`protobuf:"varint,3,opt,name=bo"
        // json:"bo,omitempty"`
        public int Bo;
        //`protobuf:"varint,4,opt,name=lfo"
        // json:"lfo,omitempty"`
        public int Lfo;
        //`protobuf:"varint,5,opt,name=pe"
        // json:"pe,omitempty"`
        boolean Pe;
        //`protobuf:"varint,6,opt,name=alg"
        // json:"alg,omitempty"`
        public int Alg;
        //`protobuf:"bytes,7,rep,name=operators"
        // json:"operators,omitempty"`
        public List<VM35FMOperator> Operators;
        //`json:"-"`
        Object XXX_NoUnkeyedLiteral;
        //`json:"-"`
        byte[] XXX_unrecognized;
        // `json:"-"`
        int XXX_sizecache;

        void Reset() {
            this = new VM35FMVoice();
        }

        String String() {
            return CompactTextString(this);
        }

        void ProtoMessage() {
        }

        Descriptor Descriptor() {
            return new Descriptor(fileDescriptor_smaf_4f8a53039970ce01, new int[] {2});
        }

        void XXX_Unmarshal(byte[] b) {
            return xxx_messageInfo_VM35FMVoice.Unmarshal(this, b);
        }

        byte[] XXX_Marshal(byte[] b, boolean deterministic) {
            return xxx_messageInfo_VM35FMVoice.Marshal(b, this, deterministic);
        }

        void XXX_Merge(proto.Message src) {
            xxx_messageInfo_VM35FMVoice.Merge(dst, src);
        }

        int XXX_Size() {
            return xxx_messageInfo_VM35FMVoice.Size(this);
        }

        void XXX_DiscardUnknown() {
            xxx_messageInfo_VM35FMVoice.DiscardUnknown(this);
        }

        proto.InternalMessageInfo xxx_messageInfo_VM35FMVoice;

        int GetDrumKey() {
            return this.DrumKey;
        }

        int GetPanpot() {
            return this.Panpot;
        }

        int GetBo() {
            return this.Bo;
        }

        int GetLfo() {
            return this.Lfo;
        }

        boolean GetPe() {
            return this.Pe;
        }

        int GetAlg() {
            return this.Alg;
        }

        VM35FMOperator[] GetOperators() {
            return this.Operators;
        }

        // Normalize は、音色データから異常な値を排除し、正常化します。
        // 元から正常な音色だったときは true を返します。
        boolean /* voice *VM35FMVoice */ Normalize() {
            var ok = new boolean[] {true};
            normalizeint(ok, this.DrumKey, 0, 127);
            normalizeint(ok, this.Panpot, 0, 31);
            normalizeint(ok, this.Bo, 0, 3);
            normalizeint(ok, this.Lfo, 0, 3);
            normalizeint(ok, this.Alg, 0, 7);
            var ops = 4;
            if (this.Alg < 2) {
                ops = 2;
            }
            while (this.Operators.size() < ops) {
                this.Operators.add(new VM35FMOperator());
                ok[0] = false;
            }
            if (ops < this.Operators.size()) {
                this.Operators = this.Operators.subList(0, ops);
                ok[0] = false;
            }
            for (var i = 0; i < this.Operators.size(); i++) {
                var op = this.Operators.get(i);
                if (op == null) {
                    op = new VM35FMOperator();
                    this.Operators.set(i, op);
                    ok[0] = false;
                }
                if (!op.Normalize()) {
                    ok[0] = false;
                }
            }
            return ok[0];
        }
    }

    public static class VM35FMOperator {

        //`protobuf:"varint,1,opt,name=multi"
        // json:"multi,omitempty"`
        public int Multi;
        //`protobuf:"varint,2,opt,name=dt"
        // json:"dt,omitempty"`
        public int Dt;
        //`protobuf:"varint,3,opt,name=ar"
        // json:"ar,omitempty"`
        public int Ar;
        //`protobuf:"varint,4,opt,name=dr"
        // json:"dr,omitempty"`
        public int Dr;
        //`protobuf:"varint,5,opt,name=sr"
        // json:"sr,omitempty"`
        public int Sr;
        //`protobuf:"varint,6,opt,name=rr"
        // json:"rr,omitempty"`
        public int Rr;
        //`protobuf:"varint,7,opt,name=sl"
        // json:"sl,omitempty"`
        public int Sl;
        //`protobuf:"varint,8,opt,name=tl"
        // json:"tl,omitempty"`
        public int Tl;
        //`protobuf:"varint,9,opt,name=ksl"
        // json:"ksl,omitempty"`
        public int Ksl;
        //`protobuf:"varint,10,opt,name=dam"
        // json:"dam,omitempty"`
        public int Dam;
        //`protobuf:"varint,11,opt,name=dvb"
        // json:"dvb,omitempty"`
        public int Dvb;
        //`protobuf:"varint,12,opt,name=fb" json:
        // "fb,omitempty"`
        public int Fb;
        //`protobuf:"varint,13,opt,name=ws"
        // json:"ws,omitempty"`
        public int Ws;
        // `protobuf:"varint,14,opt,name=xof"
        // json:"xof,omitempty"`
        public boolean Xof;
        // `protobuf:"varint,15,opt,name=sus"
        // json:"sus,omitempty"`
        boolean Sus;
        // `protobuf:"varint,16,opt,name=ksr"
        // json:"ksr,omitempty"`
        public boolean Ksr;
        // `protobuf:"varint,17,opt,name=eam"
        // json:"eam,omitempty"`
        public boolean Eam;
        // `protobuf:"varint,18,opt,name=evb"
        // json:"evb,omitempty"`
        public boolean Evb;
        // `json:"-"`
        Object XXX_NoUnkeyedLiteral;
        // `json:"-"`
        byte[] XXX_unrecognized;
        // `json:"-"`
        int XXX_sizecache;

        VM35FMOperator /* func(m *VM35FMOperator) */ Reset() {
            return this = new ArrayList<>();
        }

        String /* func(m *VM35FMOperator) */ String() {
            return proto.CompactTextString(this);
        }

        void ProtoMessage() {
        }

        Descriptor Descriptor() {
            return new Descriptor(fileDescriptor_smaf_4f8a53039970ce01, new int[] {3});
        }

        void /* func(m *VM35FMOperator) */ XXX_Unmarshal(byte[] b) {
            return xxx_messageInfo_VM35FMOperator.Unmarshal(this, b);
        }

        byte[] /* func(m *VM35FMOperator) */ XXX_Marshal(byte[] b, boolean deterministic) {
            return xxx_messageInfo_VM35FMOperator.Marshal(b, this, deterministic);
        }

        void /* func(dst *VM35FMOperator) */ XXX_Merge(proto.Message src) {
            xxx_messageInfo_VM35FMOperator.Merge(dst, src);
        }

        int /* func(m *VM35FMOperator) */ XXX_Size() {
            return xxx_messageInfo_VM35FMOperator.Size(this);
        }

        void /* func(m *VM35FMOperator) */ XXX_DiscardUnknown() {
            xxx_messageInfo_VM35FMOperator.DiscardUnknown(this);
        }

        proto.InternalMessageInfo xxx_messageInfo_VM35FMOperator;

        int GetMulti() {
            return this.Multi;
        }

        int GetDt() {
            return this.Dt;
        }

        int GetAr() {
            return this.Ar;
        }

        int GetDr() {
            return this.Dr;
        }

        int GetSr() {
            return this.Sr;
        }

        int GetRr() {
            return this.Rr;
        }

        int GetSl() {
            return this.Sl;
        }

        int GetTl() {
            return this.Tl;
        }

        int GetKsl() {
            return this.Ksl;
        }

        int GetDam() {
            return this.Dam;
        }

        int GetDvb() {
            return this.Dvb;
        }

        int GetFb() {
            return this.Fb;
        }

        int GetWs() {
            return this.Ws;
        }

        boolean GetXof() {
            return this.Xof;
        }

        boolean GetSus() {
            return this.Sus;
        }

        boolean GetKsr() {
            return this.Ksr;
        }

        boolean GetEam() {
            return this.Eam;
        }

        boolean GetEvb() {
            return this.Evb;
        }

        // Normalize は、音色データから異常な値を排除し、正常化します。
        // 元から正常な音色だったときは true を返します。
        boolean Normalize() {
            var ok = new boolean[] {true};
            normalizeint(ok, this.Multi, 0, 15);
            normalizeint(ok, this.Dt, 0, 7);
            normalizeint(ok, this.Ar, 0, 15);
            normalizeint(ok, this.Dr, 0, 15);
            normalizeint(ok, this.Sr, 0, 15);
            normalizeint(ok, this.Rr, 0, 15);
            normalizeint(ok, this.Sl, 0, 15);
            normalizeint(ok, this.Tl, 0, 63);
            normalizeint(ok, this.Ksl, 0, 3);
            normalizeint(ok, this.Dam, 0, 3);
            normalizeint(ok, this.Dvb, 0, 3);
            normalizeint(ok, this.Fb, 0, 7);
            normalizeint(ok, this.Ws, 0, 31);
            // TODO ユーザ波形のwarning
            return ok[0];
        }
    }

    static class VM35PCMVoice {

        byte[] RawData;//`protobuf:"bytes,1,opt,name=raw_data,json=rawData,proto3" json:"raw_data,omitempty"`
        Object XXX_NoUnkeyedLiteral;//`json:"-"`
        byte[] XXX_unrecognized;//  `json:"-"`
        int XXX_sizecache;// `json:"-"`

        void Reset() {
            this = new VM35PCMVoice();
        }

        String String() {
            return proto.CompactTextString(this);
        }

        void ProtoMessage() {
        }

        Descriptor Descriptor() {
            return new Descriptor(fileDescriptor_smaf_4f8a53039970ce01, new int[] {4});
        }

        void XXX_Unmarshal(byte[] b) {
            return xxx_messageInfo_VM35PCMVoice.Unmarshal(this, b);
        }

        byte[] XXX_Marshal(byte[] b, boolean deterministic) {
            return xxx_messageInfo_VM35PCMVoice.Marshal(b, this, deterministic);
        }

        void /* func(dst *VM35PCMVoice) */ XXX_Merge(proto.Message src) {
            xxx_messageInfo_VM35PCMVoice.Merge(dst, src);
        }

        int XXX_Size() {
            return xxx_messageInfo_VM35PCMVoice.Size(this);
        }

        void XXX_DiscardUnknown() {
            xxx_messageInfo_VM35PCMVoice.DiscardUnknown(this);
        }

        proto.InternalMessageInfo xxx_messageInfo_VM35PCMVoice;

        byte[] GetRawData() {
            return this.RawData;
        }

        // Normalize は、音色データから異常な値を排除し、正常化します。
        // 元から正常な音色だったときは true を返します。
        boolean Normalize() {
            var ok = true;
            if (this.RawData == null) {
                this.RawData = new byte[0];
                ok = false;
            }
            return ok;
        }
    }

    void init() {
        proto.RegisterType((VM5VoiceLib) (null), "smaf.VM5VoiceLib");
        proto.RegisterType((VM35VoicePC) (null), "smaf.VM35VoicePC");
        proto.RegisterType((VM35FMVoice) (null), "smaf.VM35FMVoice");
        proto.RegisterType((VM35FMOperator) (null), "smaf.VM35FMOperator");
        proto.RegisterType((VM35PCMVoice) (null), "smaf.VM35PCMVoice");
        proto.RegisterEnum("smaf.VM35FMVoiceVersion", VM35FMVoiceVersionname, VM35FMVoiceVersionvalue);
        proto.RegisterEnum("smaf.VoiceType", VoiceTypename, VoiceTypevalue);
    }

    void init() {
        proto.RegisterFile("smaf.proto", fileDescriptor_smaf_4f8a53039970ce01);
    }

    static final byte[] fileDescriptor_smaf_4f8a53039970ce01 = {
            // 639 bytes of a gzipped FileDescriptorProto
            0x1f, (byte) 0x8b, 0x08, 0x00, 0x00, 0x00, 0x00, 0x00, 0x02, (byte) 0xff, 0x74, 0x54, 0x51, 0x4f, (byte) 0xdb, 0x3a,
            0x14, 0x26, 0x2e, 0x34, (byte) 0xed, (byte) 0x81, (byte) 0x96, 0x60, 0x21, (byte) 0xe4, (byte) 0xcb, 0x7d, (byte) 0xa9, (byte) 0xfa, (byte) 0xd4, (byte) 0x8b,
            (byte) 0xee, (byte) 0xe5, 0x4a, 0x45, (byte) 0xbc, (byte) 0xed, (byte) 0x85, 0x75, 0x20, (byte) 0xa1, 0x35, 0x0c, 0x65, 0x5b, (byte) 0xc5, 0x5b,
            (byte) 0xe5, 0x34, 0x0e, (byte) 0xaa, (byte) 0x9a, (byte) 0xd4, (byte) 0x91, (byte) 0x9d, (byte) 0xa6, (byte) 0xeb, 0x3f, (byte) 0xdb, 0x2f, (byte) 0xd8, (byte) 0xfe, (byte) 0xd6,
            (byte) 0xe4, 0x63, (byte) 0xa7, (byte) 0x85, (byte) 0xa1, (byte) 0xbd, (byte) 0x9d, (byte) 0xef, 0x3b, (byte) 0x9f, (byte) 0xed, (byte) 0xef, 0x7c, 0x76, 0x02, (byte) 0xa0,
            0x73, (byte) 0x9e, 0x5e, 0x16, 0x4a, (byte) 0x96, (byte) 0x92, (byte) 0xee, (byte) 0x9b, (byte) 0xba, (byte) 0xff, 0x0e, 0x0e, 0x27, (byte) 0xe1, (byte) 0xf5,
            0x44, (byte) 0xce, 0x67, 0x62, 0x3c, (byte) 0x8f, (byte) 0xe9, 0x7f, (byte) 0xd0, 0x2a, (byte) 0x94, 0x7c, 0x56, 0x3c, (byte) 0xd7, (byte) 0xcc,
            (byte) 0xeb, 0x35, 0x06, (byte) 0x87, (byte) 0xc3, (byte) 0x93, 0x4b, 0x5c, 0x33, 0x09, (byte) 0xaf, (byte) 0xac, (byte) 0xea, 0x71, 0x14, 0x6d,
            0x25, (byte) 0xfd, (byte) 0x9f, (byte) 0xc4, 0x2c, (byte) 0xdf, 0x76, (byte) 0xe8, 0x10, (byte) 0xfc, 0x4a, 0x28, 0x3d, (byte) 0x97, 0x4b, (byte) 0xe6,
            (byte) 0xf5, (byte) 0xbc, 0x41, 0x77, (byte) 0xc8, 0x76, (byte) 0xab, (byte) 0xef, 0x42, 0x54, 0x4d, 0x6c, 0x3f, (byte) 0xaa, (byte) 0x85, (byte) 0x94,
            (byte) 0xc2, (byte) 0xfe, (byte) 0x92, (byte) 0xe7, (byte) 0x82, (byte) 0x91, (byte) 0x9e, 0x37, 0x68, 0x47, 0x58, (byte) 0xd3, (byte) 0xbf, (byte) 0xa0, 0x15, (byte) 0xf3,
            (byte) 0xe5, 0x62, (byte) 0x9a, (byte) 0xeb, (byte) 0x98, 0x35, 0x7a, (byte) 0xde, (byte) 0xa0, 0x13, (byte) 0xf9, 0x06, (byte) 0x87, 0x3a, (byte) 0xde, (byte) 0xb6,
            0x32, 0x1d, (byte) 0xb3, (byte) 0xfd, 0x5d, 0x6b, (byte) 0xac, 0x63, (byte) 0xda, 0x05, 0x52, (byte) 0xcc, (byte) 0xd8, 0x01, (byte) 0x92, (byte) 0xa4,
            (byte) 0x98, (byte) 0xd1, (byte) 0xbf, (byte) 0xa1, (byte) 0x9d, (byte) 0xa8, 0x55, 0x3e, 0x5d, (byte) 0xca, 0x52, (byte) 0xb0, 0x26, (byte) 0xd2, 0x2d, 0x43,
            0x3c, (byte) 0xc8, 0x52, (byte) 0xd0, 0x4b, (byte) 0x80, (byte) 0xca, (byte) 0xf8, (byte) 0x99, (byte) 0x96, (byte) 0x9b, 0x42, 0x30, 0x1f, (byte) 0xdd, 0x1e,
            0x3b, (byte) 0xb7, (byte) 0x86, (byte) 0xff, (byte) 0xb2, 0x29, 0x44, (byte) 0xd4, (byte) 0xae, (byte) 0xea, (byte) 0x92, (byte) 0xfe, 0x0b, (byte) 0xad, 0x34, (byte) 0x9f,
            0x22, 0x66, (byte) 0xad, (byte) 0x9e, (byte) 0xf7, 0x3a, 0x19, 0x37, 0x5b, (byte) 0xe4, (byte) 0xa7, 0x39, 0x16, (byte) 0xf4, 0x7f, 0x68,
            0x17, (byte) 0xb3, 0x5a, (byte) 0xde, 0x46, 0x39, (byte) 0xdd, (byte) 0xc9, 0x1f, 0x47, 0x4e, (byte) 0xdf, 0x2a, 0x66, 0x76, 0x41,
            (byte) 0xff, (byte) 0xbb, 0x67, (byte) 0x93, 0x74, 0x3b, (byte) 0x99, 0x31, (byte) 0xd1, (byte) 0xfb, 0x42, 0x6c, 0x30, (byte) 0xca, 0x4e, (byte) 0xe4,
            0x1b, (byte) 0xfc, 0x51, 0x6c, (byte) 0xe8, 0x19, 0x34, 0x0b, (byte) 0xbe, 0x2c, 0x64, (byte) 0x89, (byte) 0x91, 0x75, 0x22, (byte) 0x87,
            (byte) 0xcc, (byte) 0xf8, (byte) 0xb1, 0x74, 0x71, (byte) 0x91, 0x58, (byte) 0xd2, 0x00, 0x1a, 0x59, 0x2a, 0x5d, 0x48, (byte) 0xa6, (byte) 0xc4,
            (byte) 0x80, 0x04, 0x06, (byte) 0xd4, (byte) 0x8a, 0x48, 0x21, (byte) 0x8c, (byte) 0x82, 0x67, (byte) 0xcf, 0x2e, 0x1a, 0x53, (byte) 0xd2, 0x21,
            (byte) 0xb4, 0x65, 0x21, 0x14, 0x2f, (byte) 0xa5, (byte) 0xd2, (byte) 0xcc, (byte) 0xc7, 0x07, 0x70, (byte) 0xfa, 0x72, (byte) 0xcc, 0x4f, (byte) 0xae,
            0x19, (byte) 0xed, 0x64, (byte) 0xfd, 0x1f, 0x04, (byte) 0xba, (byte) 0xaf, (byte) 0xbb, (byte) 0xf4, 0x14, 0x0e, (byte) 0xf2, 0x55, 0x56, (byte) 0xce,
            (byte) 0x9d, 0x75, 0x0b, (byte) 0xcc, (byte) 0xf1, 0x49, 0x6d, (byte) 0x9a, 0x24, 0x68, (byte) 0x98, (byte) 0xab, (byte) 0xda, 0x30, 0x57, (byte) 0xd8,
            0x57, (byte) 0xce, 0x2f, 0x49, 0x10, 0x6b, 0x55, (byte) 0xdf, (byte) 0xa7, 0x46, (byte) 0xac, (byte) 0x94, 0x73, 0x4b, (byte) 0x94, (byte) 0xed,
            0x67, 0x78, 0x75, (byte) 0xa6, (byte) 0x9f, 0x19, 0x5c, 0x66, 0x78, 0x39, (byte) 0x9d, (byte) 0x88, (byte) 0x94, (byte) 0x99, 0x19, 0x6f,
            (byte) 0xa1, 0x33, (byte) 0x8c, (byte) 0xbf, 0x13, (byte) 0x99, (byte) 0xd2, 0x30, 0x09, (byte) 0xcf, 0x19, 0x58, 0x26, (byte) 0xe1, 0x39, 0x32,
            0x55, (byte) 0xcc, 0x0e, 0x1d, 0x53, (byte) 0xe1, 0x2b, 0x4a, 0x63, 0x76, 0x64, 0x77, 0x49, 0x11, (byte) 0xaf, 0x35,
            (byte) 0xeb, 0x58, (byte) 0xbc, (byte) 0xd6, 0x66, (byte) 0xc5, 0x37, (byte) 0x99, (byte) 0xb2, 0x2e, (byte) 0xa6, 0x68, 0x4a, (byte) 0xc3, (byte) 0xe8, (byte) 0x95,
            0x66, (byte) 0xc7, (byte) 0x96, (byte) 0xd1, 0x2b, 0x6d, 0x4f, 0x56, 0x2c, (byte) 0xb0, (byte) 0xcc, 0x42, 0x2b, (byte) 0xc3, 0x08, (byte) 0x9e,
            (byte) 0xb3, 0x13, (byte) 0xcb, 0x08, 0x7b, (byte) 0xb2, (byte) 0xa8, 0x62, 0x46, 0x1d, 0x53, (byte) 0xc5, (byte) 0xfd, 0x7f, (byte) 0xe0, (byte) 0xe8,
            (byte) 0xe5, (byte) 0xeb, 0x30, 0x6f, 0x40, (byte) 0xf1, (byte) 0xf5, 0x34, (byte) 0xe1, 0x25, (byte) 0xc7, 0x20, (byte) 0x8f, 0x22, 0x5f, (byte) 0xf1,
            (byte) 0xf5, 0x07, 0x5e, (byte) 0xf2, (byte) 0x8b, 0x35, (byte) 0xd0, (byte) 0xb7, (byte) 0xdf, 0x14, 0x3d, (byte) 0x87, (byte) 0xb3, (byte) 0xb7, (byte) 0xec, 0x34,
            (byte) 0xbc, 0x7f, 0x08, (byte) 0xf6, 0x28, 0x40, 0x73, 0x12, 0x5e, (byte) 0x8d, (byte) 0xef, (byte) 0xdf, 0x07, 0x7b, 0x34, (byte) 0xc0,
            (byte) 0x83, 0x6e, (byte) 0x9f, 0x46, (byte) 0xe3, (byte) 0xaf, (byte) 0x9f, (byte) 0xef, 0x27, (byte) 0xb7, (byte) 0x81, 0x47, 0x7d, 0x68, 0x4c, (byte) 0xc2,
            (byte) 0xeb, (byte) 0x80, (byte) 0xfc, 0x69, (byte) 0x8b, (byte) 0x9b, (byte) 0xa7, (byte) 0x80, (byte) 0x9c, (byte) 0x93, (byte) 0xc0, (byte) 0xbb, 0x78, (byte) 0x80, (byte) 0xf6, (byte) 0xf6,
            (byte) 0xf3, (byte) 0xa0, 0x27, (byte) 0xd0, (byte) 0xd9, 0x02, 0x77, 0x4c, 0x13, (byte) 0xc8, 0x5d, 0x18, (byte) 0xec, (byte) 0x99, 0x0d, 0x1f,
            0x47, 0x61, (byte) 0xe0, 0x19, (byte) 0xe2, 0x66, 0x1c, (byte) 0x90, (byte) 0xdf, (byte) 0xb4, (byte) 0xf5, 0x7e, 0x71, 0x13, 0x7f, 0x46,
            0x57, (byte) 0xbf, 0x02, 0x00, 0x00, (byte) 0xff, (byte) 0xff, (byte) 0xa2, 0x28, 0x6b, (byte) 0xb6, (byte) 0x9a, 0x04, 0x00, 0x00,
    };
}
