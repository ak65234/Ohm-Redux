package org.usfirst.frc.team1817.robot;

import edu.wpi.first.wpilibj.XboxController;

public class Controls {
    public final XboxController driver, manipulator;

    public Controls() {
        driver = new XboxController(0);
        manipulator = new XboxController(1);
    }
}