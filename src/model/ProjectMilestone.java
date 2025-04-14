package src.model;

import java.time.LocalDate;

public class ProjectMilestone {
    private int milestoneId;
    private String milestoneDescription;
    private LocalDate milestoneDate;

    public void recordMilestone() {
        System.out.println("Milestone recorded: " + milestoneDescription + " on " + milestoneDate);
    }

    public String getMilestoneDetails() {
        return milestoneDescription + " (" + milestoneDate + ")";
    }
}

