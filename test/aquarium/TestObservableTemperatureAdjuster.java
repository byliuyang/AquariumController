package aquarium;

import aquarium.hw.TemperatureAdjuster;
import aquarium.hw.TestTemperatureSensor;

import java.util.Collection;
import java.util.LinkedList;
import java.util.function.Function;

/**
 * The mock TemperatureAdjuster for testing purpose
 *
 * @version Jan 21, 2017
 */
public class TestObservableTemperatureAdjuster implements TemperatureAdjuster {
    private static final double DEFAULT_DELTA = 0.005D; // The default delta value
    private final    TestTemperatureSensor                     sensor; // The sensor
    private          double                                    delta; // The amount of
    // temperature changes
    private volatile AdjusterState                             state; // The current state of
    // adjuster
    private          Collection<Function<AdjusterState, Void>> observers; // Collection of
    // observers subscribing to adjuster state changes
    
    /**
     * Constructor.
     *
     * @param sensor The TemperatureSensor
     */
    public TestObservableTemperatureAdjuster(TestTemperatureSensor sensor) {
        this(sensor, 0.005D);
        
    }
    
    /**
     * Constructor.
     *
     * @param sensor The TemperatureSensor
     * @param delta  The amount of temperature changes when heating or cooling
     */
    public TestObservableTemperatureAdjuster(TestTemperatureSensor sensor, double delta) {
        this.sensor = sensor;
        this.delta = Math.abs(delta);
        this.state = AdjusterState.OFF;
        this.observers = new LinkedList();
    }
    
    /**
     * Add observer subscribing adjuster state change
     *
     * @param anObserver The observer
     */
    public void addObserver(Function<AdjusterState, Void> anObserver) {
        this.observers.add(anObserver);
    }
    
    /**
     * Unsubscribe observer from adjuster state change
     *
     * @param observer The observer
     */
    public void removeObserver(Function<Double, Void> observer) {
        this.observers.remove(observer);
    }
    
    /**
     * Set the delta value
     * @param delta The amount of temperature changes when heating or cooling
     */
    public void setDelta(double delta) {
        this.delta = Math.abs(delta);
    }
    
    /**
     * Set the state of adjuster
     * @param newState The new state of adjuster
     */
    public void setState(AdjusterState newState) {
        if (newState != this.state) {
            this.state = newState;
            this.notifyObservers();
            Runnable adjuster = () -> {
                while (this.getState() == newState) {
                    double newTemp = newState == AdjusterState.HEATING ?
                                     this.sensor.getTemperature() + this.delta :
                                     this.sensor.getTemperature() - this.delta;
                    
                    this.sensor.setTemperature(newTemp);
                }
            };
            if (newState != AdjusterState.OFF) {
                adjuster.run();
            }
        }
    }
    
    /**
     * Get the current state of adjust
     * @return the current state of adjust
     */
    public AdjusterState getState() {
        return this.state;
    }
    
    /**
     * Notify all observers that the state of adjuster changed
     */
    private void notifyObservers() {
        this.observers.forEach((f) -> {
            f.apply(this.state);
        });
    }
}