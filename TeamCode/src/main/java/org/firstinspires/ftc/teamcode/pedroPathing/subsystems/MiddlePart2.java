package org.firstinspires.ftc.teamcode.pedroPathing.subsystems;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
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
            , DcMotor shooter) {
        if (shooterspeed1) {
            shooter.setPower(0.81);
        } else if (shooterspeed2) {
            shooter.setPower(0.70);
        } else {
            shooter.setPower(0);
        }

    }
}
