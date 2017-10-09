package ca.mcgill.ecse211.lab4;

import lejos.hardware.Button;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.hardware.sensor.SensorModes;
import lejos.robotics.SampleProvider;

public class Lab4 {
	
	// Left motor connected to output A
	// Right motor connected to output D
	// Sensor motor to output B
	private static final EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));
	private static final EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("D"));
	private static final Port usSensorPort = LocalEV3.get().getPort("S1");
	private static final EV3ColorSensor lightsensor = new EV3ColorSensor(LocalEV3.get().getPort("S4"));

	private static SampleProvider colorSensor;
	private static float[] colorData;

	
	
	// Characteristics of our vehicle
	public static final double TRACK = 11.6;
	public static final double RADIUS = 2.1;
	
	public static void main(String[] args) {
		int buttonChoice;
		
		SensorModes usSensor = new EV3UltrasonicSensor(usSensorPort);	
		SampleProvider usDistance = usSensor.getMode("Distance");	
		float[] usData = new float[usDistance.sampleSize()];

		
		final TextLCD t = LocalEV3.get().getTextLCD();
		Odometer odometer = new Odometer(leftMotor, rightMotor, TRACK);

		Navigator navigator = new Navigator(leftMotor, rightMotor, odometer);
		
		Localizer localizer = new Localizer(odometer, usSensor, usData, navigator);
		LightLocalizer lightlocalizer = new LightLocalizer( odometer, colorSensor, colorData ,lightsensor, navigator);
		
		OdometryDisplay odometryDisplay = new OdometryDisplay(odometer,t);

		do {
			// clear the display
			t.clear();

			// ask the user whether the motors should drive in a square or float
			t.drawString("< Rising | Falling >", 0, 0);
			t.drawString("  Edge   | Edge     ", 0, 1);

			buttonChoice = Button.waitForAnyPress();
		} 
		while (buttonChoice != Button.ID_LEFT
				&& buttonChoice != Button.ID_RIGHT);

		if (buttonChoice == Button.ID_LEFT) {
			odometer.start();
			odometryDisplay.start();
			// localizer.risingEdge();
			lightlocalizer.doLocalization();			

			
		} else {
			odometer.start();
			odometryDisplay.start();
			// localizer.fallingEdge();
			lightlocalizer.doLocalization();			

		}
	/*	if (buttonChoice2 == Button.ID_LEFT) {
			lightlocalizer.doLocalization();			
		}*/
		
		while (Button.waitForAnyPress() != Button.ID_ESCAPE);
		System.exit(0);
	}
}
