/*
 * https://github.com/but80/fmfm.core
 */

package vavi.sound.yamaha.ma.cmd.cli.internal.player;


import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;


// Renderer は、波形をレンダリングしてオーディオデバイスに出力します。
// TODO: rename
public class Renderer {

    public AudioFormat Parameters;
    Stream stream;
    List<Insertion> insertions;

    // NewRenderer は、新しいRendererを作成します。
    public Renderer() throws LineUnavailableException {
        insertions = new ArrayList<>();

        var h = AudioSystem.getSourceDataLine(Parameters);
        var selectedDevinfo = h.getLineInfo();
        System.err.printf("Audio device: %s\n", selectedDevinfo.toString());

        // var selectedDevinfo *portaudio.DeviceInfo
        // devinfos, err := portaudio.Devices()
        // if err != nil {
        // 	panic(err)
        // }
        // for _, devinfo := range devinfos {
        // 	if 0 < devinfo.MaxOutputChannels {
        // 		if deviceName == devinfo.name {
        // 			selectedDevinfo = devinfo
        // 		}
        // 	}
        // }
        // if selectedDevinfo == nil {
        // 	panic("device not found")
        // }

        this.Parameters = portaudio.HighLatencyParameters(null, selectedDevinfo);
        // params := portaudio.StreamParameters{
        // 	Output: portaudio.StreamDeviceParameters{
        // 		Device: selectedDevinfo,
        // 		channels: selectedDevinfo.MaxOutputChannels,
        // 		Latency: selectedDevinfo.DefaultHighOutputLatency,
        // 	},
        // 	SampleRate: sampleRate,
        // 	FramesPerBuffer: 0,
        // }
    }

    // Insert は、インサーションエフェクトを追加します。
    public void Insert(vavi.sound.yamaha.ma.cmd.cli.internal.player.Insertion insertion) {
        this.insertions.add(insertion);
    }

    // Start は、processor によって生成される波形のオーディオデバイスへの出力を開始します。
    void Start(Supplier<double[]> processor, Consumer<Integer> controller) {
        var startTime = Instant.now();
        var maxLevel = new AtomicReference<>(32766.0 / 32767.0);

        System.err.printf("insertion %s\n", this.insertions);

        this.stream = portaudio.OpenStream(this.Parameters, (float[][] out) -> {
            // midiLatency := float64(this.stream.Info().OutputLatency) / float64(time.Millisecond)
            var sampleLen = 1000.0 / this.Parameters.getSampleRate();
            var midiLatency = (double) (out[0].length) * sampleLen;
            var now = Duration.between(startTime, Instant.now()).toMillis();
            for (var i = 0; i < out[0].length; i++) {
                now += sampleLen;
                controller.accept((int) (now - midiLatency));

                var lr = processor.get();
                for (var insertion : this.insertions) {
                    lr = insertion.Next(lr[0], lr[1]);
                }

                if (maxLevel.get() < lr[0] || maxLevel.get() < lr[1]) {
                    if (maxLevel.get() < lr[0]) {
                        maxLevel.set(lr[1]);
                    }
                    if (maxLevel.get() < lr[0]) {
                        maxLevel.set(lr[1]);
                    }
                    var db = Math.log10(maxLevel.get()) * 20.0;
                    System.err.printf("Clipping occurred: %2.1f\n", db);
                }

                out[0][i] = (float) lr[0];
                out[1][i] = (float) lr[1];
            }
        });

        System.err.printf("Sample rate: %f\n", this.stream.Info().SampleRate);
        System.err.printf("Output latency: %s\n", this.stream.Info().OutputLatency.String());

        err = this.stream.Start();
        if (err != null) {
            throw new IllegalStateException(err);
        }
    }
}
