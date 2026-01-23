package org.firstinspires.ftc.teamcode.pedroPathing.ourcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.*;
import com.qualcomm.robotcore.util.ElapsedTime; // Added for timing
import com.qualcomm.robotcore.util.Range;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;

@TeleOp(name="TeleOp Competition", group="Z")
public class TeleOpOnly extends OpMode {

    private DcMotor FLeft, BLeft, FRight, BRight;
    private DcMotor IntakeMotor;
    private DcMotorEx Shooter;
    private CRServo SmallSupportServo, LargeSupportServo, Ramp;
    private Limelight3A limelight;

    // --- TIMING & STATES ---
    private ElapsedTime shootTimer = new ElapsedTime();
    private enum ShootState { IDLE, RAMP_UP, FEEDING, RESET_RAMP }
    private ShootState currentShootState = ShootState.IDLE;

    // --- TUNING CONSTANTS ---
    final double SNAP_KP = 0.035; 
    final double X_OFFSET = -3.5; 
    final double MIN_TURN_POWER = 0.05;

    // Timing for the 3-ball sequence (Adjust these based on testing!)
    final double SHOOTER_WAIT_TIME = 0.8; // Seconds to wait for shooter speed
    final double FEED_DURATION = 2.5;    // Seconds to run the servos to clear 3 balls
    final double RAMP_RESET_TIME = 0.5;  // Seconds to run the ramp "down" at the end

    @Override
    public void init() {
        FLeft = hardwareMap.get(DcMotor.class, "FLeft");
        BLeft = hardwareMap.get(DcMotor.class, "BLeft");
        FRight = hardwareMap.get(DcMotor.class, "FRight");
        BRight = hardwareMap.get(DcMotor.class, "BRight");
        IntakeMotor = hardwareMap.get(DcMotor.class, "Intake");
        Shooter = hardwareMap.get(DcMotorEx.class, "Shooter");
        SmallSupportServo = hardwareMap.get(CRServo.class, "SmallSupportServo");
        LargeSupportServo = hardwareMap.get(CRServo.class, "LargeSupportServo");
        Ramp = hardwareMap.get(CRServo.class, "Ramp");

        limelight = hardwareMap.get(Limelight3A.class, "limelight");
        limelight.pipelineSwitch(0);
        limelight.start();

        FLeft.setDirection(DcMotor.Direction.REVERSE);
        BLeft.setDirection(DcMotor.Direction.REVERSE);
        FRight.setDirection(DcMotor.Direction.FORWARD);
        BRight.setDirection(DcMotor.Direction.FORWARD);
        Shooter.setDirection(DcMotor.Direction.REVERSE);
    }

    @Override
    public void loop() {
        // --- 1. DRIVE LOGIC ---
        double drive = -gamepad1.left_stick_y;
        double strafe = gamepad1.left_stick_x;
        double turn = gamepad1.right_stick_x;

        if (gamepad1.a) {
            LLResult result = limelight.getLatestResult();
            if (result != null && result.isValid()) {
                double error = result.getTx() - X_OFFSET;
                turn = Range.clip((error * SNAP_KP) + (Math.signum(error) * MIN_TURN_POWER), -0.6, 0.6);
            }
        }
        FLeft.setPower(drive + strafe + turn);
        BLeft.setPower(drive - strafe + turn);
        FRight.setPower(drive - strafe - turn);
        BRight.setPower(drive + strafe - turn);

        // --- 2. INTAKE ---
        IntakeMotor.setPower(gamepad2.left_stick_y);

        // --- 3. AUTO-SHOOT STATE MACHINE (Button X) ---
        switch (currentShootState) {
            case IDLE:
                if (gamepad2.x) {
                    shootTimer.reset();
                    currentShootState = ShootState.RAMP_UP;
                }
                break;

            case RAMP_UP:
                Shooter.setVelocity(2250);
                // Wait for the motor to get up to speed
                if (shootTimer.seconds() >= SHOOTER_WAIT_TIME) {
                    shootTimer.reset();
                    currentShootState = ShootState.FEEDING;
                }
                break;

            case FEEDING:
                Shooter.setVelocity(2250);
                Ramp.setPower(1.0);           // Ramp Up
                SmallSupportServo.setPower(1.0);
                LargeSupportServo.setPower(1.0);
                
                // Keep feeding until 3 balls should have passed
                if (shootTimer.seconds() >= FEED_DURATION) {
                    shootTimer.reset();
                    currentShootState = ShootState.RESET_RAMP;
                }
                break;

            case RESET_RAMP:
                Shooter.setVelocity(0);
                Ramp.setPower(-1.0);          // Ramp Down
                SmallSupportServo.setPower(0);
                LargeSupportServo.setPower(0);
                
                if (shootTimer.seconds() >= RAMP_RESET_TIME) {
                    Ramp.setPower(0);         // Stop Ramp
                    currentShootState = ShootState.IDLE;
                }
                break;
        }

        // --- 4. MANUAL OVERRIDES (Only work if Auto-Shoot is IDLE) ---
        if (currentShootState == ShootState.IDLE) {
            // Manual Shooter
            if (gamepad2.y) Shooter.setVelocity(2250);
            else if (gamepad2.b) Shooter.setVelocity(2100);
            else if (gamepad2.a) Shooter.setVelocity(0);

            // Manual Supports
            SmallSupportServo.setPower(gamepad2.right_stick_y);
            LargeSupportServo.setPower(gamepad2.right_stick_y);
        }

        telemetry.addData("Shoot State", currentShootState);
        telemetry.update();
    }
}
