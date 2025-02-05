// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.wpilibj.Filesystem;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.components.swervedrive.commands.drivebase.AbsoluteDriveAdv;
import frc.robot.components.swervedrive.subsystems.SwerveSubsystem;

import static frc.robot.components.swervedrive._C.*;

import java.io.File;

/**
 * This class is where the bulk of the robot should be declared. Since Command-based is a "declarative" paradigm, very
 * little robot logic should actually be handled in the {@link Robot} periodic methods (other than the scheduler calls).
 * Instead, the structure of the robot (including subsystems, commands, and trigger mappings) should be declared here.
 */
public class RobotContainer
{

  // The robot's subsystems and commands are defined here...
  private final SwerveSubsystem drivebase = new SwerveSubsystem(new File(Filesystem.getDeployDirectory(), "swerve/neo"));

  // Replace with CommandPS4Controller or CommandJoystick if needed
  final CommandXboxController driverXbox = new CommandXboxController(0);

  /**
   * The container for the robot. Contains subsystems, OI devices, and commands.
   */
  public RobotContainer()
  {
    // Configure the trigger bindings
    configureBindings();

    AbsoluteDriveAdv closedAbsoluteDriveAdv = new AbsoluteDriveAdv(drivebase,
         () -> -MathUtil.applyDeadband(driverXbox.getLeftY(),
                                      OperatorConstants.LEFT_Y_DEADBAND),
         () -> -MathUtil.applyDeadband(driverXbox.getLeftX(),
                                      OperatorConstants.LEFT_X_DEADBAND),
         () -> -MathUtil.applyDeadband(driverXbox.getRightX(),
                                      OperatorConstants.RIGHT_X_DEADBAND),
         driverXbox.getHID()::getYButtonPressed,
         driverXbox.getHID()::getAButtonPressed,
         driverXbox.getHID()::getXButtonPressed,
         driverXbox.getHID()::getBButtonPressed);

    Command driveFieldOrientedDirectAngle = drivebase.driveCommand(
        () -> MathUtil.applyDeadband(driverXbox.getLeftY() * .8, OperatorConstants.LEFT_Y_DEADBAND),
        () -> MathUtil.applyDeadband(driverXbox.getLeftX() * .8, OperatorConstants.LEFT_X_DEADBAND),
        () -> driverXbox.getRightX(),
        () -> (1 - driverXbox.getRightX()));

    Command driveFieldOrientedAnglularVelocity = drivebase.driveCommand(
        () -> MathUtil.applyDeadband(driverXbox.getLeftY() * .8, OperatorConstants.LEFT_Y_DEADBAND),
        () -> MathUtil.applyDeadband(driverXbox.getLeftX() * .8, OperatorConstants.LEFT_X_DEADBAND),
        () -> driverXbox.getRightX());

    Command driveFieldOrientedDirectAngleSim = drivebase.simDriveCommand(
        () -> MathUtil.applyDeadband(driverXbox.getLeftY(), OperatorConstants.LEFT_Y_DEADBAND),
        () -> MathUtil.applyDeadband(driverXbox.getLeftX(), OperatorConstants.LEFT_X_DEADBAND),
        () -> driverXbox.getRawAxis(2));

    drivebase.setDefaultCommand(
        !RobotBase.isSimulation() ? driveFieldOrientedAnglularVelocity : driveFieldOrientedAnglularVelocity);
  }

  private void configureBindings()
  {
    driverXbox.a().onTrue((Commands.runOnce(drivebase::zeroGyro)));
    driverXbox.x().onTrue(Commands.runOnce(drivebase::addFakeVisionReading));
    driverXbox.b().whileTrue(
      Commands.deferredProxy(() -> 
        drivebase.driveToPose(
          new Pose2d(
            new Translation2d(4, 4), 
            Rotation2d.fromDegrees(0)
          )
        )
      )
    );
    driverXbox.y().onTrue(Commands.runOnce(drivebase::lock));
  }

  /**
   * Use this to pass the autonomous command to the main {@link Robot} class.
   *
   * @return the command to run in autonomous
   */
  public Command getAutonomousCommand()
  {
    // An example command will be run in autonomous
    return drivebase.getAutonomousCommand("New Auto");
  }

  public void setDriveMode()
  {
    //drivebase.setDefaultCommand();
  }

  public void setMotorBrake(boolean brake)
  {
    drivebase.setMotorBrake(brake);
  }

  public SwerveSubsystem getDrivebase(){
    return drivebase;
  }
}