package org.firstinspires.ftc.teamcode.pedroPathing.ourcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.*;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;

@Autonomous(name="Universal Auto: Red/Blue Short/Long", group="Z")
public class AutoCompetitionMaster extends OpMode {

    // --- ENUMS ---
    enum Alliance { RED, BLUE }
    enum StartPos { SHORT, LONG }
    enum State { 
        DRIVE_TO_GOAL, 
        ALIGN_WITH_LIMELIGHT, 
        SHOOT_SEQUENCE, 
        MOVE_TO_BALL_LINE, 
        INTAKE_BALLS, 
        PARK, 
        DONE 
    }

    // --- SELECTIONS ---
    private Alliance selectedAlliance = Alliance.RED;
    private StartPos selectedPos = StartPos.SHORT;
    private int ballLinesToCollect = 1;
    private int linesCompleted = 0;
    private State currentState = State.DRIVE_TO_GOAL;
    private double allianceMult = 1.0; // 1 for Red, -1 for Blue

    // --- HARDWARE ---
    private DcMotor FLeft, BLeft, FRight, BRight;
    private DcMotor IntakeMotor;
    private DcMotorEx Shooter;
    private CRServo SmallSupportServo, LargeSupportServo, Ramp;
    private Limelight3A limelight;

    // --- UTILS ---
    private ElapsedTime stateTimer = new ElapsedTime();
    private final double SNAP_KP = 0.035; 
    private final double X_OFFSET = -3.5; 

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

        limelight = hardwareMap.get(Limelight3A.class, "limelight");
        limelight.pipelineSwitch(0);
        limelight.start();

        FLeft.setDirection(DcMotor.Direction.REVERSE);
        BLeft.setDirection(DcMotor.Direction.REVERSE);
        Shooter.setDirection(DcMotor.Direction.REVERSE);

        setDriveMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        setDriveMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    @Override
    public void init_loop() {
        // Menu logic
        if (dpadLeft.wasPressed(gamepad1.dpad_left)) selectedAlliance = (selectedAlliance == Alliance.RED) ? Alliance.BLUE : Alliance.RED;
        if (dpadRight.wasPressed(gamepad1.dpad_right)) selectedPos = (selectedPos == StartPos.SHORT) ? StartPos.LONG : StartPos.SHORT;
        if (aButton.wasPressed(gamepad1.a)) ballLinesToCollect = Math.max(1, ballLinesToCollect - 1);
        if (bButton.wasPressed(gamepad1.b)) ballLinesToCollect = Math.min(3, ballLinesToCollect + 1);

        allianceMult = (selectedAlliance == Alliance.RED) ? 1.0 : -1.0;

        telemetry.addLine("=== CONFIGURATION ===");
        telemetry.addData("Alliance", selectedAlliance);
        telemetry.addData("Position", selectedPos);
        telemetry.addData("Ball Lines", ballLinesToCollect);
        telemetry.update();
    }

    @Override
    public void start() {
        stateTimer.reset();
    }

    @Override
    public void loop() {
        switch (currentState) {
            case DRIVE_TO_GOAL:
                // Move forward based on Short or Long
                double forwardDist = (selectedPos == StartPos.SHORT) ? 24 : 54;
                encoderDrive(0.6, forwardDist, forwardDist, 0); 
                
                if (!FLeft.isBusy()) {
                    stateTimer.reset();
                    currentState = State.ALIGN_WITH_LIMELIGHT;
                }
                break;

            case ALIGN_WITH_LIMELIGHT:
                LLResult result = limelight.getLatestResult();
                if (result != null && result.isValid()) {
                    double error = result.getTx() - X_OFFSET;
                    double turnPower = Range.clip((error * SNAP_KP), -0.4, 0.4);
                    
                    // Turn in place to center
                    setRawDrivePower(turnPower, turnPower, -turnPower, -turnPower);

                    if (Math.abs(error) < 1.0 || stateTimer.seconds() > 2.0) {
                        stopDrive();
                        stateTimer.reset();
                        currentState = State.SHOOT_SEQUENCE;
                    }
                } else if (stateTimer.seconds() > 1.5) {
                    currentState = State.SHOOT_SEQUENCE;
                }
                break;

            case SHOOT_SEQUENCE:
                Shooter.setVelocity(2250);
                if (stateTimer.seconds() > 1.0) { // Wait for ramp up
                    Ramp.setPower(1.0);
                    SmallSupportServo.setPower(1.0);
                    LargeSupportServo.setPower(1.0);
                }
                
                if (stateTimer.seconds() > 4.0) { // Clear the balls
                    stopShooting();
                    linesCompleted++;
                    stateTimer.reset();
                    if (linesCompleted < ballLinesToCollect) currentState = State.MOVE_TO_BALL_LINE;
                    else currentState = State.PARK;
                }
                break;

            case MOVE_TO_BALL_LINE:
                // Back up and strafe toward the center of the field
                // AllianceMult mirrors the strafe direction!
                double strafeAmount = 12 * allianceMult; 
                encoderDrive(0.5, -10, -10, strafeAmount); 
                
                if (!FLeft.isBusy()) {
                    stateTimer.reset();
                    currentState = State.INTAKE_BALLS;
                }
                break;

            case INTAKE_BALLS:
                IntakeMotor.setPower(1.0);
                encoderDrive(0.3, 20, 20, 0); // Crawl forward into balls
                if (!FLeft.isBusy()) {
                    IntakeMotor.setPower(0);
                    currentState = State.DRIVE_TO_GOAL; // Repeat cycle
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

    // --- ENCODER DRIVE WITH STRAFE SUPPORT ---
    private void encoderDrive(double speed, double leftIn, double rightIn, double strafeIn) {
        int flT = (int)((leftIn + strafeIn) * Constants.CPI);
        int blT = (int)((leftIn - strafeIn) * Constants.CPI);
        int frT = (int)((rightIn - strafeIn) * Constants.CPI);
        int brT = (int)((rightIn + strafeIn) * Constants.CPI);

        FLeft.setTargetPosition(FLeft.getCurrentPosition() + flT);
        BLeft.setTargetPosition(BLeft.getCurrentPosition() + blT);
        FRight.setTargetPosition(FRight.getCurrentPosition() + frT);
        BRight.setTargetPosition(BRight.getCurrentPosition() + brT);

        setDriveMode(DcMotor.RunMode.RUN_TO_POSITION);
        setRawDrivePower(speed, speed, speed, speed);
    }

    private void setRawDrivePower(double fl, double bl, double fr, double br) {
        FLeft.setPower(fl); BLeft.setPower(bl);
        FRight.setPower(fr); BRight.setPower(br);
    }

    private void stopDrive() { setRawDrivePower(0,0,0,0); }

    private void stopShooting() {
        Shooter.setVelocity(0); Ramp.setPower(-1.0);
        SmallSupportServo.setPower(0); LargeSupportServo.setPower(0);
    }

    private void setDriveMode(DcMotor.RunMode mode) {
        FLeft.setMode(mode); BLeft.setMode(mode);
        FRight.setMode(mode); BRight.setMode(mode);
    }

    static class ButtonEdge {
        boolean last = false;
        boolean wasPressed(boolean curr) {
            boolean res = curr && !last;
            last = curr;
            return res;
        }
    }

    public static class Constants {
        public static final double TICKS = 537.7;
        public static final double DIAMETER = 3.77953;
        public static final double CPI = TICKS / (DIAMETER * Math.PI);
    }
}
