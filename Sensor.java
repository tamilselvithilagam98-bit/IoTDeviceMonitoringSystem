
package model;

public class Sensor {
    private String id;
    private String type;
    private String unit;
    private Device device;

    public Sensor(String id, String type, String unit, Device device) {
        this.id = id;
        this.type = type;
        this.unit = unit;
        this.device = device;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getUnit() {
        return unit;
    }

    public Device getDevice() {
        return device;
    }

    @Override
    public String toString() {
        return id + " | " + type + " | " + unit
                + " | Device: " + device.getId();
    }
}

