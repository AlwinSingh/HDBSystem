package src.model;

/**
 * Represents an application submitted by an applicant for a specific BTO housing project.
 * Stores flat type, application status, and supports logic for flat pricing and withdrawal.
 */

public class Application {
    protected Applicant applicant;
    protected Project project;
    protected String status; // PENDING, SUCCESSFUL, etc.
    protected String chosenFlatType;

    /**
     * Creates a new application for a specific project and flat type.
     *
     * @param applicant       The applicant who submitted the application.
     * @param project         The project applied to.
     * @param status          The current application status.
     * @param chosenFlatType  The flat type applied for (e.g., "2-Room", "3-Room").
     */

    public Application(Applicant applicant, Project project, String status, String chosenFlatType) {
        this.applicant = applicant;
        this.project = project;
        this.status = status;
        this.chosenFlatType = chosenFlatType;
    }

    /**
     * Updates the status of the application.
     *
     * @param status New status (e.g., PENDING, BOOKED).
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Flags the application as a withdrawal request.
     * Sets status to WITHDRAW_REQUESTED.
     */
    public void withdrawRequest() {
        this.status = Applicant.AppStatusType.WITHDRAW_REQUESTED.name();
    }

    /**
     * Calculates the price of the flat based on the chosen type and associated project.
     *
     * @return Price of the choosen flat type.
     */

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

    public void setProject(Project project) {
        this.project = project;
    }
    
}
