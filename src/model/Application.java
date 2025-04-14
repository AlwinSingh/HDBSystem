package src.model;

public class Application {
    private Applicant applicant;
    private Project project;
    private String status; // e.g., "Pending", "Successful", "Booked", "Unsuccessful"
    private String chosenFlatType; // "2-Room" or "3-Room"

    public Application(Applicant applicant, Project project, String chosenFlatType) {
        this.applicant = applicant;
        this.project = project;
        this.chosenFlatType = chosenFlatType;
        this.status = "Pending"; // default
    }

    // Getters
    public Applicant getApplicant() {
        return applicant;
    }

    public Project getProject() {
        return project;
    }

    public String getStatus() {
        return status;
    }

    public String getChosenFlatType() {
        return chosenFlatType;
    }

    // Setters
    public void setStatus(String newStatus) {
        this.status = newStatus;
    }

    public void withdrawRequest() {
        this.status = "Withdrawal Requested";
    }

    public void setChosenFlatType(String chosenFlatType) {
        this.chosenFlatType = chosenFlatType;
    }

    // Display summary for debugging or management
    public void displaySummary() {
        System.out.println("Application Details:");
        System.out.println("Applicant: " + applicant.getName() + " (" + applicant.getNric() + ")");
        System.out.println("Project: " + project.getName());
        System.out.println("Flat Type: " + chosenFlatType);
        System.out.println("Status: " + status);
    }
}
