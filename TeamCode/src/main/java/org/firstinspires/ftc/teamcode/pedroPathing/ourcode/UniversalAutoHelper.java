package org.firstinspires.ftc.teamcode.pedroPathing.ourcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.*;
import com.qualcomm.robotcore.util.ElapsedTime;

public class UniversalAutoHelper {

    // --- ENUMS ---
    public enum Alliance { RED, BLUE }
    public enum StartPos { SHORT, LONG }

    private enum State {
        DRIVE_TO_GOAL,
        TURN_TO_GOAL,     // CHANGED
        SHOOT_SEQUENCE,
        MOVE_TO_BALL_LINE,
        INTAKE_BALLS,
        PARK,
        DONE
    }

    // --- CONFIG ---
    private final Alliance alliance;
    private final StartPos startPos;
    private final int ballLines;
    private final double allianceMult;

    // --- STATE ---
    private State currentState = State.DRIVE_TO_GOAL;
    private int linesCompleted = 0;

    // --- HARDWARE ---
    private DcMotor FLeft, BLeft, FRight, BRight;
    private DcMotor IntakeMotor;
    private DcMotorEx Shooter;
    private CRServo SmallSupportServo, LargeSupportServo, Ramp;

    // --- UTILS ---
    private final ElapsedTime stateTimer = new ElapsedTime();
    private final OpMode opMode;

    // --- TURN CONFIG (TUNE THIS) ---
    private static final double TURN_INCHES = 6.0;   // CHANGED
    private static final double TURN_SPEED  = 0.4;

    // --- CONSTRUCTOR ---
    public UniversalAutoHelper(
            OpMode opMode,
            Alliance alliance,
            StartPos startPos,
            int ballLines
    ) {
        this.opMode = opMode;
        this.alliance = alliance;
        this.startPos = startPos;
        this.ballLines = ballLines;
        this.allianceMult = (alliance == Alliance.RED) ? 1.0 : -1.0;
    }

    // --- INIT ---
    public void init() {
        HardwareMap hw = opMode.hardwareMap;

        FLeft  = hw.get(DcMotor.class, "FLeft");
        BLeft  = hw.get(DcMotor.class, "BLeft");
        FRight = hw.get(DcMotor.class, "FRight");
        BRight = hw.get(DcMotor.class, "BRight");

        IntakeMotor = hw.get(DcMotor.class, "Intake");
        Shooter     = hw.get(DcMotorEx.class, "Shooter");

        SmallSupportServo = hw.get(CRServo.class, "SmallSupportServo");
        LargeSupportServo = hw.get(CRServo.class, "LargeSupportServo");
        Ramp              = hw.get(CRServo.class, "Ramp");

        FLeft.setDirection(DcMotor.Direction.REVERSE);
        BLeft.setDirection(DcMotor.Direction.REVERSE);
        Shooter.setDirection(DcMotor.Direction.REVERSE);

        setDriveMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        setDriveMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    public void start() {
        stateTimer.reset();
    }

    // --- MAIN UPDATE ---
    public void update() {

        switch (currentState) {

            case DRIVE_TO_GOAL:
                double forward = (startPos == StartPos.SHORT) ? 24 : 54;
                encoderDrive(0.6, forward, forward, 0);

                if (!FLeft.isBusy()) {
                    stateTimer.reset();
                    currentState = State.TURN_TO_GOAL;   // CHANGED
                }
                break;

            case TURN_TO_GOAL:
                // Spin in place using encoder strafe trick
                encoderDrive(
                        TURN_SPEED,
                        0,
                        0,
                        TURN_INCHES * allianceMult
                );

                if (!FLeft.isBusy()) {
                    stopDrive();
                    stateTimer.reset();
                    currentState = State.SHOOT_SEQUENCE;
                }
                break;

            case SHOOT_SEQUENCE:
                Shooter.setVelocity(2250);

                if (stateTimer.seconds() > 1.0) {
                    Ramp.setPower(1);
                    SmallSupportServo.setPower(1);
                    LargeSupportServo.setPower(1);
                }

                if (stateTimer.seconds() > 4.0) {
                    stopShooting();
                    linesCompleted++;
                    stateTimer.reset();

                    currentState = (linesCompleted < ballLines)
                            ? State.MOVE_TO_BALL_LINE
                            : State.PARK;
                }
                break;

            case MOVE_TO_BALL_LINE:
                encoderDrive(0.5, -10, -10, 12 * allianceMult);

                if (!FLeft.isBusy()) {
                    stateTimer.reset();
                    currentState = State.INTAKE_BALLS;
                }
                break;

            case INTAKE_BALLS:
                IntakeMotor.setPower(1);
                encoderDrive(0.3, 20, 20, 0);

                if (!FLeft.isBusy()) {
                    IntakeMotor.setPower(0);
                    currentState = State.DRIVE_TO_GOAL;
                }
                break;

            case PARK:
                encoderDrive(0.6, -24, -24, 0);
                if (!FLeft.isBusy()) currentState = State.DONE;
                break;

            case DONE:
                stopDrive();
                break;
        }
    }

    public boolean isFinished() {
        return currentState == State.DONE;
    }

    // ---------- HELPERS ----------

    private void encoderDrive(double speed, double leftIn, double rightIn, double strafeIn) {
        int fl = (int) ((leftIn + strafeIn) * Constants.CPI);
        int bl = (int) ((leftIn - strafeIn) * Constants.CPI);
        int fr = (int) ((rightIn - strafeIn) * Constants.CPI);
        int br = (int) ((rightIn + strafeIn) * Constants.CPI);

        FLeft.setTargetPosition(FLeft.getCurrentPosition() + fl);
        BLeft.setTargetPosition(BLeft.getCurrentPosition() + bl);
        FRight.setTargetPosition(FRight.getCurrentPosition() + fr);
        BRight.setTargetPosition(BRight.getCurrentPosition() + br);

        setDriveMode(DcMotor.RunMode.RUN_TO_POSITION);
        setRawDrivePower(speed, speed, speed, speed);
    }

    private void setRawDrivePower(double fl, double bl, double fr, double br) {
        FLeft.setPower(fl);
        BLeft.setPower(bl);
        FRight.setPower(fr);
        BRight.setPower(br);
    }

    private void stopDrive() {
        setRawDrivePower(0, 0, 0, 0);
    }

    private void stopShooting() {
        Shooter.setVelocity(0);
        Ramp.setPower(-1);
        SmallSupportServo.setPower(0);
        LargeSupportServo.setPower(0);
    }

    private void setDriveMode(DcMotor.RunMode mode) {
        FLeft.setMode(mode);
        BLeft.setMode(mode);
        FRight.setMode(mode);
        BRight.setMode(mode);
    }

    private static class Constants {
        static final double TICKS = 537.7;
        static final double DIAMETER = 3.77953;
        static final double CPI = TICKS / (DIAMETER * Math.PI);
    }
}
