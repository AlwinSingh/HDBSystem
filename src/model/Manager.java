package src.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import src.service.ProjectService;
import src.service.UserService;
import src.util.ConsoleUtils;

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

    public void approveApplication(String applicantNric) {
        // Todo: Placeholder — real logic will pull applicant object and change status
        System.out.println("Application for " + applicantNric + " approved (simulated).");
    }

    public void toggleProjectVisibility(String projectName, boolean visibility) {
        // Todo: Placeholder — real logic will change visibility in ProjectService
        System.out.println("Project \"" + projectName + "\" visibility set to: " + (visibility ? "ON" : "OFF"));
    }

    public void viewAllProjects() {
        // Todo: Placeholder — we will hook this into ProjectService with filters after streaming...
        System.out.println("Displaying all projects (simulated)...");
    }

    @Override
    public void showMenu(ProjectService ps, UserService us) {
        Scanner sc = new Scanner(System.in);
        int choice;
        do {
            //ConsoleUtils.clear();
            System.out.println("=== Manager Menu ===");
            System.out.println("1. Create New Project");
            System.out.println("2. View All Projects");
            System.out.println("3. Approve Applicant Application");
            System.out.println("4. Toggle Project Visibility");
            System.out.println("5. Change Password");
            System.out.println("0. Logout");
            ConsoleUtils.lineBreak();
            System.out.print("Enter your choice: ");
            choice = Integer.parseInt(sc.nextLine());

            ConsoleUtils.clear();

            switch (choice) {
                case 1 -> {
                    System.out.print("Enter new project name: ");
                    String newProj = sc.nextLine();
                    addProject(newProj);
                }
                case 2 -> viewAllProjects();
                case 3 -> {
                    System.out.print("Enter applicant NRIC to approve: ");
                    String nric = sc.nextLine();
                    approveApplication(nric);
                }
                case 4 -> {
                    System.out.print("Enter project name: ");
                    String proj = sc.nextLine();
                    System.out.print("Enter visibility (true/false): ");
                    boolean visible = Boolean.parseBoolean(sc.nextLine());
                    toggleProjectVisibility(proj, visible);
                }
                case 5 -> {
                    System.out.print("Enter new password: ");
                    String newPass = sc.nextLine();
                    changePassword(newPass);
                    System.out.println("✅ Password updated.");
                }
                case 0 -> System.out.println("Logging out...");
                default -> System.out.println("Invalid option.");
            }
            if (choice != 0) ConsoleUtils.pause();
        } while (choice != 0);
    }
}
