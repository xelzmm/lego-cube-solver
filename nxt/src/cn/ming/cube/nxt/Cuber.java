package cn.ming.cube.nxt;

import lejos.nxt.Button;

import java.io.*;
import java.util.Random;

/**
 * NXT Cuber Solver
 * Created by xelz on 2017/2/9.
 */
public class Cuber {
    public static void main(String[] args) {
//        CuberUtil.init();
////        CuberUtil.execute("U F2 D B2 U F2 D B2 U F2 D B2");
//        String[] faces = new String[]{"U", "F", "D", "B", "L", "R"};
//        String[] directions = new String[]{"", "'", "2"};
//        StringBuilder solution = new StringBuilder();
//        Random random = new Random();
//        for(int i = 1; i <= 10; i++) {
//            solution.append(faces[random.nextInt(6)]).append(directions[random.nextInt(3)]);
//            if(i != 10) solution.append(" ");
//        }
//        System.out.println(solution.toString());
//        CuberUtil.execute(solution.toString());
        new Thread(new Runnable() {
            @Override
            public void run() {
                CuberUtil.init();
            }
        }).start();

        final BluetoothHelper bt = new BluetoothHelper();
        if(!bt.connect()) {
            System.out.println("Bluetooth init failed!");
            System.out.println("Exiting...");
            Button.waitForAnyPress();
            return;
        }
        final DataInputStream in = bt.getDataIn();
        final DataOutputStream out = bt.getDataOut();
        new Thread(new Runnable() {
            @Override
            public void run() {
                do{
                    try {
                        byte done[] = {'D', 'O', 'N', 'E'};
                        System.out.print("CMD: ");
                        String command = in.readUTF();
                        if (command.equals("EXIT")) {
                            System.out.println(command);
                            break;
                        } else if (command.length() == 1) {
                            System.out.println("turn to " + command);
                            CuberUtil.changetoFacelet(command);
                        } else if (command.startsWith("SOLUTION:")) {
                            command = command.substring(10);
                            System.out.println(command);
                            CuberUtil.execute(command);
                        }
                        out.write(done);
                        out.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }while(true);
                System.out.println("Finished.");
                bt.close();
                Button.waitForAnyPress();
            }
        }).start();
    }
}
