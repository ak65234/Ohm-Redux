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
	}

	@Override
	public void autonomousInit() {
		hw.resetSensors();
		enableThreads();

		auto.start();
	}

	@Override
	public void teleopInit() {
		auto.stop();

		shiftToggle.set(false);
		throttleToggleUp.set(false);
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
		boolean dUp = ctrls.driver.getPOV() == 0; //DPad up
		boolean dDown = ctrls.driver.getPOV() == 180; //DPad down
		boolean mA = ctrls.driver.getAButton(); //A button
		boolean mX = ctrls.driver.getXButton(); //X button
		boolean mY = ctrls.driver.getYButton(); //Y button

		double mLT = ctrls.manipulator.getTriggerAxis(GenericHID.Hand.kLeft); //Left trigger
		double mRT = ctrls.manipulator.getTriggerAxis(GenericHID.Hand.kRight); //Right trigger

		drive.arcade(-dLY, dRX);

		shiftToggle.update(dRB);
		shift.setInHighGear(shiftToggle.get());

		//TODO Make sure this is doing what is expected
		throttleToggleUp.update(dUp);
		if(throttleToggleUp.get()) {
			drive.changeThrottleDown(0.01);
			throttleToggleUp.set(false);
		}
		throttleToggleDown.update(dDown);
		if(throttleToggleDown.get()) {
			drive.changeThrottleDown(-0.01);
			throttleToggleDown.set(false);
		}
			

		if (mA) {
			hand.extend();
		} else if (mX) {
			hand.score();
		} else if (mY) {
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
