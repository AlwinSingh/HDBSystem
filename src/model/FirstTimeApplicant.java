package src.model;

import java.time.LocalDate;

public class FirstTimeApplicant extends Applicant {
    private LocalDate registrationDate;

    public FirstTimeApplicant(String nric, String password, String name, int age, String maritalStatus) {
        super(nric, password, name, age, maritalStatus);
        this.registrationDate = LocalDate.now();
    }

    public boolean checkFirstTimeEligibility() {
        return age >= 35 && maritalStatus.equals("Single");
    }

    public StandardApplication submitStandardApplication(Project project) {
        if (checkFirstTimeEligibility()) {
            StandardApplication app = new StandardApplication(this, project, "2-Room");
            this.application = app;
            return app;
        }
        return null;
    }
}

