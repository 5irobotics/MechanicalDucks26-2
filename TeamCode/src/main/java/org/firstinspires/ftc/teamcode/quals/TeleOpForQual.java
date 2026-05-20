package org.firstinspires.ftc.teamcode.quals;

import  com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

@TeleOp(name="TeleOp_Quals", group="Quals")
public class TeleOpForQual extends OpMode {

    private final HardwareConstants robot = new HardwareConstants();
    private final ElapsedTime actionTimer = new ElapsedTime();

    private enum State { IDLE, SHOOT_RAMP, SHOOT_FEED, RESET }
    private State currentState = State.IDLE;

    // Tracking variables
    private int ballsToShoot = 0;
    private int ballsShot = 0;
    private double targetVelocity = 0;

    @Override
    public void init() {
        robot.init(hardwareMap);
    }

    @Override
    public void loop() {

        // ===== DRIVE (Gamepad 1) =====
        double speedMultiplier = gamepad1.left_bumper ? robot.SLOW_MODE_MULTIPLIER : 1.0;

        double drive = -gamepad1.left_stick_y;
        double strafe = gamepad1.left_stick_x;
        double turn = gamepad1.right_stick_x;

        if (gamepad1.left_trigger > 0.1) turn = -0.3;
        if (gamepad1.right_trigger > 0.1) turn = 0.3;

        robot.FLeft.setPower((drive + strafe + turn) * speedMultiplier);
        robot.BLeft.setPower((drive - strafe + turn) * speedMultiplier);
        robot.FRight.setPower((drive - strafe - turn) * speedMultiplier);
        robot.BRight.setPower((drive + strafe - turn) * speedMultiplier);

        // ===== BALL COUNT SETTINGS (Gamepad 1) =====
        if (gamepad1.dpad_left || gamepad1.x) ballsToShoot = 1;
        else if (gamepad1.dpad_up || gamepad1.y) ballsToShoot = 2;
        else if (gamepad1.dpad_right || gamepad1.b) ballsToShoot = 3;
        else if (gamepad1.dpad_down || gamepad1.a) ballsToShoot = 0;

        // ===== SHOOTING CONTROLS (Gamepad 2) =====
        switch (currentState) {
            case IDLE:
                robot.IntakeMotor.setPower(gamepad2.left_stick_y);

                if (gamepad2.y) {
                    if (ballsToShoot > 0) {
                        targetVelocity = robot.SHOOT_VELOCITY * 1.2;
                        ballsShot = 0;
                        actionTimer.reset();
                        currentState = State.SHOOT_RAMP;
                    }
                }
                else if (gamepad2.b) {
                    if (ballsToShoot > 0) {
                        targetVelocity = robot.SHOOT_VELOCITY;
                        ballsShot = 0;
                        actionTimer.reset();
                        currentState = State.SHOOT_RAMP;
                    }
                }

                break;

            case SHOOT_RAMP:
                robot.Shooter.setVelocity(targetVelocity);
                // Use a longer ramp time for the very first ball, and a shorter recovery time for others
                double currentRampTime = (ballsShot == 0) ? robot.RAMP_TIME_FIRST : robot.RAMP_TIME_RECOVERY;

                
                if (actionTimer.seconds() >= currentRampTime) {
                    actionTimer.reset();
                    currentState = State.SHOOT_FEED;
                }
                break;

            case SHOOT_FEED:
                double currentFeedTime = (ballsShot == 0) ? robot.FEED_TIME_FIRST : robot.FEED_TIME/ballsToShoot;
                robot.Shooter.setVelocity(targetVelocity);

                robot.BottomSupport.setPower(robot.SUPPORT_SPEED);
                robot.TopSupport.setPower(robot.SUPPORT_SPEED);

                if (actionTimer.seconds() >= currentFeedTime) {
                    robot.BottomSupport.setPower(0);
                    robot.TopSupport.setPower(0);
                    ballsShot++;

                    if (ballsShot < ballsToShoot) {
                        actionTimer.reset();
                        currentState = State.SHOOT_RAMP;
                    } else {
                        currentState = State.RESET;
                    }
                }
                break;

            case RESET:
                robot.stopAll();
                targetVelocity = 0;
                ballsShot = 0;
                currentState = State.IDLE;
                break;
        }

        // ===== MANUAL OVERRIDE (Gamepad 2) =====
        if (gamepad2.a) {
            currentState = State.RESET;
        }

        robot.IntakeMotor.setPower(gamepad2.left_stick_y);

        robot.BottomSupport.setPower(-gamepad2.right_stick_y);
        robot.TopSupport.setPower(-gamepad2.right_stick_y);

//        if (currentState == State.IDLE && Math.abs(gamepad2.right_stick_y) > 0.1) {
//            robot.BottomSupport.setPower(-gamepad2.right_stick_y);
//            robot.TopSupport.setPower(-gamepad2.right_stick_y);
//        } else if (currentState == State.IDLE) {
//            robot.BottomSupport.setPower(0);
//            robot.TopSupport.setPower(0);
//        }

        // Telemetry
        telemetry.addData("State", currentState);
        telemetry.addData("Target Vel", targetVelocity);
        telemetry.addData("Actual Vel", robot.Shooter.getVelocity());
        telemetry.addData("Balls Shot", ballsShot + "/" + ballsToShoot);
        telemetry.update();
    }
}
