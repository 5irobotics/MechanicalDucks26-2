package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp(name="BasicTeleOp", group="TeleOp")
public class BasicTeleOp extends LinearOpMode {

    private DcMotor FLeft;
    private DcMotor BLeft;
    private DcMotor FRight;
    private DcMotor BRight;

    @Override
    public void runOpMode()     {

        FLeft = hardwareMap.get(DcMotor.class, "FLeft");
        BLeft = hardwareMap.get(DcMotor.class, "BLeft");
        FRight = hardwareMap.get(DcMotor.class, "FRight");
        BRight = hardwareMap.get(DcMotor.class, "BRight");

        FLeft.setDirection(DcMotor.Direction.REVERSE);
        BLeft.setDirection(DcMotor.Direction.REVERSE);

        waitForStart();

        while (opModeIsActive()) {

            double drive = gamepad1.left_stick_y;
            double strafe = gamepad1.left_stick_x; //:)
            double turn = gamepad1.right_stick_x;

            FLeft.setPower((drive + strafe + turn));
            BLeft.setPower((drive - strafe + turn));
            FRight.setPower((drive - strafe - turn));
            BRight.setPower((drive + strafe - turn));

            telemetry.addData("Status", "Running");
            telemetry.update();
        }

        }
    }
