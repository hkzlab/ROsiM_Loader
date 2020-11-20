package info.hkzlab.rosim.loader.utilities;

public class XModemCRC {
    private XModemCRC() {};
    
    public static int calc_crc(final byte[] buffer) {
        int crc = 0;
        int count = buffer.length;
        int buf_idx = 0;
        short i;

        while (--count >= 0) {
            crc = crc ^ (buffer[buf_idx++] & 0xFF) << 8;
            i = 8;
            do {
                if ((crc & 0x8000) != 0) crc = crc << 1 ^ 0x1021;
                else crc = crc << 1;

                crc &= 0xFFFF;
            } while(--i > 0);
         }

        return crc;
    }
}
