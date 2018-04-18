package org.usfirst.frc.team1817.robot;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.SpeedControllerGroup;

public class Shoulder implements Runnable {

	private final SpeedControllerGroup shoulder;
	private final Encoder shoulderEncoder;
	private final Thread t;
	
	int state = 0;
	private final int FLAT = 0;
	private final int SCORE = 1;
	private final int UP = 2;
	
	
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
	
}
