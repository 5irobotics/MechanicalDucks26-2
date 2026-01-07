package org.firstinspires.ftc.teamcode.pedroPathing.ourcode;

import static org.firstinspires.ftc.teamcode.pedroPathing.Tuning.changes;
import static org.firstinspires.ftc.teamcode.pedroPathing.Tuning.drawOnlyCurrent;
import static org.firstinspires.ftc.teamcode.pedroPathing.Tuning.draw;
import static org.firstinspires.ftc.teamcode.pedroPathing.Tuning.follower;
import static org.firstinspires.ftc.teamcode.pedroPathing.Tuning.stopRobot;
import static org.firstinspires.ftc.teamcode.pedroPathing.Tuning.telemetryM;

import com.bylazar.configurables.PanelsConfigurables;
import com.bylazar.configurables.annotations.Configurable;
import com.bylazar.configurables.annotations.IgnoreConfigurable;
import com.bylazar.field.FieldManager;
import com.bylazar.field.PanelsField;
import com.bylazar.field.Style;
import com.bylazar.telemetry.PanelsTelemetry;
import com.bylazar.telemetry.TelemetryManager;
import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.*;
import com.pedropathing.math.*;
import com.pedropathing.paths.*;
import com.pedropathing.telemetry.SelectableOpMode;
import com.pedropathing.util.*;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;


import org.firstinspires.ftc.teamcode.pedroPathing.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * This is the Tuning class. It contains a selection menu for various tuning OpModes.
 *
 * @author Baron Henderson - 20077 The Indubitables
 * @version 1.0, 6/26/2025
 */
@Configurable
@TeleOp(name = "PLAYME")
public class Meet3TeleOp extends SelectableOpMode {
    public static Follower follower;

    @IgnoreConfigurable
    static PoseHistory poseHistory;

    @IgnoreConfigurable
    static TelemetryManager telemetryM;

    @IgnoreConfigurable
    static ArrayList<String> changes = new ArrayList<>();

    public Meet3TeleOp() {
        super("Select Alliance", a -> {
            a.folder("Red", l -> {
                l.folder("Short", s -> {
                    s.add("1 Line", redShortOneLine::new);
                    s.add("2 Line", redShortTwoLine::new);
                    s.add("3 Line", redShortThreeLine::new);
                        });
                l.folder("Long", m -> {
                    m.add("1 Line", redLongOneLine::new);
                    m.add("2 Line", redLongTwoLine::new);
                    m.add("3 Line", redLongThreeLine::new);
                });

            });
            a.folder("Blue", l -> {
                l.folder("Short", b-> {
                    b.add("1 Line", blueShortOneLine::new);
                    b.add("2 Line", blueShortTwoLine::new);
                    b.add("3 Line", blueShortThreeLine::new);
                });
                l.folder("Long", c -> {
                    c.add("1 Line", blueLongOneLine::new);
                    c.add("2 Line", blueLongTwoLine::new);
                    c.add("3 Line", blueLongThreeLine::new);
                });

        });
    });

    @Override
    public void onSelect() {
        if (follower == null) {
            follower = Constants.createFollower(hardwareMap);
            PanelsConfigurables.INSTANCE.refreshClass(this);
        } else {
            follower = Constants.createFollower(hardwareMap);
        }

        follower.setStartingPose(new Pose());

        poseHistory = follower.getPoseHistory();

        telemetryM = PanelsTelemetry.INSTANCE.getTelemetry();
    }
    class redShortOneLine extends OpMode{
        @Override
        public void init() {

        }

        @Override
        public void loop() {

        }
    }


    }
}




