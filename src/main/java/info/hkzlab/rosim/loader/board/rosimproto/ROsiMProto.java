package info.hkzlab.rosim.loader.board.rosimproto;

import java.util.ArrayList;

public class ROsiMProto {
    private final static String CMD_START = ">";
    private final static String CMD_END = "<";
   
    private final static String RESP_START = "[";
    private final static String RESP_END = "]";
    
    private final static String COMMENT_START = "#";

    private final static char CMD_RESET = 'K';
    private final static char CMD_MODEL = 'M';

    private final static char CMD_DEFAULT = 'D';
    private final static char CMD_VIEW = 'V';
    //private final static char CMD_TEST = 'T';
    
    private final static char CMD_WRITE = 'W';
    private final static char CMD_READ = 'R';
    private final static char CMD_ADDRESS = 'A';
    private final static char CMD_ADRINCR = 'I';
    
    private final static char CMD_ERST = 'E';
    private final static char CMD_IOSW = 'S';
    private final static char CMD_RWSW = 'X';

    private final static String CMD_RESP_ERROR = "CMD_ERROR";

    public static boolean isStringComment(String str) {
        return str.trim().startsWith(COMMENT_START);
    }

    public static boolean isStringResponseCommand(String cmd) {
        cmd = cmd.trim();
        return cmd.startsWith(RESP_START) && cmd.endsWith(RESP_END);
    }

    public static String buildMODELCommand() {
        return CMD_START+CMD_MODEL+CMD_END;
    }
    
    public static String buildADRINCRCommand() {
        return CMD_START+CMD_ADRINCR+CMD_END;
    }
    
    public static String buildDEFAULTCommand() {
        return CMD_START+CMD_DEFAULT+CMD_END;
    }
    
    public static String buildVIEWCommand() {
        return CMD_START+CMD_VIEW+CMD_END;
    }

    public static String buildREADCommand() {
        return CMD_START+CMD_READ+CMD_END;
    }

    public static String buildERSTCommand(final boolean reset) {
        return ""+CMD_START+CMD_ERST+" "+(reset ? '1':'0')+CMD_END;
    }

    public static String buildRWSWCommand(final boolean rw) {
        return ""+CMD_START+CMD_RWSW+" "+(rw ? '1':'0')+CMD_END;
    }

    public static String buildIOSWCommand(final boolean io) {
        return ""+CMD_START+CMD_IOSW+" "+(io ? '1':'0')+CMD_END;
    }

    public static String buildWRITECommand(final int data) {
        return ""+CMD_START+CMD_WRITE+" "+String.format("%04X", data & 0xFFFF)+CMD_END;
    }
    
    public static String buildADDRESSCommand(final int address) {
        return ""+CMD_START+CMD_ADDRESS+" "+String.format("%08X", address & 0x7FFFF)+CMD_END;
    }

    public static String buildRESETCommand() {
        return ""+CMD_START+CMD_RESET+CMD_END;
    }

    public static int handleREADResponse(final String response) {
        return handleByteResponse(response, CMD_READ);
    }

    public static int handleByteResponse(final String response, final char expectedCmd) {
        String[] readRes = parseResponse(response);

        if((readRes == null) || readRes.length != 2 || !isStringResponseCommand(response) || readRes[0].charAt(0) != expectedCmd) return -1;
        
        try {
            return Integer.parseInt(readRes[1], 16);
        } catch(NumberFormatException e) {
            return -1;
        }
    }

    public static int handleMODELResponse(final String response) {
        return handleByteResponse(response, CMD_MODEL);
    }

    public static int handleWRITEResponse(final String response) {
         String[] readRes = parseResponse(response);

        if((readRes == null) || readRes.length != 2 || !isStringResponseCommand(response) || readRes[0].charAt(0) != CMD_WRITE) return -1;
        
        try {
            return Integer.parseInt(readRes[1], 16);
        } catch(NumberFormatException e) {
            return -1;
        }       
    }

    public static String[] parseResponse(String response) {
        if(response == null) return null;

        ArrayList<String> respString = new ArrayList<>();        
        response = response.trim();

        if(response.equals(CMD_RESP_ERROR)) {
            respString.add(CMD_RESP_ERROR);
        } else if(response.startsWith(RESP_START) && response.endsWith(RESP_END)) {
            response = response.substring(1, response.length()-1).trim();
            char command = response.charAt(0);
            switch(command) {
                case CMD_MODEL:
                case CMD_READ: 
                case CMD_WRITE: {
                        String[] cmd_comp = response.split(" ");
                        if(cmd_comp.length != 2) return null;
                        return cmd_comp;
                    }
                default:
                    return null;
            }

        } else return null;

        return respString.toArray(new String[respString.size()]);
    }
}