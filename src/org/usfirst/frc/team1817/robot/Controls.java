package org.usfirst.frc.team1817.robot;

import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.GenericHID.RumbleType;

public class Controls {
	public final XboxController driver, manipulator;

	public static class POV {
		public static final int UP = 0;
		public static final int DOWN = 180;
		public static final int LEFT = 90;
		public static final int RIGHT = 270;
	}

	public Controls() {
		driver = new XboxController(0);
		manipulator = new XboxController(1);
	}

	/**
	 * Set the rumble to the appropriate level based on the distance
	 * 
	 * @param near
	 *            If the cube is near the robot
	 * @param acquired
	 *            If the cube is touching the bumper
	 * @param extended
	 *            If the hand is in the extended position or not
	 */
	public void rumbleIt(boolean near, boolean acquired, boolean extended) {
		driver.setRumble(RumbleType.kLeftRumble, near && extended ? 1 : 0);
		driver.setRumble(RumbleType.kRightRumble, acquired && extended ? 1 : 0);
	}
}