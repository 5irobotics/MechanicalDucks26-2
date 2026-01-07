package org.firstinspires.ftc.teamcode.pedroPathing.ourcode.auto;
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
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.pedroPathing.ourcode.auto.subsystems.Drive2;

import org.firstinspires.ftc.teamcode.pedroPathing.Constants;

import java.util.function.Supplier;

@Configurable
@TeleOp( name = "PLAY ME", group = "Z")
public class Auto1 extends LinearOpMode {
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

    Drive2 drive = new Drive2();


    @Override
    public void runOpMode() {

        IntakeMotor = hardwareMap.get(DcMotorEx.class, "Intake");
        SmallSupportServo = hardwareMap.get(CRServo.class, "SmallSupportServo");
        LargeSupportServo = hardwareMap.get(CRServo.class, "LargeSupportServo");
        Hood = hardwareMap.get(Servo.class, "Hood");
        Ramp = hardwareMap.get(CRServo.class, "Ramp");
        LauncherMotor = hardwareMap.get(DcMotorEx.class, "Shooter");

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

        waitForStart();

        drive.encoderDrive(1, -43, 10,
                FLeft, FRight, BLeft, BRight);


    }


    }