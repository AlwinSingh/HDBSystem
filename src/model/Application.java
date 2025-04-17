package src.model;

public class Application {
    protected Applicant applicant;
    protected Project project;
    protected String status; // PENDING, SUCCESSFUL, etc.
    protected String chosenFlatType;
    private int applicationId;

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

    public double getFlatPrice() {
        if (chosenFlatType.equalsIgnoreCase("2-Room")) {
            return project.getPrice2Room();
        } else if (chosenFlatType.equalsIgnoreCase("3-Room")) {
            return project.getPrice3Room();
        } else {
            throw new IllegalArgumentException("❌ Unknown flat type: " + chosenFlatType);
        }
    }

    public String getStatus() { return status; }
    public Project getProject() { return project; }
    public String getFlatType() { return chosenFlatType; }
    public Applicant getApplicant() { return applicant; }
    public int getApplicationId() {return applicationId;}
}

