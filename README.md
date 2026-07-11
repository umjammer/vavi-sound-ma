[![Release](https://jitpack.io/v/umjammer/vavi-sound-ma.svg)](https://jitpack.io/#umjammer/vavi-sound-ma)
[![Java CI](https://github.com/umjammer/vavi-sound-ma/actions/workflows/maven.yml/badge.svg)](https://github.com/umjammer/vavi-sound-ma/actions/workflows/maven.yml)
[![CodeQL](https://github.com/umjammer/vavi-sound-ma/actions/workflows/codeql.yml/badge.svg)](https://github.com/umjammer/vavi-sound-ma/actions/workflows/codeql.yml)
![Java](https://img.shields.io/badge/Java-25-b07219)

# vavi-sound-ma

a YAMAHA MA-5 (YMU765) / YMF825 FM synthesizer sound voice patch loader.

this project is based on [fmfm.core](https://github.com/but80/fmfm.core).

### sample of

 * protobuf, maven plugin
 * jazzer junit5

## Install

* [maven](https://jitpack.io/#umjammer/vavi-sound-ma)

## Usage

### user

- [vavi-apps-mfiplayer](https://github.com/umjammer/vavi-apps-mfiplayer)

## References

* Yamaha
  * [original](https://github.com/but80/fmfm.core)
  * https://github.com/madscient/FITOMApp/blob/dbb7c257e78916dc5efb0a0c4d2b808f195b5a05/FITOM_config/Program.cs#L113
  * https://github.com/denjhang/MA-3-MegaMod
  * https://github.com/GillesLACAUD/OPLA-Source-code
  * https://github.com/vampirefrog/fmtoy
  * https://github.com/soywiz-archive/jdosbox (opl series)
  * https://github.com/jcrona/rovio-fw (ma series controller side)
  * https://github.com/tillt/retrocode (smaf)
  * https://moddingwiki.shikadi.net/wiki/IBK_Format
  * http://khhl0fx.web.fc2.com/melo/neiro.html 🇯🇵
  * https://lpcwiki.miraheze.org/wiki/Yamaha_SMAF/MA-5
  * https://keim.hatenablog.com/entry/20080827/p1 🇯🇵

* ProtoBuffer
  * https://github.com/protostuff/protostuff

### License

[MIT License](LICENSE)

## TODO

- Analyze ATS-MA5 output
  - Waveform of DVB
  - MIDI vibrato resolution
  - Channel pan resolution
  - Channel pan and voice pan blending
- extract soundfont reader .vm3 .vm5 ... `vavi.sound.yamaha.smaf.voice`
  - DefMA3_16.vm3
- protobuf is not needed (it makes things difficult for this project)
