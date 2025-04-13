package src.model;

import src.service.OfficerService;
import src.service.ProjectService;
import src.service.UserService;
import src.util.CSVWriter;
import src.util.ConsoleUtils;
import src.util.InputValidator;

/**
 * Officer class, inherits from Applicant and adds BTO project handling capability.
 */
public class Officer extends Applicant {
    private String assignedProjectName;
    private String registrationStatus; // "PENDING", "APPROVED", "REJECTED"

    public enum RegistrationStatusType {
        PENDING,
        APPROVED,
        REJECTED
    }

    public Officer(String nric, String password, String name, int age, String maritalStatus) {
        super(nric, password, name, age, maritalStatus);
        this.assignedProjectName = null;
        this.registrationStatus = ""; // By default, not registered
    }

    public String getAssignedProjectName() {
        return assignedProjectName;
    }

    public String getRegistrationStatus() {
        return registrationStatus;
    }

    public void registerForProject(String projectName) {
        if (assignedProjectName != null) {
            System.out.println("You are already registered for a project: " + assignedProjectName);
        } else {
            this.assignedProjectName = projectName;
            this.registrationStatus = RegistrationStatusType.PENDING.name();
            System.out.println("Officer registration submitted for project: " + projectName);
        }
    }

    public void setRegistrationStatus(String status) {
        this.registrationStatus = status;
    }

    public void setAssignedProjectName(String assignedProjectName) {
        this.assignedProjectName = assignedProjectName;
    }

    @Override
    public void showMenu(ProjectService ps, UserService us) {
        OfficerService officerService = new OfficerService(ps, us);

        int choice;
        do {
            // ConsoleUtils.clear(); // Optional: comment if you want to disable screen clearing

            System.out.println("=== Officer Menu ===");
            System.out.println("1. Register for Project");
            System.out.println("2. View Assigned Project Details");
            System.out.println("3. View Applicant List");
            System.out.println("4. Approve/Reject Applicants");
            System.out.println("5. Book Flat for Applicant");
            System.out.println("6. Generate Receipt");
            System.out.println("7. Change Password");
            System.out.println("0. Logout");
            ConsoleUtils.lineBreak();

            try {
                choice = InputValidator.getInt("Enter your choice: ");
            } catch (NumberFormatException e) {
                choice = -1; // keep loop going safely
            }

            ConsoleUtils.clear();

            switch (choice) {
                case 1 -> {
                    System.out.print("Enter project name to register: ");
                    String projName = InputValidator.getNonEmptyString("Enter project name to register: ");
                    officerService.registerForProject(this, projName);
                }
                case 2 -> officerService.viewAssignedProject(this);
                case 3 -> officerService.viewApplicantList(this);
                case 4 -> {
                    String nric = InputValidator.getNonEmptyString("Enter applicant NRIC to approve/reject: ");
                    boolean approve = InputValidator.getYesNo("Approve this applicant? (Y/N): ");
                    officerService.handleApplication(nric, approve);
                }
                case 5 -> {
                    String nric = InputValidator.getNonEmptyString("Enter applicant NRIC to book flat: ");
                    officerService.bookFlat(nric);
                }
                case 6 -> {
                    String nric = InputValidator.getNonEmptyString("Enter applicant NRIC to generate receipt: ");
                    officerService.generateReceipt(nric);
                }
                case 7 -> {
                    String newPass = InputValidator.getNonEmptyString("Enter new password: ");
                    changePassword(newPass);
                    boolean updatedSuccessfully = CSVWriter.updateUserPassword(this);
                    System.out.println(updatedSuccessfully ? "✅ Password updated." : "❌ Failed to update password.");
                }
                case 0 -> System.out.println("Logging out...");
                default -> System.out.println("Invalid option.");
            }

            if (choice != 0) ConsoleUtils.pause();
        } while (choice != 0);
    }
}
