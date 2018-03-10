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

    public void update(boolean value) {
        if (value) {
            flip = true;
        } else if (flip) {
            flip = false;
            state = !state;
        }
    }

    public void set(boolean value) {
        state = value;
    }

    public boolean get() {
        return state;
    }
}