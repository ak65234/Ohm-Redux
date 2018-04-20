package org.usfirst.frc.team1817.robot;

import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;

public class Hardware {
	private final double DISTANCE_PER_PULSE = Math.PI * 6.0 / 250.0;
	//private final double DISTANCE_PER_PULSE = Math.PI * 6.0 / 60;

	private final double NEAR_CUBE = 0.9;
	private final double HAS_CUBE = 1.15;

	public final DifferentialDrive chassis;
	public final Encoder leftEncoder, rightEncoder, wristEncoder, shoulderEncoder;
	public final ADXRS450_Gyro gyro;
	public final PowerDistributionPanel pdp;
	public final Servo frontShifter, backShifter;
	public final SpeedControllerGroup intake, shoulder;
	public final VictorSP wrist;
	public final AnalogInput cubeSensor;

	public Hardware() {
		VictorSP left = new VictorSP(0);
		VictorSP right = new VictorSP(1);
		chassis = new DifferentialDrive(left, right);

		VictorSP intake1 = new VictorSP(2);
		VictorSP intake2 = new VictorSP(6);
		intake2.setInverted(true);
		intake = new SpeedControllerGroup(intake2, intake1);

		VictorSP shoulder1 = new VictorSP(8);
		shoulder1.setInverted(true);
		VictorSP shoulder2 = new VictorSP(9);

		shoulder = new SpeedControllerGroup(shoulder1, shoulder2);

		shoulderEncoder = new Encoder(6,7);

		wrist = new VictorSP(7);

		/*
		frontShifter = new Servo(8);
		backShifter = new Servo(9);
		*/
		frontShifter = new Servo (11);
		backShifter = new Servo(12);

		//leftEncoder = new Encoder(0, 1);
		leftEncoder = new Encoder(2, 3);
		leftEncoder.setDistancePerPulse(DISTANCE_PER_PULSE);
		leftEncoder.setReverseDirection(true);

		rightEncoder = new Encoder(4, 5);
		rightEncoder.setDistancePerPulse(DISTANCE_PER_PULSE);

		//wristEncoder = new Encoder(4, 5);
		wristEncoder = new Encoder(0, 1);
		//wristEncoder.setReverseDirection(true);


		pdp = new PowerDistributionPanel();
		cubeSensor = new AnalogInput(3);

		gyro = new ADXRS450_Gyro();
		if (gyro.isConnected()) {
			gyro.calibrate();
		}

	}

	public void resetSensors() {
		leftEncoder.reset();
		rightEncoder.reset();
		gyro.reset();
	}

	public void resetEncoders() {
		leftEncoder.reset();
		rightEncoder.reset();
	}

	public boolean nearCube() {
		return cubeSensor.getVoltage() >= NEAR_CUBE;
	}

	public boolean hasCube() {
		return cubeSensor.getVoltage() >= HAS_CUBE;
	}

	/**
	 * Returns if the encoders and gyro are at a rest
	 */
	public boolean driveAtRest() {
		return leftEncoder.getRate() == 0 && rightEncoder.getRate() == 0 && Math.abs(gyro.getRate()) < 0.05;
	}

	public double getDistance() {
		return Math.max(leftEncoder.getDistance(), rightEncoder.getDistance());
	}
}