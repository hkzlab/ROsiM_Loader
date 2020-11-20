# ROsiM Loader

## Introduction

The ROsiM Loader is the companion application for the [ROsiM ROM simulator](https://github.com/hkzlab/ROsiM) and it's used to upload ROM dumps into the board's memory and automatically control the line drivers.

It's a Maven Java project, and can be used on both Windows and Linux without issues.

## Command line

Command syntax for this tool is pretty simple:

```sh
java -jar loader.jar <serial port> <source file> <file type>
```

Where `<serial port>` is the serial device on Linux (e.g. `/dev/ttyUSB0`) or the COM port on windows (e.g. `COM4`) and `<source file>` is the file to be uploaded.

`<file type>` can be one of the following:

- `BIN_8`: The file will be treated as a binary dump from an 8 bit ROM
- `BIN_16`: The file will be treated as a binary dump from a 16 bit ROM. ODD bytes in the file will end up in the high byte of the output, EVEN bytes will end up in the low byte.
- `BIN_16S`: The file will be treated like in `BIN_16`, but each two bytes will be swapped.

## Upload procedure

Once the upload starts, the board will enable its external RESET line, you should connect this to the reset line of your target board.

While the upload is ongoing, the internal SRAMs are disconnected from the target's circuit, they will be connected only after the upload completes, immediately followed by the RESET line being disabled.

