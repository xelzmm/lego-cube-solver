package cn.ming.cube;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.Sound;
import lejos.nxt.comm.BTConnection;
import lejos.nxt.comm.Bluetooth;

/**
 * Created by xelz on 2017/2/16.
 */
public class BluetoothTest {
    BTConnection connection;

    public static void main(String[] args) {
        BluetoothTest test = new BluetoothTest();
        test.connect();
    }

    public void connect() {
        boolean fail = false;
        LCD.clear();
        LCD.drawString("Waiting", 0, 0);
        connection = Bluetooth.waitForConnection(); // this method is very
        // patient.
        Sound.beep();
        LCD.clear();
        try {
            byte[] hello = new byte[4];
            int len = connection.read(hello, hello.length);
//            System.out.println(new String(hello) + " " + len);

            if (len != 4 || hello[0] != 'C' || hello[1] != 'U'
                    || (hello[2] != 'B') || (hello[3] != 'E')) {
                fail = true;
                connection.close();
                return;
            } else {
//                LCD.drawString("CUBE", 0, 10);
                OutputStream os = connection.openOutputStream();
                os.write(hello);
                os.flush();
            }
        } catch (Exception e) {
            LCD.drawString("connection error " + e.getMessage(), 0, 0);
        }
        if (!fail) {
            LCD.drawString("Hello by iPhone!", 0, 0);
            dataIn = connection.openDataInputStream();
            dataOut = connection.openDataOutputStream();
            Button.waitForAnyPress();
            close();
        }
    }

    public DataInputStream getDataIn() {
        return dataIn;
    }

    public DataOutputStream getDataOut() {
        return dataOut;
    }

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
