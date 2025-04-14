package src.service;

import java.util.ArrayList;
import java.util.List;
import src.model.Applicant;
import src.model.Enquiry;
import src.model.Project;
import src.util.CSVWriter;
import src.util.FilePath;
import src.util.InputValidator;

public class ApplicantService {

    private ProjectService projectService;
    private UserService userService;
    private EnquiryService enquiryService;


    public ApplicantService(ProjectService projectService, UserService userService) {
        this.projectService = projectService;
        this.userService = userService;
        this.enquiryService = new EnquiryService(projectService, userService);
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
        // Allow re-application if the current application status is UNSUCCESSFUL or null.
        if (applicant.getAppliedProjectName() != null 
           && !applicant.getAppliedProjectName().isEmpty() 
           && !applicant.getApplicationStatus().equalsIgnoreCase("UNSUCCESSFUL")) {
            System.out.println("⚠️ You have already applied for a project.");
            return false;
        }
        if (!project.hasAvailableUnits(flatType)) {
            System.out.println("❌ Selected flat type is unavailable.");
            return false;
        }
        boolean eligible = isEligible(applicant, flatType);
        if (!eligible) {
            System.out.println("❌ You are not eligible for this flat type.");
            return false;
        }
        // Record the application
        applicant.applyForProject(project.getName(), flatType);
        project.addApplicant(applicant.getNric());
        System.out.println("✅ Application submitted!");
        CSVWriter.updateApplicant(applicant, FilePath.APPLICANT_LIST_FILE);
        CSVWriter.saveProject(project, FilePath.PROJECT_LIST_FILE);
        return true;
    }

    /**
     * Modified withdrawFromProject: sets the applicant's status to "WITHDRAWAL_REQUESTED".
     * The manager's approval process will later set it to "UNSUCCESSFUL".
     */
    public void withdrawFromProject(Applicant applicant) {
        if (applicant.getAppliedProjectName() == null || applicant.getAppliedProjectName().isEmpty()) {
            System.out.println("⚠️ You have not applied for any project.");
            return;
        }
        applicant.setApplicationStatus("WITHDRAWAL_REQUESTED");
        System.out.println("Withdrawal request submitted for project " + applicant.getAppliedProjectName() +
                           ". Please await manager approval.");
        CSVWriter.updateApplicant(applicant, FilePath.APPLICANT_LIST_FILE);
    }


public void editEnquiry(Applicant applicant) {
    List<Enquiry> userEnquiries = enquiryService.getEnquiriesForUser(applicant.getNric());

    if (userEnquiries.isEmpty()) {
        System.out.println("⚠️ You have not created any enquiries.");
        return;
    }

    System.out.println("=== Your Enquiries ===");
    for (int i = 0; i < userEnquiries.size(); i++) {
        Enquiry e = userEnquiries.get(i);
        System.out.println((i + 1) + ". [#" + e.getEnquiryId() + "] " + e.getContent());
    }

    int index = InputValidator.getInt("Enter enquiry number to edit: ") - 1;
    if (index < 0 || index >= userEnquiries.size()) {
        System.out.println("⚠️ Invalid choice.");
        return;
    }

    String newContent = InputValidator.getNonEmptyString("Enter new enquiry content: ");
    boolean success = enquiryService.editEnquiry(userEnquiries.get(index).getEnquiryId(), newContent);

    if (success) {
        System.out.println("✅ Enquiry updated.");
    }
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
