package src.service;

import src.model.Applicant;
import src.model.Project;
import src.util.CSVWriter;
import src.util.FilePath;

import java.util.ArrayList;
import java.util.List;

public class ApplicantService {

    private ProjectService projectService = null;

    public ApplicantService(ProjectService projectService) {
        this.projectService = projectService;
    }

    public List<Project> getEligibleProjects(Applicant applicant) {
        List<Project> eligible = new ArrayList<>();
        for (Project project : projectService.getAllProjects().values()) {
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

    public boolean applyForProject(Applicant applicant, Project project, String flatType) {
        if (project == null) {
            System.out.println("❌ Project not found.");
            return false;
        }

        // Check if already applied
        if (applicant.getAppliedProjectName() != null && !applicant.getAppliedProjectName().isEmpty()) {
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
        applicant.applyForProject(project.getName(), flatType);
        project.addApplicant(applicant.getNric());

        System.out.println("✅ Application submitted!");
        CSVWriter.updateApplicant(applicant, FilePath.APPLICANT_LIST_FILE);
        CSVWriter.saveProject(project, FilePath.PROJECT_LIST_FILE);

        return true;
    }

    public void withdrawFromProject(Applicant applicant) {
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
        CSVWriter.updateApplicant(applicant, FilePath.APPLICANT_LIST_FILE);
        CSVWriter.saveProject(project, FilePath.PROJECT_LIST_FILE);
    }

    public void viewApplicationStatus(Applicant applicant) {
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
