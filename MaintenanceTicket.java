package model;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

public class MaintenanceTicket {

    private String id;
    private Alert alert;
    private String status;
    private String resolutionNotes;
    private List<String> actionLogs;
    private LocalDateTime createdAt;
    private LocalDateTime closedAt;

    public MaintenanceTicket(String id, Alert alert) {
        this.id = id;
        this.alert = alert;
        this.status = "OPEN";
        this.actionLogs = new ArrayList<>();
        this.createdAt = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public Alert getAlert() {
        return alert;
    }

    public String getStatus() {
        return status;
    }

    public List<String> getActionLogs() {
        return actionLogs;
    }

    public String getResolutionNotes() {
        return resolutionNotes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getClosedAt() {
        return closedAt;
    }

    public void addAction(Technician technician, String action) {
        actionLogs.add(technician.getName() + " ("
                + technician.getId() + "): " + action);
    }

    public void close(String notes) {
        resolutionNotes = notes;
        status = "CLOSED";
        closedAt = LocalDateTime.now();
        alert.resolve();
    }
}