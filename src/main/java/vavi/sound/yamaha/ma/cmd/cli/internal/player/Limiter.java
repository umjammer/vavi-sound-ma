/*
 * https://github.com/but80/fmfm.core
 */

package vavi.sound.yamaha.ma.cmd.cli.internal.player;

// Insertion is an interface that abstracts insertion effects.
interface Insertion {

    // Next generates the next sample and returns its amplitude to the left and right.
    double[] next(double l, double r);
}

// Limiter is an insertion effect called a "limiter."
public class Limiter implements Insertion {

    double sampleRate;
    double attack;
    double attackInv;
    double release;
    double threshold;
    double thresholdDB;
    double attenuation;
    double[][] buffer;
    int bufferPos;

    // NewLimiter creates a new Limiter.
    public Limiter(double sampleRate) {
        this.sampleRate = sampleRate;
        this.setThreshold(-3.0).setLookAhead(.005).setAttack(.005).setRelease(.02);
    }

    // SetThreshold sets the threshold level [dB].
    public Limiter setThreshold(double v) {
        this.threshold = Math.pow(10, v / 20.0);
        this.thresholdDB = v;
        return this;
    }

    // SetLookAhead sets the look ahead time [seconds].
    Limiter setLookAhead(double v) {
        var n = (int) (Math.ceil(this.sampleRate * v));
        this.buffer = new double[n][2];
        this.bufferPos = 0;
        return this;
    }

    // SetAttack sets the attack time.
    Limiter setAttack(double sec) {
        this.attack = this.timeToMultiplier(sec);
        this.attackInv = 1.0 - this.attack;
        return this;
    }

    // SetRelease sets the release time.
    Limiter setRelease(double sec) {
        this.release = this.timeToMultiplier(sec);
        return this;
    }

    double timeToMultiplier(double sec) {
        var n = sec * this.sampleRate;
        return Math.pow(0.1 / 0.9, 1 / n); // result ^ n = 0.1/0.9;
//		return Math.exp(-0.9542 / n);
    }

    // Next generates the next sample and returns its amplitude to the left and right.
    @Override
    public double[] next(double l, double r) {
        this.buffer[this.bufferPos][0] = l;
        this.buffer[this.bufferPos][1] = r;
        this.bufferPos = (this.bufferPos + 1) % this.buffer.length;
        var v = Math.max(Math.abs(l), Math.abs(r));
        l = this.buffer[this.bufferPos][0];
        r = this.buffer[this.bufferPos][1];
        if (.0 <= this.thresholdDB) {
            return new double[] {l, r};
        }
        if (this.threshold <= v) {
            var db = 20.0 * Math.log10(v);
            var target = this.thresholdDB - db;
            this.attenuation = this.attack * this.attenuation + this.attackInv * target;
        } else {
            this.attenuation *= this.release;
        }
        var a = Math.pow(10.0, this.attenuation / 20.0);
        return new double[] {l * a, r * a};
    }
}
