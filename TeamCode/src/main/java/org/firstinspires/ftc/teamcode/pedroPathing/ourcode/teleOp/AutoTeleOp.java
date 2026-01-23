package org.firstinspires.ftc.teamcode.pedroPathing.ourcode.teleOp;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.*;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

@TeleOp(name="AUTO + TELEOP MENU", group="Z")
public class AutoTeleOp extends OpMode {

    enum Mode { TELEOP, AUTO_TELEOP, AUTO }
    enum Alliance { RED, BLUE }
    enum Path { SHORT, LONG }

    // ======== Menu selections ========
    Mode selectedMode = Mode.TELEOP;
    Alliance selectedAlliance = Alliance.RED;
    Path selectedPath = Path.SHORT;
    int ballRows = 1;

    // ======== Robot hardware ========
    private DcMotor FLeft, BLeft, FRight, BRight;
    private DcMotor IntakeMotor;
    private DcMotorEx Shooter;
    private CRServo SmallSupportServo, LargeSupportServo, Ramp;

    private ElapsedTime autoTimer = new ElapsedTime();
    private boolean autoDriving = false;
    private int autoStep = 0;

    // ======== Button edge detection ========
    private ButtonEdge dpadUp = new ButtonEdge();
    private ButtonEdge dpadDown = new ButtonEdge();
    private ButtonEdge dpadLeft = new ButtonEdge();
    private ButtonEdge dpadRight = new ButtonEdge();
    private ButtonEdge aButton = new ButtonEdge();
    private ButtonEdge bButton = new ButtonEdge();
    private ButtonEdge xButton = new ButtonEdge();
    private ButtonEdge yButton = new ButtonEdge();

    @Override
    public void init() {
        // Motors
        FLeft = hardwareMap.get(DcMotor.class, "FLeft");
        BLeft = hardwareMap.get(DcMotor.class, "BLeft");
        FRight = hardwareMap.get(DcMotor.class, "FRight");
        BRight = hardwareMap.get(DcMotor.class, "BRight");

        IntakeMotor = hardwareMap.get(DcMotor.class, "Intake");
        Shooter = hardwareMap.get(DcMotorEx.class, "Shooter");
        SmallSupportServo = hardwareMap.get(CRServo.class, "SmallSupportServo");
        LargeSupportServo = hardwareMap.get(CRServo.class, "LargeSupportServo");
        Ramp = hardwareMap.get(CRServo.class, "Ramp");

        // Motor directions
        FLeft.setDirection(DcMotor.Direction.REVERSE);
        BLeft.setDirection(DcMotor.Direction.REVERSE);
        FRight.setDirection(DcMotor.Direction.FORWARD);
        BRight.setDirection(DcMotor.Direction.FORWARD);
        Shooter.setDirection(DcMotor.Direction.REVERSE);

        setDriveMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        setDriveMode(DcMotor.RunMode.RUN_USING_ENCODER);

        telemetry.addLine("Initialized! Use D-Pad + Buttons to select options.");
        telemetry.update();
    }

    @Override
    public void loop() {

        // ======= Menu selection (before start) =======
        if (false) {
            handleMenuSelection();
            return;
        }

        // ======= Main robot logic =======
        switch (selectedMode) {
            case TELEOP:
                teleOpLoop();
                break;
            case AUTO_TELEOP:
            case AUTO:
                autoLoop();
                if (selectedMode == Mode.AUTO_TELEOP) {
                    teleOpLoop();
                }
                break;
        }

        // Telemetry
        telemetry.addLine("Mode: " + selectedMode);
        telemetry.addLine("Alliance: " + selectedAlliance);
        telemetry.addLine("Path: " + selectedPath);
        telemetry.addLine("Ball Rows: " + ballRows);
        telemetry.update();
    }

    private void handleMenuSelection() {
        // Mode selection
        if (dpadUp.wasPressed(gamepad1.dpad_up)) {
            selectedMode = Mode.values()[(selectedMode.ordinal() + 1) % Mode.values().length];
        }
        if (dpadDown.wasPressed(gamepad1.dpad_down)) {
            selectedMode = Mode.values()[(selectedMode.ordinal() - 1 + Mode.values().length) % Mode.values().length];
        }

        // Alliance selection
        if (dpadLeft.wasPressed(gamepad1.dpad_left)) {
            selectedAlliance = selectedAlliance == Alliance.RED ? Alliance.BLUE : Alliance.RED;
        }

        // Path selection
        if (dpadRight.wasPressed(gamepad1.dpad_right)) {
            selectedPath = selectedPath == Path.SHORT ? Path.LONG : Path.SHORT;
        }

        // Ball rows selection
        if (aButton.wasPressed(gamepad1.a)) ballRows = Math.max(1, ballRows - 1);
        if (bButton.wasPressed(gamepad1.b)) ballRows = Math.min(5, ballRows + 1);

        telemetry.addLine("=== MENU SELECTION ===");
        telemetry.addData("Mode (DPad Up/Down)", selectedMode);
        telemetry.addData("Alliance (DPad Left)", selectedAlliance);
        telemetry.addData("Path (DPad Right)", selectedPath);
        telemetry.addData("Ball Rows (A/B)", ballRows);
        telemetry.addLine("Press START to begin");
        telemetry.update();
    }

    // ======= TeleOp =======
    private void teleOpLoop() {
        double drive = -gamepad1.left_stick_y;
        double strafe = gamepad1.left_stick_x;
        double turn = gamepad1.right_stick_x;

        FLeft.setPower(drive + strafe + turn);
        BLeft.setPower(drive - strafe + turn);
        FRight.setPower(drive - strafe - turn);
        BRight.setPower(drive + strafe - turn);

        IntakeMotor.setPower(gamepad2.left_stick_y);
        SmallSupportServo.setPower(gamepad2.right_stick_y);
        LargeSupportServo.setPower(gamepad2.right_stick_y);

        if (xButton.wasPressed(gamepad2.x)) shootBalls(ballRows);
    }

    // ======= Auto =======
    private void autoLoop() {
        switch (autoStep) {
            case 0:
                // Drive forward depending on path
                double distance = selectedPath == Path.SHORT ? 24 : 48;
                encoderDrive(0.5, distance, distance);
                autoTimer.reset();
                autoStep++;
                break;

            case 1:
                if (!FLeft.isBusy()) {
                    // Shoot balls
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
                break;
        }
    }

    // ======= Drive helpers =======
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

        autoDriving = true;
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

    private void shootBalls(int numBalls) {
        SmallSupportServo.setPower(1.0);
        LargeSupportServo.setPower(1.0);
        // Shooting handled in autoLoop or teleOp manually
    }

    // ======= ButtonEdge helper =======
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
