
package src.service;

import src.model.*;
import src.util.CsvUtil;
import src.util.EnquiryCsvMapper;
import java.util.*;

public class EnquireService {

    private static final String ENQUIRY_CSV_PATH = "data/EnquiryList.csv";

    public static List<Enquiry> loadFromCSV() {
        return EnquiryCsvMapper.loadAll(ENQUIRY_CSV_PATH);
    }

    public static void saveAllToCSV(List<Enquiry> enquiries) {
        EnquiryCsvMapper.saveAll(ENQUIRY_CSV_PATH, enquiries);
    }

    public static void submitEnquiry(Applicant applicant, Scanner sc) {
        List<Project> projects = ProjectLoader.loadProjects()
                .stream().filter(Project::isVisible).toList();

        if (projects.isEmpty()) {
            System.out.println("❌ No visible projects available.");
            return;
        }

        System.out.println("\n📋 Available Projects:");
        for (int i = 0; i < projects.size(); i++) {
            System.out.printf("[%d] %s (%s)\n",
                    i + 1, projects.get(i).getProjectName(), projects.get(i).getNeighborhood());
        }

        System.out.print("Select project (0 to cancel): ");
        int choice;
        try {
            choice = Integer.parseInt(sc.nextLine().trim());
            if (choice == 0) return;
            if (choice < 1 || choice > projects.size()) throw new Exception();
        } catch (Exception e) {
            System.out.println("❌ Invalid choice.");
            return;
        }

        Project selected = projects.get(choice - 1);

        System.out.print("Enter your enquiry: ");
        String content = sc.nextLine().trim();
        if (content.isBlank() || !content.matches(".*[a-zA-Z].*")) {
            System.out.println("❌ Invalid content. Must contain alphabetic characters.");
            return;
        }

        List<Enquiry> all = loadFromCSV();
        int newId = all.size() + 1;

        Enquiry newEntry = new Enquiry(newId, content, Enquiry.STATUS_PENDING,
                applicant.getNric(), applicant.getName(), selected.getProjectName());

        all.add(newEntry);
        saveAllToCSV(all);

        System.out.println("✅ Enquiry submitted.");
    }

    public static void viewOwnEnquiries(Applicant applicant) {
        List<Enquiry> all = loadFromCSV();
        List<Enquiry> own = all.stream()
                .filter(e -> e.getApplicantNric().equalsIgnoreCase(applicant.getNric()))
                .toList();

        if (own.isEmpty()) {
            System.out.println("❌ No enquiries found.");
            return;
        }

        for (Enquiry e : own) {
            System.out.println("──────────────────────────");
            System.out.println("🆔 ID: " + e.getEnquiryId());
            System.out.println("🏠 Project: " + e.getProjectName());
            System.out.println("📝 Content: " + e.getContent());
            System.out.println("📌 Status: " + e.getStatus());

            if (e.getReplies().isEmpty()) {
                System.out.println("💬 Replies: No replies yet.");
            } else {
                System.out.println("💬 Replies:");
                for (EnquiryReply r : e.getReplies()) {
                    String role = (r.getResponder() instanceof HDBManager) ? "Manager" : "Officer";
                    System.out.printf("  - %s replied with: %s [%s]\n", role, r.getContent(), r.getTimestamp());
                }

                if (e.isClosed()) {
                    System.out.println("🔒 This enquiry has been CLOSED after a reply from HDB Personnel. Please submit another enquiry.");
                }
            }
        }
    }

    public static void editOwnEnquiry(Applicant applicant, Scanner sc) {
        List<Enquiry> all = loadFromCSV();
        List<Enquiry> editable = all.stream()
                .filter(e -> e.getApplicantNric().equalsIgnoreCase(applicant.getNric()))
                .filter(e -> Enquiry.STATUS_PENDING.equalsIgnoreCase(e.getStatus()))
                .toList();

        if (editable.isEmpty()) {
            System.out.println("❌ No editable enquiries.");
            return;
        }

        for (int i = 0; i < editable.size(); i++) {
            System.out.printf("[%d] 🆔 %d — %s\n", i + 1, editable.get(i).getEnquiryId(), editable.get(i).getContent());
        }

        System.out.print("Select enquiry to edit (0 to cancel): ");
        int choice = getValidChoice(sc, editable.size());
        if (choice == 0) return;

        Enquiry selected = editable.get(choice - 1);
        System.out.println("Current: " + selected.getContent());
        System.out.print("New content: ");
        String newContent = sc.nextLine().trim();
        if (newContent.isBlank() || !newContent.matches(".*[a-zA-Z].*")) {
            System.out.println("❌ Invalid content.");
            return;
        }

        selected.editContent(newContent);
        saveAllToCSV(all);
        System.out.println("✅ Enquiry updated.");
    }

    public static void deleteOwnEnquiry(Applicant applicant, Scanner sc) {
        List<Enquiry> all = loadFromCSV();
        List<Enquiry> deletable = all.stream()
                .filter(e -> e.getApplicantNric().equalsIgnoreCase(applicant.getNric()))
                .filter(e -> Enquiry.STATUS_PENDING.equalsIgnoreCase(e.getStatus()))
                .toList();

        if (deletable.isEmpty()) {
            System.out.println("❌ No deletable enquiries.");
            return;
        }

        for (int i = 0; i < deletable.size(); i++) {
            System.out.printf("[%d] 🆔 %d — %s\n", i + 1, deletable.get(i).getEnquiryId(), deletable.get(i).getContent());
        }

        System.out.print("Select enquiry to delete (0 to cancel): ");
        int choice = getValidChoice(sc, deletable.size());
        if (choice == 0) return;

        Enquiry selected = deletable.get(choice - 1);
        System.out.print("Confirm delete (Y/N): ");
        if (!sc.nextLine().trim().equalsIgnoreCase("Y")) {
            System.out.println("❌ Cancelled.");
            return;
        }

        selected.delete();
        saveAllToCSV(all);
        System.out.println("✅ Enquiry deleted.");
    }

    public static void replyToEnquiry(User user, Scanner sc) {
        List<Enquiry> all = loadFromCSV();
        List<String> projectNames = getHandledProjects(user);
    
        List<Enquiry> relevant = all.stream()
                .filter(e -> projectNames.contains(e.getProjectName()))
                .filter(e -> Enquiry.STATUS_PENDING.equalsIgnoreCase(e.getStatus()))
                .toList();
    
        if (relevant.isEmpty()) {
            System.out.println("📭 No pending enquiries available for reply.");
            return;
        }
    
        System.out.println("\n📬 Pending Enquiries:");
        for (int i = 0; i < relevant.size(); i++) {
            Enquiry e = relevant.get(i);
            System.out.printf("[%d] 🆔 %d — %s (%s)\n", i + 1, e.getEnquiryId(), e.getApplicantName(), e.getProjectName());
            System.out.println("   " + e.getContent());
        }
    
        System.out.print("Select enquiry to reply (0 to cancel): ");
        int choice = getValidChoice(sc, relevant.size());
        if (choice == 0) return;
    
        Enquiry selected = relevant.get(choice - 1);
    
        // Final double-check if it's already closed (edge case)
        if (selected.isClosed()) {
            System.out.println("⚠️ This enquiry has already been closed.");
            return;
        }
    
        System.out.print("Enter reply: ");
        String reply = sc.nextLine().trim();
        if (reply.isBlank() || !reply.matches(".*[a-zA-Z].*")) {
            System.out.println("❌ Invalid reply. Must contain letters.");
            return;
        }
    
        selected.addReply(reply, user);  // Stores reply content cleanly
        saveAllToCSV(all);
    
        System.out.println("✅ Reply submitted and enquiry marked as CLOSED.");
    }
    

    public static void viewAllEnquiriesForManager(HDBManager manager) {
        List<Enquiry> all = loadFromCSV();
        if (all.isEmpty()) {
            System.out.println("❌ No enquiries in system.");
            return;
        }
        for (Enquiry e : all) display(e);
    }

    public static void viewEnquiriesForOfficer(HDBOfficer officer) {
        String project = Optional.ofNullable(officer.getAssignedProject())
                .map(Project::getProjectName)
                .orElse(null);
        if (project == null) {
            System.out.println("❌ No assigned project.");
            return;
        }

        List<Enquiry> all = loadFromCSV();
        List<Enquiry> own = all.stream()
                .filter(e -> e.getProjectName().equalsIgnoreCase(project))
                .toList();

        if (own.isEmpty()) {
            System.out.println("❌ No enquiries for your project.");
            return;
        }

        for (Enquiry e : own) display(e);
    }

    private static int getValidChoice(Scanner sc, int max) {
        try {
            int choice = Integer.parseInt(sc.nextLine().trim());
            if (choice >= 0 && choice <= max) return choice;
        } catch (Exception ignored) {}
        return -1;
    }

    private static void display(Enquiry e) {
        System.out.println("──────────────────────────");
        System.out.println("🆔 Enquiry ID : " + e.getEnquiryId());
        System.out.println("👤 Applicant  : " + e.getApplicantName() + " (NRIC: " + e.getApplicantNric() + ")");
        System.out.println("🏠 Project    : " + e.getProjectName());
        System.out.println("📝 Content    : " + e.getContent());
        System.out.println("📌 Status     : " + e.getStatus());
    
        if (e.getReplies().isEmpty()) {
            System.out.println("💬 Replies    : No replies yet.");
        } else {
            System.out.println("💬 Replies    :");
            for (EnquiryReply reply : e.getReplies()) {
                String role = reply.getResponderRole();  // ✅ fixed
                System.out.printf("  - %s replied with: %s [%s]\n",
                    role,
                    reply.getContent(),
                    reply.getTimestamp()
                );
            }
        }
    }
    

    private static List<String> getHandledProjects(User user) {
        List<String> projects = new ArrayList<>();
        if (user instanceof HDBManager manager) {
            List<Map<String, String>> projectData = CsvUtil.read("data/ProjectList.csv");
            for (Map<String, String> p : projectData) {
                if (manager.getNric().equalsIgnoreCase(p.get("ManagerNRIC"))) {
                    projects.add(p.get("Project Name"));
                }
            }
        } else if (user instanceof HDBOfficer officer && officer.getAssignedProject() != null) {
            projects.add(officer.getAssignedProject().getProjectName());
        }
        return projects;
    }
}
