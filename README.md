[![Release](https://jitpack.io/v/umjammer/vavi-sound-ma.svg)](https://jitpack.io/#umjammer/vavi-sound-ma)
[![Java CI](https://github.com/umjammer/vavi-sound-ma/actions/workflows/maven.yml/badge.svg)](https://github.com/umjammer/vavi-sound-ma/actions/workflows/maven.yml)
[![CodeQL](https://github.com/umjammer/vavi-sound-ma/actions/workflows/codeql.yml/badge.svg)](https://github.com/umjammer/vavi-sound-ma/actions/workflows/codeql.yml)
![Java](https://img.shields.io/badge/Java-17-b07219)

# vavi-sound-ma

**WORK IN PROGRESS**

**fmFM** (Fake Mobile FM synth) is a YAMAHA MA-5 (YMU765) / YMF825 clone software FM synthesizer.

this is a fork of [fmfm.core](https://github.com/but80/fmfm.core).

### sample of

 * ~~protbuf, maven plugin~~
 * jazzer junit5

## Install

* [maven](https://jitpack.io/#umjammer/vavi-sound-ma)

## Usage

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
- soundfont reader 
