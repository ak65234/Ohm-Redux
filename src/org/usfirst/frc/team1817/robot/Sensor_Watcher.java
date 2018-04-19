package org.usfirst.frc.team1817.robot;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Sensor_Watcher implements Runnable {

	private final Thread t;
	private final Hardware hw;

	public Sensor_Watcher(Hardware hw) {
		this.hw = hw;
		t = new Thread(this, "Sensors");
		t.start();
	}

	public void run() {
		while (!Thread.interrupted()) {
			SmartDashboard.putNumber("Left encoder", hw.leftEncoder.getDistance());
			SmartDashboard.putNumber("Right encoder", hw.rightEncoder.getDistance());

			SmartDashboard.putNumber("Left encoder ticks", hw.leftEncoder.get());
			SmartDashboard.putNumber("Right encoder ticks", hw.rightEncoder.get());

			SmartDashboard.putNumber("Gyro", hw.gyro.getAngle());
			//SmartDashboard.putNumber("Amperage Draw", hw.pdp.getTotalCurrent());
			
			SmartDashboard.putNumber("Cube Sensor Voltage", hw.cubeSensor.getVoltage());
			Timer.delay(0.005);
		}

	}
}
