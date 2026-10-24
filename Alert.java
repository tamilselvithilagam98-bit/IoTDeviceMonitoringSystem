
package model;

import java.time.LocalDateTime;

public class Alert {
    private String id;
    private Reading reading;
    private String severity;
    private String status;
    private LocalDateTime createdAt;

    public Alert(String id, Reading reading, String severity) {
        this.id = id;
        this.reading = reading;
        this.severity = severity;
        this.status = "OPEN";
        this.createdAt = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public Reading getReading() {
        return reading;
    }

    public Sensor getSensor() {
        return reading.getSensor();
    }

    public Device getDevice() {
        return reading.getDevice();
    }

    public String getSeverity() {
        return severity;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void acknowledge() {
        status = "ACKNOWLEDGED";
    }

    public void resolve() {
        status = "RESOLVED";
    }
}
