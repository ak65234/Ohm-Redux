package org.usfirst.frc.team1817.robot;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;;

public class Auto implements Runnable {
    private final SendableChooser<String> auto = new SendableChooser<>();
    private final String TIMED_CROSS = "Timed Cross Line";
    private final String SWITCH_AUTO = "Switch Test";

    private final int ROBOT_LENGTH = 38;
    // private final int DISTANCE_TO_SWITCH_FRONT = 144 - ROBOT_LENGTH;
    // private final int DISTANCE_TO_SWITCH_MID = 170 - ROBOT_LENGTH / 2;
    private final int DISTANCE_TO_SWITCH_BACK = 196;
    // private final int SWITCH_LENGTH = 154;
    private final int LEFT_TURN = -90;
    // private final int RIGHT_TURN = 90;

    private int state;
    private final int DISABLED = 0;
    private final int ENABLED = 1;

    private final DriverStation ds;
    private final Hardware hw;
    private final Drive drive;
    private final Shift shift;
    // private final Hand hand;
    // private final Fingers fingers;
    private final Timer timer;
    private final Thread t;

    public Auto(Hardware hw, Drive drive, Shift shift, Hand hand, Fingers fingers) {
        state = DISABLED;

        this.ds = DriverStation.getInstance();

        auto.addDefault(TIMED_CROSS, TIMED_CROSS);
        auto.addObject(SWITCH_AUTO, SWITCH_AUTO);
        SmartDashboard.putData(auto);

        this.hw = hw;
        this.drive = drive;
        this.shift = shift;
        // this.hand = hand;
        // this.fingers = fingers;

        timer = new Timer();
        timer.start();

        t = new Thread(this, "Auto");
        t.start();
    }

    public void run() {
        while (!Thread.interrupted()) {
            SmartDashboard.putNumber("AUTO STATE", state);

            switch (state) {
            case DISABLED:
                break;
            case ENABLED:
                runAuto();
                break;
            }

            if (!ds.isAutonomous()) {
                stop();
            }

            Timer.delay(0.005);
        }
    }

    public void disable() {
        state = DISABLED;
    }

    public void start() {
        state = ENABLED;

        timer.reset();
        timer.start();
    }

    public void stop() {
        state = DISABLED;

        timer.stop();
        timer.reset();
    }

    public void runAuto(){
        double time = timer.get();
        switch(auto.getSelected()){
            case TIMED_CROSS:
                timedCross(time);
                break;
            case SWITCH_AUTO:
                switchAuto(time);
                break;
        }
    }

    public void timedCross(double time) {
        shift.setInHighGear(false);

        if (time < 7.5) {
            drive.stop();
        } else if (time < 12) {
            drive.arcade(0.75, 0);
        } else {
            drive.stop();
        }
    }

    public void switchAuto(double time) {
        if (time < 5.0) {
            gyroDriveForward(0.75, DISTANCE_TO_SWITCH_BACK + ROBOT_LENGTH / 2);
        } else if(time < 7.5){
            gyroTurn(0.75, LEFT_TURN);
        } else {
            drive.stop();
        }
    }

    private double normalize(double value, double max) {
        return Math.max(-max, Math.min(value, max));
    }

    public void gyroDriveForward(double speed, double distance) {
        double angle = hw.gyro.getAngle();
        double dist = Math.max(hw.leftEncoder.getDistance(), hw.rightEncoder.getDistance());

        if (distance > 0 && dist < distance)
            drive.arcade(speed, normalize(-angle / 100.0, speed));
        else if (distance <= 0 && dist > distance)
            drive.arcade(-speed, normalize(-angle / 100.0, speed));
    }

    public void gyroTurn(double speed, double angle) {
        double ang = hw.gyro.getAngle();

        double turn = (angle - ang) / 10.0;

        drive.arcade(0, normalize(turn, speed));
    }
}