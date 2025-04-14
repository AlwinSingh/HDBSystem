package src.model;

import java.util.Map;
import src.service.ManagerService;
import src.service.ProjectService;
import src.service.UserService;
import src.util.CSVWriter;
import src.util.ConsoleUtils;
import src.util.InputValidator;

public class Manager extends User {

    public Manager(String nric, String password, String name, int age, String maritalStatus) {
        super(nric, password, name, age, maritalStatus);
    }

    @Override
    public void showMenu(ProjectService ps, UserService us) {
        ManagerService managerService = new ManagerService(ps, us);
        int choice;
        do {
            System.out.println("=== Manager Menu ===");
            System.out.println("1. View All Projects");
            System.out.println("2. Create New Project");
            System.out.println("3. Edit Project - Open/Close Projects & Modify Visibility");
            System.out.println("4. View Officer Registrations");
            System.out.println("5. Approve/Reject Officer Registrations");
            System.out.println("6. Approve/Reject Applicant Applications");
            System.out.println("7. Approve/Reject Applicant Withdrawal Requests");
            System.out.println("8. Change Password");
            System.out.println("0. Logout");
            ConsoleUtils.lineBreak();

            try {
                choice = InputValidator.getInt("Enter your choice: ");
            } catch (NumberFormatException e) {
                choice = -1; // keep loop going safely
            }
            ConsoleUtils.clear();

            switch (choice) {
                case 1 -> managerService.viewAllProjects(this);
                case 2 -> managerService.createProject(this);
                case 3 -> {
                    Map<String, Project> projects = managerService.getProjectsByManagerNric(this.getNric());
                    if (projects.size() > 0) {
                        String projectName = InputValidator.getNonEmptyString("Enter project name to edit: ");
                        managerService.editProject(this, projectName);
                    } else {
                        System.out.println("⚠️ You have no projects to edit!");
                    }
                }
                case 4 -> managerService.viewOfficerRegistrations(this);
                case 5 -> {
                    if (!managerService.hasPendingOfficerRegistrations(this)) {
                        System.out.println("No pending officer applications.");
                    } else {
                        String officerNRIC = InputValidator.getNonEmptyString("Enter officer NRIC to approve/reject: ");
                        boolean approveOfficer = InputValidator.getYesNo("Approve this officer?");
                        managerService.approveOrRejectOfficer(officerNRIC, approveOfficer, this);
                    }
                }
                case 6 -> {
                    if (!managerService.hasPendingApplicantApplications(this)) {
                        System.out.println("No pending applicant applications available.");
                    } else {
                        managerService.handleApplicationApprovalCLI(this);
                        String applicantNRIC = InputValidator.getNonEmptyString("Enter applicant NRIC to process: ");
                        boolean approveApplicant = InputValidator.getYesNo("Approve this applicant?");
                        managerService.approveOrRejectApplication(applicantNRIC, approveApplicant);
                    }
                }
                case 7 -> {
                    if (!managerService.hasPendingWithdrawalRequests(this)) {
                        System.out.println("No pending withdrawal requests available.");
                    } else {
                        managerService.handleWithdrawalCLI(this);
                        String applicantNRIC = InputValidator.getNonEmptyString("Enter applicant NRIC for withdrawal to process: ");
                        boolean approveWithdrawal = InputValidator.getYesNo("Approve this withdrawal?");
                        managerService.approveOrRejectWithdrawal(applicantNRIC, approveWithdrawal);
                    }
                }
                case 8 -> {
                    String newPass = InputValidator.getNonEmptyString("Enter new password: ");
                    changePassword(newPass);
                    boolean updatedSuccessfully = CSVWriter.updateUserPassword(this);
                    System.out.println(updatedSuccessfully
                            ? "✅ Password updated, please log out and log back in."
                            : "❌ Failed to update password.");
                    try {
                        Thread.sleep(3000); // Wait 3 seconds
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    choice = 0;
                }
                case 0 -> System.out.println("👋 Logged out successfully.");
                default -> System.out.println("Invalid option.");
            }
            if (choice != 0) ConsoleUtils.pause();
        } while (choice != 0);
    }
}
