package com.kevin.mytwittercodechallenge;

public class TemperatureConverter {
    /**
     * Converts temperature in Celsius to temperature in Fahrenheit.
     *
     * @param temperatureInCelsius Temperature in Celsius to convert.
     * @return Temperature in Fahrenheit.
     */
    public static double celsiusToFahrenheit(double temperatureInCelsius) {
        return temperatureInCelsius * 1.8 + 32;
    }
}
