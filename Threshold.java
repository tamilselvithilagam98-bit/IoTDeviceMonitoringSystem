
package model;

public class Threshold {
    private Sensor sensor;
    private double minimum;
    private double maximum;

    public Threshold(Sensor sensor, double minimum, double maximum) {
        this.sensor = sensor;
        this.minimum = minimum;
        this.maximum = maximum;
    }

    public Sensor getSensor() {
        return sensor;
    }

    public double getMinimum() {
        return minimum;
    }

    public double getMaximum() {
        return maximum;
    }

    public boolean isBreached(double value) {
        return value < minimum || value > maximum;
    }
}
