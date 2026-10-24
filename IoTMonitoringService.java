package service;

import java.util.*;
import model.*;
import exception.IoTValidationException;

public class IoTMonitoringService {

    private Map<String, Device> devices = new LinkedHashMap<>();
    private Map<String, Sensor> sensors = new LinkedHashMap<>();
    private Map<String, Threshold> thresholds = new HashMap<>();

    private List<Reading> readings = new ArrayList<>();
    private List<Alert> alerts = new ArrayList<>();
    private List<MaintenanceTicket> tickets = new ArrayList<>();

    private int readingCount = 0;
    private int alertCount = 0;
    private int ticketCount = 0;

    public void addDevice(Device device) throws IoTValidationException {
        if (devices.containsKey(device.getId())) {
            throw new IoTValidationException("Device ID already exists.");
        }
        devices.put(device.getId(), device);
        System.out.println("Device added successfully.");
    }

    public void addSensor(Sensor sensor) throws IoTValidationException {
        if (!devices.containsKey(sensor.getDevice().getId())) {
            throw new IoTValidationException("Device is not registered.");
        }
        if (sensors.containsKey(sensor.getId())) {
            throw new IoTValidationException("Sensor ID already exists.");
        }
        sensors.put(sensor.getId(), sensor);
        System.out.println("Sensor added successfully.");
    }

    public void setThreshold(String sensorId, double min, double max)
            throws IoTValidationException {
        Sensor sensor = sensors.get(sensorId);

        if (sensor == null) {
            throw new IoTValidationException("Sensor is not registered.");
        }
        if (!Double.isFinite(min) || !Double.isFinite(max) || min >= max) {
            throw new IoTValidationException(
                    "Invalid threshold. Minimum must be less than maximum.");
        }

        thresholds.put(sensorId, new Threshold(sensor, min, max));
        System.out.println("Threshold set successfully.");
    }

    public void ingestReading(String sensorId, double value)
            throws IoTValidationException {
        Sensor sensor = sensors.get(sensorId);

        if (sensor == null) {
            throw new IoTValidationException("Sensor is not registered.");
        }
        if (!Double.isFinite(value)) {
            throw new IoTValidationException("Reading must be a finite number.");
        }

        Reading reading = new Reading("R" + (++readingCount), sensor, value);
        readings.add(reading);
        System.out.println("Reading recorded: " + reading.getId());

        if (thresholds.containsKey(sensorId)) {
            generateAlert(reading);
        } else {
            System.out.println("No threshold configured for this sensor.");
        }
    }

    public void generateAlert(Reading reading)
            throws IoTValidationException {
        Threshold threshold = thresholds.get(reading.getSensor().getId());

        if (threshold == null) {
            throw new IoTValidationException("Set a threshold first.");
        }

        if (!threshold.isBreached(reading.getValue())) {
            System.out.println("Reading is within threshold. No alert.");
            return;
        }

        for (Alert alert : alerts) {
            if (alert.getReading() == reading) {
                System.out.println("Alert already exists: " + alert.getId());
                return;
            }
        }

        double min = threshold.getMinimum();
        double max = threshold.getMaximum();
        double value = reading.getValue();

        String severity =
                (value < min - (max - min) * 0.25
                || value > max + (max - min) * 0.25)
                ? "CRITICAL" : "HIGH";

        Alert alert = new Alert("A" + (++alertCount), reading, severity);
        alerts.add(alert);

        System.out.println("ALERT GENERATED: " + alert.getId()
                + " | Severity: " + severity);
    }

    public void generateLatestAlert(String sensorId)
            throws IoTValidationException {
        if (!sensors.containsKey(sensorId)) {
            throw new IoTValidationException("Sensor is not registered.");
        }

        Reading latest = null;

        for (Reading reading : readings) {
            if (reading.getSensor().getId().equals(sensorId)) {
                latest = reading;
            }
        }

        if (latest == null) {
            throw new IoTValidationException("No readings available.");
        }

        generateAlert(latest);
    }

    public void acknowledgeAlert(String alertId)
            throws IoTValidationException {
        Alert alert = findAlert(alertId);

        if (!alert.getStatus().equals("OPEN")) {
            throw new IoTValidationException("Only OPEN alerts can be acknowledged.");
        }

        alert.acknowledge();
        System.out.println("Alert acknowledged: " + alertId);
    }

    public void createTicket(String alertId)
            throws IoTValidationException {
        Alert alert = findAlert(alertId);

        if (!alert.getStatus().equals("ACKNOWLEDGED")) {
            throw new IoTValidationException(
                    "Acknowledge the alert before creating a ticket.");
        }

        for (MaintenanceTicket ticket : tickets) {
            if (ticket.getAlert() == alert
                    && !ticket.getStatus().equals("CLOSED")) {
                throw new IoTValidationException(
                        "An active ticket already exists for this alert.");
            }
        }

        MaintenanceTicket ticket =
                new MaintenanceTicket("T" + (++ticketCount), alert);
        tickets.add(ticket);

        System.out.println("Maintenance ticket created: " + ticket.getId());
    }

    public void addTechnicianAction(String ticketId, String technicianId,
            String technicianName, String action)
            throws IoTValidationException {
        MaintenanceTicket ticket = findTicket(ticketId);

        if (ticket.getStatus().equals("CLOSED")) {
            throw new IoTValidationException("Ticket is already closed.");
        }
        if (action == null || action.trim().isEmpty()) {
            throw new IoTValidationException("Action log cannot be empty.");
        }
        if (technicianId == null || technicianId.trim().isEmpty()
                || technicianName == null || technicianName.trim().isEmpty()) {
            throw new IoTValidationException(
                    "Technician ID and name cannot be empty.");
        }

        Technician technician =
                new Technician(technicianId.trim(), technicianName.trim());
        ticket.addAction(technician, action.trim());

        System.out.println("Technician action logged.");
    }

    public void closeTicket(String ticketId, String notes)
            throws IoTValidationException {
        MaintenanceTicket ticket = findTicket(ticketId);

        if (ticket.getStatus().equals("CLOSED")) {
            throw new IoTValidationException("Ticket is already closed.");
        }
        if (ticket.getActionLogs().isEmpty()) {
            throw new IoTValidationException(
                    "Log a technician action before closing the ticket.");
        }
        if (notes == null || notes.trim().isEmpty()) {
            throw new IoTValidationException(
                    "Resolution notes cannot be empty.");
        }

        ticket.close(notes.trim());
        System.out.println("Ticket closed successfully: " + ticketId);
    }

    private Alert findAlert(String id) throws IoTValidationException {
        for (Alert alert : alerts) {
            if (alert.getId().equalsIgnoreCase(id)) {
                return alert;
            }
        }
        throw new IoTValidationException("Alert not found.");
    }

    private MaintenanceTicket findTicket(String id)
            throws IoTValidationException {
        for (MaintenanceTicket ticket : tickets) {
            if (ticket.getId().equalsIgnoreCase(id)) {
                return ticket;
            }
        }
        throw new IoTValidationException("Ticket not found.");
    }

    public void showAlerts() {
        System.out.println("\n--- LIVE ALERT LIST ---");

        if (alerts.isEmpty()) {
            System.out.println("No alerts.");
            return;
        }

        for (Alert alert : alerts) {
            System.out.println(alert.getId()
                    + " | Device: " + alert.getDevice().getId()
                    + " | Sensor: " + alert.getSensor().getId()
                    + " | Value: " + alert.getReading().getValue()
                    + " " + alert.getSensor().getUnit()
                    + " | Severity: " + alert.getSeverity()
                    + " | Status: " + alert.getStatus());
        }
    }

    public void showTickets() {
        System.out.println("\n--- MAINTENANCE TICKETS ---");

        if (tickets.isEmpty()) {
            System.out.println("No maintenance tickets.");
            return;
        }

        for (MaintenanceTicket ticket : tickets) {
            System.out.println("\nTicket: " + ticket.getId()
                    + " | Status: " + ticket.getStatus()
                    + " | Alert: " + ticket.getAlert().getId());

            for (String action : ticket.getActionLogs()) {
                System.out.println("Action: " + action);
            }

            if (ticket.getResolutionNotes() != null) {
                System.out.println("Resolution: "
                        + ticket.getResolutionNotes());
            }
        }
    }

    public void showDashboard() {
        System.out.println("\n--- DEVICE / SENSOR DASHBOARD ---");

        for (Device device : devices.values()) {
            System.out.println("\nDevice: " + device
                    + " | Sensors: " + countSensors(device));

            for (Sensor sensor : sensors.values()) {
                if (!sensor.getDevice().getId().equals(device.getId())) {
                    continue;
                }

                Reading latest = null;
                int count = 0;

                for (Reading reading : readings) {
                    if (reading.getSensor().getId().equals(sensor.getId())) {
                        latest = reading;
                        count++;
                    }
                }

                System.out.println("Sensor: " + sensor.getId()
                        + " (" + sensor.getType() + ")"
                        + " | Readings: " + count
                        + " | Last: "
                        + (latest == null ? "No readings"
                        : latest.getValue() + " " + sensor.getUnit()
                        + " at " + latest.getTimestamp()));
            }
        }

        System.out.println("\nTotal readings: " + readings.size()
                + " | Total alerts: " + alerts.size()
                + " | Total tickets: " + tickets.size());
    }

    private int countSensors(Device device) {
        int count = 0;

        for (Sensor sensor : sensors.values()) {
            if (sensor.getDevice().getId().equals(device.getId())) {
                count++;
            }
        }

        return count;
    }

    public Collection<Device> getDevices() {
        return devices.values();
    }

    public Collection<Sensor> getSensors() {
        return sensors.values();
    }

    public List<Alert> getAlerts() {
        return alerts;
    }

    public List<MaintenanceTicket> getTickets() {
        return tickets;
    }
}