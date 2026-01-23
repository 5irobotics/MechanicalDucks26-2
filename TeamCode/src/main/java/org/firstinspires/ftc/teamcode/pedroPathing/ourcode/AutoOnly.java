package org.firstinspires.ftc.teamcode.pedroPathing.ourcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.*;
import com.qualcomm.robotcore.util.ElapsedTime;

@TeleOp(name="Auto Only", group="Z")
public class AutoOnly extends OpMode {

    enum Alliance { RED, BLUE }
    enum Path { SHORT, LONG }

    private Alliance selectedAlliance = Alliance.RED;
    private Path selectedPath = Path.SHORT;
    private int ballRows = 1;

    private DcMotor FLeft, BLeft, FRight, BRight;
    private DcMotor IntakeMotor;
    private DcMotorEx Shooter;
    private CRServo SmallSupportServo, LargeSupportServo, Ramp;

    private ElapsedTime autoTimer = new ElapsedTime();
    private int autoStep = 0;
    private boolean autoStarted = false;

    // Button edges
    private ButtonEdge dpadUp = new ButtonEdge();
    private ButtonEdge dpadDown = new ButtonEdge();
    private ButtonEdge dpadLeft = new ButtonEdge();
    private ButtonEdge dpadRight = new ButtonEdge();
    private ButtonEdge aButton = new ButtonEdge();
    private ButtonEdge bButton = new ButtonEdge();

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

        FLeft.setDirection(DcMotor.Direction.REVERSE);
        BLeft.setDirection(DcMotor.Direction.REVERSE);
        FRight.setDirection(DcMotor.Direction.FORWARD);
        BRight.setDirection(DcMotor.Direction.FORWARD);
        Shooter.setDirection(DcMotor.Direction.REVERSE);

        setDriveMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        setDriveMode(DcMotor.RunMode.RUN_USING_ENCODER);

        telemetry.addLine("Initialized! Use DPad + Buttons to select options.");
        telemetry.update();
    }

    @Override
    public void loop() {
        if (!autoStarted) {
            handleMenuSelection();
            if (gamepad1.start) autoStarted = true;
            return;
        }

        runAuto();
    }

    private void handleMenuSelection() {
        if (dpadLeft.wasPressed(gamepad1.dpad_left))
            selectedAlliance = selectedAlliance == Alliance.RED ? Alliance.BLUE : Alliance.RED;
        if (dpadRight.wasPressed(gamepad1.dpad_right))
            selectedPath = selectedPath == Path.SHORT ? Path.LONG : Path.SHORT;
        if (aButton.wasPressed(gamepad1.a)) ballRows = Math.max(1, ballRows - 1);
        if (bButton.wasPressed(gamepad1.b)) ballRows = Math.min(5, ballRows + 1);

        telemetry.addLine("=== AUTO MENU ===");
        telemetry.addData("Alliance (DPad Left)", selectedAlliance);
        telemetry.addData("Path (DPad Right)", selectedPath);
        telemetry.addData("Ball Rows (A/B)", ballRows);
        telemetry.addLine("Press START to begin");
        telemetry.update();
    }

    private void runAuto() {
        switch (autoStep) {
            case 0:
                double distance = selectedPath == Path.SHORT ? 24 : 48;
                encoderDrive(0.5, distance, distance);
                autoTimer.reset();
                autoStep++;
                break;
            case 1:
                if (!FLeft.isBusy()) {
                    Shooter.setVelocity(2000);
                    SmallSupportServo.setPower(1.0);
                    LargeSupportServo.setPower(1.0);
                    autoTimer.reset();
                    autoStep++;
                }
                break;
            case 2:
                if (autoTimer.seconds() > 5) {
                    SmallSupportServo.setPower(0);
                    LargeSupportServo.setPower(0);
                    autoStep++;
                }
                break;
            default:
                stopDrive();
        }
    }

    private void encoderDrive(double speed, double leftInches, double rightInches) {
        int leftTicks = (int) (leftInches * Constants.COUNTS_PER_INCH);
        int rightTicks = (int) (rightInches * Constants.COUNTS_PER_INCH);

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

    static class ButtonEdge {
        boolean last = false;
        boolean wasPressed(boolean current) {
            boolean pressed = current && !last;
            last = current;
            return pressed;
        }
    }

    public static class Constants {
        public static final double TICKS_PER_REV = 537.7;
        public static final double WHEEL_DIAMETER = 3.77953;
        public static final double COUNTS_PER_INCH = TICKS_PER_REV / (WHEEL_DIAMETER * Math.PI);
    }
}
