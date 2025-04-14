package src.model;

import java.time.LocalDateTime;

public class InterviewScheduler extends Appointment {
    private String interviewType;

    public InterviewScheduler(int appointmentId, LocalDateTime dateTime, String location, String interviewType) {
        super(appointmentId, dateTime, location, "Scheduled");
        this.interviewType = interviewType;
    }

    public String getInterviewType() {
        return interviewType;
    }

    public void setInterviewType(String interviewType) {
        this.interviewType = interviewType;
    }

    @Override
    public String toString() {
        return "Interview - Type: " + interviewType + " at " + getLocation() + " on " + getDateTime();
    }

}
