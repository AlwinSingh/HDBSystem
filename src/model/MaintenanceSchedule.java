package src.model;

import java.time.LocalDateTime;

public class MaintenanceSchedule extends Appointment {
    private String taskDescription;

    public MaintenanceSchedule(int appointmentId, LocalDateTime dateTime, String location, String taskDescription) {
        super(appointmentId, dateTime, location, "Scheduled");
        this.taskDescription = taskDescription;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    public void setTaskDescription(String desc) {
        this.taskDescription = desc;
    }

    @Override
    public String toString() {
        return "Maintenance - " + taskDescription + " at " + getLocation() + " on " + getDateTime();
    }
}

