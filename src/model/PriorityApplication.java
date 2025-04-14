package src.model;

public class PriorityApplication extends Application {
    private double priorityFee = 30.0;
    private double priorityScore;
    private String additionalCriteria;

    public PriorityApplication(Applicant applicant, Project project, String status, String chosenFlatType) {
        super(applicant, project, status, chosenFlatType);
        this.priorityScore = 0.0;
        this.priorityFee = 20.0;
    }
    

    public double calculatePriority() {
        priorityScore = applicant.getAge() * 1.2; // mock logic
        return priorityScore;
    }

    public boolean validatePriority() {
        return applicant != null && additionalCriteria != null;
    }
}

