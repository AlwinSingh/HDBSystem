package src.model;

import src.service.ApplicantService;
import src.service.ProjectService;
import src.service.UserService;
import src.util.CSVWriter;
import src.util.ConsoleUtils;
import src.util.InputValidator;

public class Applicant extends User {
    protected String appliedProjectName;
    protected String flatTypeApplied; // 2-Room or 3-Room only...
    protected String applicationStatus; // Uses fixed Strings ENUM as declare below like "PENDING", "SUCCESSFUL", "BOOKED", "UNSUCCESSFUL" to ensure data consistency

    public enum AppStatusType {
        PENDING,
        SUCCESSFUL,
        UNSUCCESSFUL,
        BOOKED
    }

    public Applicant(String nric, String password, String name, int age, String maritalStatus) {
        super(nric, password, name, age, maritalStatus); // Calls the user constructor
        this.flatTypeApplied = null;
        this.appliedProjectName = null;
        this.applicationStatus = null;
    }

    public Applicant(String nric, String password, String name, int age, String maritalStatus, String flatTypeApplied, String appliedProjectName, String applicationStatus) {
        super(nric, password, name, age, maritalStatus); // Calls the user constructor
        this.flatTypeApplied = flatTypeApplied;
        this.appliedProjectName = appliedProjectName;
        this.applicationStatus = applicationStatus;
    }

    public String getAppliedProjectName() {
        return appliedProjectName;
    }

    public String getFlatTypeApplied() {
        return flatTypeApplied;
    }

    public String getApplicationStatus() {
        return applicationStatus;
    }

    public void setApplicationStatus(String applicationStatus) {
        this.applicationStatus = applicationStatus;
    }

    public void applyForProject(String projectName, String flatType) {
        this.appliedProjectName = projectName;
        this.flatTypeApplied = flatType;
        this.applicationStatus = AppStatusType.PENDING.name();
        System.out.println("Application submitted for project: " + projectName + " (" + flatType + ")");
    }

    public void withdrawApplication() {
        if (appliedProjectName == null) {
            System.out.println("No application to withdraw.");
        } else {
            this.appliedProjectName = null;
            this.flatTypeApplied = null;
            this.applicationStatus = null;
            System.out.println("Application withdrawn.");
        }
    }

    public void viewApplicationStatus() {
        if (appliedProjectName == null || appliedProjectName.isEmpty()) {
            System.out.println("No application found.");
        } else {
            System.out.println("Project: " + appliedProjectName);
            System.out.println("Flat Type: " + flatTypeApplied);
            System.out.println("Status: " + applicationStatus);
        }
    }

    private void printEligibleProjects(Applicant applicant, ApplicantService applicantService) {
        var eligible = applicantService.getEligibleProjects(applicant);
        if (eligible.isEmpty()) {
            System.out.println("⚠️ No eligible projects available.");
        } else {
            System.out.println("Eligible Projects:\n");
            eligible.forEach(Project::displaySummary);
        }

        if (applicant.getAppliedProjectName() != null && !applicant.getAppliedProjectName().isEmpty()) {
            System.out.println("⚠️ You have already applied for project " + applicant.getAppliedProjectName() + "\n");
        }
    }

    @Override
    public void showMenu(ProjectService projectService, UserService userService) {
        ApplicantService applicantService = new ApplicantService(projectService);

        int choice;
        do {
            System.out.println("=== Applicant Menu ===");
            System.out.println("1. View Application Status");
            System.out.println("2. View Eligible Projects");
            System.out.println("3. Apply for Project");
            System.out.println("4. Withdraw Application");
            System.out.println("5. Change Password");
            System.out.println("0. Logout");
            ConsoleUtils.lineBreak();

            try {
                choice = InputValidator.getInt("Enter your choice: ");
            } catch (NumberFormatException e) {
                choice = -1; // keep loop going safely
            }

            ConsoleUtils.clear();

            switch (choice) {
                case 1 -> applicantService.viewApplicationStatus(this);
                case 2 -> printEligibleProjects(this, applicantService);
                case 3 -> {
                    if (this.getAppliedProjectName() != null && !this.getAppliedProjectName().isEmpty()) {
                        System.out.println("⚠️ You have already applied for project " + this.getAppliedProjectName() + "\n");
                    } else {
                        printEligibleProjects(this, applicantService);
                        String projectName = InputValidator.getNonEmptyString("Enter project name to apply: ");
                        Project project = projectService.getProjectByName(projectName);

                        if (project == null) {
                            System.out.println("Project not found.");
                        } else {
                            String flatType = InputValidator.getNonEmptyString("Enter flat type (2-Room / 3-Room): ");
                            applicantService.applyForProject(this, project, flatType);
                        }
                    }
                }
                case 4 -> applicantService.withdrawFromProject(this);
                case 5 -> {
                    String newPass = InputValidator.getNonEmptyString("Enter new password: ");
                    changePassword(newPass);
                    boolean updatedSuccessfully = CSVWriter.updateUserPassword(this);
                    System.out.println(updatedSuccessfully ? "✅ Password updated, please log out and log back in, redirecting in 3 seconds!" : "❌ Failed to update password.");

                    try {
                        Thread.sleep(3000); // Wait 3 seconds
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    choice = 0;
                }
                case 0 -> System.out.println("Logging out...");
                default -> System.out.println("⚠️ Invalid input. Please enter a valid option.");
            }

            if (choice != 0) ConsoleUtils.pause();
        } while (choice != 0);
    }
}
