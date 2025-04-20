package src.service;

import src.model.*;
import src.util.EnquiryCsvMapper;
import src.util.CsvUtil;
import java.util.*;
import java.util.stream.Collectors;

import src.util.FilePath;
import src.util.ProjectCsvMapper;


/**
 * Provides services related to project enquiries, including submitting,
 * editing, deleting, and responding to enquiries for applicants and HDB personnel.
 */
public class EnquireService {



    /**
     * Loads all enquiries from the CSV file.
     *
     * @return A list of all enquiries in the system.
     */
    public static List<Enquiry> loadFromCSV() {
        return EnquiryCsvMapper.loadAll();
    }


    /**
     * Allows an applicant to submit a new enquiry about a project.
     *
     * @param applicant The applicant submitting the enquiry.
     * @param sc Scanner for user input.
     */
    public static void submitEnquiry(Applicant applicant, Scanner sc) {
        Set<String> appliedProjectNames = new HashSet<>();
        if (applicant.getApplication() != null && applicant.getApplication().getProject() != null) {
            appliedProjectNames.add(applicant.getApplication().getProject().getProjectName());
        }

        List<Project> projects = ProjectLoader.loadProjects().stream()
                .filter(p -> p.isVisible() || appliedProjectNames.contains(p.getProjectName()))
                .toList();

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

        System.out.print("Enter your enquiry (or type 'cancel' to go back): ");
        String content = sc.nextLine().trim();
        if (content.equalsIgnoreCase("cancel") || content.equals("0")) {
            System.out.println("🔙 Enquiry cancelled.");
            return;
        }

        if (content.isBlank() || !content.matches(".*[a-zA-Z].*")) {
            System.out.println("❌ Invalid content. Must contain alphabetic characters.");
            return;
        }

        int newId = loadFromCSV().stream()
                .mapToInt(Enquiry::getEnquiryId)
                .max()
                .orElse(0) + 1;

        Enquiry newEntry = new Enquiry(newId, content, Enquiry.STATUS_PENDING,
                applicant.getNric(), applicant.getName(), selected.getProjectName());

        EnquiryCsvMapper.add(newEntry);
        System.out.println("✅ Enquiry submitted.");
    }


    /**
     * Displays all enquiries submitted by the current applicant.
     *
     * @param applicant The logged-in applicant.
     */
    public static void viewOwnEnquiries(Applicant applicant) {
        List<Enquiry> own = loadFromCSV().stream()
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


    /**
     * Lets the applicant edit their existing enquiry if it is still pending.
     *
     * @param applicant The logged-in applicant.
     * @param sc Scanner for user input.
     */
    public static void editOwnEnquiry(Applicant applicant, Scanner sc) {
        List<Enquiry> editable = loadFromCSV().stream()
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
        System.out.print("New content (or type 'cancel' to go back): ");
        String newContent = sc.nextLine().trim();

        if (newContent.equalsIgnoreCase("cancel")) {
            System.out.println("🔙 Edit cancelled.");
            return;
        }

        if (newContent.isBlank() || !newContent.matches(".*[a-zA-Z].*")) {
            System.out.println("❌ Invalid content.");
            return;
        }

        selected.editContent(newContent);
        EnquiryCsvMapper.update(selected);
        System.out.println("✅ Enquiry updated.");
    }



    /**
     * Allows an applicant to delete their pending enquiry.
     *
     * @param applicant The applicant attempting to delete an enquiry.
     * @param sc Scanner for user input.
     */
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
        EnquiryCsvMapper.saveAll(all);  // acceptable as deletion is rare
        System.out.println("✅ Enquiry deleted.");
    }


    /**
     * Enables an officer or manager to reply to a pending enquiry.
     *
     * @param user The HDB personnel replying.
     * @param sc Scanner for input.
     */
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

        selected.addReply(reply, user);
        EnquiryCsvMapper.update(selected);

        System.out.println("✅ Reply submitted and enquiry marked as CLOSED.");
    }


    /**
     * Shows all enquiries in the system to a manager.
     *
     * @param manager The logged-in HDB manager.
     */
    public static void viewAllEnquiriesForManager(HDBManager manager) {
        List<Enquiry> all = loadFromCSV();
        if (all.isEmpty()) {
            System.out.println("❌ No enquiries in system.");
            return;
        }
        for (Enquiry e : all) display(e);
    }


    /**
     * Shows all enquiries related to the officer's assigned project.
     *
     * @param officer The logged-in HDB officer.
     */
    public static void viewEnquiriesForOfficer(HDBOfficer officer) {
        String project = Optional.ofNullable(officer.getAssignedProject())
                .map(Project::getProjectName)
                .orElse(null);
        if (project == null) {
            System.out.println("❌ No assigned project.");
            return;
        }

        List<Enquiry> own = loadFromCSV().stream()
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
                String role = reply.getResponderRole();
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
            List<Map<String, String>> projectData = CsvUtil.read(FilePath.PROJECT_LIST_FILE);
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


    /**
     * Displays every enquiry currently stored in the system.
     */
    public static void viewAllEnquiries() {
        List<Enquiry> all = loadFromCSV();
        if (all.isEmpty()) {
            System.out.println("📭 No enquiries in the system.");
            return;
        }

        System.out.println("\n📋 All Enquiries:");
        for (Enquiry e : all) {
            System.out.printf("📨 Enquiry #%d | Applicant: %s (%s) | Project: %s | Status: %s\n",
                    e.getEnquiryId(), e.getApplicantName(), e.getApplicantNric(), e.getProjectName(), e.getStatus());
            System.out.println("📣 " + e.getContent());

            if (!e.getReplies().isEmpty()) {
                System.out.println("💬 Replies:");
                for (EnquiryReply r : e.getReplies()) {
                    System.out.printf("   - [%s] %s: %s\n", r.getTimestamp(), r.getResponderRole(), r.getContent());
                }
            }
            System.out.println("────────────────────────────────────────────");
        }
    }


    /**
     * Lets a manager respond to enquiries for projects they oversee.
     *
     * @param manager The logged-in manager.
     * @param sc Scanner for user input.
     */
    public static void replyAsManager(HDBManager manager, Scanner sc) {
        Set<String> managedProjects = ProjectCsvMapper.loadAll().stream()
                .filter(p -> p.getManager() != null && p.getManager().getNric().equalsIgnoreCase(manager.getNric()))
                .map(Project::getProjectName)
                .collect(Collectors.toSet());

        List<Enquiry> all = loadFromCSV();
        List<Enquiry> myEnquiries = all.stream()
                .filter(e -> managedProjects.contains(e.getProjectName()))
                .filter(e -> Enquiry.STATUS_PENDING.equalsIgnoreCase(e.getStatus()))
                .toList();

        if (myEnquiries.isEmpty()) {
            System.out.println("📭 No open enquiries for your projects.");
            return;
        }

        System.out.println("\n📬 Enquiries:");
        for (int i = 0; i < myEnquiries.size(); i++) {
            Enquiry e = myEnquiries.get(i);
            System.out.printf("[%d] %s (%s): %s\n", i + 1, e.getApplicantName(), e.getApplicantNric(), e.getContent());
        }

        System.out.print("Choose enquiry to reply (0 to cancel): ");
        try {
            int choice = Integer.parseInt(sc.nextLine());
            if (choice == 0) return;
            if (choice < 1 || choice > myEnquiries.size()) throw new IndexOutOfBoundsException();

            Enquiry selected = myEnquiries.get(choice - 1);
            System.out.print("Enter reply: ");
            String reply = sc.nextLine().trim();

            selected.addReply(reply, manager);
            EnquiryCsvMapper.update(selected);
            System.out.println("✅ Reply sent and enquiry marked as CLOSED.");

        } catch (Exception e) {
            System.out.println("❌ Invalid selection.");
        }
    }

}
