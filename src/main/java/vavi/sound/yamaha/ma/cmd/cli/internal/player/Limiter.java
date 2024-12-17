/*
 * https://github.com/but80/fmfm.core
 */

package vavi.sound.yamaha.ma.cmd.cli.internal.player;

// Insertion は、インサーションエフェクトを抽象化したインタフェースです。
interface Insertion {

    // Next は、次のサンプルを生成し、その左右それぞれの振幅を返します。
    double[] Next(double l, double r);
}

// Limiter は、インサーションエフェクト「リミッター」です。
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

    // NewLimiter は、新しい Limiter を作成します。
    Limiter(double sampleRate) {
        this.sampleRate = sampleRate;
        this.SetThreshold(-3.0).SetLookAhead(.005).SetAttack(.005).SetRelease(.02);
    }

    // SetThreshold は、スレッショルドレベル [dB] を設定します。
    Limiter SetThreshold(double v) {
        this.threshold = Math.pow(10, v / 20.0);
        this.thresholdDB = v;
        return this;
    }

    // SetLookAhead は、先読み時間 [秒] を設定します。
    Limiter SetLookAhead(double v) {
        var n = (int) (Math.ceil(this.sampleRate * v));
        this.buffer = new double[n][2];
        this.bufferPos = 0;
        return this;
    }

    // SetAttack は、アタックタイムを設定します。
    Limiter SetAttack(double sec) {
        this.attack = this.timeToMultiplier(sec);
        this.attackInv = 1.0 - this.attack;
        return this;
    }

    // SetRelease は、リリースタイムを設定します。
    Limiter SetRelease(double sec) {
        this.release = this.timeToMultiplier(sec);
        return this;
    }

    double timeToMultiplier(double sec) {
        var n = sec * this.sampleRate;
        return Math.pow(0.1 / 0.9, 1 / n); // result ^ n = 0.1/0.9;
//		return Math.exp(-0.9542 / n);
    }

    // Next は、次のサンプルを生成し、その左右それぞれの振幅を返します。
    @Override
    public double[] Next(double l, double r) {
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
