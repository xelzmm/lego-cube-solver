package cn.ming.cube.nxt;

import java.util.LinkedList;

import lejos.nxt.*;

/**
 * Cuber Util
 * Created by xelz on 2017/2/9.
 */

class CuberUtil {
    // private static UltrasonicSensor sonic = new UltrasonicSensor(SensorPort.S1);
    private static final String U = "U";
    private static final String D = "D";
    private static final String F = "F";
    private static final String B = "B";
    private static final String L = "L";
    private static final String R = "R";
    private static final int PUSH_ANGLE = 70;
    private static final int HOLD_ANGLE = 45;
    private static final int RELEASE_ANGLE = 5;

    private static LinkedList<String> pushChain = new LinkedList<String>(),
            rotateChain = new LinkedList<String>();

    static {
        pushChain.add(D);
        pushChain.add(F);
        pushChain.add(U);
        pushChain.add(B);
        rotateChain.add(F);
        rotateChain.add(L);
        rotateChain.add(B);
        rotateChain.add(R);
    }

    private static NXTRegulatedMotor base = Motor.A;
    private static NXTRegulatedMotor arm = Motor.B;

    /**
     * Execute a specified expression
     * @param exp the expression to be executed
     */
    static void execute(String exp) {
        for (int i = 0; i < exp.length(); i++) {
            changetoFacelet(exp.substring(i, i + 1));
            if (i == exp.length() - 1 || exp.charAt(i + 1) == ' ') {
                turnClockwise();
                i++;
            } else if (exp.charAt(i + 1) == '\'') {
                turnAntiClockwise();
                i += 2;
            } else if (exp.charAt(i + 1) == '2') {
                turnSemiCycle();
                i += 2;
            }
//            Button.waitForAnyPress();
            // base.rotate(base.getTachoCount() - base.getPosition());
//            System.out.println(base.getPosition() + "/" + base.getTachoCount());
            // Button.waitForAnyPress();
        }
    }

    /**
     * make the specified facelet downwards to bottom
     * @param facelet target face
     */
    public static void changetoFacelet(String facelet) {
        switch (pushChain.indexOf(facelet)) {
            case 0:
                return;
            case 1:
                push();
                return;
            case 2:
                push();
                push();
                return;
            case 3:
                rotate(180, true);
                push();
                return;
            default:
        }
        switch (rotateChain.indexOf(facelet)) {
            case 1:
                rotate(90, true);
                push();
                return;
            case 3:
                rotate(-90, true);
                push();
                return;
            default:
        }
    }

    /**
     * push the cube using the long arm.
     * Face FRONT turns to DOWN
     */
    private static void push() {
        // push
        arm.rotate(PUSH_ANGLE);
        // shake to avoid stuck
//        arm.rotate(-5);
//        arm.rotate(5);
        sleep(50);
        // arm go back
        arm.rotate(-PUSH_ANGLE);
//        Button.waitForAnyPress();
        // reset facelet
        pushChain.add(pushChain.remove(0));
        rotateChain.set(0, pushChain.get(1));
        rotateChain.set(2, pushChain.get(3));
        //sleep(200);
    }

    /**
     * hold the first and second layer
     */
    private static void hold() {
        arm.rotateTo(HOLD_ANGLE);
    }

    /**
     * release the cube, arm goes back
     */
    private static void release() {
        arm.rotateTo(RELEASE_ANGLE);
    }

    /**
     * rotate the base (DOWN FACE) with a specified angle
     * @param angle the degrees to rotate
     * @param changeFacelet whether to update the facelet chain
     */
    private static void rotate(int angle, boolean changeFacelet) {
        int currentPosition = base.getTachoCount();
        switch (angle) {
            case 90:
                if (currentPosition > 225) {
                    base.rotateTo(currentPosition - 270);
                } else {
                    base.rotateTo(currentPosition + 90);
                }
                if (changeFacelet)
                    rotateChain.add(rotateChain.remove(0));
                break;
            case 180:
                if (currentPosition > 135) {
                    base.rotateTo(currentPosition - 180);
                }
                else {
                    base.rotateTo(currentPosition + 180);
                }
                if (changeFacelet) {
                    rotateChain.add(rotateChain.remove(0));
                    rotateChain.add(rotateChain.remove(0));
                }
                break;
            case -90:
                if (currentPosition < 45) {
                    base.rotateTo(currentPosition + 270);
                } else {
                    base.rotateTo(currentPosition - 90);
                }
                if (changeFacelet)
                    rotateChain.add(0, rotateChain.remove(3));
                break;
            default:
        }
        if (changeFacelet) {
            pushChain.set(1, rotateChain.get(0));
            pushChain.set(3, rotateChain.get(2));
        }
        //sleep(200);
    }

    /**
     * twist DOWN FACE 90 degrees
     */
    private static void turnClockwise() {
        hold();
        rotate(90, false);
        release();
    }

    /**
     * twist DOWN FACE -90 degrees
     */
    private static void turnAntiClockwise() {
        hold();
        rotate(-90, false);
        release();
    }

    /**
     * twist DOWN FACE 180 degrees
     */
    private static void turnSemiCycle() {
        hold();
        rotate(180, false);
        release();
    }

    /**
     * initialize the position of base and arm
     */
    static void init() {
        NXTMotor baseMotor = new NXTMotor(MotorPort.A);
        baseMotor.setPower(30);
        NXTMotor armMotor = new NXTMotor(MotorPort.B);
        armMotor.setPower(30);
        baseMotor.backward();
        armMotor.backward();
        int currentPositionA = baseMotor.getTachoCount();
        int currentPositionB = armMotor.getTachoCount();
        int previousPositionA, previousPositionB;
        do {
            previousPositionA = currentPositionA;
            previousPositionB = currentPositionB;
            sleep(100);
            currentPositionA = baseMotor.getTachoCount();
            currentPositionB = armMotor.getTachoCount();
        } while (currentPositionA != previousPositionA
                || currentPositionB != previousPositionB);
        baseMotor.stop();
        armMotor.stop();
        Motor.A.resetTachoCount();
        Motor.A.rotate(6);
        Motor.A.resetTachoCount();
        Motor.B.resetTachoCount();
//        System.out.println(Motor.A.getPosition() + "/" + Motor.A.getTachoCount());
        System.out.println("init done.");
    }

    private static void sleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ignored) {
        }
    }
}
