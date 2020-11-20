package info.hkzlab.rosim.loader.board.boardio;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import info.hkzlab.rosim.loader.utilities.XModemCRC;
import jssc.SerialPort;
import jssc.SerialPortException;

public class XModemSender {
    private final static Logger logger = LoggerFactory.getLogger(XModemSender.class);

    private static int XMODEM_RETRIES = 15;

    private static int XMODEM_DATA_SIZE = 128;

    private static int XModem_SOH = 0x01;
    private static int XModem_EOT = 0x04;
    private static int XModem_ACK = 0x06;
    //private static int XModem_NACK = 0x15;
    private static int XModem_C = 0x43;

    private XModemSender() {
    };

    private static byte[] createPacket(byte[] buffer, int offset, int num) {
        byte[] data_pkt = new byte[XMODEM_DATA_SIZE];
        ByteBuffer pkt = ByteBuffer.allocate(XMODEM_DATA_SIZE + 5); // Allocate space for the XMODEM packet
        int remaining_data = buffer.length - offset;

        System.arraycopy(buffer, offset, data_pkt, 0, (XMODEM_DATA_SIZE < remaining_data) ? XMODEM_DATA_SIZE : remaining_data);
        int crc = XModemCRC.calc_crc(data_pkt);

        pkt.put((byte) XModem_SOH);
        pkt.put((byte) (num & 0xFF));
        pkt.put((byte) (~num & 0xFF));
        pkt.put(data_pkt);
        pkt.put((byte) ((crc >> 8) & 0xFF));
        pkt.put((byte) (crc & 0xFF));

        return pkt.array();
    }

    private static boolean sync(SerialPort port) throws SerialPortException {
        long start = System.currentTimeMillis();

        while((System.currentTimeMillis() - start) < 10_000) { // Wait for 10 seconds to sync the communication
            if(port.getInputBufferBytesCount() > 0) {
                byte[] data = port.readBytes(1);
                if((data[0] & 0xFF) == XModem_C) return true;
                else continue;
            } else try { Thread.sleep(1); } catch(InterruptedException e) {};
        } 

        return false;
    }

   public static boolean upload(SerialPort port, byte[] buffer) {
        int retries = XMODEM_RETRIES;

        int tot_pkts = (buffer.length / XMODEM_DATA_SIZE) + ((buffer.length % XMODEM_DATA_SIZE) > 0 ? 1 : 0);

        try {
            logger.info("XMODEM upload() -> starting upload of " + tot_pkts + " packets!");
            if (!sync(port)) return false;
            logger.debug("XMODEM upload() -> synced");

            int cur_pkt = 0;
            while((cur_pkt < tot_pkts) && (retries > 0)) {
                logger.debug("XMODEM upload() -> Sending packet " + cur_pkt + "/" + (tot_pkts-1));

                byte[] pkt = createPacket(buffer, cur_pkt * XMODEM_DATA_SIZE, cur_pkt);
                port.writeBytes(pkt);

                if(waitACK(port)) { 
                    cur_pkt++; 
                    retries = XMODEM_RETRIES;
                } else { 
                    logger.error("XMODEM upload() -> Failed transmission for packet " + cur_pkt + ". Retries left: " + (retries-1));
                    retries--;
                    continue; 
                }
            }

            if(retries > 0) {
                logger.info("XMODEM upload() -> Transmission done, sending EOT");
                retries = XMODEM_RETRIES;

                do {
                    port.writeByte((byte)XModem_EOT);
                    retries--;
                } while(!waitACK(port) && (retries > 0));
            }

            if(retries > 0) return true;
        } catch (SerialPortException e) {
            e.printStackTrace();
        }

        return false;
   }

   private static boolean waitACK(SerialPort port) throws SerialPortException {
        long start = System.currentTimeMillis();

        while((System.currentTimeMillis() - start) < 3_000) { // Wait for 3 seconds for an ack
            if(port.getInputBufferBytesCount() > 0) {
                byte[] data = port.readBytes(1);
                if((data[0] & 0xFF) == XModem_ACK) return true;
                else return false; // Everything else is not good...
            } else try { Thread.sleep(1); } catch(InterruptedException e) {};
        } 

        return false;
   }
}
