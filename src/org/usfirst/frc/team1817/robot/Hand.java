package org.usfirst.frc.team1817.robot;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.VictorSP;

public class Hand implements Runnable {
	private final int STOWED_THRESH = 0;
	private final int EXTENDED_THRESH = -180;
	private final int SCORE_THRESH = -90; // Ideally, half way between
	private final double DEADBAND = 0.05;
	private final double MAX = 0.75;
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
			switch (state) {
			case DISABLED:
				wrist.stopMotor();

				if (wristEncoder.getDistance() < -10)
					wristEncoder.reset();
				break;
			case STOW:
				setPosition(STOWED_THRESH);

				if (wristEncoder.getDistance() < -10) {
					state = 0;
				}
				break;
			case EXTEND:
				setPosition(EXTENDED_THRESH);
				break;
			case SCORE:
				setPosition(SCORE_THRESH);
				break;
			}

			Timer.delay(0.005);
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

	private void setPosition(double value) {
		double speed = value - wristEncoder.getDistance();
		speed /= RATE;
		speed = normalize(speed, MAX);
		speed = deadBand(speed);

		wrist.set(speed);
	}

	private double normalize(double value, double max) {
		return Math.max(-max, Math.min(value, max));
	}

	private double deadBand(double value) {
		return Math.abs(value) > DEADBAND ? value : 0;
	}
}
