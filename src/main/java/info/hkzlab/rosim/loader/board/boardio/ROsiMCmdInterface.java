package info.hkzlab.rosim.loader.board.boardio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import info.hkzlab.rosim.loader.board.rosimproto.ROsiMProto;
import info.hkzlab.rosim.loader.exceptions.ROsiMBoardException;

public class ROsiMCmdInterface {
    private static final Logger logger = LoggerFactory.getLogger(ROsiMCmdInterface.class);

    private final ROsiMManager dpm;

    public ROsiMCmdInterface(ROsiMManager dpm) {
        this.dpm = dpm;
    }

    public void resetBoard() {
        dpm.writeCommand(ROsiMProto.buildRESETCommand());
    }

    public void setDefaults() throws ROsiMBoardException {
        dpm.writeCommand(ROsiMProto.buildDEFAULTCommand());
        if(!ROsiMProto.handleDEFAULTResponse(dpm.readResponse())) {
            logger.error("setDefaults() -> FAILED!");
            throw new ROsiMBoardException("setDefaults() command failed!");            
        }
    }

    public int write(int data) throws ROsiMBoardException {
        dpm.writeCommand(ROsiMProto.buildWRITECommand(data));
        int res = ROsiMProto.handleWRITEResponse(dpm.readResponse());

        if(res < 0) {
            logger.error("write("+String.format("%08X", data)+") -> FAILED!");
            throw new ROsiMBoardException("write("+String.format("%08X", data)+") command failed!");
        }

        return res;
    }
}
