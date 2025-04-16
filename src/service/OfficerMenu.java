package src.service;

import src.model.*;
import src.util.ApplicantCsvMapper;
import src.util.EnquiryCsvMapper;
import src.util.OfficerCsvMapper;
import src.util.ProjectCsvMapper;

import java.util.*;
import java.util.stream.Collectors;

public class OfficerMenu {

    private static final String APPLICANT_PATH = "data/ApplicantList.csv";
    private static final String OFFICER_PATH = "data/OfficerList.csv";
    private static final String PROJECT_PATH = "data/ProjectList.csv";
    private static final String ENQUIRY_PATH = "data/EnquiryList.csv";

    public static void show(HDBOfficer officer) {
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("\n===== 🧑‍💼 HDB Officer Dashboard =====");
            System.out.println("Welcome, Officer " + officer.getName());
            System.out.println("1. View registration status");
            System.out.println("2. Register for project");
            System.out.println("3. View assigned project details");
            System.out.println("4. Book flat for applicant");
            System.out.println("5. Generate receipt for applicant");
            System.out.println("6. View & reply to enquiries");
            System.out.println("0. Logout");
            System.out.print("Enter choice: ");

            switch (sc.nextLine().trim()) {
                case "1" -> viewRegistrationStatus(officer);
                case "2" -> registerForProject(officer, sc);
                case "3" -> viewAssignedProjectDetails(officer);
                case "4" -> bookFlat(officer, sc);
                case "5" -> generateReceipt(officer);
                case "6" -> handleEnquiries(officer, sc);
                case "0" -> {
                    System.out.println("👋 Logging out...");
                    return;
                }
                default -> System.out.println("❌ Invalid input.");
            }
        }
    }

    private static void viewRegistrationStatus(HDBOfficer officer) {
        officer.viewOfficerRegistrationStatus();
    }

    private static void registerForProject(HDBOfficer officer, Scanner sc) {
        if (officer.getAssignedProject() != null) {
            System.out.println("✅ You are already registered to project: " +
                officer.getAssignedProject().getProjectName());
            return;
        }
    
        List<Project> projects = ProjectCsvMapper.loadAll(PROJECT_PATH);
    
        System.out.println("\n📋 Available Projects:");
        List<Project> available = projects.stream()
            .filter(p -> p.isVisible() && !p.getOfficerNRICs().contains(officer.getNric()))
            .collect(Collectors.toList());
    
        if (available.isEmpty()) {
            System.out.println("❌ No visible projects available.");
            return;
        }
    
        for (int i = 0; i < available.size(); i++) {
            System.out.printf("[%d] %s (%s)\n", i + 1, available.get(i).getProjectName(), available.get(i).getNeighborhood());
        }
    
        System.out.print("Choose project number to register: ");
        try {
            int idx = Integer.parseInt(sc.nextLine().trim()) - 1;
            if (idx < 0 || idx >= available.size()) throw new IndexOutOfBoundsException();
    
            Project selected = available.get(idx);
            boolean registered = officer.registerToHandleProject(selected);
            if (registered) {
                OfficerCsvMapper.updateOfficer(OFFICER_PATH, officer);
                System.out.println("✅ Registration submitted.");
            } else {
                System.out.println("❌ Could not register. Check your current assignment or application status.");
            }
    
        } catch (Exception e) {
            System.out.println("❌ Invalid selection.");
        }
    }
    
    private static void viewAssignedProjectDetails(HDBOfficer officer) {
        Project assigned = officer.getAssignedProject();
        if (assigned == null) {
            System.out.println("❌ No assigned project.");
            return;
        }

        System.out.println("\n📌 Assigned Project Details:");
        System.out.println("🏢 Name: " + assigned.getProjectName());
        System.out.println("📍 Location: " + assigned.getNeighborhood());
        System.out.println("🏠 2-Room Available: " + assigned.getRemainingFlats("2-Room"));
        System.out.println("🏠 3-Room Available: " + assigned.getRemainingFlats("3-Room"));
        System.out.println("📅 Period: " + assigned.getOpenDate() + " to " + assigned.getCloseDate());
    }

    private static void bookFlat(HDBOfficer officer, Scanner sc) {
        Project assigned = officer.getAssignedProject();
        if (assigned == null) {
            System.out.println("❌ No assigned project.");
            return;
        }

        List<Applicant> applicants = ApplicantCsvMapper.loadAll(APPLICANT_PATH);
        List<Applicant> pending = applicants.stream()
            .filter(a -> a.getApplication() != null)
            .filter(a -> a.getApplication().getProject().equals(assigned))
            .filter(a -> "SUCCESSFUL".equalsIgnoreCase(a.getApplication().getStatus()))
            .collect(Collectors.toList());

        if (pending.isEmpty()) {
            System.out.println("❌ No applicants ready for booking.");
            return;
        }

        System.out.println("\n📋 Eligible Applicants:");
        for (int i = 0; i < pending.size(); i++) {
            Applicant a = pending.get(i);
            System.out.printf("[%d] %s (NRIC: %s)\n", i + 1, a.getName(), a.getNric());
        }

        System.out.print("Select applicant to book: ");
        try {
            int idx = Integer.parseInt(sc.nextLine().trim()) - 1;
            Applicant selected = pending.get(idx);
            String flatType = selected.getApplication().getFlatType();
            officer.bookFlat(selected.getApplication(), flatType);
            ApplicantCsvMapper.updateApplicant(APPLICANT_PATH, selected);
            ProjectCsvMapper.saveAll(PROJECT_PATH, ProjectCsvMapper.loadAll(PROJECT_PATH));
            System.out.println("✅ Booking successful.");
        } catch (Exception e) {
            System.out.println("❌ Invalid booking.");
        }
    }

    private static void handleEnquiries(HDBOfficer officer, Scanner sc) {
        Project assignedProject = officer.getAssignedProject();
        if (assignedProject == null) {
            System.out.println("❌ No assigned project.");
            return;
        }

        List<Enquiry> allEnquiries = EnquiryCsvMapper.loadAll(ENQUIRY_PATH);
        List<Enquiry> projectEnquiries = allEnquiries.stream()
            .filter(e -> e.getProject().getProjectName().equalsIgnoreCase(assignedProject.getProjectName()))
            .collect(Collectors.toList());

        if (projectEnquiries.isEmpty()) {
            System.out.println("📭 No enquiries found for your project.");
            return;
        }

        System.out.println("\n📬 Enquiries for Project: " + assignedProject.getProjectName());
        for (int i = 0; i < projectEnquiries.size(); i++) {
            Enquiry e = projectEnquiries.get(i);
            System.out.printf("[%d] %s: %s\n", i + 1, e.getApplicant().getName(), e.getContent());
            if (!e.getReplies().isEmpty()) {
                System.out.println("    💬 Latest Reply: " + e.getReplies().get(e.getReplies().size() - 1));
            }
        }

        System.out.print("Select an enquiry to reply (or 0 to cancel): ");
        try {
            int idx = Integer.parseInt(sc.nextLine().trim());
            if (idx == 0) return;
            if (idx < 1 || idx > projectEnquiries.size()) throw new IndexOutOfBoundsException();

            Enquiry selected = projectEnquiries.get(idx - 1);
            System.out.print("Enter your reply: ");
            String reply = sc.nextLine().trim();

            selected.replyFromOfficer(reply);  // ✅ Abstracted into Enquiry
            EnquiryCsvMapper.saveAll(ENQUIRY_PATH, allEnquiries);
            System.out.println("✅ Reply sent and enquiry marked as closed.");

        } catch (Exception e) {
            System.out.println("❌ Invalid selection.");
        }
    }

    private static void generateReceipt(HDBOfficer officer) {
        System.out.println("\n🧾 Generate Receipt: Work in Progress...");
        System.out.println("This feature is currently under development and will be available soon.");
    }
}
