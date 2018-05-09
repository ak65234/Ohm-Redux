package org.usfirst.frc.team1817.robot;

import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.Timer;

public class Fingers implements Runnable {
	private final double DEADZONE = 0.1;

	public final double INTAKE = 1;
	public final double OUTTAKE = -1;

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

	/**
	 * Disables the fingers
	 */
	public void disable() {
		state = DISABLED;
	}

	/**
	 * Enables the fingers
	 */
	public void enable() {
		state = ENABLED;
	}

	/**
	 * Sets the fingers to move at a desired speed
	 */
	private void move() {
		speed = deadband(speed);
		intake.set(speed);
	}

	/**
	 * Sets the desired speed
	 * 
	 * @param value
	 *            The speed which the fingers should turn (From -1 to +1)
	 */
	public void setSpeed(double value) {
		speed = value;
	}

	/**
	 * Implements a deadband
	 * 
	 * @param value
	 *            The value wished to be applied to the motor
	 * @return The speed wished to be applied as long as it is outside the deadband
	 */
	private double deadband(double value) {
		return Math.abs(value) > DEADZONE ? value : 0.0;
	}
}