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

import aquarium.hw.TestTemperatureSensor;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;
import java.util.Queue;
import java.util.function.Function;

import static aquarium.TemperatureController.TemperatureScale;
import static aquarium.TemperatureController.TemperatureScale.CELSIUS;
import static aquarium.TemperatureController.TemperatureScale.FAHRENHEIT;
import static aquarium.hw.TemperatureAdjuster.AdjusterState;
import static aquarium.hw.TemperatureAdjuster.AdjusterState.*;
import static org.junit.Assert.*;

public class TemperatureControllerTest {
    private TemperatureController             controller;
    private TestTemperatureSensor             sensor;
    private TestObservableTemperatureAdjuster adjuster;
    private Queue<AdjusterState>              expectedStates;
    private Queue<Double>                     desireTemperatures;
    private Queue<Double>                     switchPoints;
    private Queue<TemperatureScale>           switchScales;
    private boolean                           stateChanged;
    
    @Before
    public void setUp() {
        sensor = new TestTemperatureSensor(0.5);
        adjuster = new TestObservableTemperatureAdjuster(sensor, 0.1);
        expectedStates = new LinkedList<>();
        desireTemperatures = new LinkedList<>();
        switchPoints = new LinkedList<>();
        switchScales = new LinkedList<>();
    }
    
    @Test
    public void createTemperatureController() {
        controller = TemperatureControllerImpl.makeTemperatureController(sensor, adjuster);
        assertNotNull(controller);
    }
    
    @Test
    public void createControllerWithDefaultScaleAndVariance() {
        controller = TemperatureControllerImpl.makeTemperatureController(sensor, adjuster);
        assertNotNull(controller);
        assertEquals(CELSIUS, controller.getTemperatureScale());
        assertEquals(1.0, controller.getAcceptableVariation(), 0.0);
    }
    
    // Tests for initialize temperature controller 
    
    @Test
    public void temperatureDesired() {
        expectedStates.add(OFF);
        adjuster.addObserver(assertState(expectedStates));
        controller = TemperatureControllerImpl.makeTemperatureController(sensor, adjuster);
        sensor.setTemperature(19.8);
    }
    
    @Test
    public void temperatureLowerThanDesired() {
        expectedStates.add(HEATING);
        expectedStates.add(OFF);
        
        adjuster.addObserver(assertState(expectedStates));
        controller = TemperatureControllerImpl.makeTemperatureController(sensor, adjuster);
        sensor.setTemperature(15.0);
    }
    
    @Test
    public void temperatureHigherThanDesired() {
        expectedStates.add(COOLING);
        expectedStates.add(OFF);
        adjuster.setState(OFF);
        
        adjuster.addObserver(assertState(expectedStates));
        controller = TemperatureControllerImpl.makeTemperatureController(sensor, adjuster);
        sensor.setTemperature(23.0);
    }
    
    // Tests for set desired temperature 
    
    @Test
    public void setDesiredTemperatureTo22() {
        sensor.setTemperature(20.0);
        adjuster.addObserver(assertTempAndState(expectedStates, desireTemperatures));
        
        controller = TemperatureControllerImpl.makeTemperatureController(sensor, adjuster);
        controller.setAcceptableVariation(0.5);
        
        assertEquals(OFF, adjuster.getState());
        
        expectedStates.add(HEATING);
        expectedStates.add(OFF);
        
        desireTemperatures.add(22.0);
        
        controller.setTemperature(desireTemperatures.peek());
    }
    
    @Test
    public void setDesiredTemperatureTo18() {
        sensor.setTemperature(20.0);
        adjuster.addObserver(assertTempAndState(expectedStates, desireTemperatures));
        
        controller = TemperatureControllerImpl.makeTemperatureController(sensor, adjuster);
        controller.setAcceptableVariation(1.0);
        controller.setTemperature(20);
        
        assertEquals(OFF, adjuster.getState());
        
        expectedStates.add(COOLING);
        expectedStates.add(OFF);
        
        desireTemperatures.add(18.0);
        
        controller.setTemperature(desireTemperatures.peek());
    }
    
    @Test
    public void setDesiredTemperatureTo20AndHalf() {
        sensor.setTemperature(20.0);
        adjuster.addObserver(assertTempAndState(expectedStates, desireTemperatures));
        
        controller = TemperatureControllerImpl.makeTemperatureController(sensor, adjuster);
        controller.setAcceptableVariation(1.0);
        controller.setTemperature(20);
        
        assertEquals(OFF, adjuster.getState());
        
        expectedStates.add(OFF);
        desireTemperatures.add(20.5);
        
        controller.setTemperature(desireTemperatures.peek());
    }
    
    @Test
    public void setDesiredTemperatureTo18And21() {
        sensor.setTemperature(20.0);
        sensor.addObserver(switchTemperature(switchPoints, 0.5));
        
        controller = TemperatureControllerImpl.makeTemperatureController(sensor, adjuster);
        controller.setAcceptableVariation(0.5);
        controller.setTemperature(20);
        
        expectedStates.add(COOLING);
        expectedStates.add(HEATING);
        expectedStates.add(OFF);
        
        switchPoints.add(19.0);
        
        desireTemperatures.add(18.0);
        desireTemperatures.add(21.0);
        
        stateChanged = true;
    
        adjuster.addObserver(assertTempAndState(expectedStates, desireTemperatures));
        controller.setTemperature(desireTemperatures.peek());
    }
    
    @Test
    public void setDesiredTemperatureTo23And19() {
        sensor.setTemperature(20.0);
        sensor.addObserver(switchTemperature(switchPoints, 0.5));
        
        controller = TemperatureControllerImpl.makeTemperatureController(sensor, adjuster);
        controller.setAcceptableVariation(0.5);
        controller.setTemperature(20);
        
        assertEquals(OFF, adjuster.getState());
        
        expectedStates.add(HEATING);
        expectedStates.add(COOLING);
        expectedStates.add(OFF);
        
        switchPoints.add(20.5);
        
        desireTemperatures.add(23.0);
        desireTemperatures.add(19.0);
        
        stateChanged = true;
    
        adjuster.addObserver(assertTempAndState(expectedStates, desireTemperatures));
        controller.setTemperature(desireTemperatures.peek());
    }
    
    // Tests for setting temperature scale
    @Test
    public void celsiusToFahrenheit() {
        controller = TemperatureControllerImpl.makeTemperatureController(sensor, adjuster);
        controller.setTemperatureScale(CELSIUS);
        sensor.setTemperature(20);
        controller.setAcceptableVariation(0.5);
        controller.setTemperatureScale(FAHRENHEIT);
        assertEquals(68, Math.floor(controller.getCurrentTemperature()), 0);
        assertEquals(0.9, controller.getAcceptableVariation(), 0.1);
        assertEquals(OFF, adjuster.getState());
    }
    
    @Test
    public void fahrenheitToCelsius() {
        controller = TemperatureControllerImpl.makeTemperatureController(sensor, adjuster);
        controller.setTemperatureScale(FAHRENHEIT);
        sensor.setTemperature(68);
        controller.setAcceptableVariation(0.9);
        controller.setTemperatureScale(CELSIUS);
        assertEquals(20.0, Math.floor(controller.getCurrentTemperature()), 0.1);
        assertEquals(0.5, controller.getAcceptableVariation(), 0.1);
        assertEquals(OFF, adjuster.getState());
    }
    
    @Test
    public void celsiusTo25() {
        sensor.setTemperature(20);
        sensor.addObserver(switchScale(switchPoints));
        controller = TemperatureControllerImpl.makeTemperatureController(sensor, adjuster);
        controller.setTemperatureScale(CELSIUS);
        adjuster.setState(OFF);
        controller.setAcceptableVariation(0.5);
        
        expectedStates.add(HEATING);
        expectedStates.add(OFF);
        
        switchPoints.add(22.0);
        switchScales.add(FAHRENHEIT);
        
        desireTemperatures.add(25.0);
        
        stateChanged = true;
        
        adjuster.addObserver(assertTempAndState(expectedStates, desireTemperatures));
        controller.setTemperature(desireTemperatures.peek());
    }
  
    @Test
    public void fahrenheitTo65() {
        sensor.setTemperature(Utility.fahrenheitToCelsius(75));
        sensor.addObserver(switchScale(switchPoints));
        controller = TemperatureControllerImpl.makeTemperatureController(sensor, adjuster);
        controller.setTemperatureScale(FAHRENHEIT);
        
        expectedStates.add(COOLING);
        expectedStates.add(OFF);
    
        controller.setAcceptableVariation(0.9);
        
        switchPoints.add(70.0);
        switchScales.add(CELSIUS);
        
        desireTemperatures.add(18.3);
        
        adjuster.setState(OFF);
        
        stateChanged = true;
        adjuster.addObserver(assertTempAndState(expectedStates, desireTemperatures));
        controller.setTemperature(Utility.celsiusToFahrenheit(desireTemperatures.peek()));
    }
    
    // Tests of setting acceptable variation
    @Test
    public void setAcceptableVariationLower() {
        sensor.setTemperature(20.0);
        
        controller = TemperatureControllerImpl.makeTemperatureController(sensor, adjuster);
        
        assertEquals(OFF, adjuster.getState());
        controller.setAcceptableVariation(12);
        desireTemperatures.add(30.0);
        controller.setTemperature(desireTemperatures.peek());
    
        expectedStates.add(HEATING);
        expectedStates.add(OFF);
        adjuster.addObserver(assertTempAndState(expectedStates, desireTemperatures));
        controller.setAcceptableVariation(2);
    }
    
    @Test
    public void setAcceptableVariationHigher() {
        sensor.setTemperature(30.0);
        
        controller = TemperatureControllerImpl.makeTemperatureController(sensor, adjuster);
        
        assertEquals(OFF, adjuster.getState());
        controller.setAcceptableVariation(12);
        desireTemperatures.add(20.0);
        controller.setTemperature(desireTemperatures.peek());
        
        expectedStates.add(COOLING);
        expectedStates.add(OFF);
        adjuster.addObserver(assertTempAndState(expectedStates, desireTemperatures));
        controller.setAcceptableVariation(2);
    }
    
    @Test
    public void setAcceptableVariationLarger() {
        sensor.setTemperature(20.0);
        
        controller = TemperatureControllerImpl.makeTemperatureController(sensor, adjuster);
        
        assertEquals(OFF, adjuster.getState());
        controller.setAcceptableVariation(2);
        desireTemperatures.add(30.0);
        controller.setTemperature(desireTemperatures.peek());
        
        expectedStates.add(OFF);
        adjuster.addObserver(assertTempAndState(expectedStates, desireTemperatures));
        controller.setAcceptableVariation(12);
    }
    
    // Tests for water temperature goes out of acceptable variance from desired temperature
    
    @Test
    public void waterTemperatureOutOfDesiredHigher() {
        sensor.setTemperature(20.0);
        
        controller = TemperatureControllerImpl.makeTemperatureController(sensor, adjuster);
        
        assertEquals(OFF, adjuster.getState());
        controller.setAcceptableVariation(2);
        desireTemperatures.add(22.0);
        controller.setTemperature(desireTemperatures.peek());
        
        expectedStates.add(COOLING);
        expectedStates.add(OFF);
        adjuster.addObserver(assertTempAndState(expectedStates, desireTemperatures));
        sensor.setTemperature(26);
    }
    
    @Test
    public void waterTemperatureOutOfDesiredLower() {
        sensor.setTemperature(20.0);
        
        controller = TemperatureControllerImpl.makeTemperatureController(sensor, adjuster);
        
        assertEquals(OFF, adjuster.getState());
        controller.setAcceptableVariation(2);
        desireTemperatures.add(22.0);
        controller.setTemperature(desireTemperatures.peek());
        
        expectedStates.add(HEATING);
        expectedStates.add(OFF);
        adjuster.addObserver(assertTempAndState(expectedStates, desireTemperatures));
        sensor.setTemperature(18);
    }
    
    // Test Helpers
    
    /**
     * Return a function which changes to new desired temperature when reaching certain water 
     * temperature
     * 
     * @param switchPoints List of water temperature switch points
     * @param variance Acceptable variance of water temperature
     * 
     * @return null
     */
    private Function<Double, Void> switchTemperature(Queue<Double> switchPoints, double variance) {
        return (Double temperature) -> {
            if (stateChanged && isInDesiredVariance(temperature, switchPoints.peek(), variance)
                && !desireTemperatures.isEmpty()) {
                desireTemperatures.remove();
                switchPoints.remove();
                stateChanged = false;
                controller.setTemperature(desireTemperatures.peek());
            }
            return null;
        };
    }
    
    /**
     * Return an assertion function checking whether the new adjuster state equals to the old
     * adjuster state
     *
     * @param expectedStates List of expected adjuster states
     *
     * @return an assertion function checking whether the new adjuster state equals to the old
     * adjuster state
     */
    private Function<AdjusterState, Void> assertState(Queue<AdjusterState> expectedStates) {
        return (AdjusterState state) -> {
            assertEquals(expectedStates.remove(), state);
            return null;
        };
    }
    
    /**
     * Return a function which changes to new temperature scale when reaching certain water 
     * temperature
     * 
     * @param switchPoints List of water temperature switch points
     * 
     * @return a function which changes to new temperature scale when reaching certain water 
     * temperature
     */
    private Function<Double, Void> switchScale(Queue<Double> switchPoints) {
        return (Double temperature) -> {
            if (stateChanged && isInDesiredVariance(controller.getCurrentTemperature(),
                                                    switchPoints.peek(), controller
                                                            .getAcceptableVariation()) &&
                !desireTemperatures.isEmpty()) {
                switchPoints.remove();
                stateChanged = false;
                controller.setTemperatureScale(switchScales.remove());
            }
            return null;
        };
    }
    
    /**
     * Return a function which asserts adjuster reaching sequence of expected states and 
     * sequence of desired temperature 
     * 
     * @param expectedStates Sequence of expected adjuster states
     * @param desireTemperatures Sequence of desired water temperature
     * 
     * @return Return a function which asserts adjuster reaching sequence of expected states and 
     * sequence of desired temperature 
     */
    private Function<AdjusterState, Void> assertTempAndState(Queue<AdjusterState> expectedStates,
                                                             Queue<Double> desireTemperatures) {
        return (AdjusterState state) -> {
            assertEquals(expectedStates.remove(), state);
            if (state == OFF) {
                double temperature = controller.getTemperatureScale() == CELSIUS ?
                                     desireTemperatures.remove() : Utility.celsiusToFahrenheit
                        (desireTemperatures.remove());
                assertTrue(isInDesiredVariance(controller.getCurrentTemperature(), temperature,
                                               controller.getAcceptableVariation()));
                if (!desireTemperatures.isEmpty())
                    controller.setTemperature(desireTemperatures.peek());
            }
            return null;
        };
    }
    
    /**
     * Return true when a temperature is in acceptable variance of desired temperature, false 
     * otherwise
     * 
     * @param temperature The temperature
     * @param desiredTemperature The desired temperature
     * @param variance The acceptable variance of desired temperature
     * 
     * @return true when a temperature is in variation of desired temperature, false otherwise
     */
    private boolean isInDesiredVariance(double temperature, double desiredTemperature, double
            variance) {
        double diff = temperature - desiredTemperature;
        return diff < variance && diff > -variance;
    }
}