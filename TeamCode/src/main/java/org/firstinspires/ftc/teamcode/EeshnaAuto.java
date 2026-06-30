package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.util.ElapsedTime;

@Autonomous(name = "EeshnaAuto")
public class EeshnaAuto extends LinearOpMode {
    private DcMotor bLeft, fLeft, bRight, fRight;
    private ElapsedTime runtime = new ElapsedTime();

    static final double COUNTS_PER_MOTOR_REV = 537.6;
    static final double DRIVE_GEAR_REDUCTION = 1.0;
    static final double WHEEL_DIAMETER_INCHES = 4.0;
    static final double COUNTS_PER_INCH = (COUNTS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION) /
            (WHEEL_DIAMETER_INCHES * 3.1415);

    @Override
    public void runOpMode() {
        bLeft = hardwareMap.get(DcMotor.class, "bLeft");
        fLeft = hardwareMap.get(DcMotor.class, "fLeft");
        bRight = hardwareMap.get(DcMotor.class, "bRight");
        fRight = hardwareMap.get(DcMotor.class, "fRight");

        bLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        fLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        bRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        fRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        bLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        fLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        bRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        fRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        waitForStart();

        encoderDrive(1,12);
        sleep(2000);
        encoderTurn(1,12,true);
        sleep(2000);
        encoderStrafe(1,12, false);
        sleep(2000);

    }

    private void encoderDrive(double speed, double Inches) {

        int target = (int)(Inches * COUNTS_PER_INCH);

        bLeft.setTargetPosition(bLeft.getCurrentPosition() + target);
        fLeft.setTargetPosition(fLeft.getCurrentPosition() + target);
        bRight.setTargetPosition(bRight.getCurrentPosition() + target);
        fRight.setTargetPosition(fRight.getCurrentPosition() + target);

        bLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        fLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        bRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        fRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        bLeft.setPower(Math.abs(speed));
        fLeft.setPower(Math.abs(speed));
        bRight.setPower(Math.abs(speed));
        fRight.setPower(Math.abs(speed));

        while (opModeIsActive() &&
               (bLeft.isBusy() && fLeft.isBusy() && bRight.isBusy() && fRight.isBusy())) {
            telemetry.addData("Status:", "Running to target");
            telemetry.update();
        }

        bLeft.setPower(0);
        fLeft.setPower(0);
        bRight.setPower(0);
        fRight.setPower(0);

        bLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        fLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        bRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        fRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        telemetry.addData("Status:", "Complete");
        telemetry.update();

    }

    private void encoderTurn(double speed, double Inches, boolean right) {

        int target = (int)(Inches * COUNTS_PER_INCH);

        if (right) {
            bLeft.setTargetPosition(bLeft.getCurrentPosition() + target);
            fLeft.setTargetPosition(fLeft.getCurrentPosition() + target);
            bRight.setTargetPosition(bRight.getCurrentPosition() - target);
            fRight.setTargetPosition(fRight.getCurrentPosition() - target);
        }
        else{
            bLeft.setTargetPosition(bLeft.getCurrentPosition() - target);
            fLeft.setTargetPosition(fLeft.getCurrentPosition() - target);
            bRight.setTargetPosition(bRight.getCurrentPosition() + target);
            fRight.setTargetPosition(fRight.getCurrentPosition() + target);
        }



        bLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        fLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        bRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        fRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        bLeft.setPower(Math.abs(speed));
        fLeft.setPower(Math.abs(speed));
        bRight.setPower(Math.abs(speed));
        fRight.setPower(Math.abs(speed));

        while (opModeIsActive() &&
                (bLeft.isBusy() && fLeft.isBusy() && bRight.isBusy() && fRight.isBusy())) {
            telemetry.addData("Status:", "Running to target");
            telemetry.update();
        }

        bLeft.setPower(0);
        fLeft.setPower(0);
        bRight.setPower(0);
        fRight.setPower(0);

        bLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        fLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        bRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        fRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        telemetry.addData("Status:", "Complete");
        telemetry.update();

    }

    private void encoderStrafe(double speed, double Inches, boolean right) {

        int target = (int)(Inches * COUNTS_PER_INCH);

        if (right) {
            bLeft.setTargetPosition(bLeft.getCurrentPosition() - target);
            fLeft.setTargetPosition(fLeft.getCurrentPosition() + target);
            bRight.setTargetPosition(bRight.getCurrentPosition() + target);
            fRight.setTargetPosition(fRight.getCurrentPosition() - target);
        }
        else{
            bLeft.setTargetPosition(bLeft.getCurrentPosition() + target);
            fLeft.setTargetPosition(fLeft.getCurrentPosition() - target);
            bRight.setTargetPosition(bRight.getCurrentPosition() - target);
            fRight.setTargetPosition(fRight.getCurrentPosition() + target);
        }



        bLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        fLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        bRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        fRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        bLeft.setPower(Math.abs(speed));
        fLeft.setPower(Math.abs(speed));
        bRight.setPower(Math.abs(speed));
        fRight.setPower(Math.abs(speed));

        while (opModeIsActive() &&
                (bLeft.isBusy() && fLeft.isBusy() && bRight.isBusy() && fRight.isBusy())) {
            telemetry.addData("Status:", "Running to target");
            telemetry.update();
        }

        bLeft.setPower(0);
        fLeft.setPower(0);
        bRight.setPower(0);
        fRight.setPower(0);

        bLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        fLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        bRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        fRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        telemetry.addData("Status:", "Complete");
        telemetry.update();

    }

    private void stopReset() {
        bLeft.setPower(0);
        fLeft.setPower(0);
        bRight.setPower(0);
        fRight.setPower(0);

        bLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        fLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        bRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        fRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

    }


}
