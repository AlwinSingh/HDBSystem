package src.model;

import java.util.List;

public class HDBOfficer extends Applicant {
    private Project assignedProject;
    private String registrationStatus;

    public HDBOfficer(String nric, String password, String name, int age, String maritalStatus) {
        super(nric, password, name, age, maritalStatus);
    }

    public boolean registerToHandleProject(Project project) {
        if (assignedProject == null && application == null) {
            registrationStatus = "PENDING";
            return true;
        }
        return false;
    }

    public void viewOfficerRegistrationStatus() {
        System.out.println("Officer registration: " + registrationStatus);
    }

    public void bookFlat(Application app, String flatType) {
        if (assignedProject != null && app.getProject().equals(assignedProject)) {
            app.setStatus("BOOKED");
            assignedProject.decrementFlatCount(flatType);
        }
    }

    public Receipt generateReceipt(Application app) {
        return new Receipt(app.getApplicant().getName(), app.getApplicant().getNric(), app.getApplicant().getAge(),
                app.getApplicant().getMaritalStatus(), app.getProject().getProjectName(),
                app.getProject().getNeighborhood(), app.getFlatType());
    }

    public void setRegistrationStatus(String status) {
        this.registrationStatus = status;
    }

    public void setAssignedProjectByName(String projectName, List<Project> allProjects)
    {
        for (Project p : allProjects) {
            if (p.getProjectName().equalsIgnoreCase(projectName)) {
                this.assignedProject = p;
                return;
            }
        }
        System.out.println("⚠️ Project not found: " + projectName);
    }

    public Project getAssignedProject() {
        return assignedProject;
    }

    public String getRegistrationStatus() {
        return registrationStatus;
    }
}
