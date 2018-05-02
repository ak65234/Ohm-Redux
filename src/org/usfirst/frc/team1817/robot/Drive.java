package org.usfirst.frc.team1817.robot;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Drive implements Runnable {
	private final double DEADZONE = 0.1;
	private double ramp = 0.1;
	private boolean inAuto = false;

	private int state;
	private final int DISABLED = 0;
	private final int ENABLED = 1;

	private int mode;
	private final int TANK = 0;
	private final int ARCADE = 1;
	private final int STOP = 2;

	private DifferentialDrive chassis;
	private PowerDistributionPanel pdp;
	private double leftOrPower, rightOrTurn;
	private final Thread t;

	private double throttleDown = 0.04;

	public Drive(Hardware hw) {
		state = DISABLED;
		mode = ARCADE;

		this.chassis = hw.chassis;
		this.pdp = hw.pdp;

		leftOrPower = 0.0;
		rightOrTurn = 0.0;

		t = new Thread(this, "Drive");
		t.start();
	}

	public void run() {
		while (!Thread.interrupted()) {
			switch (state) {
			case DISABLED:
				chassis.stopMotor();
				leftOrPower = 0.0;
				rightOrTurn = 0.0;
				break;
			case ENABLED:
				drive();
				break;
			}

			Timer.delay(0.005);
		}
	}

	public void drive() {
		switch (mode) {
		case TANK:
			chassis.tankDrive(deadband(leftOrPower), deadband(rightOrTurn));
			break;
		case ARCADE:
			SmartDashboard.putNumber("Throttle down", throttleDown);
			SmartDashboard.putNumber("Forward", leftOrPower);
			SmartDashboard.putNumber("Turn", rightOrTurn);
			if(!inAuto) {
				chassis.arcadeDrive(deadband(leftOrPower), deadband(rightOrTurn));
			} else {
				chassis.arcadeDrive(leftOrPower, rightOrTurn);
			}
			break;
		case STOP:
			chassis.stopMotor();
			leftOrPower = 0;
			rightOrTurn = 0;
			break;
		}
	}

	public void disable() {
		state = DISABLED;
	}

	public void enable() {
		state = ENABLED;
	}

	public void setAuto() {
		inAuto = true;
		ramp = 0.01; //Had strange acceleration at 0.025
	}

	public void setTeleop() {
		inAuto = false;
		ramp = 0.1;
	}

	public void tank(double left, double right) {
		mode = TANK;

		double deltaL = Math.max(Math.min(left - leftOrPower, ramp), -ramp);
		double deltaR = Math.max(Math.min(right - rightOrTurn, ramp), -ramp);

		leftOrPower += deltaL;
		rightOrTurn += deltaR;
	}

	public void arcade(double power, double turn) {
		mode = ARCADE;
		if (getDriveCurrent() < 120 || throttleDown < 0.0009 && !DriverStation.getInstance().isAutonomous()) {
			double deltaP = Math.max(Math.min(power - leftOrPower, ramp), -ramp);
			double deltaT = Math.max(Math.min(turn - rightOrTurn, ramp), -ramp);

			leftOrPower += deltaP;
			rightOrTurn += deltaT;
		} else if(!DriverStation.getInstance().isAutonomous()){ //Throttle down the gearbox to conserve power
			double deltaP, deltaT;
			if (leftOrPower > 0) {
				deltaP = -throttleDown;
			} else {
				deltaP = throttleDown;
			}
			if (rightOrTurn > 0) {
				deltaT = -throttleDown;
			} else {
				deltaT = throttleDown;
			}
			if (inAuto && power == 0) {
				leftOrPower = 0;
			}
			if (inAuto && turn == 0) {
				rightOrTurn = 0;
			}
			leftOrPower += deltaP;
			rightOrTurn += deltaT;
		}
	}

	private double getDriveCurrent() {
		double current = pdp.getTotalCurrent();
		//double current = 0;
		//Left and right gearboxes
		//current += pdp.getCurrent(PH) + pdp.getCurrent(PH) + pdp.getCurrent(PH);
		//current += pdp.getCurrent(PH) + pdp.getCurrent(PH) + pdp.getCurrent(PH);
		return current;
	}

	/**
	 * Change the increment at which the throttle will decrease.
	 * 
	 * @param increment
	 *            Increment at which the robot will throttle down
	 */
	public void changeThrottleDown(double increment) {
		throttleDown += increment;
		if (throttleDown < 0.0) {
			throttleDown = 0;
		}
	}

	public void stop() {
		mode = STOP;
	}

	private double deadband(double value) {
		return Math.abs(value) > DEADZONE ? value : 0.0;
	}
}