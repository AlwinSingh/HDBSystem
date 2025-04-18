package src.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class HDBManager extends User {
    private List<Project> managedProjects;
    private Project assignedProject;

    public HDBManager(String nric, String password, String name, int age, String maritalStatus) {
        super(nric, password, name, age, maritalStatus);
        this.managedProjects = new ArrayList<>();
    }

    public Project createProject(String projectName, String neighborhood, int units2Room, int units3Room, int officerSlots, LocalDate openDate, LocalDate closeDate) {
        Project newProject = new Project();
        newProject.setProjectName(projectName);
        newProject.setNeighborhood(neighborhood);
        newProject.setAvailableFlats2Room(units2Room);
        newProject.setAvailableFlats3Room(units3Room);
        newProject.setOfficerSlots(officerSlots);
        newProject.setOpenDate(openDate);
        newProject.setCloseDate(closeDate);
        managedProjects.add(newProject);
        return newProject;
    }

    public void editProject(Project proj, String newProjectName, String newNeighborhood, int newUnits2Room, int newUnits3Room, int newOfficerSlots, LocalDate newOpenDate, LocalDate newCloseDate) {
        proj.setProjectName(newProjectName);
        proj.setNeighborhood(newNeighborhood);
        proj.setAvailableFlats2Room(newUnits2Room);
        proj.setAvailableFlats3Room(newUnits3Room);
        proj.setOfficerSlots(newOfficerSlots);
        proj.setOpenDate(newOpenDate);
        proj.setCloseDate(newCloseDate);
    }

    public void deleteProject(Project project) {
        managedProjects.remove(project);
    }

    public void toggleVisibility(Project project, boolean isVisible) {
        if (isVisible) project.openProject();
        else project.closeProject();
    }

    public void approveOfficerRegistration(HDBOfficer officer) {
        officer.setRegistrationStatus(HDBOfficer.RegistrationStatusType.APPROVED.name());
    }

    public void rejectOfficerRegistration(HDBOfficer officer) {
        officer.setRegistrationStatus(HDBOfficer.RegistrationStatusType.REJECTED.name());
    }

    public void approveApplication(Application app) {
        app.setStatus(Applicant.AppStatusType.SUCCESSFUL.name());
        app.getProject().decrementFlatCount(app.getFlatType());
    }

    public void rejectApplication(Application app) {
        app.setStatus(Applicant.AppStatusType.UNSUCCESSFUL.name());
    }

    public void approveWithdrawal(Application app) {
        app.setStatus(Applicant.AppStatusType.WITHDRAWAL_APPROVED.name());
    }

    public void rejectWithdrawal(Application app) {
        app.setStatus(Applicant.AppStatusType.WITHDRAWAL_REJECTED.name());
    }

    public List<String> generateReport(String filterType) {
        List<String> reports = new ArrayList<>();
        for (Project p : managedProjects) {
            reports.add("Report for: " + p.getProjectName() + " (" + p.getNeighborhood() + ")");
        }
        return reports;
    }

    public Project getAssignedProject() {
        return assignedProject;
    }

    public void setAssignedProject(Project assignedProject) {
        this.assignedProject = assignedProject;
    }
}
