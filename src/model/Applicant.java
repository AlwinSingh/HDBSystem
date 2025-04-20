package src.model;

/**
 * Represents an applicant in the HDB system.
 * Extends the User class and includes application-related state and actions.
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
     * Constructs a new applicant with the specified user details.
     *
     * @param nric           Applicant's NRIC.
     * @param password       Login password.
     * @param name           Full name of the applicant.
     * @param age            Age of the applicant.
     * @param maritalStatus  Marital status of the applicant.
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
     * Indicates whether this user is currently acting as an HDB officer.
     * This will return true only if the applicant is an instance of {@link HDBOfficer}
     * and has not yet applied as an applicant.
     *
     * @return True if this user is a non-applicant HDB officer and false otherwise.
     */
    public boolean isOfficer() {
        return (this instanceof HDBOfficer) && this.application == null;
    }
  
}
