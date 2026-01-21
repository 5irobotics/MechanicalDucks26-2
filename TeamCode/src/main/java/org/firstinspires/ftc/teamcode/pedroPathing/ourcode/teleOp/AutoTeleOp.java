package org.firstinspires.ftc.teamcode.pedroPathing.ourcode.teleOp;

import com.pedropathing.telemetry.SelectableOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.*;

@TeleOp(name = "HI HI HI HI HI", group = "Z")
public class AutoTeleOp extends SelectableOpMode {

    public AutoTeleOp() {
        super("Select Mode", menu -> {
            menu.add("TeleOp Only",
                    () -> new DriveMode("NONE", false));

            menu.folder("Auto + TeleOp", m -> {
                m.add("Red Short (24in)", () -> new DriveMode("RED_SHORT", true));
                m.add("Blue Short (24in)", () -> new DriveMode("BLUE_SHORT", true));
                m.add("Test Turn (90deg)", () -> new DriveMode("TEST_TURN", true));
            });

            menu.folder("Auto Only", m -> {
                m.add("Red Short", () -> new DriveMode("RED_SHORT", false));
                m.add("Blue Short", () -> new DriveMode("BLUE_SHORT", false));
            });
        });
    }

    @Override
    public void onSelect() {}

    /* ===================== DRIVE MODE ===================== */

    public static class DriveMode extends OpMode {

        double F = 0.8592;
        double P = 5.9890;

        private final String autoType;
        private final boolean goTeleOpAfterAuto;

        private boolean autoStarted = false;
        private boolean autoFinished = false;
        private boolean autoDriving = false;

        private DcMotor IntakeMotor, FLeft, BLeft, FRight, BRight;
        private DcMotorEx Shooter;
        private CRServo SmallSupportServo, LargeSupportServo, Ramp;

        private final ButtonEdge shooterHigh = new ButtonEdge();
        private final ButtonEdge shooterLow  = new ButtonEdge();
        private final ButtonEdge shooterOff  = new ButtonEdge();
        private final ButtonEdge rampOut     = new ButtonEdge();
        private final ButtonEdge rampIn      = new ButtonEdge();

        public DriveMode(String autoType, boolean goTeleOpAfterAuto) {
            this.autoType = autoType;
            this.goTeleOpAfterAuto = goTeleOpAfterAuto;
        }

        @Override
        public void init() {

            IntakeMotor = hardwareMap.get(DcMotor.class, "Intake");
            Shooter = hardwareMap.get(DcMotorEx.class, "Shooter");
            SmallSupportServo = hardwareMap.get(CRServo.class, "SmallSupportServo");
            LargeSupportServo = hardwareMap.get(CRServo.class, "LargeSupportServo");
            Ramp = hardwareMap.get(CRServo.class, "Ramp");

            FLeft = hardwareMap.get(DcMotor.class, "FLeft");
            BLeft = hardwareMap.get(DcMotor.class, "BLeft");
            FRight = hardwareMap.get(DcMotor.class, "FRight");
            BRight = hardwareMap.get(DcMotor.class, "BRight");

            SmallSupportServo.setDirection(DcMotorSimple.Direction.REVERSE);
            FLeft.setDirection(DcMotorSimple.Direction.REVERSE);
            BLeft.setDirection(DcMotorSimple.Direction.REVERSE);
            Shooter.setDirection(DcMotorSimple.Direction.REVERSE);

            setDriveMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            setDriveMode(DcMotor.RunMode.RUN_USING_ENCODER);

            Shooter.setPIDFCoefficients(
                    DcMotor.RunMode.RUN_USING_ENCODER,
                    new PIDFCoefficients(P, 0, 0, F)
            );

            telemetry.addLine("Initialized");
            telemetry.addData("Auto Mode", autoType);
            telemetry.update();
        }

        @Override
        public void loop() {

            /* ================= TELEMETRY (GLOBAL) ================= */
            telemetry.addData("Auto Mode", autoType);
            telemetry.addData("Auto Started", autoStarted);
            telemetry.addData("Auto Finished", autoFinished);
            telemetry.addData("Auto Driving", autoDriving);

            telemetry.addData("FL Encoder", FLeft.getCurrentPosition());
            telemetry.addData("FR Encoder", FRight.getCurrentPosition());
            telemetry.addData("BL Encoder", BLeft.getCurrentPosition());
            telemetry.addData("BR Encoder", BRight.getCurrentPosition());

            telemetry.addData("FL Busy", FLeft.isBusy());
            telemetry.addData("FR Busy", FRight.isBusy());
            telemetry.addData("BL Busy", BLeft.isBusy());
            telemetry.addData("BR Busy", BRight.isBusy());

            /* ================= AUTO MOTION HANDLER ================= */
            if (autoDriving) {

                telemetry.addLine("AUTO: Driving to target");

                if (!FLeft.isBusy() && !FRight.isBusy()
                        && !BLeft.isBusy() && !BRight.isBusy()) {

                    stopDrivetrain();
                    setDriveMode(DcMotor.RunMode.RUN_USING_ENCODER);

                    autoDriving = false;
                    autoFinished = true;

                    telemetry.addLine("AUTO: Finished movement");

                    if (!goTeleOpAfterAuto) {
                        telemetry.addLine("AUTO ONLY: Stopping OpMode");
                        telemetry.update();
                        requestOpModeStop();
                    }
                }

                telemetry.update();
                return;
            }

            /* ================= AUTO START ================= */
            if (!autoType.equals("NONE") && !autoFinished && !autoStarted) {
                telemetry.addLine("AUTO: Starting");
                runSelectedAuto();
                autoStarted = true;
                telemetry.update();
                return;
            }

            /* ================= TELEOP ================= */
            telemetry.addLine("TELEOP: Active");

            double drive = -gamepad1.left_stick_y;
            double strafe = gamepad1.left_stick_x;
            double turn = gamepad1.right_stick_x;

            FLeft.setPower((drive + strafe + turn) * Constants.DRIVE_SPEED_MULT);
            BLeft.setPower((drive - strafe + turn) * Constants.DRIVE_SPEED_MULT);
            FRight.setPower((drive - strafe - turn) * Constants.DRIVE_SPEED_MULT);
            BRight.setPower((drive + strafe - turn) * Constants.DRIVE_SPEED_MULT);

            if (shooterHigh.wasPressed(gamepad2.y)) {
                Shooter.setVelocity(Constants.SHOOTER_HIGH_RPM);
                telemetry.addLine("Shooter: HIGH");
            }

            if (shooterLow.wasPressed(gamepad2.b)) {
                Shooter.setVelocity(Constants.SHOOTER_LOW_RPM);
                telemetry.addLine("Shooter: LOW");
            }

            if (shooterOff.wasPressed(gamepad2.x)) {
                Shooter.setPower(0);
                telemetry.addLine("Shooter: OFF");
            }

            if (rampOut.wasPressed(gamepad2.dpad_down))
                Ramp.setPower(Constants.RAMP_OUT);

            if (rampIn.wasPressed(gamepad2.dpad_up))
                Ramp.setPower(Constants.RAMP_IN);

            if (!gamepad2.dpad_down && !gamepad2.dpad_up)
                Ramp.setPower(0);

            IntakeMotor.setPower(-gamepad2.right_stick_y * Constants.INTAKE_SPEED);

            double helperPower = -gamepad2.left_stick_y * Constants.HELPER_WHEEL_SPEED;
            SmallSupportServo.setPower(helperPower);
            LargeSupportServo.setPower(helperPower);

            telemetry.update();
        }

        /* ================= AUTO ================= */

        private void runSelectedAuto() {
            telemetry.addData("AUTO COMMAND", autoType);

            switch (autoType) {
                case "RED_SHORT":
                case "BLUE_SHORT":
                    encoderDriving(0.5, 24, 24);
                    break;

                case "TEST_TURN":
                    encoderTurn(0.4, 90);
                    break;
            }
        }

        public void encoderTurn(double speed, double degrees) {
            double turnDist =
                    Math.PI * Constants.TRACK_WIDTH_INCHES * (Math.abs(degrees) / 360.0);

            double left = degrees > 0 ? turnDist : -turnDist;
            double right = -left;

            telemetry.addData("Turn Degrees", degrees);
            encoderDriving(speed, left, right);
        }

        public void encoderDriving(double speed, double leftInches, double rightInches) {

            int leftTicks = (int) (leftInches * Constants.COUNTS_PER_INCH);
            int rightTicks = (int) (rightInches * Constants.COUNTS_PER_INCH);

            telemetry.addData("Left Inches", leftInches);
            telemetry.addData("Right Inches", rightInches);
            telemetry.addData("Left Ticks", leftTicks);
            telemetry.addData("Right Ticks", rightTicks);

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

        private void stopDrivetrain() {
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
    }

    /* ================= UTILS ================= */

    static class ButtonEdge {
        boolean last;
        boolean wasPressed(boolean current) {
            boolean pressed = current && !last;
            last = current;
            return pressed;
        }
    }

    public static class Constants {
        public static double DRIVE_SPEED_MULT = 1.0;
        public static double INTAKE_SPEED = 1.0;
        public static double HELPER_WHEEL_SPEED = 1.0;
        public static double RAMP_OUT = 1.0;
        public static double RAMP_IN = -1.0;

        public static double SHOOTER_HIGH_RPM = 2250;
        public static double SHOOTER_LOW_RPM = 1925;

        public static final double TICKS_PER_REV = 537.7;
        public static final double WHEEL_DIAMETER = 3.77953;
        public static final double COUNTS_PER_INCH =
                TICKS_PER_REV / (WHEEL_DIAMETER * Math.PI);

        public static final double TRACK_WIDTH_INCHES = 14.5;
    }
}
