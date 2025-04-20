package src.model;

/**
 * Represents an applicant in the HDB system.
 * Extends the User class and includes application-related state and actions.
 */
public class Applicant extends User {
    protected Application application;

    public enum AppStatusType {
        PENDING,
        SUCCESSFUL,
        UNSUCCESSFUL,
        BOOKED,
        WITHDRAW_REQUESTED,
        WITHDRAWAL_APPROVED,
        WITHDRAWAL_REJECTED
    }

    public Applicant(String nric, String password, String name, int age, String maritalStatus) {
        super(nric, password, name, age, maritalStatus);
    }

    public void setApplication(Application app) {
        this.application = app;
    }
    
    public Application getApplication() {
        return this.application;
    }

    /**
     * Attempts to apply the applicant to a given project and flat type.
     * Only succeeds if the applicant has not already applied.
     *
     * @param proj      The project to apply for.
     * @param flatType  The flat type the applicant wants (2-Room or 3-Room).
     * @return True if application is successful; false if already applied.
     */
    public boolean applyForProject(Project proj, String flatType) {
        if (this.application != null) return false;
    
        Application app = new Application(this, proj, Applicant.AppStatusType.PENDING.name(), flatType);
        this.application = app;
        return true;
    }

    /**
     * Requests withdrawal from the current application.
     * Updates status to WITHDRAW_REQUESTED.
     */
    public void requestWithdrawal() {
        if (application != null) {
            application.withdrawRequest();
        }
    }

    /**
     * Prints the current application status to the console, if available.
     */
    public void viewApplicationStatus() {
        if (application != null) {
            System.out.println("Application status: " + application.getStatus());
        }
    }

    /**
     * Indicates whether this user is an HDB officer.
     * Always returns false for applicants.
     *
     * @return false
     */
    public boolean isOfficer() {
        return false;
    }
    
}
