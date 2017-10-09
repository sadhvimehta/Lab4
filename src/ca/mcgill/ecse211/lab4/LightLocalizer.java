package ca.mcgill.ecse211.lab4;

import lejos.hardware.Sound;
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
	private static double SENSOR_DISTANCE = 7;
	private EV3ColorSensor lightsensor;
	double [] lightData;
	
	private SampleProvider colorSensor;
	private float[] colorData;

	public LightLocalizer(Odometer odometer, SampleProvider colorSensor, float[] colorData,
			EV3ColorSensor lightsensor, Navigator navigator) {
		this.odometer = odometer;
		this.navigation = navigator;
		this.lightData = new double [5];
		this.lightsensor = lightsensor;
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
		navigation.setSpeed(0,0);
		navigation.turnTo(0);
	}
	
	/**
	 * A method which moves our vehicle to the estimated origin after localizing to 0 degrees
	 */
	private void goToApproxOrig() {
		// turn towards corner, and move backwards until sensor reads a line
		navigation.turnTo(225);
		int lineIndex=0;
		while (lineIndex < 1) {
			if(lightsensor.getColorID()==Color.BLACK) {
				lineIndex++;
			}
			navigation.setSpeed(-50, -50);
		}
		Sound.beep();
 		navigation.setSpeed(0,0);
 		//move forward so that the middle point of the robot is approximatelly on 0,0
 		//navigation.goForward(SENSOR_DISTANCE);
 		//navigation.setSpeed(0,0);
	}
	
	/** 
	 * A method to rotate our vehicle and collect data from light sensors
	 */
	private void rotateLightSensor() {
		navigation.setSpeed(-50 , 50);
		int lineIndex=1;
		while(lineIndex < 5){
		if(lightsensor.getColorID()==Color.BLACK) {
			lightData[lineIndex]=odometer.getTheta();
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
		double deltaThetaY= (lightData[4]-lightData[2]);
		double deltaThetaX= (lightData[3]-lightData[1]);
		
		//use trig to determine position of the robot 
		double Xnew = (-2)*SENSOR_DISTANCE*Math.cos(Math.PI*deltaThetaX/(2*180));
		double Ynew = (-2)*SENSOR_DISTANCE*Math.cos(Math.PI*deltaThetaY/(2*180));
		
		//set new "corrected" position
		odometer.setPosition(new double [] {Xnew, Ynew, Math.atan2(Ynew, Xnew)+180 }, new boolean [] {true, true, true});
	}
}
