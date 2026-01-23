package org.firstinspires.ftc.teamcode.pedroPathing.ourcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.*;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;

@TeleOp(name="TeleOp Competition Final", group="Z")
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

    // Sequence Timings
    final double SHOOTER_WAIT_TIME = 0.8;
    final double FEED_DURATION = 2.5;   
    final double RAMP_RESET_TIME = 0.5; 

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
        // --- 1. DRIVE LOGIC & SLOW MODE ---
        double drive = -gamepad1.left_stick_y;
        double strafe = gamepad1.left_stick_x;
        double turn = gamepad1.right_stick_x;

        // Slow Mode: If Left Bumper is held, cut power to 30% for precision
        double speedMultiplier = gamepad1.left_bumper ? 0.3 : 1.0;

        // Auto-Align (Button A)
        if (gamepad1.a) {
            LLResult result = limelight.getLatestResult();
            if (result != null && result.isValid()) {
                double error = result.getTx() - X_OFFSET;
                turn = Range.clip((error * SNAP_KP) + (Math.signum(error) * MIN_TURN_POWER), -0.6, 0.6);
            }
        }

        FLeft.setPower((drive + strafe + turn) * speedMultiplier);
        BLeft.setPower((drive - strafe + turn) * speedMultiplier);
        FRight.setPower((drive - strafe - turn) * speedMultiplier);
        BRight.setPower((drive + strafe - turn) * speedMultiplier);

        // --- 2. EMERGENCY CANCEL ---
        // Pressing B on Gamepad 1 or Left Bumper on Gamepad 2 kills the sequence
        if (gamepad2.left_bumper) {
            currentShootState = ShootState.IDLE;
            Shooter.setVelocity(0);
            Ramp.setPower(0);
            SmallSupportServo.setPower(0);
            LargeSupportServo.setPower(0);
        }

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
                if (shootTimer.seconds() >= SHOOTER_WAIT_TIME) {
                    shootTimer.reset();
                    currentShootState = ShootState.FEEDING;
                }
                break;

            case FEEDING:
                Shooter.setVelocity(2250);
                Ramp.setPower(1.0); 
                SmallSupportServo.setPower(1.0);
                LargeSupportServo.setPower(1.0);
                
                if (shootTimer.seconds() >= FEED_DURATION) {
                    shootTimer.reset();
                    currentShootState = ShootState.RESET_RAMP;
                }
                break;

            case RESET_RAMP:
                Shooter.setVelocity(0);
                Ramp.setPower(-1.0); // Retract Ramp
                SmallSupportServo.setPower(0);
                LargeSupportServo.setPower(0);
                
                if (shootTimer.seconds() >= RAMP_RESET_TIME) {
                    Ramp.setPower(0);
                    currentShootState = ShootState.IDLE;
                }
                break;
        }

        // --- 4. MANUAL CONTROLS & INTAKE ---
        IntakeMotor.setPower(gamepad2.left_stick_y);

        if (currentShootState == ShootState.IDLE) {
            // Manual Shooter
            if (gamepad2.y) Shooter.setVelocity(2250);
            else if (gamepad2.b) Shooter.setVelocity(2100);
            else if (gamepad2.a) Shooter.setVelocity(0);

            // Manual Support/Ramp Override
            SmallSupportServo.setPower(gamepad2.right_stick_y);
            LargeSupportServo.setPower(gamepad2.right_stick_y);
        }

        telemetry.addData("Status", currentShootState);
        telemetry.addData("Drivetrain", speedMultiplier < 1.0 ? "SLOW MODE" : "NORMAL");
        telemetry.update();
    }
}
