/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.stlpriory.robotics.subsystems;

import edu.wpi.first.wpilibj.CANJaguar;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.can.CANTimeoutException;
import edu.wpi.first.wpilibj.command.Subsystem;
import org.stlpriory.robotics.RobotMap;
import org.stlpriory.robotics.commands.drivetrain.DriveWithGamepad;
import org.stlpriory.robotics.misc.Debug;
import org.stlpriory.robotics.misc.Utils;

/**
 *
 */
public class CANDriveTrain extends Subsystem {

    private RobotDrive drive;
    private static CANJaguar leftFrontJag;
    private static CANJaguar rightFrontJag;
    private static CANJaguar leftRearJag;
    private static CANJaguar rightRearJag;
    private static double direction = 1;

    public CANDriveTrain() {
        super("CANDriveTrain");
        Debug.println("[CANDriveTrain] Instantiating...");

        CANJaguar.ControlMode controlMode = CANJaguar.ControlMode.kPercentVbus;
        //CANJaguar.ControlMode controlMode = CANJaguar.ControlMode.kCurrent;
        //CANJaguar.ControlMode controlMode = CANJaguar.ControlMode.kSpeed;
        //CANJaguar.ControlMode controlMode = CANJaguar.ControlMode.kPosition;
        //CANJaguar.ControlMode controlMode = CANJaguar.ControlMode.kVoltage;

        // The proportional gain of the Jaguar's PID controller.
        double p = 30;
        // The integral gain of the Jaguar's PID controller.
        double i = 0.005;
        // The differential gain of the Jaguar's PID controller.
        double d = 0.25;

        try {
            Debug.println("[CANDriveTrain] Initializing left front CANJaguar to CAN bus address "
                    + RobotMap.LEFT_FRONT_DRIVE_MOTOR_CAN_BUS_ADDRESS);
            leftFrontJag = new CANJaguar(RobotMap.LEFT_FRONT_DRIVE_MOTOR_CAN_BUS_ADDRESS,controlMode);
            //leftFrontJag.setPID(p, i, d);
        } catch (CANTimeoutException ex) {
            ex.printStackTrace();
        }

        try {
            Debug.println("[CANDriveTrain] Initializing left rear CANJaguar to CAN bus address "
                    + RobotMap.LEFT_REAR_DRIVE_MOTOR_CAN_BUS_ADDRESS);
            leftRearJag = new CANJaguar(RobotMap.LEFT_REAR_DRIVE_MOTOR_CAN_BUS_ADDRESS,controlMode);
            //leftRearJag.setPID(p, i, d);
        } catch (CANTimeoutException ex) {
            ex.printStackTrace();
        }

        try {
            Debug.println("[CANDriveTrain] Initializing right front CANJaguar to CAN bus address "
                    + RobotMap.RIGHT_FRONT_DRIVE_MOTOR_CAN_BUS_ADDRESS);
            rightFrontJag = new CANJaguar(RobotMap.RIGHT_FRONT_DRIVE_MOTOR_CAN_BUS_ADDRESS,controlMode);
            //rightFrontJag.setPID(p, i, d);
        } catch (CANTimeoutException ex) {
            ex.printStackTrace();
        }

        try {
            Debug.println("[CANDriveTrain] Initializing right rear CANJaguar to CAN bus address "
                    + RobotMap.RIGHT_REAR_DRIVE_MOTOR_CAN_BUS_ADDRESS);
            rightRearJag = new CANJaguar(RobotMap.RIGHT_REAR_DRIVE_MOTOR_CAN_BUS_ADDRESS,controlMode);
            //rightRearJag.setPID(p, i, d);
        } catch (CANTimeoutException ex) {
            ex.printStackTrace();
        }

        // References for CAN programming:
        // http://forums.usfirst.org/showthread.php?t=14709
        //

        Debug.println("[CANDriveTrain] Initializing RobotDrive");
        drive = new RobotDrive(leftFrontJag, leftRearJag, rightFrontJag, rightRearJag);
        drive.setSafetyEnabled(false);
        drive.setExpiration(0.1);
        drive.setSensitivity(0.5);
        drive.setMaxOutput(1.0);
        drive.setInvertedMotor(RobotDrive.MotorType.kFrontRight, true);
        drive.setInvertedMotor(RobotDrive.MotorType.kRearRight, true);

        Debug.println("[CANDriveTrain] Instantiation complete.");
    }

    /**
     * Initialize and set default command
     */
    public void initDefaultCommand() {
        Debug.println("[CANDriveTrain.initDefaultCommand()] Setting default command to " + DriveWithGamepad.class.getName());
        setDefaultCommand(new DriveWithGamepad());
    }

    public void setForwards() {
        direction = 1;
    }

    public void setBackwards() {
        direction = -1;
    }

    public void stop() {
        Debug.println("[CANDriveTrain.stop]");
        drive.tankDrive(0.0, 0.0);
    }

    public boolean canDrive() {
        return true;
    }

    public void tankDrive(double leftValue, double rightValue) {
        leftValue *= direction;
        rightValue *= direction;
        if (canDrive()) {
            drive.tankDrive(leftValue, rightValue);
        }
    }

    public void arcadeDrive(double moveValue, double rotateValue) {
        moveValue *= direction;
        rotateValue *= direction;
        if (canDrive()) {
            drive.arcadeDrive(moveValue, rotateValue);
        }
    }

    /**
     * Drive method for Mecanum wheeled robots.
     */
    public void mecanumDrive(Joystick joystick) {
        /*
         * Three-axis joystick mecanum control.
         * Let x represent strafe left/right
         * Let y represent rev/fwd
         * Let z represent spin CCW/CW axes
         * where each varies from -1 to +1.
         * So:
         * y = -1 corresponds to full speed reverse,
         * y= +1 corresponds to full speed forward,
         * x= -1 corresponds to full speed strafe left,
         * x= +1 corresponds to full speed strafe right,
         * z= -1 corresponds to full speed spin CCW,
         * z= +1 corresponds to full speed spin CW
         *
         * Axis indexes:
         * 1 - LeftX
         * 2 - LeftY
         * 3 - Triggers (Each trigger = 0 to 1, axis value = right - left)
         * 4 - RightX
         * 5 - RightY
         * 6 - DPad Left/Right
         */

        double rawLeftX = joystick.getRawAxis(1);
        double rawLeftY = joystick.getRawAxis(2);
        double rawZ = joystick.getRawAxis(3);

        double scaledLeftX = Utils.scale(rawLeftX);
        double scaledLeftY = Utils.scale(rawLeftY);

        double right     = -scaledLeftX;
        double forward   =  scaledLeftY;
        double rotation  = -rawZ;
        double clockwise =  rawZ;

        drive.mecanumDrive_Cartesian(right, -forward, rotation, clockwise);
    }

    public void straight(double speed) {
        speed *= direction;
        if (canDrive()) {
            drive.tankDrive(speed, speed * 0.75);
        }
    }

    public void turnLeft() { // sets the motor speeds to start a left turn
        arcadeDrive(0.0, 1.0);
    }

    public void driveWithJoystick(Joystick joystick) {
        drive.arcadeDrive(joystick);
    }

    public void driveWithGamepad(Joystick joystick) {
        mecanumDrive(joystick);
    }

}
