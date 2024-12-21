package vavi.sound.yamaha.ma.sim;

import java.util.ArrayList;
import java.util.Comparator;

import static vavi.sound.yamaha.ma.sim.EnvelopeGenerator.epsilon;
import static vavi.sound.yamaha.ma.ymf.ymfdata.Data.ChannelCount;
import static vavi.sound.yamaha.ma.ymf.ymfdata.Data.DebugDumpFPS;


public class Chip {

    /** sampleRate は、出力波形の目標サンプルレートです。 */
    double sampleRate;
    /** totalLevel は、出力のトータルな音量[dB]です。 */
    double totalLevel;
    /** dumpMIDIChannel は、ダンプ表示対象のMIDIチャンネルです。未使用時は -1 です。 */
    int dumpMIDIChannel;
    /** channels は、このチップが備える全チャンネルです。 */
    Channel[] channels;

    double[] currentOutput;

    /** NewChip は、新しい Chip を作成します。 */
    public Chip(int sampleRate, double totalLevel, int dumpMIDIChannel) {
        this.sampleRate = sampleRate;
        this.totalLevel = totalLevel;
        this.dumpMIDIChannel = dumpMIDIChannel;
        this.channels = new Channel[ChannelCount];
        this.currentOutput = new double[2];

        this.initChannels();
    }

    int debugDumpCount = 0;

    /** SampleRate は、このチップに設定されているサンプルレートを返します。 */
    double sampleRate() {
        return this.sampleRate;
    }

    /** Next は、次のサンプルを生成し、その左右それぞれの振幅を返します。 */
    public double[] next() {
        double l = 0, r = 0;
        for (var channel : this.channels) {
            double cl, cr;
            synchronized (this) {
                var dd = channel.next();
                cl = dd[0];
                cr = dd[1];
            }
            l += cl;
            r += cr;
        }
        var v = Math.pow(10, this.totalLevel / 20);

        if (0 <= this.dumpMIDIChannel) {
            debugDumpCount++;
            if ((int) (this.sampleRate / DebugDumpFPS) <= debugDumpCount) {
                debugDumpCount = 0;
                var toDump = new ArrayList<Channel>();
                for (var ch : this.channels) {
                    if (ch.midiChannelID == this.dumpMIDIChannel && epsilon < ch.currentLevel()) {
                        toDump.add(ch);
                    }
                }
                if (!toDump.isEmpty()) {
                    toDump.sort(Comparator.comparingDouble(Channel::currentLevel));
                    for (var ch : toDump) {
                        System.out.print(ch.dump());
                    }
                    System.out.println("------------------------------");
                }
            }
        }

        return new double[] {l * v, r * v};
    }

    void initChannels() {
        this.channels = new Channel[ChannelCount];
        for (int i = 0; i < this.channels.length; i++) {
            this.channels[i] = new Channel(i, this);
        }
    }
}
