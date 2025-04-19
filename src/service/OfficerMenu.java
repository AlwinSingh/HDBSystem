package src.service;

import java.util.*;
import java.util.stream.Collectors;
import src.model.*;
import src.util.ApplicantCsvMapper;
import src.util.AmenitiesCsvMapper;
import src.util.EnquiryCsvMapper;
import src.util.OfficerCsvMapper;
import src.util.PaymentCsvMapper;
import src.util.ProjectCsvMapper;

public class OfficerMenu {

    public static void show(HDBOfficer officer) {
        Scanner sc = new Scanner(System.in);
    
        while (true) {
            System.out.println("\n===== 🧑‍💼 HDB Officer Dashboard =====");
            System.out.println("Welcome, Officer " + officer.getName());
    
            System.out.println("\n📋 Registration");
            System.out.printf(" [1] 📝 View Status           [2] 🏗️ Register for Project%n");
    
            System.out.println("\n📂 Project");
            System.out.printf(" [3] 📄 View Details          [4] 🏠 Book Flat for Applicant%n");
            System.out.printf(" [5] 🧾 Generate Receipt      [6] 📍 Update Location%n");
            System.out.printf(" [7] ➕ Add Amenity%n");
    
            System.out.println("\n📬 Enquiries");
            System.out.printf(" [8] 💬 View & Reply to Enquiries%n");
    
            System.out.println("\n🔐 Account");
            System.out.printf(" [9] 🔑 Change Password");
    
            if (officer.getRegistrationStatus() == null ||
                officer.getRegistrationStatus().equalsIgnoreCase("REJECTED")) {
                System.out.printf("   [10] 🔁 Switch to Applicant Dashboard%n");
            }
    
            System.out.printf("   [0] 🚪 Logout%n");
    
            System.out.print("\n➡️ Enter your choice: ");
            String choice = sc.nextLine().trim();
    
            switch (choice) {
                case "1" -> viewRegistrationStatus(officer);
                case "2" -> registerForProject(officer, sc);
                case "3" -> viewAssignedProjectDetails(officer);
                case "4" -> bookFlat(officer, sc);
                case "5" -> generateReceipt(officer, sc);
                case "6" -> updateLocation(officer, sc);
                case "7" -> addOrUpdateAmenity(officer, sc);
                case "8" -> handleEnquiries(officer, sc);
                case "9" -> {
                    if (AuthService.changePassword(officer, sc)) return;
                }          
                case "10" -> {
                    if (officer.getRegistrationStatus() == null ||
                        officer.getRegistrationStatus().equalsIgnoreCase("REJECTED")) {
                        System.out.println("🔁 Switching to Applicant Dashboard...");
                        ApplicantMenu.show(officer);
                        return;
                    } else {
                        System.out.println("❌ You are not eligible to access the Applicant dashboard.");
                    }
                }
                case "0" -> {
                    AuthService.logout();
                    return;
                }
                default -> System.out.println("❌ Invalid input. Please try again.");
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
    
        List<Project> available = OfficerService.getAvailableProjectsForOfficer(officer);
    
        if (available.isEmpty()) {
            System.out.println("❌ No visible projects available.");
            return;
        }
    
        System.out.println("\n📋 Available Projects:");
        for (int i = 0; i < available.size(); i++) {
            System.out.printf("[%d] %s (%s)\n", i + 1, available.get(i).getProjectName(), available.get(i).getNeighborhood());
        }
    
        System.out.print("Choose project number to register: ");
        try {
            int idx = Integer.parseInt(sc.nextLine().trim()) - 1;
            if (idx < 0 || idx >= available.size()) throw new IndexOutOfBoundsException();
    
            Project selected = available.get(idx);
            boolean registered = OfficerService.registerForProject(officer, selected);
            if (registered) {
                System.out.println("✅ Registration submitted.");
            } else {
                System.out.println("❌ Could not register. Check your current assignment or application status.");
            }
    
        } catch (Exception e) {
            System.out.println("❌ Invalid selection.");
        }
    }
    
    
    private static void viewAssignedProjectDetails(HDBOfficer officer) {
        Project p = OfficerService.getAssignedProject(officer);
        if (p == null) {
            System.out.println("❌ No assigned project.");
            return;
        }
    
        System.out.println("\n📌 Assigned Project Details:");
        System.out.println(OfficerService.getProjectSummary(p, officer));
    
        List<Amenities> amenities = OfficerService.getProjectAmenities(p);
        if (!amenities.isEmpty()) {
            System.out.println("\n🏞️ Nearby Amenities:");
            for (Amenities a : amenities) {
                System.out.println("   - " + a.getAmenityDetails());
            }
        }
    }
    
    

    private static void bookFlat(HDBOfficer officer, Scanner sc) {
        Project assigned = officer.getAssignedProject();
        if (assigned == null) {
            System.out.println("❌ No assigned project.");
            return;
        }
    
        if (HDBOfficer.RegistrationStatusType.PENDING.name().equalsIgnoreCase(officer.getRegistrationStatus())) {
            System.out.println("⚠️ You cannot perform bookings as your registration to this project is still pending approval.");
            return;
        }
    
        List<Applicant> eligible = OfficerService.getBookableApplicants(assigned);
    
        if (eligible.isEmpty()) {
            System.out.println("❌ No applicants ready for booking.");
            return;
        }
    
        System.out.println("\n📋 Eligible Applicants:");
        for (int i = 0; i < eligible.size(); i++) {
            Applicant a = eligible.get(i);
            System.out.printf("[%d] %s (NRIC: %s)\n", i + 1, a.getName(), a.getNric());
        }
    
        System.out.print("Select applicant to book: ");
        try {
            int idx = Integer.parseInt(sc.nextLine().trim()) - 1;
            if (idx < 0 || idx >= eligible.size()) throw new IndexOutOfBoundsException();
    
            Applicant selected = eligible.get(idx);
            boolean success = OfficerService.bookFlatAndGenerateInvoice(officer, selected);
            if (success) {
                System.out.println("✅ Booking successful.");
            } else {
                System.out.println("❌ Booking failed. Please check eligibility and data.");
            }
    
        } catch (Exception e) {
            System.out.println("❌ Invalid booking.");
        }
    }
    
    
    
    private static void handleEnquiries(HDBOfficer officer, Scanner sc) {
        List<Enquiry> projectEnquiries = OfficerService.getPendingEnquiriesForProject(officer);
    
        if (projectEnquiries.isEmpty()) {
            System.out.println("📭 No open enquiries found for your project.");
            return;
        }
    
        System.out.println("\n📬 Enquiries for Project: " + officer.getAssignedProject().getProjectName());
        for (int i = 0; i < projectEnquiries.size(); i++) {
            Enquiry e = projectEnquiries.get(i);
            System.out.printf("[%d] %s: %s\n", i + 1, e.getApplicant().getName(), e.getContent());
        }
    
        System.out.print("Select an enquiry to reply (or 0 to cancel): ");
        try {
            int idx = Integer.parseInt(sc.nextLine().trim());
            if (idx == 0) return;
            if (idx < 1 || idx > projectEnquiries.size()) throw new IndexOutOfBoundsException();
    
            Enquiry selected = projectEnquiries.get(idx - 1);
            System.out.print("Enter your reply: ");
            String reply = sc.nextLine().trim();
    
            boolean ok = OfficerService.replyToEnquiry(selected, officer, reply);
            if (ok) {
                System.out.println("✅ Reply sent and enquiry marked as CLOSED.");
            } else {
                System.out.println("❌ Failed to reply.");
            }
    
        } catch (Exception e) {
            System.out.println("❌ Invalid selection.");
        }
    }
    
    
    
    private static void generateReceipt(HDBOfficer officer, Scanner sc) {
        if (!"APPROVED".equalsIgnoreCase(officer.getRegistrationStatus())) {
            System.out.println("❌ Access denied. Officer registration status must be APPROVED to generate receipts.");
            return;
        }
    
        List<Invoice> awaitingReceipts = OfficerService.getInvoicesAwaitingReceipt(officer);
    
        if (awaitingReceipts.isEmpty()) {
            System.out.println("📭 No paid invoices awaiting receipts.");
            return;
        }
    
        System.out.println("\n📋 Paid Invoices (Awaiting Receipt):");
        for (int i = 0; i < awaitingReceipts.size(); i++) {
            Invoice inv = awaitingReceipts.get(i);
            System.out.printf("[%d] Invoice #%d | %s | %s | $%.2f\n",
                i + 1, inv.getPaymentId(), inv.getApplicantNRIC(), inv.getFlatType(), inv.getAmount());
        }
    
        System.out.print("Select invoice to issue receipt for (0 to cancel): ");
        try {
            int idx = Integer.parseInt(sc.nextLine().trim());
            if (idx == 0) return;
            if (idx < 1 || idx > awaitingReceipts.size()) {
                System.out.println("❌ Invalid selection.");
                return;
            }
    
            Invoice selectedInvoice = awaitingReceipts.get(idx - 1);
            boolean success = OfficerService.generateReceiptForInvoice(officer, selectedInvoice);
    
            if (!success) {
                System.out.println("❌ Receipt generation failed.");
            }
    
        } catch (Exception e) {
            System.out.println("❌ Invalid input.");
        }
    }
    
    private static void updateLocation(HDBOfficer officer, Scanner sc) {
        if (!OfficerService.canUpdateLocation(officer)) {
            System.out.println("❌ Access denied. Officer registration must be APPROVED and a project must be assigned.");
            return;
        }

        Project p = officer.getAssignedProject();
        boolean success = OfficerService.updateProjectLocation(p, sc);
        if (success) {
            System.out.println("✅ Location updated.");
        }
    }
    

    private static void addOrUpdateAmenity(HDBOfficer officer, Scanner sc) {
        if (!OfficerService.canManageAmenities(officer)) {
            System.out.println("❌ Access denied. Officer registration status must be APPROVED to manage amenities.");
            return;
        }
    
        OfficerService.manageAmenityInteraction(officer.getAssignedProject(), sc);
    } 

}
