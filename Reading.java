
package model;

import java.time.LocalDateTime;

public class Reading {
    private String id;
    private Sensor sensor;
    private double value;
    private LocalDateTime timestamp;

    public Reading(String id, Sensor sensor, double value) {
        this.id = id;
        this.sensor = sensor;
        this.value = value;
        this.timestamp = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public Sensor getSensor() {
        return sensor;
    }

    public Device getDevice() {
        return sensor.getDevice();
    }

    public double getValue() {
        return value;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
