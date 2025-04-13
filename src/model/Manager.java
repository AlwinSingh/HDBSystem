package src.model;

import java.util.ArrayList;
import java.util.List;

import src.service.ManagerService;
import src.service.ProjectService;
import src.service.UserService;
import src.util.CSVWriter;
import src.util.ConsoleUtils;
import src.util.InputValidator;

/**
 * Manager class, represents HDB staff who can create and manage BTO projects.
 */
public class Manager extends User {
    private List<String> projectsCreated;

    public Manager(String nric, String password, String name, int age, String maritalStatus) {
        super(nric, password, name, age, maritalStatus);
        this.projectsCreated = new ArrayList<>();
    }

    public List<String> getProjectsCreated() {
        return projectsCreated;
    }

    public void addProject(String projectName) {
        projectsCreated.add(projectName);
        System.out.println("Project \"" + projectName + "\" created and assigned to manager: " + name);
    }

    public void replaceProjectName(String oldName, String newName) {
        int index = projectsCreated.indexOf(oldName);
        if (index != -1) {
            projectsCreated.set(index, newName);
            System.out.println("✅ Project name updated from \"" + oldName + "\" to \"" + newName + "\" for manager: " + name);
        } else {
            System.out.println("⚠️ Project \"" + oldName + "\" not found in manager’s list.");
        }
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
            System.out.println("5. Approve/Reject Officers");
            System.out.println("6. Change Password");
            System.out.println("0. Logout");
            ConsoleUtils.lineBreak();

            try {
                choice = InputValidator.getInt("Enter your choice: ");
            } catch (NumberFormatException e) {
                choice = -1; // keep loop going safely
            }

            ConsoleUtils.clear();

            switch (choice) {
                case 1 -> managerService.viewAllProjects();
                case 2 -> managerService.createProject(this);
                case 3 -> {
                    String projectName = InputValidator.getNonEmptyString("Enter project name to edit: ");
                    managerService.editProject(this, projectName);
                }
                case 4 -> managerService.viewOfficerRegistrations(this);
                case 5 -> {
                    String officerNRIC = InputValidator.getNonEmptyString("Enter officer NRIC to approve/reject: ");
                    boolean approve = InputValidator.getYesNo("Approve this officer? (Y/N): ");
                    managerService.approveOrRejectOfficer(officerNRIC, approve, this);
                }
                case 6 -> {
                    String newPass = InputValidator.getNonEmptyString("Enter new password: ");
                    changePassword(newPass);
                    boolean updatedSuccessfully = CSVWriter.updateUserPassword(this);
                    System.out.println(updatedSuccessfully ? "✅ Password updated." : "❌ Failed to update password.");
                }
                case 0 -> {
                    System.out.println("👋 Logged out successfully.");
                    return;
                }
                default -> System.out.println("Invalid option.");
            }
            if (choice != 0) ConsoleUtils.pause();
        } while (choice != 0);
    }
}
