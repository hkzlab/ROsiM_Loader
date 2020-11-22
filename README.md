# ROsiM Loader

## Introduction

The ROsiM Loader is the companion application for the [ROsiM ROM simulator](https://github.com/hkzlab/ROsiM) and it's used to upload ROM dumps into the board's memory and automatically control the line drivers.

## How to build

It's a Maven Java project, and can be used on both Windows and Linux without issues. If you have Maven and a JDK >= 1.8, it should be sufficient to go in the base directory of this project and run

```sh
mvn package -f pom.xml
```

You'll then get your generated JARs in the `target` directory.

## Command line

Command syntax for this tool is pretty simple:

```sh
java -jar loader.jar <serial port> <source file> <file type> [I]
```

Where `<serial port>` is the serial device on Linux (e.g. `/dev/ttyUSB0`) or the COM port on windows (e.g. `COM4`) and `<source file>` is the file to be uploaded.

`<file type>` can be one of the following:

- `BIN_8`: The file will be treated as a binary dump from an 8 bit ROM
- `BIN_16`: The file will be treated as a binary dump from a 16 bit ROM. ODD bytes in the file will end up in the high byte of the output, EVEN bytes will end up in the low byte.
- `BIN_16S`: The file will be treated like in `BIN_16`, but each two bytes will be swapped.

If present, `I` will tell the ROsiM board to invert the external reset logic and use the `RESET` header in place of the `/RESET` one.

## Upload procedure

Once the upload starts, the board will assert its external RESET line. This line is meant to be connected to the reset circuitry of the target board.

While the upload is ongoing the SRAMs are isolated from the target's circuitrey, they will be connected only after the upload completes, immediately followed by the RESET line being disabled.

Once the upload completes and the RESET line is deasserted, the program will remain running, periodically pinging the ROSiM board to check the connection. 
If the user wishes to quit, press CTRL-C and the board will be reset to defaults and disconnected.
