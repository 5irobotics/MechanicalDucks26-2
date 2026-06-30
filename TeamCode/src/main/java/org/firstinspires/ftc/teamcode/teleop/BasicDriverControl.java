package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcore.external.Telemetry;

@TeleOp(name = "BasicTeleOp")
public class BasicDriverControl extends LinearOpMode {

    private DcMotor fLeft, bLeft, fRight, bRight;

    @Override
    public void runOpMode() {

        fLeft = hardwareMap.get(DcMotor.class, "fLeft");
        bLeft = hardwareMap.get(DcMotor.class, "bLeft");
        fRight = hardwareMap.get(DcMotor.class, "fRight");
        bRight = hardwareMap.get(DcMotor.class, "bRight");

        fLeft.setDirection(DcMotor.Direction.FORWARD);
        bLeft.setDirection(DcMotor.Direction.FORWARD);
        fRight.setDirection(DcMotor.Direction.REVERSE);
        bRight.setDirection(DcMotor.Direction.REVERSE);

        waitForStart();
        while (opModeIsActive()) {

// --- Drivetrain control ---
            double forward = gamepad1.right_stick_y; // forward/backward
            double turn = gamepad1.left_stick_x; // rotation
            double strafe = gamepad1.right_stick_x; // strafing

            double frontLeftPower = forward + turn + strafe;
            double backLeftPower = forward + turn - strafe;
            double frontRightPower = forward - turn - strafe;
            double backRightPower = forward - turn + strafe;


// Set drivetrain powers
            fLeft.setPower(frontLeftPower);
            bLeft.setPower(backLeftPower);
            fRight.setPower(frontRightPower);
            bRight.setPower(backRightPower);

        }

    }

}
