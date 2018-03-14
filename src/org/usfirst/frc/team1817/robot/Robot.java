package org.usfirst.frc.team1817.robot;

import edu.wpi.first.wpilibj.TimedRobot;
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

	@Override
	public void robotInit() {
		hw = new Hardware();
		ctrls = new Controls();

		drive = new Drive(hw);
		shift = new Shift(hw);
		hand = new Hand(hw);
		fingers = new Fingers(hw);

		shiftToggle = new Toggle();

		auto = new Auto(hw, drive, shift, hand, fingers);
	}

	@Override
	public void autonomousInit() {
		enableThreads();

		auto.start();
	}

	@Override
	public void teleopInit() {
		auto.stop();

		enableThreads();

		shiftToggle.set(false);
	}

	@Override
	public void teleopPeriodic() {
		double dLY = ctrls.driver.getY(GenericHID.Hand.kLeft);
		double dRX = ctrls.driver.getX(GenericHID.Hand.kRight);
		double dLT = ctrls.driver.getTriggerAxis(GenericHID.Hand.kLeft);
		double dRT = ctrls.driver.getTriggerAxis(GenericHID.Hand.kRight);
		boolean dRB = ctrls.driver.getBumper(GenericHID.Hand.kRight);

		double mLT = ctrls.manipulator.getTriggerAxis(GenericHID.Hand.kLeft);
		double mRT = ctrls.manipulator.getTriggerAxis(GenericHID.Hand.kRight);
		boolean mA = ctrls.manipulator.getAButton();
		boolean mX = ctrls.manipulator.getXButton();
		boolean mY = ctrls.manipulator.getYButton();

		drive.arcade(-dLY, dRX);

		shiftToggle.update(dRB);
		shift.setInHighGear(shiftToggle.get());

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
