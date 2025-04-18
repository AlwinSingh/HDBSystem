package src.model;

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

    public boolean applyForProject(Project proj, String flatType) {
        if (this.application != null) return false;
    
        Application app = new Application(this, proj, Applicant.AppStatusType.PENDING.name(), flatType);
        this.application = app;
        return true;
    }
    

    public void requestWithdrawal() {
        if (application != null) {
            application.withdrawRequest();
        }
    }

    public void viewApplicationStatus() {
        if (application != null) {
            System.out.println("Application status: " + application.getStatus());
        }
    }
}
