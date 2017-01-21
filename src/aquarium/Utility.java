package aquarium;

/**
 * Created by harryliu on 1/21/17.
 */
public class Utility {
    /**
     * Convert Celsius temperature to corresponding Fahrenheit temperature
     * @param c The source Celsius temperature in degree
     * @return the corresponding Fahrenheit temperature in degree
     */
    public static double celsiusToFahrenheit(double c) {
        return c * 9 / 5.0 + 32.0;
    }
    
    /**
     * Convert Fahrenheit temperature to corresponding Celsius temperature
     * @param f The source Fahrenheit temperature in degree
     * @return the corresponding Celsius temperature in degree
     */
    public static double fahrenheitToCelsius(double f) {
        return (f - 32.0) * 5.0 / 9;
    }
    
    /**
     * Convert Celsius temperature variation to corresponding Fahrenheit temperature variation
     * @param c The source Celsius temperature variation in degree
     * @return the corresponding Fahrenheit temperature variation in degree
     */
    public static double celsiusToFahrenheitVariance(double c) {
        return c * 9 / 5.0;
    }
    
    /**
     * Convert Fahrenheit temperature variation to corresponding Celsius temperature variation
     * @param f The source Fahrenheit temperature variation in degree
     * @return the corresponding Celsius temperature variation in degree
     */
    public static double fahrenheitToCelsiusVariance(double f) {
        return f * 5.0 / 9;
    }
}
