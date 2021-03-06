package info.hkzlab.rosim.loader;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import info.hkzlab.rosim.loader.utilities.BitUtils;
import info.hkzlab.rosim.loader.utilities.XModemCRC;

public class UtilsTest 
{
    @Test
    public void bitUtilsShouldCorrectlyModifyBitfields() {
        assertEquals("0b01010101 with a selection mask 0x55 should be consolidated into 0b1111", 0x0F, BitUtils.consolidateBitField(0x55, 0x55));
        assertEquals("0b11111111 with a selection mask 0x55 should be consolidated into 0b1111", 0x0F, BitUtils.consolidateBitField(0xFF, 0x55));
        assertEquals("0b01010101 with a selection mask 0xAA should be consolidated into 0", 0, BitUtils.consolidateBitField(0x55, 0xAA));
        assertEquals("0b01010101 with a selection mask 0xAA should be consolidated into 0b0101", 0x05, BitUtils.consolidateBitField(0x55, 0xF0));
        
        assertEquals("0b00001111 with a scatter mask 0xAA should be scattered into 0b10101010", 0xAA, BitUtils.scatterBitField(0x0F, 0xAA));
        assertEquals("0b00001111 with a scatter mask 0x03 should be scattered into 0b00000011", 0x03, BitUtils.scatterBitField(0x0F, 0x03));
        assertEquals("0b01010101 with a scatter mask 0x0F should be scattered into 0b00000101", 0x05, BitUtils.scatterBitField(0x55, 0x0F));
        assertEquals("0b01011111 with a scatter mask 0xF0 should be scattered into 0b11110000", 0xF0, BitUtils.scatterBitField(0x5F, 0xF0));
    }

    @Test
    public void crcShouldBeCorrectlyCalculated() {
        byte[] sampleData = new byte[] { 0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x38, 0x39, (byte)0xFF};

        int crc = XModemCRC.calc_crc(sampleData);

        assertEquals("CRC should be correctly calculated", 0xFB82, crc);
    }
}
