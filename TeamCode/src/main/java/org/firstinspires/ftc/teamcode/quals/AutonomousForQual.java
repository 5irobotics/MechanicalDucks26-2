package org.firstinspires.ftc.teamcode.quals;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.*;
import com.pedropathing.math.*;
import com.pedropathing.paths.*;
import com.pedropathing.telemetry.SelectableOpMode;
import com.pedropathing.util.*;

import org.firstinspires.ftc.teamcode.pedroPathing.Constants;

@Autonomous(name="Auto_Quals", group="Quals")
public class AutonomousForQual extends LinearOpMode {

    // ===== SELECTION ENUMS =====
    enum Alliance { RED, BLUE }
    enum StartDistance { NEAR, FAR }
    enum ScoreLevel { ROW_1, ROW_1_2, ROW_1_2_3 }
    enum AutoState { TO_ARTIFACT, COLLECT, TO_SCORE, SCORE, PARK, IDLE }

    private final HardwareConstants robot = new HardwareConstants();
    private Follower drive;

    private Alliance alliance = Alliance.RED;
    private StartDistance distance = StartDistance.NEAR;
    private ScoreLevel scoreLevel = ScoreLevel.ROW_1;
    private AutoState state = AutoState.IDLE;

    private int artifactIndex = 0;
    private int scoreIndex = 0;
    
    // Timer and sub-state for mechanism control
    private final ElapsedTime actionTimer = new ElapsedTime();
    private int subState = 0;

    @Override
    public void runOpMode() {

        robot.init(hardwareMap);
        drive = Constants.createFollower(hardwareMap);
        drive.setPose(getStartPose());

        // ===== INIT SELECTION MENU =====
        boolean aLast=false, bLast=false, xLast=false;
        while (opModeInInit()) {

            if (gamepad1.x && !xLast)
                alliance = (alliance==Alliance.RED)?Alliance.BLUE:Alliance.RED;

            if (gamepad1.a && !aLast)
                distance = (distance==StartDistance.NEAR)?StartDistance.FAR:StartDistance.NEAR;

            if (gamepad1.b && !bLast)
                switch(scoreLevel){
                    case ROW_1: scoreLevel=ScoreLevel.ROW_1_2; break;
                    case ROW_1_2: scoreLevel=ScoreLevel.ROW_1_2_3; break;
                    case ROW_1_2_3: scoreLevel=ScoreLevel.ROW_1; break;
                }

            aLast=gamepad1.a;
            bLast=gamepad1.b;
            xLast=gamepad1.x;

            telemetry.addLine("=== AUTO CONFIG ===");
            telemetry.addData("Alliance", alliance);
            telemetry.addData("Distance", distance);
            telemetry.addData("ScoreLevel", scoreLevel);
            telemetry.update();
        }

        waitForStart();
        if (!opModeIsActive()) return;
        state = AutoState.TO_ARTIFACT;

        // ===== MAIN STATE LOOP =====
        while(opModeIsActive() && state!=AutoState.IDLE){

            drive.update();

            switch(state){
                case TO_ARTIFACT: goToArtifact(); break;
                case COLLECT: collect(); break;
                case TO_SCORE: goToScore(); break;
                case SCORE: score(); break;
                case PARK: park(); break;
            }
        }
    }

    // ===== PEDRO PATH HELPERS =====
    private Pose mirrorIfBlue(Pose pose){
        if(alliance==Alliance.BLUE)
            return new Pose(-pose.getX(),pose.getY(),Math.PI-pose.getHeading());
        return pose;
    }

    private Pose getStartPose(){
        Pose near = new Pose(12,-60,Math.toRadians(90));
        Pose far = new Pose(-36,-60,Math.toRadians(90));
        return mirrorIfBlue(distance==StartDistance.NEAR?near:far);
    }

    private final Pose[] ARTIFACTS = { new Pose(-24,-12,0), new Pose(0,-12,0), new Pose(24,-12,0)};
    private final Pose ROW_1 = new Pose(0,36,Math.toRadians(180));
    private final Pose ROW_2 = new Pose(0,40,Math.toRadians(180));
    private final Pose ROW_3 = new Pose(0,44,Math.toRadians(180));
    private final Pose PARK = new Pose(48,60,Math.toRadians(180));

    private int getScoreCount(){
        switch(scoreLevel){
            case ROW_1: return 1;
            case ROW_1_2: return 2;
            case ROW_1_2_3: return 3;
        }
        return 1;
    }

    private Pose getScorePose(){
        Pose pose;
        switch(scoreIndex){
            case 0: pose=ROW_1; break;
            case 1: pose=ROW_2; break;
            case 2: pose=ROW_3; break;
            default: pose=ROW_1;
        }
        return mirrorIfBlue(pose);
    }

    // ===== STATES =====
    private void goToArtifact(){
        PathChain path = new PathBuilder(drive)
                .addPath(new BezierLine(drive.getPose(),mirrorIfBlue(ARTIFACTS[artifactIndex])))
                .build();
        drive.followPath(path);
        state=AutoState.COLLECT;
    }

    private void collect(){
        if(!drive.isBusy()){
            // Intake sequence
            if (subState == 0) {
                robot.IntakeMotor.setPower(1.0);
                actionTimer.reset();
                subState = 1;
            }
            
            if (subState == 1 && actionTimer.seconds() > 1.5) {
                robot.IntakeMotor.setPower(0);
                subState = 0; // Reset substate for next use
                state=AutoState.TO_SCORE;
            }
        }
    }

    private void goToScore(){
        PathChain path = new PathBuilder(drive)
                .addPath(new BezierLine(drive.getPose(),getScorePose()))
                .build();
        drive.followPath(path);
        state=AutoState.SCORE;
    }

    private void score(){
        if(!drive.isBusy()){
            // Scoring sequence (from TeleOp)
            if (subState == 0) {
                robot.Shooter.setVelocity(robot.SHOOT_VELOCITY);
                actionTimer.reset();
                subState = 1;
            }
            
            if (subState == 1 && actionTimer.seconds() >= robot.RAMP_TIME_FIRST) {
                robot.BottomSupport.setPower(robot.SUPPORT_SPEED);
                robot.TopSupport.setPower(robot.SUPPORT_SPEED);
                actionTimer.reset();
                subState = 2;
            }
            
            if (subState == 2 && actionTimer.seconds() >= robot.FEED_TIME) {
                robot.stopAll();
                subState = 0; // Reset substate for next use
                
                scoreIndex++;
                artifactIndex++;
                if(scoreIndex<getScoreCount()) state=AutoState.TO_ARTIFACT;
                else state=AutoState.PARK;
            }
        }
    }

    private void park(){
        PathChain path = new PathBuilder(drive)
                .addPath(new BezierLine(drive.getPose(),mirrorIfBlue(PARK)))
                .build();
        drive.followPath(path);
        state=AutoState.IDLE;
    }
}
