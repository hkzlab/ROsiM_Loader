package info.hkzlab.rosim.loader.exceptions;

import java.io.IOException;

public class ROsiMBoardException extends IOException {
    private static final long serialVersionUID = 1L;

    public ROsiMBoardException(String message) {
        super(message);
    }
}