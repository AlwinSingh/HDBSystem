package src.service;

import src.model.*;
import src.util.CsvUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ApplicantMenu {

    public static void show(Applicant applicant) {
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n===== 🏠 Applicant Dashboard =====");
            System.out.println("Welcome, " + applicant.getName());
            System.out.println("1. View eligible open projects");
            System.out.println("2. Apply for a project");
            System.out.println("3. View my application");
            System.out.println("4. Request withdrawal");
            System.out.println("0. Logout");
            System.out.print("Enter choice: ");
            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1" -> viewEligibleProjects(applicant);
                case "2" -> applyForProject(applicant, sc);
                case "3" -> viewApplication(applicant);
                case "4" -> requestWithdrawal(applicant);
                case "0" -> {
                    System.out.println("🚪 Logged out.\n");
                    return;
                }
                default -> System.out.println("❌ Invalid choice. Please select a valid option.");
            }
        }
    }

    private static void viewEligibleProjects(Applicant applicant) {
        List<Project> projects = ProjectLoader.loadProjects();
        System.out.println("\n📋 Eligible Open Projects:");
        boolean anyShown = false;

        for (Project p : projects) {
            if (p != null && p.getProjectName() != null && isEligible(applicant, p) && p.isVisible()) {
                anyShown = true;
                System.out.println("────────────────────────────");
                System.out.println("🏠 Project Name      : " + p.getProjectName());
                System.out.println("📍 Location          : " + p.getNeighborhood());
                System.out.println("📅 Application Period: " + p.getOpenDate() + " to " + p.getCloseDate());
                System.out.println("🏢 2-Room Units      : " + p.getRemainingFlats("2-Room") + " ($" + p.getPrice2Room() + ")");
                System.out.println("🏢 3-Room Units      : " + p.getRemainingFlats("3-Room") + " ($" + p.getPrice3Room() + ")");
            }
        }

        if (!anyShown) {
            System.out.println("❌ No eligible open projects available at the moment.");
        }
    }

    private static boolean isEligible(Applicant applicant, Project project) {
        String status = applicant.getMaritalStatus();
        int age = applicant.getAge();
        return status != null &&
                ((status.equalsIgnoreCase("Single") && age >= 35) ||
                (status.equalsIgnoreCase("Married") && age >= 21));
    }

    private static void applyForProject(Applicant applicant, Scanner sc) {
        Application existingApp = applicant.getApplication();
        if (existingApp != null) {
            String status = existingApp.getStatus();
            if ("WITHDRAWAL_REQUESTED".equalsIgnoreCase(status)) {
                System.out.println("⚠️ Withdrawal is pending. Please wait for it to be processed before reapplying.");
            } else {
                System.out.println("⚠️ You already have an active application for: " + existingApp.getProject().getProjectName());
                System.out.println("Status: " + status);
            }
            return;
        }

        List<Project> eligibleProjects = new ArrayList<>();
        for (Project p : ProjectLoader.loadProjects()) {
            if (p != null && p.getProjectName() != null && isEligible(applicant, p) && p.isVisible()) {
                eligibleProjects.add(p);
            }
        }

        if (eligibleProjects.isEmpty()) {
            System.out.println("❌ No eligible projects available.");
            return;
        }

        System.out.println("\n📋 Eligible Projects:");
        for (int i = 0; i < eligibleProjects.size(); i++) {
            Project p = eligibleProjects.get(i);
            System.out.printf("[%d] %s (%s)\n", i + 1, p.getProjectName(), p.getNeighborhood());
        }

        int choice = -1;
        while (choice < 1 || choice > eligibleProjects.size()) {
            System.out.print("Enter project number to apply: ");
            try {
                choice = Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("❌ Please enter a valid number.");
            }
        }

        Project selected = eligibleProjects.get(choice - 1);
        String flatType = "2-Room";

        if (applicant.getMaritalStatus().equalsIgnoreCase("Married")) {
            while (true) {
                System.out.print("Choose flat type (2-Room / 3-Room): ");
                flatType = sc.nextLine().trim();
                if (flatType.equals("2-Room") || flatType.equals("3-Room")) break;
                System.out.println("❌ Invalid flat type. Choose either 2-Room or 3-Room.");
            }
        }

        if (selected.getRemainingFlats(flatType) <= 0) {
            System.out.println("❌ No available units for " + flatType + " in this project.");
            return;
        }

        boolean success = applicant.applyForProject(selected, flatType);
        if (success) {
            System.out.println("✅ Application submitted for " + selected.getProjectName() +
                    " (" + flatType + "). Status: PENDING.");
            CsvUtil.updateApplicantApplication("data/ApplicantList.csv", applicant);
        } else {
            System.out.println("❌ Application failed.");
        }
    }

    private static void viewApplication(Applicant applicant) {
        Application app = applicant.getApplication();

        if (app == null) {
            System.out.println("❌ You have not applied for any project yet.");
            return;
        }

        Project p = app.getProject();
        System.out.println("\n📄 Your Application");
        System.out.println("🏠 Project   : " + p.getProjectName() + " (" + p.getNeighborhood() + ")");
        System.out.println("🏢 Flat Type : " + app.getFlatType());

        String status = app.getStatus();
        if ("WITHDRAWAL_REQUESTED".equalsIgnoreCase(status)) {
            System.out.println("🔁 Status    : WITHDRAWAL REQUESTED (Pending review)");
        } else {
            System.out.println("✅ Status    : " + status);
        }
    }

    private static void requestWithdrawal(Applicant applicant) {
        Scanner sc = new Scanner(System.in);
        Application app = applicant.getApplication();
    
        if (app == null) {
            System.out.println("❌ You don’t have any application to withdraw.");
            return;
        }
    
        if ("WITHDRAWAL_REQUESTED".equalsIgnoreCase(app.getStatus())) {
            System.out.println("ℹ️ You’ve already requested a withdrawal.");
            return;
        }
    
        System.out.print("❓ Confirm withdrawal for " 
                         + app.getProject().getProjectName() + "? (Y/N): ");
        String input = sc.nextLine().trim().toUpperCase();
    
        if (!input.equals("Y")) {
            System.out.println("🔁 Withdrawal cancelled.");
            return;
        }
    
        app.setStatus("WITHDRAWAL_REQUESTED");
        CsvUtil.updateApplicantApplication("data/ApplicantList.csv", applicant);
        System.out.println("✅ Withdrawal request submitted for project: " + app.getProject().getProjectName());
    }
    
}
