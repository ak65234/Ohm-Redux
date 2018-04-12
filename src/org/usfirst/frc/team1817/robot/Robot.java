package org.usfirst.frc.team1817.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.GenericHID;

public class Robot extends TimedRobot {

	private Hardware hw;
	private Controls ctrls;
	private Auto auto;
	private Drive drive;
	private Shift shift;
	private Hand hand;
	private Fingers fingers;

	private Toggle shiftToggle;
	private Toggle throttleToggleUp;
	private Toggle throttleToggleDown;

	@Override
	public void robotInit() {
		hw = new Hardware();
		ctrls = new Controls();

		drive = new Drive(hw);
		shift = new Shift(hw);
		hand = new Hand(hw);
		fingers = new Fingers(hw);

		shiftToggle = new Toggle();
		throttleToggleUp = new Toggle();
		throttleToggleDown = new Toggle();

		auto = new Auto(hw, drive, shift, hand, fingers);

		new Sensor_Watcher(hw);
	}

	@Override
	public void autonomousInit() {
		hw.resetSensors();
		enableThreads();

		drive.setAuto();
		auto.start();
	}

	@Override
	public void teleopInit() {
		auto.stop();

		drive.setTeleop();
		shiftToggle.set(false);
		throttleToggleUp.set(false);
		throttleToggleDown.set(false);
		hand.stow();

		enableThreads();

		fingers.setSpeed(0);

	}

	@Override
	public void teleopPeriodic() {
		double dLY = ctrls.driver.getY(GenericHID.Hand.kLeft); //Left Y axis
		double dRX = ctrls.driver.getX(GenericHID.Hand.kRight); //Right X axis
		double dLT = ctrls.driver.getTriggerAxis(GenericHID.Hand.kLeft); //Left trigger
		double dRT = ctrls.driver.getTriggerAxis(GenericHID.Hand.kRight); //Right trigger
		boolean dRB = ctrls.driver.getBumper(GenericHID.Hand.kRight); //Right bumper
		boolean dUp = ctrls.driver.getPOV() == Controls.POV.UP; //DPad up
		boolean dDown = ctrls.driver.getPOV() == Controls.POV.DOWN; //DPad down
		boolean dA = ctrls.driver.getAButton(); //A button
		boolean dX = ctrls.driver.getXButton(); //X button
		boolean dY = ctrls.driver.getYButton(); //Y button

		double mLT = ctrls.manipulator.getTriggerAxis(GenericHID.Hand.kLeft); //Left trigger
		double mRT = ctrls.manipulator.getTriggerAxis(GenericHID.Hand.kRight); //Right trigger

		drive.arcade(-dLY, dRX);

		shift.setInHighGear(shiftToggle.update(dRB));

		//TODO Make sure this is doing what is expected
		if (throttleToggleUp.update(dUp)) {
			drive.changeThrottleDown(0.01);
			throttleToggleUp.set(false);
		}
		if (throttleToggleDown.update(dDown)) {
			drive.changeThrottleDown(-0.01);
			throttleToggleDown.set(false);
		}

		if (dA) {
			hand.extend();
		} else if (dX) {
			hand.score();
		} else if (dY) {
			hand.stow();
		} else {
			hand.manualMove(mLT - mRT);
		}

		fingers.setSpeed(dRT - dLT);

		SmartDashboard.putNumber("Left encoder", hw.leftEncoder.getDistance());
		SmartDashboard.putNumber("Right Encoder", hw.rightEncoder.getDistance());
		SmartDashboard.putNumber("Gyro", hw.gyro.getAngle());
	}

	@Override
	public void disabledInit() {
		disableThreads();
	}

	public void enableThreads() {
		drive.enable();
		shift.enable();
		fingers.enable();
	}

	public void disableThreads() {
		drive.disable();
		shift.disable();
		hand.disable();
		fingers.disable();
	}
}
