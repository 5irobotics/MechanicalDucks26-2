package org.firstinspires.ftc.teamcode.pedroPathing.ourcode.teleOp;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name="MIDDLEPART", group="DONTPLAY")
public class MiddlePart2 extends OpMode {

    @Override
    public void init() {
//a
    }

    @Override
    public void loop() {

    }

    public void Intake(double y, double helper_button1, double helper_button2,
                       DcMotor intake1, CRServo intake_helper1, CRServo intake_helper2) {
        intake1.setPower(-y);
        intake_helper1.setPower(-helper_button1);
        intake_helper2.setPower(-helper_button2);

    }



    public void Shooter(boolean shooterspeed1, boolean shooterspeed2
            , DcMotorEx shooter) {
        if (shooterspeed1) {
            shooter.setVelocity(2250);
        } else if (shooterspeed2) {
            shooter.setVelocity(1925);
        } else {
            shooter.setPower(0);
        }

    }

    public void Ramp(boolean HButtonOUT, boolean HButtonIN, CRServo Ramp) {
        if (HButtonOUT){
            Ramp.setPower(1);
        } else if (HButtonIN) {
            Ramp.setPower(-1);
        }
    }



    public void Hood(boolean HButtonOUT, boolean HButtonIN, Servo Hood) {
        if (HButtonOUT){
            Hood.setPosition(0.7);
        } else if (HButtonIN) {
            Hood.setPosition(0.3);
        }
    }
}