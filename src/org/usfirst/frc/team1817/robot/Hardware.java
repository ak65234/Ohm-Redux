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
	private final double DISTANCE_PER_PULSE = Math.PI * 6.0 / 250.0; //Comp bot encoder
	//private final double DISTANCE_PER_PULSE = Math.PI * 6.0 / 60; //Practice bot encoder

	private final double NEAR_CUBE = 0.9; //Voltage the Sharp sensor returns when the an object is near it
	private final double HAS_CUBE = 1.15; //Voltage the Sharp sensor returns when an object is about touching the bumper

	public final DifferentialDrive chassis;
	public final Encoder leftEncoder, rightEncoder, wristEncoder, shoulderEncoder;
	public final ADXRS450_Gyro gyro;
	public final PowerDistributionPanel pdp;
	public final Servo frontShifter, backShifter;
	public final SpeedControllerGroup intake, shoulder;
	public final VictorSP wrist;
	public final AnalogInput cubeSensor;

	public Hardware() {
		//Drive motors. They are on a PWM splitter
		VictorSP left = new VictorSP(0);
		VictorSP right = new VictorSP(1);
		chassis = new DifferentialDrive(left, right);

		//Intake motors. Both are correctly polarized in the wiring
		VictorSP intake1 = new VictorSP(2);
		VictorSP intake2 = new VictorSP(6);
		intake2.setInverted(true);
		intake = new SpeedControllerGroup(intake2, intake1);

		//Shoulder motors. Both are correctly polarized in the wiring
		VictorSP shoulder1 = new VictorSP(8);
		shoulder1.setInverted(true);
		VictorSP shoulder2 = new VictorSP(9);
		shoulder = new SpeedControllerGroup(shoulder1, shoulder2);

		shoulderEncoder = new Encoder(5,6);

		wrist = new VictorSP(7);

		/*
		//Old servo shifters. They were causing a runtime error on their current ports
		frontShifter = new Servo(8);
		backShifter = new Servo(9);
		*/
		//Servo shifters
		frontShifter = new Servo (11);
		backShifter = new Servo(12);

		//leftEncoder = new Encoder(0, 1);
		//Encoder on the left side of the drivetrain
		leftEncoder = new Encoder(2, 3);
		leftEncoder.setDistancePerPulse(DISTANCE_PER_PULSE);
		leftEncoder.setReverseDirection(true);

		//rightEncoder = new Encoder(4, 5);
		//Encoder on the right side of the drivetrain
		rightEncoder = new Encoder(14, 15);
		rightEncoder.setDistancePerPulse(DISTANCE_PER_PULSE);

		//wristEncoder = new Encoder(4, 5);
		//Encoder for the wrist
		wristEncoder = new Encoder(0, 1);
		//wristEncoder.setReverseDirection(true);


		pdp = new PowerDistributionPanel();
		cubeSensor = new AnalogInput(3);

		gyro = new ADXRS450_Gyro();
		if (gyro.isConnected()) {
			gyro.calibrate();
		}

	}

	/**
	 * Reset sensors that should be reset
	 * This should not touch the wrist or shoulder
	 */
	public void resetSensors() {
		leftEncoder.reset();
		rightEncoder.reset();
		gyro.reset();
	}

	/**
	 * Reset ONLY the drive encoders
	 */
	public void resetEncoders() {
		leftEncoder.reset();
		rightEncoder.reset();
	}

	/**
	 * Is the robot near (presumably) a cube?
	 * @return True if the robot is near an object
	 */
	public boolean nearCube() {
		return cubeSensor.getVoltage() >= NEAR_CUBE;
	}

	/**
	 * Is there an object on or near the bumper?
	 * @return True if there is an object near the bumper
	 */
	public boolean hasCube() {
		return cubeSensor.getVoltage() >= HAS_CUBE;
	}

	/**
	 * Returns if the encoders and gyro are at a rest
	 * @return If the drivetrain is presumed to be at rest
	 */
	public boolean driveAtRest() { //TODO Tune this to do better with deadzones
		return Math.abs(leftEncoder.getRate()) < 0.5 && 
				Math.abs(rightEncoder.getRate()) < 0.5 && 
				Math.abs(gyro.getRate()) < 0.1;
	}

	/**
	 * Gets the highest distance traveled by both the drive encoders
	 * @return Distance in inches
	 */
	public double getDistance() {
		return Math.max(leftEncoder.getDistance(), rightEncoder.getDistance());
	}
}