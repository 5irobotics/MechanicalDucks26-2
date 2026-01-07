package org.firstinspires.ftc.teamcode.pedroPathing.ourcode.auto.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

public class Drive2 {

    private ElapsedTime runtime = new ElapsedTime();
    static final double COUNTS_PER_MOTOR_REV = 537.6;
    static final double DRIVE_GEAR_REDUCTION = 1.0;
    static final double WHEEL_DIAMETER_INCHES = 4.0;
    static final double COUNTS_PER_INCH = (COUNTS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION) /
            (WHEEL_DIAMETER_INCHES * 3.1415);


    public void encoderDrive(double speed, double Inches, double timeoutS, DcMotor motorfl, DcMotor motorfr, DcMotor motorbr, DcMotor motorbl) {
        int newFrontLeftTarget, newFrontRightTarget, newBackLeftTarget, newBackRightTarget;

        int moveCounts = (int) (Inches * COUNTS_PER_INCH);

        newFrontLeftTarget = motorfl.getCurrentPosition() + moveCounts;
        newBackLeftTarget = motorbl.getCurrentPosition() + moveCounts;
        newFrontRightTarget = motorfr.getCurrentPosition() + moveCounts;
        newBackRightTarget = motorbr.getCurrentPosition() + moveCounts;

        motorfl.setTargetPosition(newFrontLeftTarget);
        motorfr.setTargetPosition(newFrontRightTarget);
        motorbl.setTargetPosition(newBackLeftTarget);
        motorbr.setTargetPosition(newBackRightTarget);

        motorfl.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motorfr.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motorbl.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motorbr.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        runtime.reset();

        motorfl.setPower(Math.abs(speed));
        motorfr.setPower(Math.abs(speed));
        motorbl.setPower(Math.abs(speed));
        motorbr.setPower(Math.abs(speed));

        /*while (opModeIsActive() &&
                (runtime.seconds() < timeoutS) &&
                (frontLeft.isBusy() && frontRight.isBusy() &&
                        backLeft.isBusy() && backRight.isBusy())) {
            telemetry.addData("Path", "Running to target");
            telemetry.update();
        }*/
    }

    public void fourWheelDrive(double speed, double flInches, double frInches, double brInches, double blInches, double timeoutS, DcMotor motorfl, DcMotor motorfr, DcMotor motorbr, DcMotor motorbl) {
        int newFrontLeftTarget, newFrontRightTarget, newBackLeftTarget, newBackRightTarget;

        int flmoveCounts = (int) (flInches * COUNTS_PER_INCH);
        int frmoveCounts = (int) (frInches * COUNTS_PER_INCH);
        int brmoveCounts = (int) (brInches * COUNTS_PER_INCH);
        int blmoveCounts = (int) (blInches * COUNTS_PER_INCH);

        newFrontLeftTarget = motorfl.getCurrentPosition() + flmoveCounts;
        newBackLeftTarget = motorbl.getCurrentPosition() + frmoveCounts;
        newFrontRightTarget = motorfr.getCurrentPosition() + brmoveCounts;
        newBackRightTarget = motorbr.getCurrentPosition() + blmoveCounts;

        motorfl.setTargetPosition(newFrontLeftTarget);
        motorfr.setTargetPosition(newFrontRightTarget);
        motorbl.setTargetPosition(newBackLeftTarget);
        motorbr.setTargetPosition(newBackRightTarget);

        motorfl.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motorfr.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motorbl.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motorbr.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        runtime.reset();

        motorfl.setPower(Math.abs(speed));
        motorfr.setPower(Math.abs(speed));
        motorbl.setPower(Math.abs(speed));
        motorbr.setPower(Math.abs(speed));

        /*while (opModeIsActive() &&
                (runtime.seconds() < timeoutS) &&
                (frontLeft.isBusy() && frontRight.isBusy() &&
                        backLeft.isBusy() && backRight.isBusy())) {
            telemetry.addData("Path", "Running to target");
            telemetry.update();
        }*/
    }
    public void right_turn(double speed, double flInches, double frInches, double brInches, double blInches, double timeoutS, DcMotor motorfl, DcMotor motorfr, DcMotor motorbr, DcMotor motorbl) {
        int newFrontLeftTarget, newFrontRightTarget, newBackLeftTarget, newBackRightTarget;

        int flmoveCounts = (int) (flInches * COUNTS_PER_INCH);
        int frmoveCounts = (int) (frInches * COUNTS_PER_INCH);
        int brmoveCounts = (int) (brInches * COUNTS_PER_INCH);
        int blmoveCounts = (int) (blInches * COUNTS_PER_INCH);

        newFrontLeftTarget = motorfl.getCurrentPosition() + flmoveCounts;
        newBackLeftTarget = motorbl.getCurrentPosition() + frmoveCounts;
        newFrontRightTarget = motorfr.getCurrentPosition() + brmoveCounts;
        newBackRightTarget = motorbr.getCurrentPosition() + blmoveCounts;

        motorfl.setTargetPosition(newFrontLeftTarget);
        motorfr.setTargetPosition(-newFrontRightTarget);
        motorbl.setTargetPosition(newBackLeftTarget);
        motorbr.setTargetPosition(-newBackRightTarget);

        motorfl.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motorfr.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motorbl.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motorbr.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        runtime.reset();

        motorfl.setPower(Math.abs(speed));
        motorfr.setPower(Math.abs(speed));
        motorbl.setPower(Math.abs(speed));
        motorbr.setPower(Math.abs(speed));

        /*while (opModeIsActive() &&
                (runtime.seconds() < timeoutS) &&
                (frontLeft.isBusy() && frontRight.isBusy() &&
                        backLeft.isBusy() && backRight.isBusy())) {
            telemetry.addData("Path", "Running to target");
            telemetry.update();
        }*/
    }


    public void left_turn(double speed, double flInches, double frInches, double brInches, double blInches, double timeoutS, DcMotor motorfl, DcMotor motorfr, DcMotor motorbr, DcMotor motorbl) {
        int newFrontLeftTarget, newFrontRightTarget, newBackLeftTarget, newBackRightTarget;

        int flmoveCounts = (int) (flInches * COUNTS_PER_INCH);
        int frmoveCounts = (int) (frInches * COUNTS_PER_INCH);
        int brmoveCounts = (int) (brInches * COUNTS_PER_INCH);
        int blmoveCounts = (int) (blInches * COUNTS_PER_INCH);

        newFrontLeftTarget = motorfl.getCurrentPosition() + flmoveCounts;
        newBackLeftTarget = motorbl.getCurrentPosition() + frmoveCounts;
        newFrontRightTarget = motorfr.getCurrentPosition() + brmoveCounts;
        newBackRightTarget = motorbr.getCurrentPosition() + blmoveCounts;

        motorfl.setTargetPosition(-newFrontLeftTarget);
        motorfr.setTargetPosition(newFrontRightTarget);
        motorbl.setTargetPosition(newBackLeftTarget);
        motorbr.setTargetPosition(-newBackRightTarget);

        motorfl.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motorfr.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motorbl.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motorbr.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        runtime.reset();

        motorfl.setPower(Math.abs(speed));
        motorfr.setPower(Math.abs(speed));
        motorbl.setPower(Math.abs(speed));
        motorbr.setPower(Math.abs(speed));

        /*while (opModeIsActive() &&
                (runtime.seconds() < timeoutS) &&
                (frontLeft.isBusy() && frontRight.isBusy() &&
                        backLeft.isBusy() && backRight.isBusy())) {
            telemetry.addData("Path", "Running to target");
            telemetry.update();
        }*/
    }
}
