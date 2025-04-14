package src.model;

import java.util.List;

public class Applicant extends User {
    protected Application application;

    public Applicant(String nric, String password, String name, int age, String maritalStatus) {
        super(nric, password, name, age, maritalStatus);
    }

    public void setApplication(Application app) {
        this.application = app;
    }
    
    public Application getApplication() {
        return this.application;
    }
       

    public List<Project> viewOpenProjects(List<Project> allProjects) {
        // TODO: filter by visibility and eligibility
        return null;
    }

    public boolean applyForProject(Project proj, String flatType) {
        if (this.application != null) return false;
    
        Application app = new Application(this, proj, "PENDING", flatType);
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
