package ca.mcgill.ecse211.lab4;


import ca.mcgill.ecse211.lab4.Odometer;
import lejos.hardware.motor.EV3LargeRegulatedMotor;

public class Navigator {
	
	
	// vehicle variables
	private static Odometer odometer;
	private static EV3LargeRegulatedMotor leftMotor, rightMotor;
	private final double RADIUS, TRACK;
	private final int MOTOR_ACCELERATION = 200;
	
	// navigation variables
	public static final int FORWARD_SPEED = 250, ROTATE_SPEED = 100;

	public Navigator(EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor, Odometer odometer) {
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
		this.odometer = odometer;
		this.RADIUS = Lab4.RADIUS;
		this.TRACK = Lab4.TRACK;
		leftMotor.setAcceleration(MOTOR_ACCELERATION);
		rightMotor.setAcceleration(MOTOR_ACCELERATION);
	}
	
	// Convert how far they need to travel
	private static int convertDistance(double radius, double distance) {
		return (int) ((180.0 * distance) / (Math.PI * radius));
	}


	// Determine the angle the motors need to turn
	private static int convertAngle(double radius, double TRACK, double angle) {
		return convertDistance(radius, Math.PI * TRACK * angle / 360.0);
	}
	

	// Drives robot to specified cartesian coordinate
	public void travelTo(double x, double y) {
		x= x*30.48;
		y= y*30.48;
		
		double deltaX = x - odometer.getX();
		double deltaY = y - odometer.getY();
		
		
		// calculate the minimum angle
		double minAngle = Math.toDegrees(Math.atan2(deltaX, deltaY)) - odometer.getThetaDegrees();
		
		// Adjust the angle to make sure it takes the min angle
		if (minAngle < -180) {
			minAngle = 360 + minAngle;
		} else if (minAngle > 180) {
			minAngle = minAngle - 360;
		}
		
		// turn to the minimum angle
		turnTo(minAngle);
		
		// calculate the distance to next point
		double distance  = Math.hypot(deltaX, deltaY);
		
		// move to the next point
		leftMotor.setSpeed(FORWARD_SPEED);
		rightMotor.setSpeed(FORWARD_SPEED);
		leftMotor.rotate(convertDistance(RADIUS,distance), true);
		rightMotor.rotate(convertDistance(RADIUS, distance), false);

		leftMotor.stop(true);
		rightMotor.stop(true);
		leftMotor.setSpeed(0);
		rightMotor.setSpeed(0);
	}
	

	// Turn to the min angle that you have chosen
	public void turnTo(double theta) {
		leftMotor.setSpeed(ROTATE_SPEED);
		rightMotor.setSpeed(ROTATE_SPEED);
		
		if(theta < 0) { // if angle is negative, turn to the left
			leftMotor.rotate(-convertAngle(RADIUS, TRACK, -theta), true);
			rightMotor.rotate(convertAngle(RADIUS, TRACK, -theta), false);
		} 
		else { // angle is positive, turn to the right
			leftMotor.rotate(convertAngle(RADIUS, TRACK, theta), true);
			rightMotor.rotate(-convertAngle(RADIUS, TRACK, theta), false);
		}
		
		leftMotor.setSpeed(0);
		rightMotor.setSpeed(0);
	}
	
	public void stop() {
		leftMotor.stop(true);
		rightMotor.stop(true);
	}
	
	
	public void setSpeed(int leftM, int rightM) {
		leftMotor.setSpeed(leftM);
		rightMotor.setSpeed(rightM);
		
		if (leftM > 0) {
			leftMotor.forward();
		} else {
			leftMotor.backward();
		}
		
		if (rightM > 0) {
			rightMotor.forward();
		} else {
			rightMotor.backward();
		}
	}
	
}
