package vavi.sound.yamaha.ma.sim;

import java.util.ArrayList;
import java.util.Comparator;

import static vavi.sound.yamaha.ma.sim.EnvelopeGenerator.epsilon;
import static vavi.sound.yamaha.ma.ymf.ymfdata.Data.ChannelCount;
import static vavi.sound.yamaha.ma.ymf.ymfdata.Data.DebugDumpFPS;


public class Chip {

    /** sampleRate is the target sample rate of the output waveform. */
    double sampleRate;
    /** totalLevel is the total volume of the output [dB]. */
    double totalLevel;
    /** dumpMIDIChannel is the MIDI channel for which the dump is displayed. If unused, it is set to -1. */
    int dumpMIDIChannel;
    /** channels is the total number of channels this chip has. */
    Channel[] channels;

    double[] currentOutput;

    /** NewChip creates a new Chip. */
    public Chip(int sampleRate, double totalLevel, int dumpMIDIChannel) {
        this.sampleRate = sampleRate;
        this.totalLevel = totalLevel;
        this.dumpMIDIChannel = dumpMIDIChannel;
        this.channels = new Channel[ChannelCount];
        this.currentOutput = new double[2];

        this.initChannels();
    }

    int debugDumpCount = 0;

    /** SampleRate returns the sample rate that this chip is set to. */
    double sampleRate() {
        return this.sampleRate;
    }

    /** Next generates the next sample and returns its amplitude to the left and right. */
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

    public Channel[] getChannels() {
        return channels;
    }
}
