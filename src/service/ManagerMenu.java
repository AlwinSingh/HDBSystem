package src.service;

import src.model.HDBManager;

import java.util.Scanner;

public class ManagerMenu {

    public static void show(HDBManager manager) {
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n===== 🧱 HDB Manager Dashboard =====");
            System.out.println("Welcome, Manager " + manager.getName());
            System.out.println("\n🏗 Project Management");
            System.out.println("1. Create a new project");
            System.out.println("2. Edit a project");
            System.out.println("3. Delete a project");
            System.out.println("4. Toggle project visibility");

            System.out.println("\n📋 Project Viewing");
            System.out.println("5. View all projects");
            System.out.println("6. View my projects");

            System.out.println("\n🧑‍💼 Officer Registration");
            System.out.println("7. View officer registrations");
            System.out.println("8. Approve/reject officer registration");

            System.out.println("\n🧑‍💻 Applicant Applications");
            System.out.println("9. View applicant applications");
            System.out.println("10. Approve/reject applicant applications");
            System.out.println("11. Approve/reject withdrawal requests");

            System.out.println("\n📈 Reporting");
            System.out.println("12. Generate applicant booking reports");

            System.out.println("\n0. Logout");
            System.out.print("Enter your choice: ");

            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1" -> createProject(manager, sc);
                case "2" -> editProject(manager, sc);
                case "3" -> deleteProject(manager, sc);
                case "4" -> toggleVisibility(manager, sc);
                case "5" -> viewAllProjects();
                case "6" -> viewMyProjects(manager);
                case "7" -> viewOfficerRegistrations(manager);
                case "8" -> handleOfficerApproval(manager, sc);
                case "9" -> viewApplicantApplications(manager);
                case "10" -> handleApplicantApproval(manager, sc);
                case "11" -> handleWithdrawalRequests(manager, sc);
                case "12" -> generateReports(manager, sc);
                case "0" -> {
                    System.out.println("👋 Logging out...");
                    return;
                }
                default -> System.out.println("❌ Invalid input. Try again.");
            }
        }
    }

    private static void createProject(HDBManager manager, Scanner sc) {}
    private static void editProject(HDBManager manager, Scanner sc) {}
    private static void deleteProject(HDBManager manager, Scanner sc) {}
    private static void toggleVisibility(HDBManager manager, Scanner sc) {}

    private static void viewAllProjects() {}
    private static void viewMyProjects(HDBManager manager) {}

    private static void viewOfficerRegistrations(HDBManager manager) {}
    private static void handleOfficerApproval(HDBManager manager, Scanner sc) {}

    private static void viewApplicantApplications(HDBManager manager) {}
    private static void handleApplicantApproval(HDBManager manager, Scanner sc) {}
    private static void handleWithdrawalRequests(HDBManager manager, Scanner sc) {}

    private static void generateReports(HDBManager manager, Scanner sc) {}
}
