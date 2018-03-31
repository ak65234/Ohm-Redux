package org.usfirst.frc.team1817.robot;

import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;

public class Hardware {
    private final double DISTANCE_PER_PULSE = Math.PI * 6.0 / 250.0;

    public final DifferentialDrive chassis;
    public final Encoder leftEncoder, rightEncoder, wristEncoder;
    public final ADXRS450_Gyro gyro;
    public final PowerDistributionPanel pdp;
    public final Servo frontShifter, backShifter;
    public final SpeedControllerGroup intake;
    public final VictorSP wrist;

    public Hardware() {
        VictorSP left = new VictorSP(0);
        left.setInverted(true);
        VictorSP right = new VictorSP(1);
        right.setInverted(true);
        chassis = new DifferentialDrive(left, right);

        VictorSP intake1 = new VictorSP(2);
        intake1.setInverted(true);
        VictorSP intake2 = new VictorSP(6);
        intake = new SpeedControllerGroup(intake2, intake1);

        wrist = new VictorSP(7);

        frontShifter = new Servo(8);
        backShifter = new Servo(9);

        //leftEncoder = new Encoder(0, 1);
        leftEncoder = new Encoder(2, 3);
        leftEncoder.setDistancePerPulse(DISTANCE_PER_PULSE);
        leftEncoder.setReverseDirection(true);

        rightEncoder = new Encoder(4, 5);
        rightEncoder.setDistancePerPulse(DISTANCE_PER_PULSE);

        //wristEncoder = new Encoder(4, 5);
        wristEncoder = new Encoder(0, 1);

        pdp = new PowerDistributionPanel();

        gyro = new ADXRS450_Gyro();
        if (gyro.isConnected()) {
            gyro.calibrate();
        }

    }

    public void resetSensors(){
        leftEncoder.reset();
        rightEncoder.reset();
        gyro.reset();
    }
    
    public void resetEncoders() {
    	leftEncoder.reset();
    	rightEncoder.reset();
    }
}