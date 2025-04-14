package src.model;

import src.service.OfficerService;
import src.service.ProjectService;
import src.service.UserService;
import src.util.CSVWriter;
import src.util.ConsoleUtils;
import src.util.InputValidator;

public class Officer extends Applicant {
    private String assignedProjectName;
    private String registrationStatus; // "PENDING", "APPROVED", "REJECTED"

    public enum RegistrationStatusType {
        PENDING,
        APPROVED,
        REJECTED
    }

    public Officer(String nric, String password, String name, int age, String maritalStatus, String assignedProjectName, String registrationStatus) {
        super(nric, password, name, age, maritalStatus);
        this.assignedProjectName = assignedProjectName;
        this.registrationStatus = registrationStatus;
    }

    public String getAssignedProjectName() {
        return assignedProjectName;
    }

    public String getRegistrationStatus() {
        return registrationStatus;
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
            System.out.println("=== Officer Menu ===");
            System.out.println("1. Register for Project");
            System.out.println("2. View Assigned Project Details");
            System.out.println("3. View Applicant List");
            System.out.println("4. Book Flat for Applicant");
            System.out.println("5. Generate Receipt");
            System.out.println("6. Change Password");
            System.out.println("0. Logout");
            ConsoleUtils.lineBreak();

            try {
                choice = InputValidator.getInt("Enter your choice: ");
            } catch (NumberFormatException e) {
                choice = -1;
            }
            ConsoleUtils.clear();

            switch (choice) {
                case 1 -> officerService.registerForProject(this);
                case 2 -> officerService.viewAssignedProject(this);
                case 3 -> officerService.viewApplicantList(this);
                case 4 -> officerService.bookFlat(this);
                case 5 -> {
                    if (this.getAssignedProjectName() == null || this.getAssignedProjectName().isEmpty()) {
                        System.out.println("⚠️ You have not registered for any project.");
                    } else {
                        if (this.getRegistrationStatus().equalsIgnoreCase(RegistrationStatusType.APPROVED.name())) {
                            String applicantNRIC = InputValidator.getNonEmptyString("Enter applicant NRIC to generate receipt: ");
                            officerService.generateReceipt(applicantNRIC);
                        } else {
                            System.out.println("⚠️ Your registration status has not been approved for " + this.getAssignedProjectName());
                        }
                    }
                }
                case 6 -> {
                    String newPass = InputValidator.getNonEmptyString("Enter new password: ");
                    changePassword(newPass);
                    boolean updatedSuccessfully = CSVWriter.updateUserPassword(this);
                    System.out.println(updatedSuccessfully ? "✅ Password updated." : "❌ Failed to update password.");
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    choice = 0;
                }
                case 0 -> System.out.println("Logging out...");
                default -> System.out.println("Invalid option.");
            }
            if (choice != 0) ConsoleUtils.pause();
        } while (choice != 0);
    }
}
