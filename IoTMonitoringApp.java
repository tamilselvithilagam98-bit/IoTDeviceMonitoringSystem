package main;

import java.util.Scanner;
import model.*;
import service.IoTMonitoringService;
import exception.IoTValidationException;

public class IoTMonitoringApp {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        IoTMonitoringService service = new IoTMonitoringService();

        int choice = 0;

        while (choice != 13) {
            System.out.println("\n===== IoT DEVICE MONITORING SYSTEM =====");
            System.out.println("1. Add Device");
            System.out.println("2. Add Sensor");
            System.out.println("3. Set Threshold");
            System.out.println("4. Ingest Reading");
            System.out.println("5. Generate Alert");
            System.out.println("6. Acknowledge Alert");
            System.out.println("7. Create Maintenance Ticket");
            System.out.println("8. Close Ticket");
            System.out.println("9. Log Technician Action");
            System.out.println("10. Show Live Alerts");
            System.out.println("11. Show Maintenance Tickets");
            System.out.println("12. Show Dashboard");
            System.out.println("13. Exit");
            System.out.print("Enter your choice: ");

            try {
                choice = Integer.parseInt(sc.nextLine());

                switch (choice) {

                    case 1:
                        System.out.print("Device ID: ");
                        String did = sc.nextLine();
                        System.out.print("Device Name: ");
                        String dname = sc.nextLine();
                        System.out.print("Location: ");
                        String location = sc.nextLine();

                        service.addDevice(
                                new Device(did, dname, location));
                        break;

                    case 2:
                        System.out.print("Sensor ID: ");
                        String sid = sc.nextLine();
                        System.out.print("Sensor Type: ");
                        String type = sc.nextLine();
                        System.out.print("Unit: ");
                        String unit = sc.nextLine();
                        System.out.print("Device ID: ");
                        String deviceId = sc.nextLine();

                        Device device = null;

                        for (Device d : service.getDevices()) {
                            if (d.getId().equals(deviceId)) {
                                device = d;
                                break;
                            }
                        }

                        if (device == null) {
                            throw new IoTValidationException(
                                    "Device is not registered.");
                        }

                        service.addSensor(
                                new Sensor(sid, type, unit, device));
                        break;

                    case 3:
                        System.out.print("Sensor ID: ");
                        String thresholdSensor = sc.nextLine();
                        System.out.print("Minimum value: ");
                        double min = Double.parseDouble(sc.nextLine());
                        System.out.print("Maximum value: ");
                        double max = Double.parseDouble(sc.nextLine());

                        service.setThreshold(thresholdSensor, min, max);
                        break;

                    case 4:
                        System.out.print("Sensor ID: ");
                        String readingSensor = sc.nextLine();
                        System.out.print("Reading value: ");
                        double value = Double.parseDouble(sc.nextLine());

                        service.ingestReading(readingSensor, value);
                        break;

                    case 5:
                        System.out.print("Sensor ID: ");
                        String alertSensor = sc.nextLine();

                        service.generateLatestAlert(alertSensor);
                        break;

                    case 6:
                        System.out.print("Alert ID: ");
                        service.acknowledgeAlert(sc.nextLine());
                        break;

                    case 7:
                        System.out.print("Alert ID: ");
                        service.createTicket(sc.nextLine());
                        break;

                    case 8:
                        System.out.print("Ticket ID: ");
                        String closeId = sc.nextLine();
                        System.out.print("Resolution notes: ");
                        String notes = sc.nextLine();

                        service.closeTicket(closeId, notes);
                        break;

                    case 9:
                        System.out.print("Ticket ID: ");
                        String ticketId = sc.nextLine();
                        System.out.print("Technician ID: ");
                        String techId = sc.nextLine();
                        System.out.print("Technician Name: ");
                        String techName = sc.nextLine();
                        System.out.print("Action performed: ");
                        String action = sc.nextLine();

                        service.addTechnicianAction(
                                ticketId, techId, techName, action);
                        break;

                    case 10:
                        service.showAlerts();
                        break;

                    case 11:
                        service.showTickets();
                        break;

                    case 12:
                        service.showDashboard();
                        break;

                    case 13:
                        System.out.println("Exiting IoT Monitoring System.");
                        break;

                    default:
                        System.out.println("Invalid choice. Try again.");
                }

            } catch (IoTValidationException e) {
                System.out.println("Error: " + e.getMessage());
            } catch (NumberFormatException e) {
                System.out.println("Error: Please enter a valid number.");
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }

        sc.close();
    }
}