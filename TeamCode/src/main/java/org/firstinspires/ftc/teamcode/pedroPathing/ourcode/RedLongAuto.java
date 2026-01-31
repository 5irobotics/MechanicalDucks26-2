package org.firstinspires.ftc.teamcode.pedroPathing.ourcode;

import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;


@Autonomous(name = "Red Long")
public class RedLongAuto extends LinearOpMode {

    private DcMotor FLeft, BLeft, FRight, BRight;
    private DcMotor IntakeMotor;
    private DcMotorEx Shooter;
    private CRServo SmallSupportServo, LargeSupportServo, Ramp;
    private Limelight3A limelight;

    @Override
    public void runOpMode() {
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
        Ramp.setDirection(DcMotorSimple.Direction.REVERSE);

        PIDFCoefficients pidfCoefficients =
                new PIDFCoefficients(5.986, 0, 0, 0.896);
        Shooter.setPIDFCoefficients(
                DcMotorEx.RunMode.RUN_USING_ENCODER,
                pidfCoefficients
        );

        waitForStart();

        encoderDrive(0.5,15,15);
        sleep(2000);
        encoderTurn(1,-45);
        shootBalls();
        encoderDrive(.5,15,15);
    }


    private void encoderDrive(double speed, double leftInches, double rightInches) {
        int leftTicks = (int) (leftInches * AutoTeleOp.Constants.COUNTS_PER_INCH);
        int rightTicks = (int) (rightInches * AutoTeleOp.Constants.COUNTS_PER_INCH);

        FLeft.setTargetPosition(FLeft.getCurrentPosition() + leftTicks);
        BLeft.setTargetPosition(BLeft.getCurrentPosition() + leftTicks);
        FRight.setTargetPosition(FRight.getCurrentPosition() + rightTicks);
        BRight.setTargetPosition(BRight.getCurrentPosition() + rightTicks);

        setDriveMode(DcMotor.RunMode.RUN_TO_POSITION);

        FLeft.setPower(speed);
        BLeft.setPower(speed);
        FRight.setPower(speed);
        BRight.setPower(speed);


    }

    public void encoderTurn(double speed, double degrees) {
        double TURN_DIAMETER_INCHES = 18.0;
        double TURN_CIRCUMFERENCE = Math.PI * TURN_DIAMETER_INCHES;
        double TURN_FRACTION = Math.abs(degrees) / 360.0;
        double TURN_DISTANCE_INCHES = TURN_CIRCUMFERENCE * TURN_FRACTION;

        double leftInches = degrees > 0 ? TURN_DISTANCE_INCHES : -TURN_DISTANCE_INCHES;
        double rightInches = degrees > 0 ? -TURN_DISTANCE_INCHES : TURN_DISTANCE_INCHES;

        encoderDrive(speed, leftInches, rightInches);
    }

    private void stopDrive() {
        FLeft.setPower(0);
        BLeft.setPower(0);
        FRight.setPower(0);
        BRight.setPower(0);
    }

    private void setDriveMode(DcMotor.RunMode mode) {
        FLeft.setMode(mode);
        BLeft.setMode(mode);
        FRight.setMode(mode);
        BRight.setMode(mode);
    }

    private void shootBalls() {
        Ramp.setPower(1);
        Shooter.setVelocity(2000);
        sleep(2000);
        SmallSupportServo.setPower(1.0);
        LargeSupportServo.setPower(-1.0);
        sleep(15000);
    }
}

