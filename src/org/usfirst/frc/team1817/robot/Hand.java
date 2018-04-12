package org.usfirst.frc.team1817.robot;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Hand implements Runnable {
	private final int STOWED_THRESH = 30;
	private final int EXTENDED_THRESH = 310;
	private final int SCORE_THRESH = 215;
	private final double DEADBAND = 0.05;
	private final double REDUCED = 0.35;
	private final double MAX = 0.85;
	private final double RATE = 50.0;

	private int state;
	private final int DISABLED = 0;
	private final int STOW = 1;
	private final int EXTEND = 2;
	private final int SCORE = 3;

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
			}

			//Timer.delay(0.005);
			Timer.delay(0.1);
		}
	}

	public void disable() {
		state = DISABLED;
	}

	public void stow() {
		state = STOW;
	}

	public void extend() {
		state = EXTEND;
	}

	public void score() {
		state = SCORE;
	}

	private void setPosition(double targetPos) {
		double currentPos = wristEncoder.getDistance();
		double speed = targetPos - currentPos;

		speed /= RATE;
		boolean againstGravity = state == SCORE // SCORING always fights gravity
				|| state == EXTEND && currentPos < SCORE_THRESH // Moving to EXTEND from STOW
				|| state == STOW && currentPos > SCORE_THRESH / 2.0; // Moving to STOW while EXTENDED

		if (againstGravity) {
			speed = normalize(speed, MAX);
		} else {
			speed = normalize(speed, REDUCED);
		}
		speed = deadBand(speed);

		wrist.set(-speed);
	}

	private double normalize(double value, double max) {
		return Math.max(-max, Math.min(value, max));
	}

	private double deadBand(double value) {
		return Math.abs(value) > DEADBAND ? value : 0;
	}

	private boolean validRange(double value) {
		return Math.abs(value) > DEADBAND;
	}

	public void manualMove(double value) {
		if (validRange(value)) {
			disable();
			wrist.set(value);
		}
	}
}
