package info.hkzlab.rosim.loader.uploader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import info.hkzlab.rosim.loader.board.boardio.ROsiMCmdInterface;
import info.hkzlab.rosim.loader.exceptions.ROsiMBoardException;
import info.hkzlab.rosim.loader.exceptions.ROsiMProtoException;

public class ROsiMUploader {
    public static enum FileType {
        BIN_8, BIN_16, BIN_16S // hi-lo bytes swapped
    };

    private final static Logger logger = LoggerFactory.getLogger(ROsiMUploader.class);

    private static final long MAX_FILESIZE = 1_048_576l;

    private ROsiMUploader() {
    };

    static public boolean upload(ROsiMCmdInterface rsci, String filePath, FileType fType) {
        switch (fType) {
            case BIN_8:
            case BIN_16:
            case BIN_16S:
                return uploadBinary(rsci, filePath, fType);
            default:
                return false;
        }
    }

    static private boolean uploadBinary(ROsiMCmdInterface rsci, String filePath, FileType fType) {
        boolean result = true;
        Path fp = Path.of(filePath);
        long fSize;
        byte[] fileBuffer;

        try {
            fSize = Files.size(fp);
            if (fSize > ((fType == FileType.BIN_8) ? (MAX_FILESIZE / 2) : MAX_FILESIZE)) {
                logger.error("File is above the maximum size that can be store by the ROsiM!");
                return false;
            }
            fileBuffer = Files.readAllBytes(fp);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        logger.info("Read the file, preparing for upload.");

        long start_time = System.currentTimeMillis();

        try {
            rsci.switchIO(true);
            
            switch(fType) {
                case BIN_8:
                    result = rsci.uploadBuffer(fileBuffer, 0);
                    break;
                case BIN_16:
                    result = rsci.uploadBuffer(fileBuffer, 1);
                    break;
                case BIN_16S:
                    result = rsci.uploadBuffer(fileBuffer, 2);
                    break;
                default:
                    result = false;
                    break;
            }
            
            if(result) {
                logger.info("XMODEM upload of " + (fileBuffer.length/1024) + "Kb completed in " + (System.currentTimeMillis()-start_time)/1000 + " seconds.");
                logger.info("Switching to external I/O!");
                rsci.switchIO(false);
            } else {
                logger.info("XMODEM upload failed.");
            }
        } catch (ROsiMBoardException | ROsiMProtoException e) {
            e.printStackTrace();
            return false;
        }

        return result;
    }
}
