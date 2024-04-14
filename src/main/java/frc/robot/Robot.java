// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.Filesystem;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import frc.robot.components.swervedrive._C;
import frc.robot.components.swervedrive.subsystems.SwerveSubsystem;

import java.io.File;
import java.io.IOException;

import swervelib.SwerveModule;
import swervelib.motors.SparkMaxSwerve;
import swervelib.parser.SwerveParser;

public class Robot extends TimedRobot
{

  private static Robot   instance;
  private        Command m_autonomousCommand;

  private RobotContainer m_robotContainer;

  private Timer disabledTimer;

  public Robot()
  {
    instance = this;
  }

  public static Robot getInstance()
  {
    return instance;
  }

  @Override
  public void robotInit(){
    m_robotContainer = new RobotContainer();

    disabledTimer = new Timer();
  }

  @Override
  public void robotPeriodic(){
    CommandScheduler.getInstance().run();
  }

  @Override
  public void disabledInit(){
    m_robotContainer.setMotorBrake(true);
    disabledTimer.reset();
    disabledTimer.start();
  }

  @Override
  public void disabledPeriodic(){
    if (disabledTimer.hasElapsed(_C.DrivebaseConstants.WHEEL_LOCK_TIME))
    {
      m_robotContainer.setMotorBrake(false);
      disabledTimer.stop();
    }
  }

  @Override
  public void autonomousInit(){
    m_robotContainer.setMotorBrake(true);
    m_autonomousCommand = m_robotContainer.getAutonomousCommand();

    if (m_autonomousCommand != null)
    {
      m_autonomousCommand.schedule();
    }
  }

  public void autonomousPeriodic(){
  }

  @Override
  public void teleopInit(){
    if (m_autonomousCommand != null) {
      m_autonomousCommand.cancel();
    }
    m_robotContainer.setDriveMode();
    m_robotContainer.setMotorBrake(true);
  }

  @Override
  public void teleopPeriodic(){
    String[] names = new String[]{"frontLeft", "frontRight", "backLeft", "backRight"};
      for (SwerveModule module : m_robotContainer.getDrivebase().getRawDrive().getModules()) {
      SmartDashboard.putNumber("Drive Velocity (Module[" + names[module.moduleNumber] + "])", module.getDriveMotor().getVelocity());
    }
  }

  @Override
  public void testInit(){
    CommandScheduler.getInstance().cancelAll();
    try
    {
      new SwerveParser(new File(Filesystem.getDeployDirectory(), "swerve"));
    } catch (IOException e)
    {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void testPeriodic(){}

  @Override
  public void simulationInit(){}

  @Override
  public void simulationPeriodic(){}
}