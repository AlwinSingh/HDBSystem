package src.model;

/**
 * Represents a user who applies for BTO projects in the HDB system.
 * Inherits from {@link User} and contains application-related data and behavior.
 */

public class Applicant extends User {
    protected Application application;

    /**
     * Represents the possible statuses for an applicant's application.
     */
    public enum AppStatusType {
        PENDING,
        SUCCESSFUL,
        UNSUCCESSFUL,
        BOOKED,
        WITHDRAW_REQUESTED,
        WITHDRAWAL_APPROVED,
        WITHDRAWAL_REJECTED
    }

    /**
     * Constructs a new applicant with the specified personal and login details.
     *
     * @param nric          Applicant's NRIC.
     * @param password      Login password.
     * @param name          Full name of the applicant.
     * @param age           Age of the applicant.
     * @param maritalStatus Marital status (e.g., Single, Married).
     */
    public Applicant(String nric, String password, String name, int age, String maritalStatus) {
        super(nric, password, name, age, maritalStatus);
    }


    /**
     * Assigns an application to this applicant.
     *
     * @param app The application to assign.
     */
    public void setApplication(Application app) {
        this.application = app;
    }
    
    /**
     * Retrieves the current application associated with this applicant.
     *
     * @return The current application object, or null if none exists.
     */
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
     * Indicates whether this applicant is also an HDB officer who has not yet applied for a project.
     *
     * @return True if the user is a registered officer without an active application.
     */
    public boolean isOfficer() {
        return true;
    }
  
}
