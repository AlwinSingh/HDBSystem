package src.model;

import java.time.LocalDateTime;

public class Appointment {
    private int appointmentId;
    private LocalDateTime dateTime;
    private String location;
    private String status;

    public Appointment(int appointmentId, LocalDateTime dateTime, String location, String status) {
        this.appointmentId = appointmentId;
        this.dateTime = dateTime;
        this.location = location;
        this.status = status;
    }

    // ✅ Getters
    public int getAppointmentId() {
        return appointmentId;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public String getLocation() {
        return location;
    }

    public String getStatus() {
        return status;
    }

    // ✅ Setters
    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // ✅ Appointment logic
    public boolean scheduleAppointment() {
        this.status = "Scheduled";
        return true;
    }

    public boolean rescheduleAppointment(LocalDateTime newDateTime) {
        this.dateTime = newDateTime;
        this.status = "Rescheduled";
        return true;
    }

    public boolean cancelAppointment() {
        this.status = "Cancelled";
        return true;
    }
}
