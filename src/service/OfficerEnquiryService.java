package src.service;

import java.util.List;
import java.util.Scanner;

import src.model.Enquiry;
import src.model.HDBOfficer;
import src.model.Project;
import src.util.EnquiryCsvMapper;

public class OfficerEnquiryService {
    /**
     * Opens the enquiry handling interface for the officer, allowing them to view and respond to enquiries.
     *
     * @param officer The logged-in officer.
     * @param sc      Scanner for input.
     */
    public static void handleEnquiries(HDBOfficer officer, Scanner sc) {
        List<Enquiry> projectEnquiries = getPendingEnquiriesForProject(officer);
    
        if (projectEnquiries.isEmpty()) {
            System.out.println("📭 No open enquiries found for your project.");
            return;
        }
    
        System.out.println("\n📬 Enquiries for Project: " + officer.getAssignedProject().getProjectName());
        for (int i = 0; i < projectEnquiries.size(); i++) {
            Enquiry e = projectEnquiries.get(i);
            System.out.printf("[%d] %s: %s\n", i + 1, e.getApplicantName(), e.getContent());
        }
    
        System.out.print("Select an enquiry to reply (or 0 to cancel): ");
        try {
            int idx = Integer.parseInt(sc.nextLine().trim());
            if (idx == 0) return;
            if (idx < 1 || idx > projectEnquiries.size()) throw new IndexOutOfBoundsException();
    
            Enquiry selected = projectEnquiries.get(idx - 1);
            System.out.print("Enter your reply: ");
            String reply = sc.nextLine().trim();
    
            boolean ok = replyToEnquiry(selected, officer, reply);
            if (ok) {
                System.out.println("✅ Reply sent and enquiry marked as CLOSED.");
            } else {
                System.out.println("❌ Failed to reply.");
            }
    
        } catch (Exception e) {
            System.out.println("❌ Invalid selection.");
        }
    }

    public static List<Enquiry> getPendingEnquiriesForProject(HDBOfficer officer) {
        Project assigned = officer.getAssignedProject();
        if (assigned == null || !"APPROVED".equalsIgnoreCase(officer.getRegistrationStatus())) return List.of();

        return EnquiryCsvMapper.loadAll().stream()
            .filter(e -> e.getProjectName().equalsIgnoreCase(assigned.getProjectName()))
            .filter(e -> Enquiry.STATUS_PENDING.equalsIgnoreCase(e.getStatus()))
            .toList();
    }

    public static boolean replyToEnquiry(Enquiry enquiry, HDBOfficer officer, String reply) {
        enquiry.addReply(reply, officer);
        EnquiryCsvMapper.update(enquiry); // Efficient single-row update
        return true;
    }

}
