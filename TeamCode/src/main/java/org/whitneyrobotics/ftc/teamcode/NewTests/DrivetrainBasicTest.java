package org.whitneyrobotics.ftc.teamcode.NewTests;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import java.util.Arrays;
import java.util.Arrays.*;
import java.util.HashMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.whitneyrobotics.ftc.teamcode.lib.geometry.Coordinate;
import org.whitneyrobotics.ftc.teamcode.lib.geometry.Position;
import org.whitneyrobotics.ftc.teamcode.lib.util.RobotConstants;
import org.whitneyrobotics.ftc.teamcode.lib.util.Toggler;
import org.whitneyrobotics.ftc.teamcode.subsys.WHSRobotImpl;
import org.whitneyrobotics.ftc.teamcode.subsys.WHSRobotImplDrivetrainOnly;

@TeleOp(name="Basic Drivetrain Test", group="Tests")
public class DrivetrainBasicTest extends OpMode {
    private WHSRobotImplDrivetrainOnly robot;

    private final double robotCenterWidthOffset = 152.4;
    private final double robotCenterLengthOffset = 6.5;
    private Position target;

    private final int TANK_DRIVE = 0;
    private final int MECANUM_DRIVE = 1;
    private final int RTT = 2;
    private final int DTT = 3;
    private Toggler modeTog = new Toggler(4);
    private String driveMode = "Exponential Drive";
    FtcDashboard dashboard;
    Telemetry dashboardTelemetry;
    TelemetryPacket packet = new TelemetryPacket();

    @Override
    public void init(){
        dashboard = FtcDashboard.getInstance();
        dashboardTelemetry = dashboard.getTelemetry();;
        dashboardTelemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());

        dashboard.sendTelemetryPacket(packet);

        robot = new WHSRobotImplDrivetrainOnly(hardwareMap);
        robot.robotDrivetrain.resetEncoders(); // May already run without encoders
        robot.robotDrivetrain.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        Coordinate init = new Coordinate(-1800 + robotCenterWidthOffset,900,90);
        robot.setInitialCoordinate(init);
        target = new Position(-1638,910);
    }

    @Override
    public void loop(){
        HashMap<String,Object> data = new HashMap<>();
        //robot.estimateCoordinate();
        robot.estimateHeading();
        robot.estimatePosition();
        modeTog.changeState(gamepad1.x);
        data.put("Current Mode: ", modeTog.currentState());
        if(gamepad1.y){
            robot.robotDrivetrain.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            robot.robotDrivetrain.operate(0,0);
            throw new RuntimeException("Kill command issued");


        }
        if(gamepad1.b){
            robot.robotDrivetrain.resetEncoders();
            target = new Position(-1638,910);
            Coordinate init = new Coordinate(-1800 + robotCenterWidthOffset,900,90);
            robot.setInitialCoordinate(init);
            robot.rotateToTargetInProgress = false;
            robot.driveToTargetInProgress = false;
            robot.firstDriveLoop = false;
            robot.firstRotateLoop = false;
        }
        switch(modeTog.currentState()){
            case TANK_DRIVE:
                if(gamepad1.left_bumper){
                    robot.robotDrivetrain.operate(gamepad1.left_stick_y/2.54,gamepad1.right_stick_y/2.54);
                } else {
                    robot.robotDrivetrain.operate(gamepad1.left_stick_y,gamepad1.right_stick_y);
                }
                break;
            case MECANUM_DRIVE:
                if(gamepad1.left_bumper){
                    robot.robotDrivetrain.operateMecanumDrive(gamepad1.left_stick_x/2.54,-gamepad1.left_stick_y/2.54,gamepad1.right_stick_x/2.54,robot.getCoordinate().getHeading());
                    driveMode = "Slow linear drive";
                } else if (gamepad1.right_bumper){
                    robot.robotDrivetrain.operateMecanumDriveScaled(gamepad1.left_stick_x/2.54,-gamepad1.left_stick_y/2.54,gamepad1.right_stick_x/2.54,robot.getCoordinate().getHeading());
                    driveMode = "Slow exponential drive";
                } else {
                    robot.robotDrivetrain.operateMecanumDriveScaled(gamepad1.left_stick_x,-gamepad1.left_stick_y,gamepad1.right_stick_x,robot.getCoordinate().getHeading());
                    driveMode = "Exponential drive";
                }
                break;
            case RTT:
                if(gamepad1.right_bumper){
                    robot.rotateToTarget(270,false);
                }
                break;
            case DTT:
                if(gamepad1.right_bumper){
                    robot.driveToTarget(target,false);
                }
                break;

        }
        data.put("------------uwu","");
        data.put("Robot X",robot.getCoordinate().getX());
        data.put("Target X",target.getX());
        data.put("Robot Y",robot.getCoordinate().getY());
        data.put("Target Y",target.getY());
        data.put("","");
        data.put("Mecanum mode",driveMode);
        data.put(" Drive to Target",robot.driveToTargetInProgress());
        data.put("Drive kP", RobotConstants.DRIVE_CONSTANTS.kP);
        data.put("Drive kD", RobotConstants.DRIVE_CONSTANTS.kD);
        data.put("Drive kI", RobotConstants.DRIVE_CONSTANTS.kI);
        data.put("Drive PID output",robot.driveController.getOutput());
        data.put("Drive derivative",robot.driveController.getDerivative());
        data.put("Drive integral",robot.driveController.getIntegral());
        data.put("Distance to target",robot.distanceToTargetDebug);

        data.put("Robot heading",robot.getCoordinate().getHeading());
        data.put("Rotate Error",robot.angleToTargetDebug);
        data.put("Rotate to Target",robot.rotateToTargetInProgress());
        data.put("Rotate kP", RobotConstants.ROTATE_CONSTANTS.kP);
        data.put("Rotate kD", RobotConstants.ROTATE_CONSTANTS.kD);
        data.put("Rotate kI", RobotConstants.ROTATE_CONSTANTS.kI);
        data.put("Rotate PID output",robot.rotateController.getOutput());
        data.put("Rotate derivative",robot.rotateController.getDerivative());
        data.put("Rotate integral",robot.rotateController.getIntegral());
        data.put("Encoder position in ticks", Arrays.toString(robot.robotDrivetrain.getAllEncoderPositions()));
        data.put("Encoder Deltas in ticks",Arrays.toString(robot.robotDrivetrain.getAllEncoderDelta()));
        data.put("Wheel velocities (ticks/sec)",Arrays.toString(robot.robotDrivetrain.getAllWheelVelocities()));
        for(String i : data.keySet()){
            telemetry.addData(i,data.get(i));
            packet.put(i,data.get(i));
        }
        dashboard.sendTelemetryPacket(packet);

    }

    @Override
    public void stop() {
        super.stop();
    }
}
