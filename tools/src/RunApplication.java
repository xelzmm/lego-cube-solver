import lejos.nxt.remote.FileInfo;
import lejos.nxt.remote.NXTCommand;
import lejos.pc.comm.*;
import lejos.pc.tools.ToolsLogger;

import java.io.IOException;

/**
 * Run a program in a connected NXT
 * Created by xelz on 2017/2/11.
 */
public class RunApplication {
    public static void main(String[] args) {
        if(args.length != 1) {
            fail("Usage: RunApplication FileName");
        }
        String filename = args[0].endsWith(".nxj") ? args[0] : args[0] + ".nxj";
        NXTInfo[] nxts;
        NXTConnector nxtConnector = new NXTConnector();
        nxtConnector.addLogListener(new ToolsLogger());
        nxts = nxtConnector.search("", null, NXTCommFactory.USB);
        if (nxts.length == 0) {
            fail("No NXTs found");
        }
        NXTInfo nxt = nxts[0];
        try {
            log("Trying to connect to: " + nxt.name);
            NXTComm nxtComm = NXTCommFactory.createNXTComm(nxt.protocol);
            boolean open = nxtComm.open(nxt, NXTComm.LCP);
            if(!open) {
                fail("Unable to connect to: " + nxt.name + "@" + nxt.deviceAddress);
            }
            log("Connected to: " + nxt.name);
            NXTCommand nxtCommand = new NXTCommand(nxtComm);
            log("Trying to start program: " + filename);
            boolean found = false;
            FileInfo f = nxtCommand.findFirst("*.*");
            while (f != null)
            {
                if(f.fileName.equals(filename)) {
                    found = true;
                    break;
                }
                f = nxtCommand.findNext(f.fileHandle);
            }
            if(!found) {
                fail("File " + filename + " not found!");
                return;
            }
            nxtCommand.startProgram(filename);
            log("Start program " + filename + " succeed.");
        } catch (NXTCommException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void fail(String reason) {
        System.err.println(reason);
        System.exit(-1);
    }
    
    private static void log(String msg) {
        System.out.println(msg);
    }
}
