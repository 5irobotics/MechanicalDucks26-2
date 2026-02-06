package org.firstinspires.ftc.teamcode.quals;

import com.qualcomm.robotcore.hardware.*;

public class HardwareConstants {

    // ===== DRIVE MOTORS =====
    public DcMotor FLeft, BLeft, FRight, BRight;

    // ===== MECHANISMS =====
    public DcMotor IntakeMotor, BottomSupport, TopSupport;
    public CRServo Ramp;
    public DcMotorEx Shooter;

    // ===== CONSTANTS =====
    public final double SHOOT_VELOCITY = 2150;
    public final double RAMP_TIME_FIRST = 3.5;   // Longer ramp for the very first ball
    public final double RAMP_TIME_RECOVERY = 2.2; // Shorter ramp to recover speed between balls
    public final double RAMP_TIME = 2.75; // Legacy constant if needed elsewhere
    public final double FEED_TIME = 2.0;
    public final double SUPPORT_SPEED = 1.0;
    public final double SLOW_MODE_MULTIPLIER = 0.3;

    // ===== PIDF CONSTANTS (Tune these!) =====
    // F: Feedforward (The power needed to maintain velocity)
    // P: Proportional (The power added based on how far off you are)
    public double SHOOTER_P = 5.9890;
    public double SHOOTER_I = 0.0;
    public double SHOOTER_D = 0.0;
    public double SHOOTER_F = 0.8592 ;

    // ===== INIT METHOD =====
    public void init(HardwareMap hw) {

        // Drive
        FLeft = hw.get(DcMotor.class, "FLeft");
        BLeft = hw.get(DcMotor.class, "BLeft");
        FRight = hw.get(DcMotor.class, "FRight");
        BRight = hw.get(DcMotor.class, "BRight");

        // Mechanisms
        IntakeMotor = hw.get(DcMotor.class, "Intake");
        Shooter = hw.get(DcMotorEx.class, "Shooter");
        BottomSupport = hw.get(DcMotor.class, "BottomSupport");
        TopSupport = hw.get(DcMotor.class, "TopSupport");
        Ramp = hw.get(CRServo.class, "Ramp");

        // Directions
        FLeft.setDirection(DcMotor.Direction.REVERSE);
        BLeft.setDirection(DcMotor.Direction.REVERSE);
        Shooter.setDirection(DcMotor.Direction.REVERSE);

        // Shooter PIDF Setup
        Shooter.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        Shooter.setVelocityPIDFCoefficients(SHOOTER_P, SHOOTER_I, SHOOTER_D, SHOOTER_F);

        // Safe defaults
        stopAll();
    }

    // ===== HELPER METHODS =====
    public void stopDrive() {
        FLeft.setPower(0);
        BLeft.setPower(0);
        FRight.setPower(0);
        BRight.setPower(0);
    }

    public void stopAll() {
        stopDrive();
        IntakeMotor.setPower(0);
        BottomSupport.setPower(0);
        TopSupport.setPower(0);
        Shooter.setVelocity(0);
    }
}
