package org.usfirst.frc.team1817.robot;

public class Shoulder implements Runnable {

	int state = 0;
	private final int FLAT = 0;
	private final int SCORE = 1;
	private final int UP = 2;
	
	
	public Shoulder(Hardware hw) {

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
}
