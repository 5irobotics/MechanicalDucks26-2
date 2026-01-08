//package org.firstinspires.ftc.teamcode.pedroPathing.ourcode.teleOp;
//
//import com.bylazar.configurables.PanelsConfigurables;
//import com.bylazar.configurables.annotations.Configurable;
//import com.bylazar.configurables.annotations.IgnoreConfigurable;
//import com.bylazar.telemetry.PanelsTelemetry;
//import com.bylazar.telemetry.TelemetryManager;
//import com.pedropathing.follower.Follower;
//import com.pedropathing.geometry.Pose;
//import com.pedropathing.paths.Path;
//import com.pedropathing.paths.PathBuilder;
//import com.pedropathing.paths.PathChain;
//import com.pedropathing.telemetry.SelectScope;
//import com.pedropathing.telemetry.SelectableOpMode;
//import com.pedropathing.util.PoseHistory;
//import com.qualcomm.robotcore.eventloop.opmode.OpMode;
//import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
//import com.qualcomm.robotcore.hardware.*;
//
//import java.util.ArrayList;
//import java.util.function.Supplier;
//
//@Configurable
//@TeleOp(name = "Meet3TeleOp", group = "Z")
//public class Meet3TeleOp extends SelectableOpMode {
//
//    /* ===================== GLOBAL ===================== */
//
//    public static Follower follower;
//
//
//    @IgnoreConfigurable
//    static PoseHistory poseHistory;
//
//
//    @IgnoreConfigurable
//    static TelemetryManager telemetryM;
//
//    /* ===================== MENU ===================== */
//
//    public Meet3TeleOp() {
//        super("Select Mode", menu -> {
//
//            menu.add("TeleOp Only",
//                    () -> new DriveMode(false, false, null));
//
//            menu.folder("Auto + TeleOp", m ->
//                    buildAutoMenus(m, true));
//
//            menu.folder("Auto Only", m ->
//                    buildAutoMenus(m, false));
//        });
//    }
//
//    private static void buildAutoMenus(SelectScope<Supplier<OpMode>> m, boolean goTeleOp) {
//
//        // ================= RED =================
//        m.folder("Red", r -> {
//
//            r.folder("Short", s -> {
//                s.add("1 Line", () -> new DriveMode(true, goTeleOp, buildPath("R","S",1)));
//                s.add("2 Line", () -> new DriveMode(true, goTeleOp, buildPath("R","S",2)));
//                s.add("3 Line", () -> new DriveMode(true, goTeleOp, buildPath("R","S",3)));
//            });
//
//            r.folder("Long", s -> {
//                s.add("1 Line", () -> new DriveMode(true, goTeleOp, buildPath("R","L",1)));
//                s.add("2 Line", () -> new DriveMode(true, goTeleOp, buildPath("R","L",2)));
//                s.add("3 Line", () -> new DriveMode(true, goTeleOp, buildPath("R","L",3)));
//            });
//        });
//
//        // ================= BLUE =================
//        m.folder("Blue", b -> {
//
//            b.folder("Short", s -> {
//                s.add("1 Line", () -> new DriveMode(true, goTeleOp, buildPath("B","S",1)));
//                s.add("2 Line", () -> new DriveMode(true, goTeleOp, buildPath("B","S",2)));
//                s.add("3 Line", () -> new DriveMode(true, goTeleOp, buildPath("B","S",3)));
//            });
//
//            b.folder("Long", s -> {
//                s.add("1 Line", () -> new DriveMode(true, goTeleOp, buildPath("B","L",1)));
//                s.add("2 Line", () -> new DriveMode(true, goTeleOp, buildPath("B","L",2)));
//                s.add("3 Line", () -> new DriveMode(true, goTeleOp, buildPath("B","L",3)));
//            });
//        });
//    }
//
//    @Override
//    public void onSelect() {
//        if (follower == null) {
//            follower = org.firstinspires.ftc.teamcode.pedroPathing.Constants.createFollower(hardwareMap);
//            PanelsConfigurables.INSTANCE.refreshClass(this);
//        } else {
//            follower = org.firstinspires.ftc.teamcode.pedroPathing.Constants.createFollower(hardwareMap);
//        }
//
//        follower.setStartingPose(new Pose());
//
//        poseHistory = follower.getPoseHistory();
//
//        telemetryM = PanelsTelemetry.INSTANCE.getTelemetry();
//    }
//
//    /* ===================== DRIVE MODE ===================== */
//
//    public static class DriveMode extends OpMode {
//
//        /* ---------- Auto ---------- */
//        boolean runAuto, goTeleOpAfterAuto;
//        PathChain autoPath;
//        boolean autoFinished = false;
//
//        /* ---------- Hardware ---------- */
//        DcMotor IntakeMotor, FLeft, BLeft, FRight, BRight;
//        DcMotorEx Shooter;
//        CRServo SmallSupportServo, LargeSupportServo, Ramp;
//
//        /* ---------- Button Edges ---------- */
//        ButtonEdge shooterHigh = new ButtonEdge();
//        ButtonEdge shooterLow  = new ButtonEdge();
//        ButtonEdge shooterOff  = new ButtonEdge();
//        ButtonEdge rampOut     = new ButtonEdge();
//        ButtonEdge rampIn      = new ButtonEdge();
//
//        public DriveMode(boolean runAuto, boolean goTeleOpAfterAuto, PathChain autoPath) {
//            this.runAuto = runAuto;
//            this.goTeleOpAfterAuto = goTeleOpAfterAuto;
//            this.autoPath = autoPath;
//        }
//
//        @Override
//        public void init() {
//
//            IntakeMotor = hardwareMap.get(DcMotor.class, "Intake");
//            Shooter = hardwareMap.get(DcMotorEx.class, "Shooter");
//
//            SmallSupportServo = hardwareMap.get(CRServo.class, "SmallSupportServo");
//            LargeSupportServo = hardwareMap.get(CRServo.class, "LargeSupportServo");
//            Ramp = hardwareMap.get(CRServo.class, "Ramp");
//
//            FLeft = hardwareMap.get(DcMotor.class, "FLeft");
//            BLeft = hardwareMap.get(DcMotor.class, "BLeft");
//            FRight = hardwareMap.get(DcMotor.class, "FRight");
//            BRight = hardwareMap.get(DcMotor.class, "BRight");
//
//            SmallSupportServo.setDirection(DcMotorSimple.Direction.REVERSE);
//            FLeft.setDirection(DcMotorSimple.Direction.REVERSE);
//            BLeft.setDirection(DcMotorSimple.Direction.REVERSE);
//            Shooter.setDirection(DcMotorSimple.Direction.REVERSE);
//
//            if (runAuto && autoPath != null) {
//                follower.followPath(autoPath);
//            }
//        }
//
//        @Override
//        public void loop() {
//
//            /* ---------- AUTO ---------- */
//            if (runAuto && !autoFinished) {
//                follower.update();
//                if (!follower.isBusy()) {
//                    autoFinished = true;
//                    if (!goTeleOpAfterAuto) requestOpModeStop();
//                }
//                return;
//            }
//
//            /* ---------- TELEOP DRIVE ---------- */
//            follower.setTeleOpDrive(
//                    gamepad1.left_stick_x,
//                    -gamepad1.left_stick_y,
//                    gamepad1.right_stick_x,
//                    true
//            );
//            follower.update();
//
//            /* ---------- OPERATOR CONTROLS ---------- */
//
//            if (shooterHigh.wasPressed(gamepad2.y))
//                Shooter.setVelocity(Constants.SHOOTER_HIGH_RPM);
//
//            if (shooterLow.wasPressed(gamepad2.b))
//                Shooter.setVelocity(Constants.SHOOTER_LOW_RPM);
//
//            if (shooterOff.wasPressed(gamepad2.x))
//                Shooter.setPower(0);
//
//            if (rampOut.wasPressed(gamepad2.dpad_down))
//                Ramp.setPower(Constants.RAMP_OUT);
//
//            if (rampIn.wasPressed(gamepad2.dpad_up))
//                Ramp.setPower(Constants.RAMP_IN);
//
//            if (!gamepad2.dpad_down && !gamepad2.dpad_up)
//                Ramp.setPower(0);
//
//            IntakeMotor.setPower(-gamepad2.right_stick_y * Constants.INTAKE_SPEED);
//            SmallSupportServo.setPower(-gamepad2.left_stick_y* Constants.HELPER_WHEEL_SPEED);
//            LargeSupportServo.setPower(-gamepad2.left_stick_y* Constants.HELPER_WHEEL_SPEED);
//
//
//        }
//    }
//
//    /* ===================== UTIL ===================== */
//
//    static class ButtonEdge {
//        boolean last;
//        boolean wasPressed(boolean current) {
//            boolean pressed = current && !last;
//            last = current;
//            return pressed;
//        }
//    }
//
//    static PathChain buildPath(String alliance, String side, int line) {
//        Pose start = alliance.equals("B")
//                ? new Pose(0, 0, Math.PI)
//                : new Pose(0, 0, 0);
//
//        PathBuilder pb = new PathBuilder().addWaypoint(start);
//
//        if (side.equals("S")) {
//            pb.addWaypoint(new Pose(
//                    (alliance.equals("R") ? 24 : -24) * line,
//                    0,
//                    start.getHeading()
//            ));
//        } else {
//            pb.set(new Pose(
//                    (alliance.equals("R") ? 48 : -48),
//                    24 * line,
//                    start.getHeading() + Math.PI / 2
//            ));
//        }
//
//        return pb.build();
//    }
//    public static class Constants {
//
//        /* ---- TELEOP DRIVE ---- */
//        public static double TELEOP_DRIVE_SPEED = 1.0;
//        public static double TELEOP_TURN_SPEED = 1.0;
//
//        /* ---- SHOOTER ---- */
//        public static double SHOOTER_HIGH_RPM = 1900;
//        public static double SHOOTER_LOW_RPM = 1200;
//
//        /* ---- INTAKE ---- */
//        public static double INTAKE_SPEED = 1.0;
//        /* ---- HELPER WHEEL ---- */
//        public static double HELPER_WHEEL_SPEED = 1.0;
//
//        /* ---- RAMP ---- */
//        public static double RAMP_OUT = 1.0;
//        public static double RAMP_IN = -1.0;
//
//        /* ---- PEDRO PATHING ---- */
//        ;
//        public static double SHORT_START_Y = 125;
//        public static double SHORT_RED_START_X = 122;
//        public static double SHORT_RED_START_HEADING = 35;
//        public static double SHORT_BLUE_START_X = 22;
//        public static double SHORT_BLUE_START_HEADING = 145;
//        public static double LONG_START_Y = 95;
//        public static double LONG_START_HEADING = 90;
//        public static double LONG_RED_START_X = 84;
//        public static double LONG_BLUE_START_X = 60;
//
//        public static double SHORT_SHOOT_Y = 96;
//        public static double SHORT_RED_SHORT_X = 90;
//        public static double SHORT_BLUE_SHORT_X = 5;
//
//
//    }
//}
