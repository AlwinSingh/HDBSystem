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
        System.out.println("🛠️ Project: " + project.getProjectName());
        System.out.println("2-Room Price: " + project.getPrice2Room());
        System.out.println("3-Room Price: " + project.getPrice3Room());
    
        return chosenFlatType.equalsIgnoreCase("2-Room")
               ? project.getPrice2Room()
               : project.getPrice3Room();
    }
    

    public String getStatus() {
        return status;
    }

    public Project getProject() {
        return project;
    }

    public String getFlatType() {
        return chosenFlatType;
    }

    public Applicant getApplicant() {
        return applicant;
    }

    public int getApplicationId() {
        return applicationId;
    }
    public void setProject(Project project) {
        this.project = project;
    }
    
}
