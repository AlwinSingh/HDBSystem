package src.model;

public class Application {
    protected Applicant applicant;
    protected Project project;
    protected String status; // PENDING, SUCCESSFUL, etc.
    protected String chosenFlatType;

    public Application(Applicant applicant, Project project, String status, String chosenFlatType) {
        this.applicant = applicant;
        this.project = project;
        this.status = status;
        this.chosenFlatType = chosenFlatType;
    }
    

    public void setStatus(String status) {
        this.status = status;
    }

    public void withdrawRequest() {
        this.status = "WITHDRAWAL_REQUESTED";
    }

    public String getStatus() { return status; }
    public Project getProject() { return project; }
    public String getFlatType() { return chosenFlatType; }
    public Applicant getApplicant() { return applicant; }
}

