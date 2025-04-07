package src.model;

import java.util.Scanner;

import src.service.ApplicantService;
import src.service.ProjectService;
import src.service.UserService;
import src.util.ConsoleUtils;

/**
 * Applicant class, represents a user who applies for a BTO project.
 */
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
        if (appliedProjectName == null) {
            System.out.println("No application found.");
        } else {
            System.out.println("Project: " + appliedProjectName);
            System.out.println("Flat Type: " + flatTypeApplied);
            System.out.println("Status: " + applicationStatus);
        }
    }

    @Override
    public void showMenu(ProjectService projectService, UserService userService) {
        Scanner sc = new Scanner(System.in);
        ApplicantService applicantService = new ApplicantService(projectService, userService);

        int choice;
        do {
            //ConsoleUtils.clear();
            System.out.println("=== Applicant Menu ===");
            System.out.println("1. View Application Status");
            System.out.println("2. View Eligible Projects");
            System.out.println("3. Apply for Project");
            System.out.println("4. Withdraw Application");
            System.out.println("5. Change Password");
            System.out.println("0. Logout");
            ConsoleUtils.lineBreak();
            System.out.print("Enter your choice: ");

            try {
                choice = Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) {
                choice = -1; // keep loop going safely
            }

            ConsoleUtils.clear();

            switch (choice) {
                case 1 -> applicantService.viewStatus(this);
                case 2 -> {
                    var eligible = applicantService.getEligibleProjects(this);
                    if (eligible.isEmpty()) {
                        System.out.println("⚠️ No eligible projects available.");
                    } else {
                        System.out.println("Eligible Projects:");
                        eligible.forEach(Project::displaySummary);
                    }
                }
                case 3 -> {
                    System.out.print("Enter project name to apply: ");
                    String projectName = sc.nextLine();
                    System.out.print("Enter flat type (2-Room / 3-Room): ");
                    String flatType = sc.nextLine();
                    applicantService.apply(this, projectName, flatType);
                }
                case 4 -> applicantService.withdraw(this);
                case 5 -> {
                    System.out.print("Enter new password: ");
                    String newPass = sc.nextLine();
                    changePassword(newPass);
                    System.out.println("✅ Password updated.");
                }
                case 0 -> System.out.println("Logging out...");
                default -> System.out.println("⚠️ Invalid input. Please enter a valid option.");
            }

            if (choice != 0) ConsoleUtils.pause();
        } while (choice != 0);
    }
}
