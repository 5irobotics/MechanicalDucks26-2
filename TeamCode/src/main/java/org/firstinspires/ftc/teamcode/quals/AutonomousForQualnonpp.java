package org.firstinspires.ftc.teamcode.quals;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.teamcode.pedroPathing.ourcode.AutoTeleOp;

/**
 * Polished Non-PedroPathing Autonomous for Quals.
 * Convention: Negative values move forward, Positive values move backward.
 * Includes updated shooting logic synced with TeleOp.
 */
@Autonomous(name="Auto_Quals_Non_PP", group="Quals")
public class AutonomousForQualnonpp extends LinearOpMode {

    // ===== SELECTION ENUMS =====
    enum Alliance { RED, BLUE }
    enum StartDistance { NEAR, FAR }
    enum ScoreLevel { ROW_0, ROW_1, ROW_1_2, ROW_1_2_3 }
    enum Delay {secs_0, secs_5, secs_10, secs_15};
    enum Park {ON,OFF}

    private final HardwareConstants robot = new HardwareConstants();

    private Alliance alliance = Alliance.RED;
    private StartDistance distance = StartDistance.NEAR;
    private ScoreLevel scoreLevel = ScoreLevel.ROW_0;
    private int preLoadCount = 3;
    private Park parking = Park.OFF;


    // ===== MOVEMENT CONSTANTS (Adjust based on testing) =====
    // Negative = Forward, Positive = Backward
    private final double DIST_TO_ARTIFACT = -32.0; 
    private final double DIST_BACK_TO_SCORE = 32.0;
    private final double DIST_NEAR_TO_SCORE = -32.0;
    
    // Far Side Traversal
    private final double DIST_FAR_FORWARD = 10.0;
    private final double DIST_FAR_CROSS = -82.0;   
    
    private final double DIST_PARK = -35.0;
    private final double TURN_90 = 100.0;
    private final double TURN_45 = 45.0;

    @Override
    public void runOpMode() {
        robot.init(hardwareMap);

        // ===== INIT SELECTION MENU =====
        boolean aLast=false, bLast=false, xLast=false, leftbumpLast = false;
        while (opModeInInit()) {
            if (gamepad1.x && !xLast) alliance = (alliance == Alliance.RED) ? Alliance.BLUE : Alliance.RED;
            if (gamepad1.a && !aLast) distance = (distance == StartDistance.NEAR) ? StartDistance.FAR : StartDistance.NEAR;
            if (gamepad1.left_bumper && !leftbumpLast) parking = (parking == Park.OFF) ? Park.ON : Park.OFF;
            if (gamepad1.b && !bLast) {
                if (scoreLevel == ScoreLevel.ROW_0) scoreLevel = ScoreLevel.ROW_1;
                else if (scoreLevel == ScoreLevel.ROW_1) scoreLevel = ScoreLevel.ROW_1_2;
                else if (scoreLevel == ScoreLevel.ROW_1_2) scoreLevel = ScoreLevel.ROW_1_2_3;
                else scoreLevel = ScoreLevel.ROW_0;
            }


            if (gamepad1.dpad_up) preLoadCount = 3;
            else if (gamepad1.dpad_right) preLoadCount = 2;
            else if (gamepad1.dpad_left) preLoadCount = 1;
            else if (gamepad1.dpad_down) preLoadCount = 0;

            aLast = gamepad1.a; bLast = gamepad1.b; xLast = gamepad1.x; leftbumpLast = gamepad1.left_bumper;

            telemetry.addLine("=== MECHANICAL DUCKS AUTO CONFIG ===");
            telemetry.addData("[X] Alliance", alliance);
            telemetry.addData("[A] Distance", distance);
            telemetry.addData("[B] Mode", scoreLevel == ScoreLevel.ROW_0 ? "PRELOAD ONLY" : "COLLECT: " + scoreLevel);
            telemetry.addData("[D-Pad] Pre-Loads", preLoadCount);
            telemetry.addData("[Left Bumper] Parking", parking);
            telemetry.addLine("------------------------------------");
            telemetry.addData("Status", "Ready. Press Start.");
            telemetry.update();
        }

        waitForStart();
        if (!opModeIsActive()) return;

        // ===== 1. INITIAL POSITIONING & SCORE PRELOADS =====
        if (distance == StartDistance.FAR) {
            telemetry.addData("Status", "FAR: Crossing Field...");
            telemetry.update();
            encoderDrive(0.6, DIST_FAR_FORWARD, DIST_FAR_FORWARD);
            encoderTurn(0.5, mirrorTurn(32));
        } else {
            telemetry.addData("Status", "NEAR: Positioning...");
            telemetry.update();
            encoderDrive(0.6, DIST_NEAR_TO_SCORE, DIST_NEAR_TO_SCORE);
        }

        // Score pre-loaded balls using TeleOp logic
        scoreSequence(preLoadCount);

        // ===== 2. COLLECTION LOOP =====
        if (scoreLevel != ScoreLevel.ROW_0) {
            int count = getScoreCount();
            for (int i = 0; i < count; i++) {
                telemetry.addData("Status", "Collecting Artifact " + (i+1));
                telemetry.update();
                encoderDrive(0.6, DIST_TO_ARTIFACT, DIST_TO_ARTIFACT);
                
                robot.IntakeMotor.setPower(1.0);
                sleep(1500);
                robot.IntakeMotor.setPower(0);

                telemetry.addData("Status", "Returning to Score...");
                telemetry.update();
                encoderDrive(0.6, DIST_BACK_TO_SCORE, DIST_BACK_TO_SCORE);
                
                // Score the single collected artifact
                scoreSequence(1);
            }
        }

        if(parking == Park.ON && distance == StartDistance.FAR) {

            // ===== 3. PARK =====
            telemetry.addData("Status", "Parking...");
            telemetry.update();
            encoderTurn(0.5, mirrorTurn(distance == StartDistance.NEAR ? TURN_45 : TURN_90));
            encoderDrive(0.6, -DIST_PARK, -DIST_PARK);
        } else if (parking == Park.ON && distance == StartDistance.NEAR) {
            // ===== 3. PARK =====
            telemetry.addData("Status", "Parking...");
            telemetry.update();
            encoderTurn(0.5, mirrorTurn(distance == StartDistance.NEAR ? TURN_45 : TURN_90));
            encoderDrive(0.6, DIST_PARK, DIST_PARK);
        }

        telemetry.addData("Status", "Autonomous Complete!");
        telemetry.update();
    }

    private int getScoreCount(){
        if (scoreLevel == ScoreLevel.ROW_1) return 1;
        if (scoreLevel == ScoreLevel.ROW_1_2) return 2;
        if (scoreLevel == ScoreLevel.ROW_1_2_3) return 3;
        return 0;
    }

    /**
     * Updated scoreSequence to use TeleOp timing logic.
     * @param numBalls Number of balls to shoot in this sequence.
     */
    private void scoreSequence(int numBalls) {
        if (numBalls <= 0) return;

        for (int i = 0; i < numBalls; i++) {
            telemetry.addData("Action", "Shooting Ball " + (i+1) + "/" + numBalls);
            telemetry.update();

            robot.Shooter.setVelocity(robot.SHOOT_VELOCITY);
            
            // First ball uses longer ramp, subsequent balls use recovery ramp
            double currentRampTime = (i == 0) ? robot.RAMP_TIME_FIRST : robot.RAMP_TIME_RECOVERY;
            sleep((long)(currentRampTime * 1000));
            
            robot.BottomSupport.setPower(robot.SUPPORT_SPEED);
            robot.TopSupport.setPower(robot.SUPPORT_SPEED);
            
            // First ball uses specific feed time, others use calculated interval
            double currentFeedTime = (i == 0) ? robot.FEED_TIME_FIRST : (robot.FEED_TIME / numBalls);
            sleep((long)(currentFeedTime * 1000));
            
            // Pause feeding between balls to let shooter recover if more remain
            robot.BottomSupport.setPower(0);
            robot.TopSupport.setPower(0);
        }
        
        robot.stopAll();
    }

    private double mirrorTurn(double degrees) {
        return (alliance == Alliance.BLUE) ? -degrees : degrees;
    }

    private void encoderDrive(double speed, double leftInches, double rightInches) {
        int leftTicks = (int) (-leftInches * AutoTeleOp.Constants.COUNTS_PER_INCH);
        int rightTicks = (int) (-rightInches * AutoTeleOp.Constants.COUNTS_PER_INCH);

        robot.FLeft.setTargetPosition(robot.FLeft.getCurrentPosition() + leftTicks);
        robot.BLeft.setTargetPosition(robot.BLeft.getCurrentPosition() + leftTicks);
        robot.FRight.setTargetPosition(robot.FRight.getCurrentPosition() + rightTicks);
        robot.BRight.setTargetPosition(robot.BRight.getCurrentPosition() + rightTicks);

        setDriveMode(DcMotor.RunMode.RUN_TO_POSITION);
        robot.setDrivePower(Math.abs(speed));

        waitUntilNotBusy();
        robot.stopDrive();
        setDriveMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    public void encoderTurn(double speed, double degrees) {
        double trackWidth = 15.5; 
        double dist = (trackWidth * Math.PI) * (Math.abs(degrees) / 360.0);
        encoderDrive(speed, degrees > 0 ? -dist : dist, degrees > 0 ? dist : -dist);
    }

    private void setDriveMode(DcMotor.RunMode mode) {
        robot.FLeft.setMode(mode); robot.BLeft.setMode(mode);
        robot.FRight.setMode(mode); robot.BRight.setMode(mode);
    }

    private void waitUntilNotBusy() {
        ElapsedTime timeout = new ElapsedTime();
        while (opModeIsActive() && timeout.seconds() < 7.0 && (robot.FLeft.isBusy() || robot.FRight.isBusy())) {
            idle();
        }
    }
}
