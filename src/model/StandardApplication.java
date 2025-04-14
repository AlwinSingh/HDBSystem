package src.model;

public class StandardApplication extends Application {
    private double applicationFee = 10.0;

    public StandardApplication(Applicant applicant, Project project, String flatType) {
        super(applicant, project, "PENDING", flatType);
        this.applicationFee = 10.0; // Default fee or logic
    }
    
    

    public boolean validateStandard() {
        return applicant != null && project != null;
    }

    public int calculateProcessingTime() {
        return 7; // default 7-day processing
    }
}
