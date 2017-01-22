/*******************************************************************************
 * This files was developed for CS4233: Object-Oriented Analysis & Design.
 * The course was taken at Worcester Polytechnic Institute.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Copyright ©2016 Gary F. Pollice
 *******************************************************************************/

package aquarium;

import aquarium.hw.TemperatureAdjuster;
import aquarium.hw.TemperatureSensor;

import java.util.function.Function;

import static aquarium.TemperatureController.TemperatureScale.CELSIUS;
import static aquarium.Utility.*;

/**
 * The TemperatureController is a key part of the Aquarium system. It monitors the
 * water temperature and collaborates with a temperature adjuster mechanism that
 * is able to heat or cool the water.
 *
 * @version Jan 21, 2017
 */
public final class TemperatureControllerImpl implements TemperatureController {
    private TemperatureSensor   sensor; // The TemperatureSensor
    private TemperatureAdjuster adjuster; // The TemperatureAdjuster
    private TemperatureScale    temperatureScale; // The TemperatureScale
    private double              desiredTemperature; // Always keep the desired temperature in
    // Celsius degrees
    private double              temperatureVariance; // Always keep the desired temperature
    // variation in Celsius degrees
    
    private final Function<Double, Void> adjustTemperatureObserser = (Double temperature) -> {
        double diff = temperature - desiredTemperature;
        if (diff < temperatureVariance && diff > -temperatureVariance)
            adjuster.setState(TemperatureAdjuster.AdjusterState.OFF);
        else if (diff > temperatureVariance)
            adjuster.setState(TemperatureAdjuster.AdjusterState.COOLING);
        else adjuster.setState(TemperatureAdjuster.AdjusterState.HEATING);
        return null;
    }; // The observer automatically adjust temperature when water temperature or desired
    // temperature changes
    
    /**
     * Private constructor. The only way to create an instance of this class
     * is to use the factory method.
     *
     * @param sensor   the TemperatureSensor
     * @param adjuster the TemperatureAdjuster
     */
    private TemperatureControllerImpl(TemperatureSensor sensor, TemperatureAdjuster adjuster) {
        this.sensor = sensor;
        this.sensor.addObserver(this.adjustTemperatureObserser);
        this.adjuster = adjuster;
    }
    
    /**
     * Factory method for creating an instance of the TemperatureControllerImpl. This creates
     * the
     *
     * @param sensor   the sensor associated with the controller
     * @param adjuster the adjuster associated with the controller
     *
     * @return the resulting controller
     */
    static public TemperatureController makeTemperatureController(TemperatureSensor sensor,
                                                                  TemperatureAdjuster adjuster) {
        TemperatureControllerImpl controller = new TemperatureControllerImpl(sensor, adjuster);
        controller.temperatureScale = CELSIUS;
        controller.desiredTemperature = 20;
        controller.temperatureVariance = 1.0;
        return controller;
    }
    
    /*
     * @see aquarium.TemperatureController#setTemperature(double)
     */
    @Override
    public void setTemperature(double temperature) {
        desiredTemperature = temperatureScale == CELSIUS ? temperature : fahrenheitToCelsius
                (temperature);
        adjustTemperatureObserser.apply(sensor.getTemperature());
    }
    
    /*
     * @see aquarium.TemperatureController#getCurrentTemperature()
     */
    @Override
    public double getCurrentTemperature() {
        double currTemp = sensor.getTemperature();
        return temperatureScale == CELSIUS ? currTemp : celsiusToFahrenheit(currTemp);
    }
    
    /*
     * @see aquarium.TemperatureController#getAcceptableVariation()
     */
    @Override
    public double getAcceptableVariation() {
        return temperatureScale == CELSIUS ? temperatureVariance : celsiusToFahrenheitVariance
                (temperatureVariance);
    }
    
    /*
     * @see aquarium.TemperatureController#setAcceptableVariation(double)
     */
    @Override
    public void setAcceptableVariation(double variance) {
        temperatureVariance = temperatureScale == CELSIUS ? variance :
                              fahrenheitToCelsiusVariance(variance);
        adjustTemperatureObserser.apply(sensor.getTemperature());
    }
    
    /*
     * @see aquarium.TemperatureController#getTemperatureScale()
     */
    @Override
    public TemperatureScale getTemperatureScale() {
        return temperatureScale;
    }
    
    /*
     * @see aquarium.TemperatureController#setTemperatureScale(aquarium.TemperatureController
     * .TemperatureScale)
     */
    @Override
    public void setTemperatureScale(TemperatureScale newTemperatureScale) {
        temperatureScale = newTemperatureScale;
    }
}
