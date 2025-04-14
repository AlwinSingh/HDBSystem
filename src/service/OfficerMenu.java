package src.service;

import src.model.*;
import src.util.CsvUtil;

import java.util.*;

public class OfficerMenu {

    public static void show(HDBOfficer officer) {
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n===== 🛠️ Officer Dashboard =====");
            System.out.println("Hello, Officer " + officer.getName());

            if (officer.getAssignedProject() != null) {
                System.out.println("📌 Assigned Project: " + officer.getAssignedProject().getProjectName());
            } else {
                System.out.println("📌 No assigned project yet");
            }

            System.out.println("\n1. View registration status");
            System.out.println("2. Register for a project");
            System.out.println("3. View assigned project details");
            System.out.println("4. View & reply to enquiries");
            System.out.println("5. Book flat for applicant");
            System.out.println("6. Generate receipt for applicant");
            System.out.println("0. Back");
            System.out.print("Enter choice: ");

            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1" -> viewRegistrationStatus(officer);
                case "2" -> registerForProject(officer, sc);
                case "3" -> viewAssignedProjectDetails(officer);
                case "4" -> replyToEnquiries(officer, sc);
                case "5" -> bookFlat(officer, sc);
                case "6" -> generateReceipt(officer, sc);
                case "0" -> {
                    System.out.println("🔙 Returning to role selection...");
                    return;
                }
                default -> System.out.println("❌ Invalid input. Try again.");
            }
        }
    }

    private static void viewRegistrationStatus(HDBOfficer officer) {
        System.out.println("\n📋 Registration Status: " + officer.getRegistrationStatus());
    }

    private static void registerForProject(HDBOfficer officer, Scanner sc) {
        if (officer.getApplication() != null) {
            System.out.println("❌ You have applied for a BTO project. You cannot register for any projects as an officer.");
            return;
        }

        List<Project> allProjects = ProjectLoader.loadProjects();
        List<Project> available = new ArrayList<>();

        for (Project p : allProjects) {
            if (p.getOpenDate() != null && p.getCloseDate() != null) {
                if (officer.getAssignedProject() == null ||
                    !officer.getAssignedProject().getProjectName().equalsIgnoreCase(p.getProjectName())) {
                    available.add(p);
                }
            }
        }

        if (available.isEmpty()) {
            System.out.println("❌ No available projects to register.");
            return;
        }

        System.out.println("\n📋 Available Projects to Register:");
        for (int i = 0; i < available.size(); i++) {
            System.out.println("[" + (i + 1) + "] " + available.get(i).getProjectName() + " (" + available.get(i).getNeighborhood() + ")");
        }

        System.out.print("Enter project number to register: ");
        int choice = Integer.parseInt(sc.nextLine());

        if (choice < 1 || choice > available.size()) {
            System.out.println("❌ Invalid project choice.");
            return;
        }

        Project selected = available.get(choice - 1);
        if (officer.registerToHandleProject(selected)) {
            System.out.println("✅ Registration submitted for project: " + selected.getProjectName() + ". Status: PENDING");
        } else {
            System.out.println("❌ You are already registered or assigned to a project.");
        }
    }

    private static void viewAssignedProjectDetails(HDBOfficer officer) {
        if (officer.getAssignedProject() == null) {
            System.out.println("❌ You are not assigned to any project.");
            return;
        }

        var p = officer.getAssignedProject();
        System.out.println("\n===== 📊 Project Details =====");
        System.out.println("🏠 Project: " + p.getProjectName());
        System.out.println("📍 Location: " + p.getNeighborhood());
        System.out.println("📅 Application Period: " + p.getOpenDate() + " to " + p.getCloseDate());
        System.out.println("🛏️ 2-Room Units Remaining: " + p.getRemainingFlats("2-Room"));
        System.out.println("🛏️ 3-Room Units Remaining: " + p.getRemainingFlats("3-Room"));
    }

    private static void replyToEnquiries(HDBOfficer officer, Scanner sc) {
        System.out.println("💬 Feature coming soon: View & reply to enquiries.");
    }

    private static void bookFlat(HDBOfficer officer, Scanner sc) {
        System.out.println("🏘️ Feature coming soon: Book flat for applicant.");
    }

    private static void generateReceipt(HDBOfficer officer, Scanner sc) {
        System.out.println("🧾 Feature coming soon: Generate receipt.");
    }
}
