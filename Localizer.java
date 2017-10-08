package ca.mcgill.ecse211.lab4;

import lejos.robotics.SampleProvider;

public class Localizer {
	
	private Odometer odometer;
	private float[] usData;
	private SampleProvider usSensor;
	private Navigator navigator;
	private final int WALL_DISTANCE = 30;
	private final int MARGIN = 5;
	private final int ROTATE_SPEED;
	public enum type {FALLING_EDGE, RISING_EDGE};
	private type localizationType;
	
	public Localizer(Odometer odometer, SampleProvider usSensor, 
					 float[] usData, Navigator navigator, type type) {
		this.usSensor = usSensor;
		this.usData = usData;
		this.odometer = odometer;
		this.navigator = navigator;
		this.ROTATE_SPEED = navigator.ROTATE_SPEED;
		this.localizationType = type;
		
		
		if (localizationType == type.FALLING_EDGE) {
			fallingEdge();
		} else if (localizationType == type.RISING_EDGE) {
			
		}
	}
	
	
	
	public float getDistance() {
		usSensor.fetchSample(usData, 0);
		float distance = usData[0]*100;
		// reduces large outlier values
		return Math.min(100, distance);
	}
	
	public void fallingEdge() {
		double angleOne, angleTwo, orientation;
		
		while(getDistance() < WALL_DISTANCE + MARGIN) {
			navigator.setSpeed(ROTATE_SPEED, -ROTATE_SPEED);
		}
		
		angleOne = odometer.getThetaDegrees();
		
		while(getDistance() < WALL_DISTANCE + MARGIN) {
			navigator.setSpeed(-ROTATE_SPEED, ROTATE_SPEED);
		}
		
		angleTwo = odometer.getThetaDegrees();
		
		
		orientation = getOrientation(angleOne, angleTwo);
		
		odometer.setPosition(new double [] {0.0, 0.0, 
				odometer.getThetaDegrees()+orientation}, 
				new boolean [] {true, true, true});
	}
	
	public void risingEdge() {
		double angleOne, angleTwo, orientation;
		
		while(getDistance() < WALL_DISTANCE + MARGIN) {
			navigator.setSpeed(ROTATE_SPEED, -ROTATE_SPEED);
		}

		angleOne = odometer.getThetaDegrees();
		
		while(getDistance() > WALL_DISTANCE + MARGIN) {
			navigator.setSpeed(-ROTATE_SPEED, ROTATE_SPEED);
		}
		
		angleTwo = odometer.getThetaDegrees();
		
		
		orientation = getOrientation(angleOne, angleTwo);
		
		odometer.setPosition(new double [] {0.0, 0.0, 
				odometer.getThetaDegrees()+orientation}, 
				new boolean [] {true, true, true});
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
