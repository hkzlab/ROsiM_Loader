package info.hkzlab.rosim.loader.board.boardio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import info.hkzlab.rosim.loader.board.rosimproto.ROsiMProto;
import info.hkzlab.rosim.loader.exceptions.ROsiMBoardException;
import info.hkzlab.rosim.loader.exceptions.ROsiMProtoException;

public class ROsiMCmdInterface {
    private static final Logger logger = LoggerFactory.getLogger(ROsiMCmdInterface.class);

    private final ROsiMManager dpm;

    public ROsiMCmdInterface(ROsiMManager dpm) {
        this.dpm = dpm;
    }

    public void resetBoard() {
        dpm.writeCommand(ROsiMProto.buildRESETCommand());
    }

    public void setDefaults() throws ROsiMBoardException, ROsiMProtoException {
        dpm.writeCommand(ROsiMProto.buildDEFAULTCommand());
        if(!ROsiMProto.handleDEFAULTResponse(dpm.readResponse())) {
            logger.error("setDefaults() -> FAILED!");
            throw new ROsiMBoardException("setDefaults() command failed!");            
        }
    }

    public int write(int data) throws ROsiMBoardException, ROsiMProtoException {
        dpm.writeCommand(ROsiMProto.buildWRITECommand(data));
        int res = ROsiMProto.handleWRITEResponse(dpm.readResponse());

        if(res < 0) {
            logger.error("write("+String.format("%04X", data)+") -> FAILED!");
            throw new ROsiMBoardException("write("+String.format("%04X", data)+") command failed!");
        }

        return res;
    }

    public int read() throws ROsiMBoardException, ROsiMProtoException {
        dpm.writeCommand(ROsiMProto.buildREADCommand());
        int res = ROsiMProto.handleREADResponse(dpm.readResponse());

        if(res < 0) {
            logger.error("read() -> FAILED!");
            throw new ROsiMBoardException("read() command failed!");
        }

        return res;
    }

    public int address(int address) throws ROsiMBoardException, ROsiMProtoException {
        dpm.writeCommand(ROsiMProto.buildADDRESSCommand(address));
        int res = ROsiMProto.handleADDRESSResponse(dpm.readResponse());

        if(res < 0) {
            logger.error("address() -> FAILED!");
            throw new ROsiMBoardException("address() command failed!");
        }

        return res;
    }

    public int addressIncrement() throws ROsiMBoardException, ROsiMProtoException {
        dpm.writeCommand(ROsiMProto.buildADRINCRCommand());
        int res = ROsiMProto.handleADRINCRResponse(dpm.readResponse());

        if(res < 0) {
            logger.error("addressIncrement() -> FAILED!");
            throw new ROsiMBoardException("addressIncrement() command failed!");
        }

        return res;
    }

    public boolean switchRW(boolean rw) throws ROsiMBoardException, ROsiMProtoException {
        dpm.writeCommand(ROsiMProto.buildRWSWCommand(rw));
        int res = ROsiMProto.handleRWSWResponse(dpm.readResponse());

        if(res < 0) {
            logger.error("switchRW() -> FAILED!");
            throw new ROsiMBoardException("switchRW() command failed!");
        }

        return res == 1;
    }

    public boolean switchIntExt(boolean ext) {
        return false;
    }
}
