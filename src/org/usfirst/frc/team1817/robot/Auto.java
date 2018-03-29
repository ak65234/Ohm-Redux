package org.usfirst.frc.team1817.robot;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;;

public class Auto implements Runnable {

	//Autonomous selectors
	private final SendableChooser<String> AUTO = new SendableChooser<>();
	private final String TIMED_CROSS = "Timed Cross Line";
	private final String SWITCH_AUTO = "Switch";

	//Alliance station selectors
	private final SendableChooser<String> STATION = new SendableChooser<>();
	private final String LEFT_STATION = "Left station";
	private final String RIGHT_STATION = "Right station";
	private final String MIDDLE_STATION = "Middle station";

	//Field specific constants
	private final int ROBOT_LENGTH = 38;
	// private final int DISTANCE_TO_SWITCH_FRONT = 144 - ROBOT_LENGTH;
	private final int DISTANCE_TO_SWITCH_MID = 170 - ROBOT_LENGTH / 2;
	private final int DISTANCE_TO_SWITCH_BACK = 196;
	private final int SWITCH_LENGTH = 154;
	private final int LEFT_TURN = -90;
	private final int RIGHT_TURN = 90;
	private final int MID_RIGHT = 45;
	private final int MID_LEFT = -45;
	private final int HYPOTONUSE = 200;

	//Robot speeds
	private final double DRIVE_SPEED = 0.75;
	private final double TURN_SPEED = 0.65;

	//States
	private int state;
	private final int DISABLED = 0;
	private final int ENABLED = 1;

	//Robot classes
	private final DriverStation ds;
	private final Hardware hw;
	private final Drive drive;
	private final Shift shift;
	private final Hand hand;
	private final Fingers fingers;
	private final Timer timer;
	private final Thread t;

	private boolean hasError = false;

	public Auto(Hardware hw, Drive drive, Shift shift, Hand hand, Fingers fingers) {
		state = DISABLED;

		this.ds = DriverStation.getInstance();

		AUTO.addDefault(TIMED_CROSS, TIMED_CROSS);
		AUTO.addObject(SWITCH_AUTO, SWITCH_AUTO);

		SmartDashboard.putData("Auto", AUTO);

		STATION.addDefault(MIDDLE_STATION, MIDDLE_STATION);
		STATION.addObject(RIGHT_STATION, RIGHT_STATION);
		STATION.addObject(LEFT_STATION, LEFT_STATION);

		SmartDashboard.putData("Station", STATION);

		this.hw = hw;
		this.drive = drive;
		this.shift = shift;
		this.hand = hand;
		this.fingers = fingers;

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

	public void runAuto() {
		double time = timer.get();
		String station = STATION.getSelected();

		// Every autonomous will run in low gear the entire time
		shift.setInHighGear(false);

		switch (AUTO.getSelected()) {
		case TIMED_CROSS:
			timedCross(time);
			break;
		case SWITCH_AUTO:
			//Refactored to call switchLocation() every iteration to allow for recovery of an error
			if (switchLocation() == station.charAt(0)) {
				sameSideSwitchAuto(time);
			} else if (station == MIDDLE_STATION) {
				middleSwitchAuto(time);
			} else {
				oppositeSideSwitchAuto(time);
			}
			break;
		}
	}

	private void timedCross(double time) {
		if (time < 7.5) {
			drive.stop();
		} else if (time < 12) {
			drive.arcade(DRIVE_SPEED, 0);
		} else {
			drive.stop();
		}
	}

	private void middleSwitchAuto(double time) {
		if (!hasError) {
			if (time < 2.5) {
				gyroTurn(TURN_SPEED, selectAngle());
			} else if (time < 7.5) {
				gyroDriveForward(DRIVE_SPEED, HYPOTONUSE);
			} else if (time < 10) {
				gyroTurn(TURN_SPEED, -selectAngle());
			} else if (time < 11) {
				drive.arcade(0.5, 0);
			} else if (time < 12.5) {
				drive.stop();
				hand.score();
			} else {
				fingers.setSpeed(1.0);
			}
		} else {
			timer.reset(); //Make sure that the robot starts in at the correct action if an error occurs at the beginning
		}
	}

	private void sameSideSwitchAuto(double time) {
		if (time < 5.0) {
			gyroDriveForward(DRIVE_SPEED, DISTANCE_TO_SWITCH_MID);
		} else if (time < 7.5 && !hasError) {
			gyroTurn(TURN_SPEED, selectAngle());
		} else if (time < 8.5 && !hasError) {
			drive.arcade(0.5, 0);
		} else if (time < 10 && !hasError) {
			drive.stop();
			hand.score();
		} else {
			if (!hasError)
				fingers.setSpeed(1.0);
		}
	}

	private void oppositeSideSwitchAuto(double time) {
		if (time < 4.0) {
			gyroDriveForward(DRIVE_SPEED, DISTANCE_TO_SWITCH_BACK + ROBOT_LENGTH / 2);
		} else if (time < 6.5) {
			gyroTurn(TURN_SPEED, selectAngle());
		} else if (time < 9.5 && !hasError) {
			gyroDriveForward(DRIVE_SPEED + 0.15, SWITCH_LENGTH);
		} else if (time < 11.5 && !hasError) {
			gyroTurn(TURN_SPEED, selectAngle());
		} else if (time < 12.5 && !hasError) {
			drive.arcade(0.5, 0);
			hand.score();
		} else {
			drive.stop();
			if (!hasError)
				fingers.setSpeed(1.0);
		}
	}

	private char switchLocation() {
		String locations = DriverStation.getInstance().getGameSpecificMessage();
		if (locations.length() == 3) {
			hasError = false; //There is a chance that the error is recoverable later on.
			return locations.charAt(0);
		}
		hasError = true;
		return 'E'; //If the GameSpecificMessage is empty return an error state
	}

	private int selectAngle() {
		char location = switchLocation();
		if (location != 'E') {
			boolean isLeft = (location == 'L');
			switch (STATION.getSelected()) {
			case (MIDDLE_STATION):
				if (isLeft) {
					return MID_LEFT;
				} else {
					return MID_RIGHT;
				}
			case (LEFT_STATION):
				return RIGHT_TURN;
			default:
				return LEFT_TURN;
			}
		}
		return 0; //In the event that there is an error do not turn
	}

	private double normalize(double value, double max) {
		return Math.max(-max, Math.min(value, max));
	}

	private void gyroDriveForward(double speed, double distance) {
		double angle = -hw.gyro.getAngle();
		double dist = Math.max(hw.leftEncoder.getDistance(), hw.rightEncoder.getDistance());

		if (distance > 0 && dist < distance)
			drive.arcade(speed, normalize(angle / 100.0, speed));
		else if (distance <= 0 && dist > distance)
			drive.arcade(-speed, normalize(angle / 100.0, speed));
	}

	private void gyroTurn(double speed, double targetAngle) {
		double currentAngle = hw.gyro.getAngle();

		double turn = (targetAngle - currentAngle) / 10.0;

		drive.arcade(0, normalize(turn, speed));
	}
}