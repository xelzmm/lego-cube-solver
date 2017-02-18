package cn.ming.cube.nxt;

import java.io.*;

import lejos.nxt.LCD;
import lejos.nxt.Sound;
import lejos.nxt.comm.BTConnection;
import lejos.nxt.comm.Bluetooth;

/**
 * Created by xelz on 2017/2/18.
 */
public class BluetoothHelper {
    BTConnection connection;

    public boolean connect() {
        boolean fail = false;
        System.out.println("Waiting Bluetooth...");
        connection = Bluetooth.waitForConnection(); // this method is very patient.
        System.out.println("Bluetooth connected.");
        System.out.println("Address: " + connection.getAddress());
        try {
            byte[] hello = new byte[4];
            int len = connection.read(hello, hello.length);
            if (len != 4 || hello[0] != 'C' || hello[1] != 'U'
                    || (hello[2] != 'B') || (hello[3] != 'E')) {
                System.out.println(new String(hello) + " " + len);
                fail = true;
                System.out.println("Handshake Failed!");
                connection.close();
            } else {
                OutputStream os = connection.openOutputStream();
                os.write(hello);
                os.flush();
            }
        } catch (Exception e) {
            System.out.println("Connection Error " + e.getMessage());
        }
        if (!fail) {
            System.out.println("Handshake Successful!");
            dataIn = connection.openDataInputStream();
            dataOut = connection.openDataOutputStream();
            Sound.beepSequence();
        }
        return !fail;
    }

    public DataInputStream getDataIn() {
        return dataIn;
    }

    public DataOutputStream getDataOut() {
        return dataOut;
    }


//    public BufferedReader getReader() {
//        return new BufferedReader(new InputStreamReader(dataIn));
//    }
//
//    public BufferedWriter getWriter() {
//        return new BufferedWriter(new OutputStreamWriter(dataOut));
//    }

    public void close() {
        if (connection == null)
            return;
        try {
            dataIn.close();
            dataOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        connection.close();
    }

    private DataInputStream dataIn;
    private DataOutputStream dataOut;
}
