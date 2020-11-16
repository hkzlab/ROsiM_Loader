package info.hkzlab.rosim.loader;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

import org.junit.Test;

import info.hkzlab.rosim.loader.board.boardio.DuPALCmdInterface;
import info.hkzlab.rosim.loader.board.boardio.DuPALManager;
import info.hkzlab.rosim.loader.devices.PAL16L8Specs;
import info.hkzlab.rosim.loader.exceptions.DuPALBoardException;

public class CommandInterfaceTest 
{
    @Test
    public void commandInterfaceShouldBuildCorrectCommands() throws DuPALBoardException {
        DuPALManager dpmMock = mock(DuPALManager.class);
        DuPALCmdInterface dpci = new DuPALCmdInterface(dpmMock, new PAL16L8Specs());

        when(dpmMock.readResponse()).thenReturn("[R 57]");
        assertEquals("Issuing a read command via DPCI should return us the correct value", 0x57, dpci.read());
        when(dpmMock.readResponse()).thenReturn("[W 00BBCCDD]");
        assertEquals("Issuing a write command via DPCI should return us the same value as written", 0xBBCCDD, dpci.write(0xBBCCDD));
    }
}
