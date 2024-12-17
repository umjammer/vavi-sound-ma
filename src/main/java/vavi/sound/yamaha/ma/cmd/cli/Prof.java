/*
 * https://github.com/but80/fmfm.core
 */

package vavi.sound.yamaha.ma.cmd.cli;

import java.io.IOException;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpServer;

// +build prof
class Prof {

    void init() throws IOException {
        var port = 80;
        var l = HttpServer.create(new InetSocketAddress(port), 0);
        System.err.printf("> go tool pprof http://127.0.0.1:%s/debug/pprof/profile\n", port);
        System.err.printf("> pprof -http=localhost:8080 ~/pprof/pprof.127.0.0.1:%s.samples.cpu.001.pb.gz\n", port);
        l.start();
    }
}
