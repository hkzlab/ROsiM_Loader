package info.hkzlab.rosim.loader;

import java.io.File;
import org.slf4j.*;

import info.hkzlab.rosim.loader.board.boardio.*;
import info.hkzlab.rosim.loader.uploader.ROsiMUploader.FileType;

public class App {
    private final static Logger logger = LoggerFactory.getLogger(App.class);

    private final static String version = App.class.getPackage().getImplementationVersion();

    private static String serialDevice = null;
    private static String inFile = null;
    private static FileType fType = null;

    public static void main(String[] args) throws Exception {
        logger.info("ROsiM Loader " + version);

        if (args.length < 3) {
            logger.error("Wrong number of arguments passed.\n" +
                         "rosim-loader <serial_device> <input_file> <fitype>\n\n");

            return;
        }

        parseArgs(args);

        ROsiMManager dpm = new ROsiMManager(serialDevice);
        ROsiMCmdInterface dpci = new ROsiMCmdInterface(dpm);

        if (!dpm.enterRemoteMode()) {
            logger.error("Unable to put DuPAL board in REMOTE MODE!");
            System.exit(-1);
        } 

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                dpci.resetBoard();
            }
        });
    }

    private static void parseArgs(String[] args) {
        serialDevice = args[0];
        inFile = args[1];

        try {
            fType = FileType.valueOf(args[2].toUpperCase());
        } catch(IllegalArgumentException e) {
            logger.error("Invalid file type " + args[2] + " specified!");
            System.exit(-1);
        }

        checkFilePath(inFile);
    }

    private static void checkFilePath(String path) {
        File file = new File(path);

        boolean exists = file.exists();
        boolean isDirectory = file.isDirectory();
        boolean isReadable = file.canRead();

        if(isDirectory) {
            logger.error("Input path " + path + " points to a directory, please specify an input file!");
            System.exit(-1);
        }

        if(!exists) {
            logger.error("Input path " + path + " does not point to an existing file!");
            System.exit(-1);
        }

        if(!isReadable) {
            logger.error("Input file " + path + " is not readable!");
            System.exit(-1);
        }
    }
}
