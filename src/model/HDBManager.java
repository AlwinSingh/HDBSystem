package src.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents an HDB Manager who oversees projects, handles officer and applicant approvals,
 * and manages reports and visibility settings.
 */
public class HDBManager extends User {
    private List<Project> managedProjects;
    private Project assignedProject;

    public HDBManager(String nric, String password, String name, int age, String maritalStatus) {
        super(nric, password, name, age, maritalStatus);
        this.managedProjects = new ArrayList<>();
    }

    /**
     * Creates a new project with the specified parameters and adds it to this manager's list.
     */
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

    /**
     * Edits details of a given project such as name, units, and dates.
     */
    public void editProject(Project proj, String newProjectName, String newNeighborhood, int newUnits2Room, int newUnits3Room, int newOfficerSlots, LocalDate newOpenDate, LocalDate newCloseDate) {
        proj.setProjectName(newProjectName);
        proj.setNeighborhood(newNeighborhood);
        proj.setAvailableFlats2Room(newUnits2Room);
        proj.setAvailableFlats3Room(newUnits3Room);
        proj.setOfficerSlots(newOfficerSlots);
        proj.setOpenDate(newOpenDate);
        proj.setCloseDate(newCloseDate);
    }

    /**
     * Removes a project from the manager’s project list.
     */
    public void deleteProject(Project project) {
        managedProjects.remove(project);
    }

    /**
     * Toggles the public visibility of a given project.
     */
    public void toggleVisibility(Project project, boolean isVisible) {
        if (isVisible) project.openProject();
        else project.closeProject();
    }

    /**
     * Approves an officer's registration for a project.
     */
    public void approveOfficerRegistration(HDBOfficer officer) {
        officer.setRegistrationStatus(HDBOfficer.RegistrationStatusType.APPROVED.name());
    }

    /**
     * Rejects an officer's registration for a project.
     */
    public void rejectOfficerRegistration(HDBOfficer officer) {
        officer.setRegistrationStatus(HDBOfficer.RegistrationStatusType.REJECTED.name());
    }

    /**
     * Approves an applicant’s application and updates flat availability.
     */
    public void approveApplication(Application app) {
        app.setStatus(Applicant.AppStatusType.SUCCESSFUL.name());
        app.getProject().decrementFlatCount(app.getFlatType());
    }

    /**
     * Rejects an applicant’s application.
     */
    public void rejectApplication(Application app) {
        app.setStatus(Applicant.AppStatusType.UNSUCCESSFUL.name());
    }

    /**
     * Approves an applicant’s withdrawal request.
     */
    public void approveWithdrawal(Application app) {
        app.setStatus(Applicant.AppStatusType.WITHDRAWAL_APPROVED.name());
    }

    /**
     * Rejects an applicant’s withdrawal request.
     */
    public void rejectWithdrawal(Application app) {
        app.setStatus(Applicant.AppStatusType.WITHDRAWAL_REJECTED.name());
    }

    /**
     * Generates a list of report summaries for all projects managed by this manager.
     *
     * @param filterType (currently unused) – could be extended for filtering logic.
     * @return List of report summaries.
     */
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
