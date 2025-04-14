package src.model;

import java.time.LocalDate;

public class RepeatApplicant extends Applicant {
    private int previousApplicationCount;
    private LocalDate lastApplicationDate;
    private double loyaltyScore;

    public RepeatApplicant(String nric, String password, String name, int age, String maritalStatus, int count, LocalDate lastDate) {
        super(nric, password, name, age, maritalStatus);
        this.previousApplicationCount = count;
        this.lastApplicationDate = lastDate;
    }

    public void updateApplicationHistory() {
        previousApplicationCount++;
        lastApplicationDate = LocalDate.now();
    }

    public boolean checkRepeatEligibility() {
        return age >= 21 && maritalStatus.equals("Married");
    }

    public PriorityApplication submitPriorityApplication(Project project) {
        if (checkRepeatEligibility()) {
            PriorityApplication app = new PriorityApplication(this, project, "3-Room", "Married with child");
            this.application = app;
            return app;
        }
        return null;
    }
}
