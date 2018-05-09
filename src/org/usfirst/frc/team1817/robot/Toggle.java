package org.usfirst.frc.team1817.robot;

public class Toggle {
	private boolean flip;
	private boolean state;

	public Toggle() {
		state = false;
		flip = false;
	}

	public Toggle(boolean value) {
		state = value;
		flip = false;
	}

	/**
	 * Uses the low-side of button presses for performing actions
	 * 
	 * @param value
	 *            The button to be monitored
	 * @return The state of the action
	 */
	public boolean update(boolean value) {
		if (value) {
			flip = true;
		} else if (flip) {
			flip = false;
			state = !state;
		}

		return state;
	}

	/**
	 * Manually sets the state of the action
	 * 
	 * @param value
	 *            The state wished to be set
	 */
	public void set(boolean value) {
		state = value;
	}

	/**
	 * @return The state of the action
	 */
	public boolean get() {
		return state;
	}
}