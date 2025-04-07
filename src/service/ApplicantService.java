package src.service;

import src.model.Applicant;
import src.model.Project;
import src.util.CSVWriter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Service class to handle all applicant-related functionality.
 */
public class ApplicantService {

    private ProjectService projectService = null;
    private UserService userService = null;

    /*public ApplicantService(ProjectService projectService) {
        this.projectService = projectService;
    }*/

    public ApplicantService(ProjectService projectService, UserService userService) {
        this.projectService = projectService;
        this.userService = userService;
    }

    /**
     * Shows all visible and open projects that the applicant is eligible for.
     */
    public List<Project> getEligibleProjects(Applicant applicant) {
        List<Project> eligible = new ArrayList<>();
        for (Project project : projectService.getAllProjects().values()) {
            System.out.printf("Checking: %s | Visible: %b | Open: %b | 2-Room units: %d\n",
                    project.getName(), project.isVisible(), project.isOpen(), project.getTwoRoomUnits());

            if (!project.isVisible() || !project.isOpen()) continue;

            boolean isSingle = applicant.getMaritalStatus().equalsIgnoreCase("Single");
            int age = applicant.getAge();

            if (isSingle && age >= 35 && project.hasAvailableUnits("2-Room")) {
                eligible.add(project);
            } else if (!isSingle && age >= 21 && (project.hasAvailableUnits("2-Room") || project.hasAvailableUnits("3-Room"))) {
                eligible.add(project);
            }
        }
        return eligible;
    }

    public boolean apply(Applicant applicant, String projectName, String flatType) {
        Project project = projectService.getProjectByName(projectName);
        if (project == null) {
            System.out.println("❌ Project not found.");
            return false;
        }

        // Check if already applied
        if (applicant.getAppliedProjectName() != null) {
            System.out.println("⚠️ You have already applied for a project.");
            return false;
        }

        // Check if flat type is valid and available
        if (!project.hasAvailableUnits(flatType)) {
            System.out.println("❌ Selected flat type is unavailable.");
            return false;
        }

        // Check eligibility
        boolean eligible = isEligible(applicant, flatType);
        if (!eligible) {
            System.out.println("❌ You are not eligible for this flat type.");
            return false;
        }

        // Apply
        applicant.applyForProject(projectName, flatType);
        project.addApplicant(applicant.getNric());

        System.out.println("✅ Application submitted!");
        CSVWriter.saveApplicants(userService.getAllApplicants(), "data/ApplicantList.csv");

        return true;
    }

    public void withdraw(Applicant applicant) {
        if (applicant.getAppliedProjectName() == null) {
            System.out.println("⚠️ You have not applied for any project.");
            return;
        }

        String projectName = applicant.getAppliedProjectName();

        Project project = projectService.getProjectByName(projectName);

        if (project != null) {
            project.getApplicantNRICs().remove(applicant.getNric());
        }

        applicant.withdrawApplication();
        CSVWriter.saveApplicants(userService.getAllApplicants(), "data/ApplicantList.csv");
    }

    public void viewStatus(Applicant applicant) {
        applicant.viewApplicationStatus();
    }

    private boolean isEligible(Applicant applicant, String flatType) {
        String marital = applicant.getMaritalStatus();
        int age = applicant.getAge();

        if (marital.equalsIgnoreCase("Single")) {
            return flatType.equalsIgnoreCase("2-Room") && age >= 35;
        } else {
            return (flatType.equalsIgnoreCase("2-Room") || flatType.equalsIgnoreCase("3-Room")) && age >= 21;
        }
    }
}
