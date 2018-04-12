package org.usfirst.frc.team1817.robot;

import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.Timer;

public class Shift implements Runnable {
    private int state;
    private final int DISABLED = 0;
    private final int ENABLED = 1;

    private boolean lowGear;
    private final Servo front, back;
    // private final Thread t;

    public Shift(Hardware hw) {
        state = DISABLED;

        lowGear = false;

        front = hw.frontShifter;
        back = hw.backShifter;

        // t = new Thread(this, "Shift");
        // t.start();
    }

    public void run() {
        while (!Thread.interrupted()) {
            switch (state) {
            case DISABLED:
                front.setDisabled();
                back.setDisabled();
                break;
            case ENABLED:
                shift();
                break;
            }

            Timer.delay(0.005);
        }
    }

    public void disable() {
        state = DISABLED;
    }

    public void enable() {
        state = ENABLED;
    }

    public void shift() {
        if (lowGear) {
            front.setAngle(110);
            back.setAngle(0);
        } else {
            front.setAngle(0);
            back.setAngle(100);
        }
    }

    public void setInHighGear(boolean value) {
        lowGear = !value;
    }
}