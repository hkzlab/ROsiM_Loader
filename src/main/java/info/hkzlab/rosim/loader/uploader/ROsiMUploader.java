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
        BIN_8, BIN_16, BIN_16_S // hi-lo bytes swapped
    };

    private final static Logger logger = LoggerFactory.getLogger(ROsiMUploader.class);

    private static final long MAX_FILESIZE = 1_048_576l;

    private ROsiMUploader() {
    };

    static public boolean upload(ROsiMCmdInterface rsci, String filePath, FileType fType) {
        switch (fType) {
            case BIN_8:
            case BIN_16:
            case BIN_16_S:
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

        try {
            rsci.setDefaults();
            rsci.switchIO(true);
       
            switch(fType) {
                case BIN_8:
                    result = uploadBinary8(rsci, fileBuffer);
                    break;
                case BIN_16:
                    result = uploadBinary16(rsci, fileBuffer, false);
                    break;
                case BIN_16_S:
                    result = uploadBinary16(rsci, fileBuffer, true);
                    break;
                default:
                    result = false;
                    break;
            }
            
            rsci.switchIO(false);
        } catch (ROsiMBoardException | ROsiMProtoException e) {
            e.printStackTrace();
            return false;
        }

        return result;
    }

    private static boolean uploadBinary8(ROsiMCmdInterface rsci, byte[] buf)
            throws ROsiMProtoException, ROsiMBoardException {
        rsci.switchRW(false); // Write mode
        rsci.address(0);

        logger.info("uploadBinary8 -> Start upload!");

        for(int idx = 0; idx < buf.length; idx++) {
            rsci.write(buf[idx] & 0xFF);
            rsci.addressIncrement();
        }

        logger.info("uploadBinary8 -> Start verification!");

        rsci.switchRW(true); // Read mode
        rsci.address(0);

        for(int idx = 0; idx < buf.length; idx++) {
            int data = rsci.read();
            if(data != (buf[idx] & 0xFF)) {
                logger.error("uploadBinary8 -> Failed verification at address " + String.format("%08X", idx) + " E:"+String.format("%02X", data)+" A:"+String.format("%02X", (buf[idx] & 0xFF)));
                return false;
            }

            rsci.addressIncrement();
        }

        return true;
    }

    private static boolean uploadBinary16(ROsiMCmdInterface rsci, byte[] buf, boolean swap) 
        throws ROsiMProtoException, ROsiMBoardException {
        return false;
    }
}
