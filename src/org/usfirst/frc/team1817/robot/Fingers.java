package org.usfirst.frc.team1817.robot;

import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.Timer;

// TODO Manual override
public class Fingers implements Runnable {
    private final double DEADZONE = 0.1;

    private int state;
    private final int DISABLED = 0;
    private final int ENABLED = 1;

    private double speed;
    private final SpeedControllerGroup intake;
    private final Thread t;

    public Fingers(Hardware hw) {
        state = DISABLED;

        speed = 0.0;

        this.intake = hw.intake;

        t = new Thread(this, "Fingers");
        t.start();
    }

    public void run() {
        while (!Thread.interrupted()) {
            switch (state) {
            case DISABLED:
                intake.stopMotor();
                speed = 0.0;
                break;
            case ENABLED:
                move();
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

    private void move() {
        speed = deadband(speed);
        intake.set(speed);
    }

    public void setSpeed(double value) {
        speed = value;
    }

    private double deadband(double value) {
        return Math.abs(value) > DEADZONE ? value : 0.0;
    }
}