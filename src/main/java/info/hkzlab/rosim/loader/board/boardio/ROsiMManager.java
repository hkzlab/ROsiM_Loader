package info.hkzlab.rosim.loader.board.boardio;

import static jssc.SerialPort.*;

import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import info.hkzlab.rosim.loader.board.rosimproto.ROsiMProto;
import info.hkzlab.rosim.loader.exceptions.ROsiMProtoException;
import jssc.SerialPort;
import jssc.SerialPortException;
import jssc.SerialPortTimeoutException;

public class ROsiMManager {
    private final Logger logger = LoggerFactory.getLogger(ROsiMManager.class);

    private SerialPort serport = null;

    private final static int SERIAL_READ_RETRIES = 20;
    private final static String REMOTE_MODE_STRING = "REMOTE_CONTROL_ENABLED";

    public ROsiMManager(final String serPort) {
        serport = new SerialPort(serPort);

        try {
            serport.openPort();
        } catch (final SerialPortException e) {
            e.printStackTrace();
            serport = null;
        }

        if (serport != null) {
            try {
                serport.setParams(230400, DATABITS_8, STOPBITS_1, PARITY_NONE);
            } catch (final SerialPortException e) {
                e.printStackTrace();
                try {
                    serport.closePort();
                    serport = null;
                } catch (final SerialPortException e1) {
                    ;
                }
            }
        }
    }

    private void resetBoard() {
        if (serport != null && serport.isOpened()) {
            try {
                serport.setDTR(true);
                try {
                    Thread.sleep(1000);
                } catch (final InterruptedException e) {
                }
                ;
                serport.setDTR(false);
            } catch (final SerialPortException e) {
                e.printStackTrace();
            }
        }
    }

    private void purgeRX() {
        if (serport != null && serport.isOpened()) {
            while (true) {
                try {
                    serport.readString(50, 200);
                } catch (SerialPortTimeoutException | SerialPortException e) {
                    break;
                }
            }
        }
    }

    public void writeCommand(String command) {
        if ((serport != null) && serport.isOpened()) {
            try {
                logger.trace("Command -> " + command);
                serport.writeBytes(command.getBytes(StandardCharsets.US_ASCII));
            } catch (SerialPortException e) {
                e.printStackTrace();
            }
        }
    }

    public String readResponse() throws ROsiMProtoException {
        if ((serport != null) && serport.isOpened()) {
            StringBuffer respBuf = new StringBuffer();

            try {
                int retries = SERIAL_READ_RETRIES;
                String resp = null;

                while (retries-- > 0) {
                    resp = serport.readString();
                    if (resp == null) {
                        try {
                            Thread.sleep(1);
                        } catch (InterruptedException e) {
                        }
                        ;
                    } else {
                        respBuf.append(resp);
                        retries = SERIAL_READ_RETRIES; // Reset the retries counter
                        if (ROsiMProto.isStringResponseCommand(respBuf.toString()))
                            break; // If we end with a character that could terminate the response, exit from here
                        else if (ROsiMProto.isStringComment(respBuf.toString())) {
                            respBuf.delete(0, respBuf.length());
                            continue;
                        } else if (ROsiMProto.isResponseError(respBuf.toString())) {
                            throw new ROsiMProtoException("Error response to command");
                        } else if (ROsiMProto.isResponseInvalid(respBuf.toString())) {
                            throw new ROsiMProtoException("Invalid command");
                        }
                    }
                }

                logger.trace("Response <- " + respBuf.toString());

                return respBuf.toString().trim();
            } catch (SerialPortException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    public boolean enterRemoteMode() {
        if (serport != null && serport.isOpened()) {
            resetBoard();
            try { Thread.sleep(1000); } catch (final InterruptedException e) {};

            int retries = 5;

            // Wait for it to boot
            try {
                while (retries-- >= 0) {
                    logger.info("Attempting to connect to the board. Retries left " + retries);

                    serport.purgePort(PURGE_RXABORT | PURGE_TXCLEAR);
                    purgeRX();

                    try { Thread.sleep(100); } catch (InterruptedException e) {};
                    final String response = serport.readString();

                    if ((response != null)) {
                        if (response.trim().endsWith(REMOTE_MODE_STRING))
                            return true;
                        else
                            return false;
                    }

                    try { Thread.sleep(1000); } catch (InterruptedException e) {};
                }
            } catch (SerialPortException e) {
                e.printStackTrace();
                return false;
            }
        }

        return false;
    }

    public void cleanup() {
        try {
            if ((serport != null) && serport.isOpened())
                serport.closePort();
        } catch (final SerialPortException e) {
            e.printStackTrace();
        }
    }

    protected SerialPort getSerialPort() {
        return serport;
    }
}