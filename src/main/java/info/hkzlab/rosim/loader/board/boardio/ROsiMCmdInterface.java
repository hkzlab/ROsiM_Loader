package info.hkzlab.rosim.loader.board.boardio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import info.hkzlab.rosim.loader.board.rosimproto.ROsiMProto;
import info.hkzlab.rosim.loader.exceptions.ROsiMBoardException;
import info.hkzlab.rosim.loader.exceptions.ROsiMProtoException;

public class ROsiMCmdInterface {
    private static final Logger logger = LoggerFactory.getLogger(ROsiMCmdInterface.class);

    private final ROsiMManager rsm;

    public ROsiMCmdInterface(ROsiMManager rsm) {
        this.rsm = rsm;
    }

    public void resetBoard() {
        rsm.writeCommand(ROsiMProto.buildRESETCommand());
    }

    public void setDefaults() throws ROsiMBoardException, ROsiMProtoException {
        rsm.writeCommand(ROsiMProto.buildDEFAULTCommand());
        if(!ROsiMProto.handleDEFAULTResponse(rsm.readResponse())) {
            logger.error("setDefaults() -> FAILED!");
            throw new ROsiMBoardException("setDefaults() command failed!");            
        }
    }

    public int write(int data) throws ROsiMBoardException, ROsiMProtoException {
        rsm.writeCommand(ROsiMProto.buildWRITECommand(data));
        int res = ROsiMProto.handleWRITEResponse(rsm.readResponse());

        if(res < 0) {
            logger.error("write("+String.format("%04X", data)+") -> FAILED!");
            throw new ROsiMBoardException("write("+String.format("%04X", data)+") command failed!");
        }

        return res;
    }

    public int read() throws ROsiMBoardException, ROsiMProtoException {
        rsm.writeCommand(ROsiMProto.buildREADCommand());
        int res = ROsiMProto.handleREADResponse(rsm.readResponse());

        if(res < 0) {
            logger.error("read() -> FAILED!");
            throw new ROsiMBoardException("read() command failed!");
        }

        return res;
    }

    public int address(int address) throws ROsiMBoardException, ROsiMProtoException {
        rsm.writeCommand(ROsiMProto.buildADDRESSCommand(address));
        int res = ROsiMProto.handleADDRESSResponse(rsm.readResponse());

        if(res < 0) {
            logger.error("address() -> FAILED!");
            throw new ROsiMBoardException("address() command failed!");
        }

        return res;
    }

    public int addressIncrement() throws ROsiMBoardException, ROsiMProtoException {
        rsm.writeCommand(ROsiMProto.buildADRINCRCommand());
        int res = ROsiMProto.handleADRINCRResponse(rsm.readResponse());

        if(res < 0) {
            logger.error("addressIncrement() -> FAILED!");
            throw new ROsiMBoardException("addressIncrement() command failed!");
        }

        return res;
    }

    public boolean switchRW(boolean read) throws ROsiMBoardException, ROsiMProtoException {
        rsm.writeCommand(ROsiMProto.buildRWSWCommand(read));
        int res = ROsiMProto.handleRWSWResponse(rsm.readResponse());

        if(res < 0) {
            logger.error("switchRW() -> FAILED!");
            throw new ROsiMBoardException("switchRW() command failed!");
        }

        return res != 0;
    }

    public boolean switchIO(boolean internal) throws ROsiMProtoException, ROsiMBoardException {
        rsm.writeCommand(ROsiMProto.buildIOSWCommand(internal));
        int res = ROsiMProto.handleIOSWResponse(rsm.readResponse());

        if(res < 0) {
            logger.error("switchIO() -> FAILED!");
            throw new ROsiMBoardException("switchIO() command failed!");
        }

        return res != 0;
    }

    public boolean extReset(boolean enabled) throws ROsiMProtoException, ROsiMBoardException {
        rsm.writeCommand(ROsiMProto.buildERSTCommand(enabled));
        int res = ROsiMProto.handleERSTResponse(rsm.readResponse());

        if(res < 0) {
            logger.error("extReset() -> FAILED!");
            throw new ROsiMBoardException("extReset() command failed!");
        }

        return res == 0;
    }

    public String[] viewState() throws ROsiMProtoException, ROsiMBoardException {
        rsm.writeCommand(ROsiMProto.buildVIEWCommand());
        String[] res = ROsiMProto.handleVIEWResponse(rsm.readResponse());

        if(res == null) {
            logger.error("viewState() -> FAILED!");
            throw new ROsiMBoardException("viewState() command failed!");
        }

        return res;
    }
}
