package org.usfirst.frc.team1817.robot;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;

public class AutoTestClass {
	//Autonomous selectors
	private final SendableChooser<String> AUTO;
	private final String TIMED_CROSS = "Timed Cross Line/Delayed Encoder cross";
	private final String SWITCH_AUTO = "Switch";
	private final String TWO_CUBE = "Two cube (only from center on tested)";

	//Alliance station selectors
	private final SendableChooser<String> STATION;
	private final String LEFT_STATION = "Left station";
	//private final String RIGHT_STATION = "Right station";
	private final String MIDDLE_STATION = "Middle station";

	//Field specific constants
	private final int ROBOT_LENGTH = 38;
	// private final int DISTANCE_TO_SWITCH_FRONT = 144 - ROBOT_LENGTH;
	private final int DISTANCE_TO_SWITCH_MID = (170 - ROBOT_LENGTH / 3);
	private final int DISTANCE_TO_SWITCH_BACK = 196 + ROBOT_LENGTH / 2;
	private final int SWITCH_LENGTH = 154;
	private final int LEFT_TURN = -85;
	private final int RIGHT_TURN = 85;
	private final int MID_RIGHT = 35;
	private final int MID_LEFT = -35;
	private final int HYPOTONUSE = 100;

	//Robot speeds
	private final double DRIVE_SPEED = 1.0;
	private final double TURN_SPEED = 0.75;

	//Robot classes
	private final DriverStation ds;
	private final Hardware hw;
	private final Drive drive;
	private final Hand hand;
	private final Fingers fingers;
	private final Timer timer;

	private boolean hasError = false;

	public AutoTestClass(Hardware hw, Drive drive, Shift shift, Hand hand, Fingers fingers,
			SendableChooser<String> AUTO, SendableChooser<String> STATION) {

		this.ds = DriverStation.getInstance();

		this.hw = hw;
		this.drive = drive;
		this.hand = hand;
		this.fingers = fingers;

		this.AUTO = AUTO;
		this.STATION = STATION;

		timer = new Timer();
		timer.start();

	}

	public void start() {
		reset();
		timer.reset();
		timer.start();
	}

	public void stop() {
		timer.stop();
		timer.reset();
	}

	public void runAuto() {
		start();
		reset();
		String station = STATION.getSelected();
		new Thread(() -> {
			switch (AUTO.getSelected()) {
			case TIMED_CROSS:
				encoderCross();
				break;
			case SWITCH_AUTO:
				while (switchLocation() == 'E' && getTime() < 5.0) {
					//do nothing
				}
				reset();
				if (switchLocation() == 'E') {
					toSwitchMid();
				} else if (switchLocation() == station.charAt(0)) {
					sameSideSwitchAuto();
				} else if (station == MIDDLE_STATION) {
					middleSwitchAuto();
				} else {
					oppositeSideSwitchAuto();
				}
				break;
			case TWO_CUBE:
				while (switchLocation() == 'E' && getTime() < 5.0) {
					//do nothing
				}
				reset();
				if (switchLocation() == 'E') {
					toSwitchMid();
				} else if (switchLocation() == station.charAt(0)) {
					sameSideSwitchAuto();
					defendCloseCube();
				} else if (station == MIDDLE_STATION) {
					middleSwitchAuto();
					secondCubeMid();
				} else {
					oppositeSideSwitchAuto();
				}
				break;
			}
		}).start();
	}

	private void encoderCross() {
		while (getTime() < 7.5) {
			drive.stop();
		}
		while (getTime() < 11) {
			gyroDriveForward(DRIVE_SPEED, DISTANCE_TO_SWITCH_MID);
		}
		drive.stop();
	}

	/*
	 * In the event of an error drive up to the middle of the switch
	 * This will allow us to score quickly if it recovers or get us across
	 * the line if it does not
	 */
	private void toSwitchMid() {
		gyroDriveForward(DRIVE_SPEED, DISTANCE_TO_SWITCH_MID);
	}

	//TODO Tune
	private void middleSwitchAuto() {
		double angle;
		double dist;
		while (getTime() < 0.5) { //Get away from the DS
			drive.arcade(0.5, 0);
		}
		reset();
		angle = selectAngle();
		while (!goodEnoughTurn(angle) && getTime() < 1.5) { //Turn 45 to switch
			gyroTurn(TURN_SPEED, angle);
		}
		reset();
		dist = HYPOTONUSE;
		while (!goodEnoughDrive(dist) && getTime() < 2.5) { //Drive to switch length
			gyroDriveForward(DRIVE_SPEED, dist);
		}
		reset();
		angle *= -1;
		while (!goodEnoughTurn(angle) && getTime() < 1.5) { //Turn back 45 to face switch
			gyroTurn(TURN_SPEED, angle);
		}
		reset();
		while (getTime() < 0.75) { //Drive into switch
			drive.arcade(0.6, 0);
			hand.score();
		}
		reset();
		while (getTime() < 1.5) { //Shoot
			drive.stop();
			fingers.setSpeed(1.0);
		}
		fingers.setSpeed(0);
		hand.stow();
		reset();
	}

	//TODO Test and tune
	private void secondCubeMid() {
		double angle;
		double dist;
		while (getTime() < 0.75) { // First backoff
			drive.arcade(-0.5, 0.0);
		}
		reset();
		angle = -selectAngle() * 2.35;
		while (getTime() < 1.5 && !goodEnoughTurn(angle)) { //Face stack
			gyroTurn(TURN_SPEED, angle);
		}
		reset();
		dist = SWITCH_LENGTH / 2 - ROBOT_LENGTH;
		while (getTime() < 2.0 && !goodEnoughDrive(dist)) { //Enter the cubes
			gyroDriveForward(0.5, dist);
			hand.topShelf();
			fingers.setSpeed(1.0);
		}
		reset();
		hand.stow();
		fingers.setSpeed(0.0);
		dist *= -0.9;
		while (getTime() < 1.5 && !goodEnoughDrive(dist)) { //Back away
			gyroDriveForward(0.5, dist);
		}
		reset();
		angle *= -1;
		while (getTime() < 1.5 && !goodEnoughTurn(angle)) { //Face switch
			gyroTurn(TURN_SPEED, angle);
		}
		reset();
		while (getTime() < 1.0) { //Drive into switch
			drive.arcade(0.5, 0);
			hand.extend();
		}
		drive.stop();
		fingers.setSpeed(1.0);
	}

	private void sameSideSwitchAuto() {
		double angle;
		double dist;
		dist = DISTANCE_TO_SWITCH_MID;
		while (!goodEnoughDrive(dist) && getTime() < 4.5) {
			gyroDriveForward(DRIVE_SPEED, dist);
		}
		while (hasError) {
			switchLocation();
		}
		reset();
		angle = selectAngle();
		while (!goodEnoughTurn(angle) && getTime() < 2.5) {
			gyroTurn(TURN_SPEED, angle);
		}
		reset();
		while (getTime() < 1) {
			drive.arcade(0.5, 0);
		}
		reset();
		while (getTime() < 1) {
			drive.stop();
			hand.score();
		}
		reset();
		while (getTime() < 2) {
			fingers.setSpeed(1);
		}
	}

	//TODO Test and tune
	private void oppositeSideSwitchAuto() {
		double angle;
		double dist;
		dist = DISTANCE_TO_SWITCH_BACK + ROBOT_LENGTH / 2;
		while (!goodEnoughDrive(dist) && getTime() < 4.0) {
			gyroDriveForward(DRIVE_SPEED, dist);
		}
		while (hasError) {
			switchLocation();
		}
		reset();
		angle = selectAngle();
		while (!goodEnoughTurn(angle) && getTime() < 2.5) {
			gyroTurn(TURN_SPEED, angle);
		}
		reset();
		dist = SWITCH_LENGTH;
		while (!goodEnoughDrive(dist) && getTime() < 3.0) {
			gyroDriveForward(DRIVE_SPEED + 0.15, dist);
		}
		reset();
		while (!goodEnoughTurn(angle) && getTime() < 2.5) {
			gyroTurn(TURN_SPEED, angle);
		}
		reset();
		while (getTime() < 1.0) {
			drive.arcade(0.5, 0);
			hand.score();
		}
		reset();
		while (getTime() < 2.0) {
			drive.stop();
			fingers.setSpeed(1);
		}
	}

	private void defendCloseCube() {
		double angle;
		double dist;
		reset();
		while (getTime() < 0.5) {
			drive.arcade(-0.5, 0);
		}
		reset();
		angle = -selectAngle();
		while (getTime() < 1.5 && !goodEnoughTurn(angle)) {
			gyroTurn(TURN_SPEED, angle);
		}
		reset();
		dist = 40 + ROBOT_LENGTH;
		while (getTime() < 1.5 && !goodEnoughDrive(dist)) {
			gyroDriveForward(0.75, dist);
		}
		reset();
		angle *= -1;
		if (angle < 0) {
			angle -= 30;
		} else {
			angle += 30;
		}
		while (getTime() < 2 && !goodEnoughTurn(angle)) {
			gyroTurn(TURN_SPEED, angle);
		}
		reset();
		hand.extend();
		fingers.setSpeed(-2);
		while (getTime() < 1.25) {
			drive.arcade(0.6, 0);
		}
		fingers.setSpeed(0);
		hand.stow();
		drive.stop();
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

	private boolean goodEnoughDrive(double target) {
		if (!isAuto()) // Effectively skips all steps if auto has ended
			return true;
		return Math.abs(hw.getDistance() - target) <= 4;// && hw.driveAtRest();
	}

	private boolean goodEnoughTurn(double angle) {
		if (!isAuto())
			return true;
		return Math.abs((hw.gyro.getAngle()) - angle) < 5 && hw.driveAtRest();
	}

	private boolean isAuto() {
		return ds.isAutonomous();
	}

	private double getTime() {
		if (!isAuto())
			return 100.00; //Overrides time based commands
		return timer.get();
	}

	private void reset() {
		hw.resetSensors();
		timer.reset();
	}

	private double normalize(double value, double max) {
		return Math.max(-max, Math.min(value, max));
	}

	private void gyroDriveForward(double speed, double distance) {
		double angle = -hw.gyro.getAngle();
		double dist = hw.getDistance();
		double power = (distance - dist) / 25;
		if (power > speed) {
			power = speed;
		}
		if (power < -speed) {
			power = -speed;
		}
		drive.arcade(power, normalize(angle / 100.0, speed));
	}

	private void gyroTurn(double speed, double targetAngle) {
		double currentAngle = hw.gyro.getAngle();

		double turn = (targetAngle - currentAngle) / 13;

		drive.arcade(0, normalize(turn, speed));
		hw.resetEncoders();
	}
}
