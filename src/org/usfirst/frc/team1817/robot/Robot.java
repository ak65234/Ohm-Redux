package org.usfirst.frc.team1817.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Robot extends TimedRobot {
	private static final String kDefaultAuto = "Default";
	private static final String kCustomAuto = "My Auto";
	private String m_autoSelected;
	private SendableChooser<String> m_chooser = new SendableChooser<>();

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
		m_chooser.addDefault("Default Auto", kDefaultAuto);
		m_chooser.addObject("My Auto", kCustomAuto);
		SmartDashboard.putData("Auto choices", m_chooser);

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
		auto.start();
	}

	@Override
	public void teleopInit() {
		auto.stop();

		drive.enable();
		shift.enable();
		fingers.enable();

		shiftToggle.set(false);
	}

	@Override
	public void teleopPeriodic() {
		double LY = ctrls.driver.getY(GenericHID.Hand.kLeft);
		double RX = ctrls.driver.getX(GenericHID.Hand.kRight);
		double dLT = ctrls.driver.getTriggerAxis(GenericHID.Hand.kLeft);
		double dRT = ctrls.driver.getTriggerAxis(GenericHID.Hand.kRight);
		boolean RB = ctrls.driver.getBumper(GenericHID.Hand.kRight);

		double mLT = ctrls.manipulator.getTriggerAxis(GenericHID.Hand.kLeft);
		double mRT = ctrls.manipulator.getTriggerAxis(GenericHID.Hand.kRight);
		boolean A = ctrls.manipulator.getAButton();
		boolean X = ctrls.manipulator.getXButton();
		boolean Y = ctrls.manipulator.getYButton();

		drive.arcade(-LY, RX);

		shiftToggle.update(RB);
		shift.setInHighGear(shiftToggle.get());

		if (A) {
			hand.extend();
		} else if (X) {
			hand.score();
		} else if (Y) {
			hand.stow();
		} else {
			hand.manualMove(mLT - mRT);
		}

		fingers.setSpeed(dRT - dLT);
	}

	@Override
	public void disabledInit() {
		drive.disable();
		hand.disable();
		fingers.disable();
	}
}
