package org.firstinspires.ftc.teamcode.pedroPathing.ourcode;
import com.bylazar.configurables.annotations.Configurable;
import com.bylazar.telemetry.PanelsTelemetry;
import com.bylazar.telemetry.TelemetryManager;
import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.HeadingInterpolator;
import com.pedropathing.paths.Path;
import com.pedropathing.paths.PathChain;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.pedroPathing.ourcode.subsystems.MiddlePart2;

import org.firstinspires.ftc.teamcode.pedroPathing.Constants;

import java.util.function.Supplier;

@Configurable
@TeleOp( name = "PLAY ME", group = "Z")
public class TeleOp1 extends OpMode {
    DcMotor IntakeMotor;
    DcMotor LauncherMotor;
    CRServo SmallSupportServo;
    CRServo LargeSupportServo;
    Servo Hood;
    DcMotor FLeft;
    DcMotor BLeft;
    DcMotor BRight;
    DcMotor FRight;
    CRServo Ramp;

    MiddlePart2 middle = new MiddlePart2();


    @Override
    public void init() {

        IntakeMotor = hardwareMap.get(DcMotor.class, "Intake");
        SmallSupportServo = hardwareMap.get(CRServo.class, "SmallSupportServo");
        LargeSupportServo = hardwareMap.get(CRServo.class, "LargeSupportServo");
        Hood = hardwareMap.get(Servo.class, "Hood");
        Ramp = hardwareMap.get(CRServo.class, "Ramp");
        LauncherMotor = hardwareMap.get(DcMotor.class, "Shooter");

        FLeft = hardwareMap.get(DcMotor.class, "FLeft");
        BLeft = hardwareMap.get(DcMotor.class, "BLeft");
        FRight = hardwareMap.get(DcMotor.class, "FRight");
        BRight = hardwareMap.get(DcMotor.class, "BRight");

        SmallSupportServo.setDirection(DcMotorSimple.Direction.REVERSE);

        FLeft.setDirection(DcMotorSimple.Direction.FORWARD);
        BLeft.setDirection(DcMotorSimple.Direction.FORWARD);
        FRight.setDirection(DcMotorSimple.Direction.REVERSE);
        BRight.setDirection(DcMotorSimple.Direction.REVERSE);

        LauncherMotor.setDirection(DcMotorSimple.Direction.REVERSE);


    }

    @Override
    public void loop() {

    middle.Shooter(gamepad2.y, gamepad2.b, LauncherMotor);
    middle.Ramp(gamepad2.dpad_down, gamepad2.dpad_up, Ramp);
    middle.Intake(gamepad2.right_stick_y, gamepad2.left_stick_y, gamepad2.left_stick_y, IntakeMotor, SmallSupportServo, LargeSupportServo);
    middle.Hood(gamepad2.right_bumper , gamepad2.left_bumper, Hood);

        double y = -gamepad1.left_stick_y; // Remember, Y stick value is reversed
        double x = gamepad1.left_stick_x * 1.1; // Counteract imperfect strafing
        double rx = gamepad1.right_stick_x;

        // Denominator is the largest motor power (absolute value) or 1
        // This ensures all the powers maintain the same ratio,
        // but only if at least one is out of the range [-1, 1]
        double denominator = Math.max(Math.abs(y) + Math.abs(x) + Math.abs(rx), 1);
        double frontLeftPower = (y + x + rx) / denominator;
        double backLeftPower = (y - x + rx) / denominator;
        double frontRightPower = (y - x - rx) / denominator;
        double backRightPower = (y + x - rx) / denominator;

        FLeft.setPower(frontLeftPower);
        BLeft.setPower(backLeftPower);
        FRight.setPower(frontRightPower);
        BRight.setPower(backRightPower);


    }
}