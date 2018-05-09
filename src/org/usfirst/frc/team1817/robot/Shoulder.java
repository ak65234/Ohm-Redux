package org.usfirst.frc.team1817.robot;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.SpeedControllerGroup;

public class Shoulder implements Runnable {

	SpeedControllerGroup shoulder;
	Encoder shoulderEncoder;

	Thread t;

	private final double DEADBAND = 0.07;

	public Shoulder(Hardware hw) {
		this.shoulder = hw.shoulder;
		this.shoulderEncoder = hw.shoulderEncoder;
		t = new Thread(this, "Shoulder");
		t.start();

	}

	public void run() {
		/*
		while(!Thread.interrupted()) {
		
		}
		*/
	}

	/**
	 * Checks to see if an input is outside the deadband
	 * 
	 * @param value
	 *            The speed the user wishes to apply to the motor
	 * @return The speed if it is outside of the deadband
	 */
	private boolean validRange(double value) {
		return Math.abs(value) > DEADBAND;
	}

	/**
	 * Manually move the shoulder
	 * 
	 * @param value
	 *            The speed at which the shoulder should move (Going one direction
	 *            it is heavily limited as to not damage the hardware)
	 */
	public void manualMove(double value) {
		if (validRange(value)) {
			if (value > 0.0) {
				shoulder.set(value / 4.0);
			} else {
				shoulder.set(value);
			}
		} else {
			shoulder.set(0);
		}
	}

}
