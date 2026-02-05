package vavi.sound.yamaha.ma;

import java.io.ByteArrayOutputStream;
import java.util.zip.GZIPOutputStream;
import vavi.sound.yamaha.smaf.pb.smaf.Smaf.VM5VoiceLib;
import vavi.sound.yamaha.smaf.pb.smaf.Smaf.VM35VoicePC;
import vavi.sound.yamaha.smaf.pb.smaf.Smaf.VM35FMVoiceVersion;
import vavi.sound.yamaha.smaf.pb.smaf.Smaf.VoiceType;

public class GenData {
    public static void main(String[] args) throws Exception {
        VM35VoicePC pc = VM35VoicePC.newBuilder()
            .setVersion(VM35FMVoiceVersion.VM5)
            .setName("TestVoice")
            .setBankMsb(0)
            .setBankLsb(0)
            .setPc(1)
            .setVoiceType(VoiceType.FM)
            .build();

        VM5VoiceLib lib = VM5VoiceLib.newBuilder()
            .addPrograms(pc)
            .build();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        lib.writeDelimitedTo(baos);
        byte[] rawData = baos.toByteArray();

        ByteArrayOutputStream gzipBaos = new ByteArrayOutputStream();
        try (GZIPOutputStream gzos = new GZIPOutputStream(gzipBaos)) {
            gzos.write(rawData);
        }
        byte[] gzippedData = gzipBaos.toByteArray();

        System.out.println("    static final byte[] fileDescriptor_smaf_4f8a53039970ce01 = {");
        for (int i = 0; i < gzippedData.length; i++) {
            if (i % 16 == 0) {
                System.out.print("            ");
            }
            System.out.printf("(byte) 0x%02x, ", gzippedData[i]);
            if (i % 16 == 15) {
                System.out.println();
            }
        }
        System.out.println("\n    };");
    }
}
