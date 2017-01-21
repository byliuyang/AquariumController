
Aquarium system
===

The project implements the temperature controller for Aquarium system. 
It monitors the water temperature and collaborates with a temperature adjuster mechanism that is able to heat or cool the water.

Upon initialization, the desired temperature will be set by default to 20°C (68°F), the acceptable temperature variation will be 1.0°C (1.8°F), and the temperature scale set to Celsius.

A client can send a message to the temperature controller to set the desired temperature. When this happens, the controller will ensure that the temperature is changed, if necessary, to be within the acceptable variation range from the specified temperature.

A client can send a message to set the temperature scale to either Celsius or Fahrenheit. When this occurs, all reporting and public settings will use the selected scale until another change is made.

A client can send a message to the controller to set an acceptable variation. This is the amount that the temperature can vary from the desired temperature without causing the temperature adjuster to begin cooling or heating the water.

The water temperature changes and goes out of range of acceptable variation from the desired temperature. That is it is < or > the acceptable variation. The controller activates the adjuster appropriately to cause the temperature to come within an acceptable range. The state of the adjuster changes appropriately.

Install
---
Simply import the project from the supplied zipped archive into Eclipse or IntellJ

Run
---
Run unit tests with JUnit plugin

Notice: All tests are implemented with observable pattern, automatically notified when state of sensor and adjuster changes.

Code Coverage
---
Generate code coverage using EclEmma plugin

Coverage statistics:
	100% for TemperatureControllerImpl
	90.3% for Utility
	58% for TemperatureController ( Just a interface, don't really need code coverage)
	
Author
---
Name: Yang Liu
Email: yliu17@wpi.edu
Date: Jan 21, 2017

Acknowledgement
---

Starter from Prof. Gary Pollice at Worcester Polytechnic Institute 
