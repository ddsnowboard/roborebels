/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2008. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.stlpriory.robotics;


import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.stlpriory.robotics.commands.CommandBase;
import org.stlpriory.robotics.commands.drivetrain.DriveInASquare;
import org.stlpriory.robotics.misc.Debug;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class RoboRebels extends IterativeRobot {

    private Command autonomousCommand;
    private Timer timer = new Timer();
    private boolean firstTime = true;

    /**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */
    public void robotInit() {
        Debug.println("[RoboRebels.robotInit()] Initializing...");
        timer.start();

        // Initialize all subsystems and the operator interface
        CommandBase.init();

        SmartDashboard.putData(Scheduler.getInstance());

        // instantiate the command used for the autonomous period
        autonomousCommand = new DriveInASquare();
        //autonomousCommand = new Auton1();

        timer.stop();
        Debug.println("[RoboRebels.robotInit()] Done in " + timer.get() * 1e6 + " ms");
   }

    public void autonomousInit() {
        Debug.println("[mode] Autonomous");
        //commonInit();

        // schedule the autonomous command (example)
        if (autonomousCommand != null)  {
            autonomousCommand.start();
        }
     }

    /**
     * This function is called periodically during autonomous
     */
    public void autonomousPeriodic() {
        Scheduler.getInstance().run();
    }

    public void teleopInit() {
        // This makes sure that the autonomous stops running when
        // teleop starts running. If you want the autonomous to
        // continue until interrupted by another command, remove
        // this line or comment it out.
        Debug.println("[mode] Operator control");
        if (autonomousCommand != null) {
            autonomousCommand.cancel();
        }
        //commonInit();
    }

    /**
     * This function is called periodically during operator control
     */
    public void teleopPeriodic() {
        Scheduler.getInstance().run();
    }

    /**
     * A common initialization code for all modes which will be called each time
     * disabledInit(), autonomousInit(), or teleopInit() is called.
     */
    public void commonInit() {
        if (firstTime) {
            firstTime = false;
        }
    }

}
