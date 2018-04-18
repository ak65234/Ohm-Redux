package org.usfirst.frc.team1817.robot;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.SpeedControllerGroup;

public class Shoulder implements Runnable {

	private final SpeedControllerGroup shoulder;
	private final Encoder shoulderEncoder;
	private final Thread t;
	
	int state = 0;
	private final int FLAT = 0;
	private final int FLAT_THRESH = 10;
	private final int SCORE = 1;
	private final int SCORE_THRESH = 60;
	private final int UP = 2;
	private final int UP_THESH = 180;
	
	private final double MAX = 0.85;
	private final double RATE = 50.0;
	private final double DEADBAND = 0.05;
	
	
	public Shoulder(Hardware hw) {
		this.shoulder = hw.shoulder;
		this.shoulderEncoder = hw.shoulderEncoder;
		t = new Thread(this, "Shoulder");
		t.start();
	}

	public void run() {
		while (!Thread.interrupted()) {
			
			switch(state) {
			case FLAT:
				break;
			case SCORE:
				break;
			case UP:
				break;
			}

		}
	}
	
	public void flat() {
		state = FLAT;
	}
	
	public void score() {
		state = SCORE;
	}
	
	public void up() {
		state = UP;
	}
	
	private double normalize(double value, double max) {
		return Math.max(-max, Math.min(value, max));
	}
	
	private double deadBand(double value) {
		return Math.abs(value) > DEADBAND ? value : 0;
	}
	
	private void setPosition(double targetPos) {
		double currentPos = shoulderEncoder.getDistance();
		double speed = targetPos - currentPos;

		speed /= RATE;
		speed = normalize(speed, MAX);
		speed = deadBand(speed);

		shoulder.set(-speed);
	}
	
}
