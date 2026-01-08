package org.firstinspires.ftc.teamcode.pedroPathing.ourcode;


import static org.firstinspires.ftc.teamcode.pedroPathing.Tuning.*;
import static org.firstinspires.ftc.teamcode.pedroPathing.Tuning.follower;


import com.bylazar.configurables.PanelsConfigurables;
import com.bylazar.configurables.annotations.Configurable;
import com.bylazar.configurables.annotations.IgnoreConfigurable;
import com.bylazar.telemetry.PanelsTelemetry;
import com.bylazar.telemetry.TelemetryManager;
import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.Path;
import com.pedropathing.telemetry.SelectableOpMode;
import com.pedropathing.util.PoseHistory;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;


import org.firstinspires.ftc.teamcode.pedroPathing.Constants;


import java.util.ArrayList;


@Configurable
@TeleOp(name = "Meet2_TeleOp_AutoCombo", group = "Z")
public class Meet2TeleOpAuto extends OpMode {

    /* ==============================
       HARDWARE
       ============================== */
    DcMotor IntakeMotor;
    DcMotorEx LauncherMotor;
    CRServo SmallSupportServo;
    CRServo LargeSupportServo;
    CRServo Ramp;
    Servo Hood;

    DcMotor FLeft, BLeft, FRight, BRight;

    /* ==============================
       PEDRO PATHING
       ============================== */
    public static Follower follower;

    @IgnoreConfigurable
    static PoseHistory poseHistory;

    @IgnoreConfigurable
    static TelemetryManager telemetryM;

    @IgnoreConfigurable
    static ArrayList<String> changes = new ArrayList<>();
    Path autoPath;

    /* ==============================
       MODE STATE
       ============================== */
    enum RunMode {
        TELEOP_ONLY,
        AUTO_ONLY,
        AUTO_THEN_TELEOP
    }

    RunMode runMode = RunMode.TELEOP_ONLY;
    boolean autoFinished = false;

    /* ==============================
       BUTTON EDGE DETECTION
       ============================== */
    boolean prevA, prevB, prevX;

    /* ==============================
       INIT
       ============================== */
    @Override
    public void init() {

        IntakeMotor = hardwareMap.get(DcMotor.class, "Intake");
        LauncherMotor = hardwareMap.get(DcMotorEx.class, "Shooter");
        SmallSupportServo = hardwareMap.get(CRServo.class, "SmallSupportServo");
        LargeSupportServo = hardwareMap.get(CRServo.class, "LargeSupportServo");
        Ramp = hardwareMap.get(CRServo.class, "Ramp");
        Hood = hardwareMap.get(Servo.class, "Hood");

        FLeft = hardwareMap.get(DcMotor.class, "FLeft");
        BLeft = hardwareMap.get(DcMotor.class, "BLeft");
        FRight = hardwareMap.get(DcMotor.class, "FRight");
        BRight = hardwareMap.get(DcMotor.class, "BRight");

        FLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        BLeft.setDirection(DcMotorSimple.Direction.REVERSE);

        SmallSupportServo.setDirection(DcMotorSimple.Direction.REVERSE);
        LauncherMotor.setDirection(DcMotorSimple.Direction.REVERSE);


        follower.setStartingPose(Constants.PEDRO_START_POSE);

        buildAutoPath();
    }

    @Override
    public void start() {
        follower.startTeleopDrive();
        follower.update();
    }

    /* ==============================
       LOOP
       ============================== */
    @Override
    public void loop() {

        updateRunModeSelection();

        if (runMode == RunMode.AUTO_ONLY ||
                (runMode == RunMode.AUTO_THEN_TELEOP && !autoFinished)) {

            runAuto();

        } else {
            runTeleOp();
        }

        updatePreviousButtons();
    }

    /* ==============================
       AUTO
       ============================== */
    private void runAuto() {
        follower.followPath(autoPath);
        follower.update();

        if (!follower.isBusy()) {
            autoFinished = true;
        }
    }

    /* ==============================
       TELEOP
       ============================== */
    private void runTeleOp() {

        /* ----- DRIVE (Pedro Follower Assisted) ----- */
        double y = -gamepad1.left_stick_y;
        double x = gamepad1.left_stick_x;
        double rx = gamepad1.right_stick_x;

        follower.setWeightedDrivePower(
                new Pose(
                        y * Constants.TELEOP_DRIVE_SPEED,
                        x * Constants.TELEOP_DRIVE_SPEED,
                        rx * Constants.TELEOP_TURN_SPEED
                )
        );
        follower.update();

        /* ----- OPERATOR CONTROLS (GAMEPAD 2) ----- */

        // Shooter (edge-triggered)
        if (wasPressed(gamepad2.y, prevA)) {
            LauncherMotor.setVelocity(Constants.SHOOTER_HIGH_RPM);
        } else if (wasPressed(gamepad2.b, prevB)) {
            LauncherMotor.setVelocity(Constants.SHOOTER_LOW_RPM);
        }

        // Intake
        IntakeMotor.setPower(-gamepad2.right_stick_y);
        SmallSupportServo.setPower(-gamepad2.left_stick_y);
        LargeSupportServo.setPower(-gamepad2.left_stick_y);

        // Ramp
        if (gamepad2.dpad_up) Ramp.setPower(Constants.RAMP_OUT);
        else if (gamepad2.dpad_down) Ramp.setPower(Constants.RAMP_IN);
        else Ramp.setPower(0);

        // Hood
        if (gamepad2.right_bumper) Hood.setPosition(Constants.HOOD_UP);
        if (gamepad2.left_bumper) Hood.setPosition(Constants.HOOD_DOWN);

        /* ----- SPACE RESERVED FOR FUTURE OPERATOR CONTROLS ----- */
    }

    /* ==============================
       RUN MODE SELECTION
       ============================== */
    private void updateRunModeSelection() {

        if (wasPressed(gamepad1.a, prevA)) {
            runMode = RunMode.TELEOP_ONLY;
        }
        if (wasPressed(gamepad1.b, prevB)) {
            runMode = RunMode.AUTO_ONLY;
            autoFinished = false;
        }
        if (wasPressed(gamepad1.x, prevX)) {
            runMode = RunMode.AUTO_THEN_TELEOP;
            autoFinished = false;
        }
    }

    /* ==============================
       UTIL
       ============================== */
    private boolean wasPressed(boolean current, boolean previous) {
        return current && !previous;
    }

    private void updatePreviousButtons() {
        prevA = gamepad1.a;
        prevB = gamepad1.b;
        prevX = gamepad1.x;
    }

    /* ==============================
       AUTO PATH
       ============================== */
    private void buildAutoPath() {

        autoPath = new PathBuilder()
                .addPoint(new Point(0, 0))
                .addPoint(new Point(Constants.AUTO_X_1, Constants.AUTO_Y_1))
                .addPoint(new Point(Constants.AUTO_X_2, Constants.AUTO_Y_2))
                .build();
    }

    /* ==============================
       CONSTANTS (ALL TUNABLE VALUES)
       ============================== */
    public static class Constants {

        /* ---- TELEOP DRIVE ---- */
        public static double TELEOP_DRIVE_SPEED = 1.0;
        public static double TELEOP_TURN_SPEED = 1.0;

        /* ---- SHOOTER ---- */
        public static double SHOOTER_HIGH_RPM = 1900;
        public static double SHOOTER_LOW_RPM = 1200;

        /* ---- RAMP ---- */
        public static double RAMP_OUT = 1.0;
        public static double RAMP_IN = -1.0;

        /* ---- HOOD ---- */
        public static double HOOD_UP = 0.7;
        public static double HOOD_DOWN = 0.3;

        /* ---- PEDRO PATHING ---- */
        public static Pose PEDRO_START_POSE = new Pose(0, 0, 0);

        public static double AUTO_X_1 = 24;
        public static double AUTO_Y_1 = 0;

        public static double AUTO_X_2 = 48;
        public static double AUTO_Y_2 = 24;
    }
}
