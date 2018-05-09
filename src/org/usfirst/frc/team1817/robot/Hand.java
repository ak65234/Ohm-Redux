package org.usfirst.frc.team1817.robot;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Hand implements Runnable {
	private final int STOWED_THRESH = 30;
	//private final int EXTENDED_THRESH = 310;
	//private final int SCORE_THRESH = 215;
	private final int EXTENDED_THRESH = 370;
	private final int SCORE_THRESH = 215;
	private final int TOP_SHELF_THRESH = 280;
	private final double DEADBAND = 0.05;
	private final double REDUCED = 0.35;
	private final double MAX = 0.85;
	private final double RATE = 50.0;

	private int state;
	private final int DISABLED = 0;
	private final int STOW = 1;
	private final int EXTEND = 2;
	private final int SCORE = 3;
	private final int TOP_SHELF = 4;

	private final Encoder wristEncoder;
	private final VictorSP wrist;
	private final Thread t;

	public Hand(Hardware hw) {
		this.wrist = hw.wrist;
		this.wristEncoder = hw.wristEncoder;

		state = DISABLED;

		t = new Thread(this, "Hand");
		t.start();
	}

	@Override
	public void run() {
		while (!Thread.interrupted()) {
			SmartDashboard.putNumber("Wrist Encoder", wristEncoder.getDistance());
			SmartDashboard.putNumber("Wrist state", state);
			switch (state) {
			case DISABLED:
				wrist.set(0);

				if (wristEncoder.getDistance() < 10) {
					//wristEncoder.reset();
				}
				break;
			case STOW:
				setPosition(STOWED_THRESH);

				if (wristEncoder.getDistance() < 10) {
					state = DISABLED;
				}
				break;
			case EXTEND:
				setPosition(EXTENDED_THRESH);
				break;
			case SCORE:
				setPosition(SCORE_THRESH);
				break;
			case TOP_SHELF:
				setPosition(TOP_SHELF_THRESH);
				break;
			}

			//Timer.delay(0.005);
			Timer.delay(0.1);
		}
	}

	/**
	 * Disable the wrist motion
	 */
	public void disable() {
		state = DISABLED;
	}

	/**
	 * Put the hand in travel configuration
	 */
	public void stow() {
		state = STOW;
	}

	/**
	 * Extend the hand to pick up a block from the ground
	 */
	public void extend() {
		state = EXTEND;
	}

	/**
	 * Extend the hand at an angle to score the cube
	 */
	public void score() {
		state = SCORE;
	}

	/**
	 * Extend the hand just above the top of a block resting on the ground
	 */
	public void topShelf() {
		state = TOP_SHELF;
	}

	/**
	 * Make the wrist go to the desired angle
	 * 
	 * @param targetPos
	 *            One of the predefined constant angles (as set by ticks returned by
	 *            the encoder)
	 */
	private void setPosition(double targetPos) {
		double currentPos = wristEncoder.getDistance();
		double speed = targetPos - currentPos;

		speed /= RATE;
		boolean againstGravity = state == SCORE // SCORING always fights gravity
				|| state == EXTEND && currentPos < SCORE_THRESH // Moving to EXTEND from STOW
				|| state == STOW && currentPos > SCORE_THRESH / 2.0 // Moving to STOW while EXTENDED
				|| state == TOP_SHELF;
		if (againstGravity) {
			speed = normalize(speed, MAX);
		} else {
			speed = normalize(speed, REDUCED);
		}
		speed = deadBand(speed);

		wrist.set(speed);
	}

	/**
	 * Gets the restricted speed
	 * 
	 * @param value
	 *            The target value
	 * @param max
	 *            The positive maximum speed
	 * @return The properly capped speed
	 */
	private double normalize(double value, double max) {
		return Math.max(-max, Math.min(value, max));
	}

	/**
	 * Implements a deadband
	 * 
	 * @param value
	 *            The value wished to be applied to the motor
	 * @return The speed wished to be applied as long as it is outside the deadband
	 */
	private double deadBand(double value) {
		return Math.abs(value) > DEADBAND ? value : 0;
	}

	/**
	 * Forces the wrist to quit moving automatically and follow specific user input
	 * 
	 * @param value
	 *            The speed at which the user wants to move the wrist
	 */
	public void manualMove(double value) {
		if (deadBand(value) != 0) {
			disable();
			wrist.set(value);
		}
	}

	/**
	 * Used to alert the user if the hand is extended or not
	 * 
	 * @return True if the state of the hand is extended (The hand does not have to
	 *         be fully extended for the return to be true)
	 */
	public boolean isExtended() {
		return state == EXTEND;
	}
}
