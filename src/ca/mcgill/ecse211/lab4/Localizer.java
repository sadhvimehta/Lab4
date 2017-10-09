package ca.mcgill.ecse211.lab4;

import lejos.hardware.Sound;
import lejos.robotics.SampleProvider;

public class Localizer {
	
	private Odometer odometer;
	private float[] usData;
	private SampleProvider usSensor;
	private Navigator navigator;
	private final int WALL_DISTANCE = 30;
	private final int MARGIN = 3;
	private final int ROTATE_SPEED;
	
	public Localizer(Odometer odometer, SampleProvider usSensor, 
					 float[] usData, Navigator navigator) {
		this.usSensor = usSensor;
		this.usData = usData;
		this.odometer = odometer;
		this.navigator = navigator;
		this.ROTATE_SPEED = navigator.ROTATE_SPEED;
	}
	
	
	
	public float getDistance() {
		usSensor.fetchSample(usData, 0);
		float distance = usData[0]*100;
		// reduces large outlier values
		return Math.min(100, distance);
	}
	
	public void fallingEdge() {
		double angleOne, angleTwo, orientation;
		
		while(getDistance() > WALL_DISTANCE + MARGIN) {
			System.out.println("Distance: "+ getDistance());
			navigator.setSpeed(-ROTATE_SPEED/2, ROTATE_SPEED/2);
		}
		
		navigator.stop();
		System.out.println("Distance Stop: "+ getDistance());
		Sound.beep();

		angleOne = odometer.getThetaDegrees();
		
		while(getDistance() < WALL_DISTANCE - MARGIN - 3) {
			System.out.println("Distance: "+ getDistance());
			navigator.setSpeed(-ROTATE_SPEED/2, ROTATE_SPEED/2);
		}
		
		navigator.stop();
		Sound.beep();
		
		angleTwo = odometer.getThetaDegrees();
		
		orientation = getOrientation(angleOne, angleTwo) + odometer.getThetaDegrees() + 12;
		
		odometer.setPosition(new double [] {0.0, 0.0, 
				orientation}, 
				new boolean [] {true, true, true});
		
		navigator.turnTo(-orientation);
	}
	
	public void risingEdge() {
		System.out.println("Rising Edge");
		double angleOne, angleTwo, orientation;
		System.out.println("Distance1.0: "+ getDistance());
		while(getDistance() < WALL_DISTANCE - MARGIN) {
			System.out.println("Distance1: "+ getDistance());
			navigator.setSpeed(ROTATE_SPEED, -ROTATE_SPEED);
		}
		
		navigator.stop();
		System.out.println("Beep");
		Sound.beep();
		
		angleOne = odometer.getThetaDegrees();
		System.out.println("Distance2.0: " + getDistance());
		while(getDistance() > WALL_DISTANCE + MARGIN) {
			System.out.println("Distance2: " + getDistance());
			navigator.setSpeed(ROTATE_SPEED, -ROTATE_SPEED);
		}
		
		navigator.stop();
		System.out.println("Beep");
		Sound.beep();
		
		angleTwo = odometer.getThetaDegrees();
		
		orientation = getOrientation(angleOne, angleTwo) + odometer.getThetaDegrees() + 12;
		
		odometer.setPosition(new double [] {0.0, 0.0, 
				orientation}, 
				new boolean [] {true, true, true});
		
		navigator.turnTo(-orientation);
	}
	
	public double getOrientation(double angleOne, double angleTwo) {
		double angle = 0;
		if(angleOne > angleTwo) {
			angle = 225 - (angleOne + angleTwo)/2.0;
		} else {
			angle = 45 - (angleOne + angleTwo)/2.0;
		}
		
		return angle;
	}
}
