package org.firstinspires.ftc.teamcode.pedroPathing.ourcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.*;

@TeleOp(name="TeleOp Only", group="Z")
public class TeleOpOnly extends OpMode {

    private DcMotor FLeft, BLeft, FRight, BRight;
    private DcMotor IntakeMotor;
    private DcMotorEx Shooter;
    private CRServo SmallSupportServo, LargeSupportServo, Ramp;

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
    }

    @Override
    public void loop() {
        double drive = -gamepad1.left_stick_y;
        double strafe = gamepad1.left_stick_x;
        double turn = gamepad1.right_stick_x;

        FLeft.setPower(drive + strafe + turn);
        BLeft.setPower(drive - strafe + turn);
        FRight.setPower(drive - strafe - turn);
        BRight.setPower(drive + strafe - turn);

        IntakeMotor.setPower(gamepad2.left_stick_y);
        

        if(gamepad2.y){Shooter.setVelocity(2250)}
        else if(gamepad.b){Shooter.setVelocity(2100)}
        else if(gamepad.a){Shooter.setVelocity(0)}

        
        if (gamepad2.x) {
            SmallSupportServo.setPower(1.0);
            LargeSupportServo.setPower(1.0);
        } else{
            SmallSupportServo.setPower(gamepad2.right_stick_y);
            LargeSupportServo.setPower(gamepad2.right_stick_y);
        }
    }
}
