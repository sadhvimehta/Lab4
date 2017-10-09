package ca.mcgill.ecse211.lab4;

import lejos.hardware.Sound;
import lejos.ev3.tools.EV3Console;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.robotics.SampleProvider;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.robotics.Color;
import lejos.robotics.SampleProvider;

public class LightLocalizer {
	private Odometer odometer;
	private Navigator navigation;
	private static EV3LargeRegulatedMotor leftMotor, rightMotor;
	private static int FORWARD_SPEED = 100;
	private static double SENSOR_DISTANCE = 14;
	double [] lightData;
	
	private SampleProvider colorSensor;
	private float[] colorData;

	public LightLocalizer(Odometer odometer, SampleProvider colorSensor,
						  float[] colorData, Navigator navigator) {
		this.odometer = odometer;
		this.navigation = navigator;
		this.lightData = new double [5];
		this.colorSensor = colorSensor;
		this.colorData = colorData;
	}

	public void doLocalization() {
		// drive to location listed in tutorial
		// start rotating and clock all 4 gridlines
		// do trig to compute (0,0) and 0 degrees
		// when done travel to (0,0) and turn to 0 degrees
		
		// move to our estimated origin
		goToApproxOrig(); 
		
		// move our vehicle in a circle and collect data from light sensors
		rotateLightSensor();
		
		// correct position of our robot using light sensor data
		correctPosition();
		
		//travel to 0,0 then turn to the 0 angle
		navigation.travelTo(0, 0);
		
		correctAngle();
		
		navigation.setSpeed(0,0);
	}
	
	
	public void correctAngle() {
		navigation.turnTo(-odometer.getThetaDegrees()%360, true);
		colorSensor.fetchSample(colorData, 0);
		while (navigation.isNavigating()) {
			if(colorData[0] < 0.25) {
				Sound.beepSequenceUp();
				navigation.stop();
				break;
			}
			colorSensor.fetchSample(colorData, 0);
		}
		

	}
	
	/**
	 * A method which moves our vehicle to the estimated origin after localizing to 0 degrees
	 */
	private void goToApproxOrig() {
		// turn towards corner, and move backwards until sensor reads a line
		navigation.turnTo(215, false);
		int lineIndex=0;
		
		navigation.driveDistance(1, false);
		while (lineIndex < 1) {
			colorSensor.fetchSample(colorData, 0);
			if(colorData[0] < 0.25) {
				Sound.beep();
				lineIndex++;
			}
			navigation.setSpeed(-200, -200);
		}
 		navigation.setSpeed(0,0);
 		//move forward so that the middle point of the robot is approximatelly on 0,0
 		//navigation.goForward(SENSOR_DISTANCE);
 		//navigation.setSpeed(0,0);
	}
	
	/** 
	 * A method to rotate our vehicle and collect data from light sensors
	 */
	private void rotateLightSensor() {
		navigation.turnTo(-360, true);
		int lineIndex=1;
		while(navigation.isNavigating()) {
			colorSensor.fetchSample(colorData, 0);
			if(colorData[0] < 0.25 && lineIndex < 5) {
				lightData[lineIndex]=odometer.getThetaDegrees();
				lineIndex++;
				Sound.beep();
			}
		}
		navigation.setSpeed(0,0);
	}
	
	/**
	 * A method to correct the position of our robot using light sensor data
	 */
	
	private void correctPosition() {
		//compute difference in angles
		double deltaThetaY= Math.abs(lightData[3]-lightData[1]);
		double deltaThetaX= Math.abs(lightData[4]-lightData[2]);
		
		double deltaTheta = 270 + (deltaThetaY)/2 - (lightData[1]);
		
		//use trig to determine position of the robot 
		double Xnew = SENSOR_DISTANCE*Math.cos(Math.toRadians(deltaThetaX));
		double Ynew = SENSOR_DISTANCE*Math.cos(Math.toRadians(deltaThetaY));
		
		
		//set new "corrected" position
		if(deltaTheta > 0) {
			odometer.setPosition(new double [] {Xnew, Ynew, odometer.getThetaDegrees() + deltaTheta}, 
					new boolean [] {true, true, false});
		} else {
			odometer.setPosition(new double [] {Xnew, Ynew, odometer.getThetaDegrees() - deltaTheta}, 
					new boolean [] {true, true, false});
		}

	}
}
