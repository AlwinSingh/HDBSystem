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
        System.out.println("\n===== 📋 Registration Status Details =====");
    
        String regStatus = officer.getRegistrationStatus();
        Project assigned = officer.getAssignedProject();
    
        if (regStatus == null || regStatus.isBlank()) {
            System.out.println("🔍 You have not registered for any project.");
            System.out.println("💡 You may register for an open project from the Officer Dashboard.");
            return;
        }
    
        // Project context
        String projectName = (assigned != null) ? assigned.getProjectName() : "(Pending assignment)";
        System.out.println("📌 Registered Project: " + projectName);
    
        // Status interpretation
        switch (regStatus.toUpperCase()) {
            case "PENDING" -> {
                System.out.println("📊 Status: 🕒 Pending approval");
                System.out.println("⏳ Your request is being reviewed by an HDB Manager.");
                System.out.println("⛔ You may not register for another project until a decision is made.");
            }
            case "APPROVED" -> {
                System.out.println("📊 Status: ✅ Approved");
                System.out.println("🎯 You are officially assigned to this project.");
                System.out.println("🚀 You may now handle flat bookings and enquiries.");
            }
            case "REJECTED" -> {
                System.out.println("📊 Status: ❌ Rejected");
                System.out.println("⚠️ Your previous registration was rejected by a manager.");
                System.out.println("💡 You may register again for a different project.");
            }
            default -> {
                System.out.println("📊 Status: " + regStatus);
                System.out.println("⚠️ Unrecognized status. Please contact system administrator.");
            }
        }
    }

    private static void registerForProject(HDBOfficer officer, Scanner sc) {
        if (officer.getAssignedProject() != null || 
            (officer.getRegistrationStatus() != null && officer.getRegistrationStatus().equalsIgnoreCase("PENDING"))) {
            System.out.println("⚠️ You already have a pending or approved registration.");
            System.out.println("❌ Cannot register for another project at this time.");
            return;
        }
    
        List<Project> allProjects = ProjectLoader.loadProjects();
    
        List<Project> availableForRegistration = new ArrayList<>();
        for (Project p : allProjects) {
            if (p.isVisible()) {
                availableForRegistration.add(p);
            }
        }
    
        if (availableForRegistration.isEmpty()) {
            System.out.println("❌ No visible projects available for registration.");
            return;
        }
    
        System.out.println("\n📋 Available Projects:");
        for (int i = 0; i < availableForRegistration.size(); i++) {
            Project p = availableForRegistration.get(i);
            System.out.printf("[%d] %s (%s)\n", i + 1, p.getProjectName(), p.getNeighborhood());
        }
    
        System.out.print("Enter project number to register: ");
        String input = sc.nextLine();
        int choice;
    
        try {
            choice = Integer.parseInt(input);
            if (choice < 1 || choice > availableForRegistration.size()) {
                System.out.println("❌ Invalid choice.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid input. Please enter a number.");
            return;
        }
    
        Project selected = availableForRegistration.get(choice - 1);
    
        // Proceed with registration
        officer.setRegistrationStatus("PENDING");
        officer.setAssignedProjectByName(selected.getProjectName(), allProjects);
        CsvUtil.updateOfficerRegistration("data/OfficerList.csv", officer);
        System.out.println("✅ Registration submitted for project: " + selected.getProjectName());
    }
    
    

    private static void viewAssignedProjectDetails(HDBOfficer officer) {
        Project assigned = officer.getAssignedProject();
        String regStatus = officer.getRegistrationStatus();
    
        if (assigned == null) {
            if ("PENDING".equalsIgnoreCase(regStatus)) {
                System.out.println("\n🕒 Your registration is currently pending approval.");
                System.out.println("⛔ You cannot register for another project until the current request is processed.");
            } else {
                System.out.println("❌ You are not assigned to any project.");
            }
            return;
        }
    
        System.out.println("\n===== 📊 Assigned Project Details =====");
        System.out.println("🏠 Project Name: " + assigned.getProjectName());
        System.out.println("📍 Neighborhood: " + assigned.getNeighborhood());
        System.out.println("📅 Application Period: " + assigned.getOpenDate() + " to " + assigned.getCloseDate());
        System.out.println("🔒 Visibility: " + (assigned.isVisible() ? "Visible to public" : "Hidden (Officer-only)"));
        System.out.println("🛏️ 2-Room Units Available: " + assigned.getRemainingFlats("2-Room"));
        System.out.println("🛏️ 3-Room Units Available: " + assigned.getRemainingFlats("3-Room"));
        System.out.println("👨‍💼 Officer Slots: " + assigned.getOfficerSlots());
    
        // Clarify their current registration status
        if ("PENDING".equalsIgnoreCase(regStatus)) {
            System.out.println("📌 Status: 🕒 Registration pending approval");
        } else if ("APPROVED".equalsIgnoreCase(regStatus)) {
            System.out.println("📌 Status: ✅ Approved and actively assigned");
        } else if ("REJECTED".equalsIgnoreCase(regStatus)) {
            System.out.println("📌 Status: ❌ Registration rejected");
        }
    }
    
    

    private static void replyToEnquiries(HDBOfficer officer, Scanner sc) {
        Project assignedProject = officer.getAssignedProject();
        String regStatus = officer.getRegistrationStatus();
    
        if (assignedProject == null || !"APPROVED".equalsIgnoreCase(regStatus)) {
            System.out.println("❌ You must be approved and assigned to a project to reply to enquiries.");
            return;
        }
    
        // Placeholder for future implementation
        System.out.println("💬 This feature will allow officers to view and respond to enquiries related to the project.");
        System.out.println("🔧 (Coming Soon)");
    }
    
    

    private static void bookFlat(HDBOfficer officer, Scanner sc) {
        Project assignedProject = officer.getAssignedProject();
        String regStatus = officer.getRegistrationStatus();
    
        // Ensure project assignment and approved status
        if (assignedProject == null || !"APPROVED".equalsIgnoreCase(regStatus)) {
            System.out.println("❌ You are not approved to handle any project yet.");
            System.out.println("ℹ️ Only officers with approved registration can proceed with flat bookings.");
            return;
        }
    
        List<Map<String, String>> rows = CsvUtil.read("data/ApplicantList.csv");
        List<Map<String, String>> eligibleApplicants = new ArrayList<>();
    
        for (Map<String, String> row : rows) {
            String status = row.get("ApplicationStatus");
            String projectName = row.get("AppliedProjectName");
    
            if ("SUCCESSFUL".equalsIgnoreCase(status)
                    && assignedProject.getProjectName().equalsIgnoreCase(projectName)) {
                eligibleApplicants.add(row);
            }
        }
    
        if (eligibleApplicants.isEmpty()) {
            System.out.println("❌ No applicants with SUCCESSFUL application for this project.");
            return;
        }
    
        System.out.println("\n📋 Applicants ready for booking:");
        for (int i = 0; i < eligibleApplicants.size(); i++) {
            Map<String, String> row = eligibleApplicants.get(i);
            System.out.printf("[%d] %s (NRIC: %s, Flat: %s)\n", i + 1,
                    row.get("Name"), row.get("NRIC"), row.get("FlatTypeApplied"));
        }
    
        System.out.print("Select applicant to book flat for [number]: ");
        String input = sc.nextLine().trim();
        int choice;
    
        try {
            choice = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid input. Please enter a valid number.");
            return;
        }
    
        if (choice < 1 || choice > eligibleApplicants.size()) {
            System.out.println("❌ Choice out of range.");
            return;
        }
    
        Map<String, String> selected = eligibleApplicants.get(choice - 1);
        String flatType = selected.get("FlatTypeApplied");
    
        // Check unit availability before booking
        if (assignedProject.getRemainingFlats(flatType) <= 0) {
            System.out.println("❌ No available units for that flat type.");
            return;
        }
    
        selected.put("ApplicationStatus", "BOOKED");
        assignedProject.decrementFlatCount(flatType);
    
        CsvUtil.write("data/ApplicantList.csv", rows);
        System.out.println("✅ Flat booked successfully for " + selected.get("Name") +
                " (" + flatType + "). Status updated to BOOKED.");
    }
    
    
    private static void generateReceipt(HDBOfficer officer, Scanner sc) {
        Project assignedProject = officer.getAssignedProject();
        String regStatus = officer.getRegistrationStatus();
    
        if (assignedProject == null || !"APPROVED".equalsIgnoreCase(regStatus)) {
            System.out.println("❌ You must be approved and assigned to a project to generate receipts.");
            return;
        }
    
        System.out.println("\n🧾 Receipt generation is under development.");
        System.out.println("🔧 In the final version, you will be able to:");
        System.out.println(" - Retrieve a booked applicant's information");
        System.out.println(" - Generate a receipt with their project and flat details");
    }
    
    
}
