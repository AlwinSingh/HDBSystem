package src.service;

import java.util.*;
import src.model.*;

/**
 * Displays the menu and handles input actions for logged-in applicants.
 * This includes applications, enquiries, payments, feedback, and account actions.
 */
public class ApplicantMenu {

    /**
     * Shows the main dashboard menu for the applicant and  and routes input to the correct actions.
     *
     * @param applicant The currently logged-in applicant.
     */
    public static void show(Applicant applicant) {
        Scanner sc = new Scanner(System.in);
    
        while (true) {
            boolean isOfficer = applicant instanceof HDBOfficer;
    
            System.out.println("\n===== 🏠 Applicant Dashboard =====");
            System.out.println("Welcome, " + applicant.getName());
    
            System.out.println("\n📋 Applications");
            System.out.printf(" [1] 📄 View Eligible Projects   [2] 📝 Apply for a Project%n");
            System.out.printf(" [3] 🔍 View My Application      [4] ❌ Request Withdrawal%n");
    
            System.out.println("\n💳 Payments");
            System.out.printf(" [5] 💰 View & Pay Invoice       [6] 🧾 View Receipts%n");
    
            System.out.println("\n📬 Services");
            System.out.printf(" [7] 💬 Enquiry Services         [8] 📝 Feedback Services%n");
    
            System.out.println("\n🔐 Account");
            System.out.printf(" [9] 🔑 Change Password");
    
            // ✅ Always show Officer Dashboard switch if user is an officer
            if (isOfficer) {
                System.out.printf("   [10] 🔁 Switch to Officer Dashboard%n");
            }
    
            System.out.printf("%n[0] 🚪 Logout%n");
    
            System.out.print("\n➡️ Enter your choice: ");
            String choice = sc.nextLine().trim();
    
            switch (choice) {
                case "1" -> ApplicantService.handleViewEligibleProjects(applicant, sc);
                case "2" -> applyForProject(new ApplicantContext(applicant, sc));
                case "3" -> viewingApplication(new ApplicantContext(applicant, sc));
                case "4" -> requestWithdrawal(new ApplicantContext(applicant, sc));
                case "5" -> viewAndPayInvoices(applicant, sc);
                case "6" -> viewReceipts(applicant);
                case "7" -> handleEnquiries(new ApplicantContext(applicant, sc));
                case "8" -> showFeedbackServices(applicant, sc);
                case "9" -> changePassword(new ApplicantContext(applicant, sc));
                case "10" -> {
                    if (isOfficer) {
                        System.out.println("🔁 Switching to Officer Dashboard...");
                        OfficerMenu.show((HDBOfficer) applicant);
                        return;
                    } else {
                        System.out.println("❌ You are not eligible to access the Officer dashboard.");
                    }
                }
                case "0" -> {
                    applicant.logout();
                    return;
                }
                default -> System.out.println("❌ Invalid input. Please try again.");
            }
        }
    }
    
    

    /**
     * Guides the applicant through applying to a project and selecting a flat type.
     */
    private static void applyForProject(ApplicantContext ctx) {
        Applicant applicant = ctx.applicant;
        Scanner sc = ctx.scanner;
    
        if (applicant.getApplication() != null) {
            System.out.println("⚠️ You already have an active application for: "
                    + applicant.getApplication().getProject().getProjectName()
                    + " (Status: " + applicant.getApplication().getStatus() + ")");
            return;
        }
    
        List<Project> eligible = ApplicantService.getEligibleProjects(applicant);
        if (eligible.isEmpty()) {
            System.out.println("❌ No eligible projects available.");
            return;
        }
    
        for (int i = 0; i < eligible.size(); i++) {
            Project p = eligible.get(i);
            System.out.printf("[%d] %s (%s)\n", i + 1, p.getProjectName(), p.getNeighborhood());
        }
    
        System.out.print("Enter project number to apply: ");
        int choice = Integer.parseInt(sc.nextLine().trim()) - 1;
        if (choice < 0 || choice >= eligible.size()) {
            System.out.println("❌ Invalid selection.");
            return;
        }
    
        Project selected = eligible.get(choice);
    
        // 🧠 Officer conflict check (moved here)
        if (applicant instanceof HDBOfficer officer) {
            Project assigned = officer.getAssignedProject();
            String status = officer.getRegistrationStatus();
    
            if (assigned != null &&
                assigned.getProjectName().equalsIgnoreCase(selected.getProjectName()) &&
                "PENDING".equalsIgnoreCase(status)) {
                System.out.println("❌ You are already registering to this project as an officer.");
                return;
            }
        }
    
        String flatType = "2-Room";
        if ("Married".equalsIgnoreCase(applicant.getMaritalStatus())) {
            System.out.print("Choose flat type (2-Room/3-Room): ");
            flatType = sc.nextLine().trim();
        }
    
        System.out.print("Submit application for " + selected.getProjectName()
                + " (" + flatType + ")? (Y/N): ");
        if (!sc.nextLine().trim().equalsIgnoreCase("Y")) {
            System.out.println("🔁 Application cancelled.");
            return;
        }
    
        boolean ok = ApplicantService.submitApplication(applicant, selected, flatType);
        if (ok) {
            System.out.println("✅ Application submitted. Status: " + Applicant.AppStatusType.PENDING.name() + ".");
        } else {
            System.out.println("❌ Application failed.");
        }
    }
    

    /**
     * Shows detailed information about the applicant's current application.
     */
    private static void viewingApplication(ApplicantContext ctx) {
        Application app = ctx.applicant.getApplication();
        if (app == null) {
            System.out.println("❌ No application submitted.");
            return;
        }

        Project p = app.getProject();

        System.out.println("\n📄 ===== Application Details =====");
        System.out.println("🏠 Project Name      : " + p.getProjectName());
        System.out.println("📍 Neighborhood      : " + p.getNeighborhood());
        System.out.println("📍 Address           : " + p.getLocation().getAddress());
        System.out.println("🌆 District & Town   : " + p.getLocation().getDistrict() + ", " + p.getLocation().getTown());
        System.out.printf ("🗺️  Coordinates       : %.6f, %.6f\n", p.getLocation().getLat(), p.getLocation().getLng());
        System.out.println("📅 Application Period: " + p.getOpenDate() + " to " + p.getCloseDate());

        System.out.println("\n🏢 Flat Type Chosen  : " + app.getFlatType());
        System.out.println("💰 Price (2-Room)    : $" + String.format("%.2f", p.getPrice2Room()));
        System.out.println("💰 Price (3-Room)    : $" + String.format("%.2f", p.getPrice3Room()));
        System.out.println("📌 Application Status: " + (
                Applicant.AppStatusType.WITHDRAW_REQUESTED.name().equalsIgnoreCase(app.getStatus())
                        ? Applicant.AppStatusType.WITHDRAW_REQUESTED.name() + " (Pending review)"
                        : app.getStatus()));

        if (!p.getAmenities().isEmpty()) {
            System.out.println("\n🏞️ Nearby Amenities:");
            for (Amenities a : p.getAmenities()) {
                System.out.println("   - " + a.toString());
            }
        }

        System.out.println("==================================");
    }

    /**
     * Allows an applicant to request withdrawal from their submitted application.
     */
    private static void requestWithdrawal(ApplicantContext ctx) {
        Scanner sc = ctx.scanner;
        Applicant applicant = ctx.applicant;

        if (!ApplicantService.canWithdraw(applicant)) {
            return;
        }

        System.out.print("Confirm withdrawal? (Y/N): ");
        if (!sc.nextLine().trim().equalsIgnoreCase("Y")) {
            System.out.println("🔁 Withdrawal cancelled.");
            return;
        }

        ApplicantService.submitWithdrawalRequest(applicant);
        System.out.println("✅ Withdrawal request submitted.");
    }

    /**
     * Displays the enquiry service submenu and routes to submit, view, edit, or delete enquiries.
     */
    private static void handleEnquiries(ApplicantContext ctx) {
        Scanner sc = ctx.scanner;
        Applicant applicant = ctx.applicant;

        while (true) {
            System.out.println("\n===== 📨 Enquiry Services =====");
            System.out.println("1. Submit an enquiry");
            System.out.println("2. View my enquiries");
            System.out.println("3. Edit an enquiry");
            System.out.println("4. Delete an enquiry");
            System.out.println("0. Back");

            System.out.print("➡️ Enter your choice: ");
            String input = sc.nextLine().trim();

            switch (input) {
                case "1" -> EnquireService.submitEnquiry(applicant, sc);
                case "2" -> EnquireService.viewOwnEnquiries(applicant);
                case "3" -> EnquireService.editOwnEnquiry(applicant, sc);
                case "4" -> EnquireService.deleteOwnEnquiry(applicant, sc);
                case "0" -> {
                    System.out.println("🔙 Returning...");
                    return;
                }
                default -> System.out.println("❌ Invalid choice.");
            }
        }
    }

    /**
     * Displays feedback options for submitting and viewing feedback.
     */
    private static void showFeedbackServices(Applicant applicant, Scanner sc) {
        while (true) {
            System.out.println("\n📝 Feedback Services");
            System.out.println(" [1] Submit Feedback");
            System.out.println(" [2] View My Feedback");
            System.out.println(" [0] Back");
            System.out.print("➡️ Enter your choice: ");
            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1" -> {
                    System.out.print("✉️ Enter your feedback message: ");
                    String message = sc.nextLine().trim();
                    if (message.isBlank()) {
                        System.out.println("❌ Feedback cannot be empty.");
                        break;
                    }
                    boolean ok = ApplicantService.submitFeedback(applicant, message);
                    if (!ok) {
                        System.out.println("❌ You must have an active application to submit feedback.");
                    }
                }

                case "2" -> {
                    List<Feedback> myFeedback = FeedbackService.getFeedbackByApplicant(applicant.getNric());
                    FeedbackService.printFeedbackList(myFeedback);
                }

                case "0" -> {
                    System.out.println("🔙 Returning to applicant dashboard...");
                    return;
                }

                default -> System.out.println("❌ Invalid input. Please choose from the menu.");
            }
        }
    }

    /**
     * Shows a list of unpaid invoices and handles user selection for payment.
     */
    private static void viewAndPayInvoices(Applicant applicant, Scanner sc) {
        List<Invoice> allInvoices = InvoiceService.getAllInvoices().stream()
            .filter(inv -> inv.getApplicantNRIC().equalsIgnoreCase(applicant.getNric()))
            .toList();

        if (allInvoices.isEmpty()) {
            System.out.println("📭 You have no invoices at the moment.");
            return;
        }

        List<Invoice> unpaidInvoices = allInvoices.stream()
            .filter(inv -> !"Awaiting Receipt".equalsIgnoreCase(inv.getStatus()))
            .toList();

        if (unpaidInvoices.isEmpty()) {
            System.out.println("✅ All your invoices have already been paid.");
            return;
        }

        System.out.println("\n📄 Your Unpaid Invoices:");
        for (int i = 0; i < unpaidInvoices.size(); i++) {
            Invoice inv = unpaidInvoices.get(i);
            System.out.printf("[%d] Invoice #%d | Flat: %s | Amount: $%.2f | Status: %s\n",
                    i + 1, inv.getPaymentId(), inv.getFlatType(), inv.getAmount(), inv.getStatus());
        }

        System.out.print("Enter invoice number to pay (or 0 to cancel): ");
        try {
            int idx = Integer.parseInt(sc.nextLine().trim());
            if (idx == 0) return;
            if (idx < 1 || idx > unpaidInvoices.size()) {
                System.out.println("❌ Invalid selection.");
                return;
            }

            Invoice selected = unpaidInvoices.get(idx - 1);

            System.out.println("Choose payment method:");
            System.out.println("1. PayNow");
            System.out.println("2. Bank Transfer");
            System.out.println("3. Credit Card");
            System.out.print("➡️ Enter your choice: ");
            int methodChoice = Integer.parseInt(sc.nextLine().trim());

            PaymentMethod method = null;
            switch (methodChoice) {
                case 1 -> method = PaymentMethod.PAYNOW;
                case 2 -> method = PaymentMethod.BANK_TRANSFER;
                case 3 -> method = PaymentMethod.CREDIT_CARD;
                default -> System.out.println("❌ Invalid payment method.");
            }

            if (method == null) return;

            ApplicantService.processInvoicePayment(applicant, selected, method);

        } catch (Exception e) {
            System.out.println("❌ Invalid input.");
        }
    }


    /**
     * Displays all receipts issued to the applicant.
     */
    private static void viewReceipts(Applicant applicant) {
        List<Receipt> myReceipts = ApplicantService.getReceiptsByApplicant(applicant);

        if (myReceipts.isEmpty()) {
            System.out.println("📭 No receipts found for your account.");
            return;
        }

        System.out.println("\n📑 Your Receipts:");
        for (Receipt r : myReceipts) {
            System.out.println("────────────────────────────");
            System.out.println("📄 Receipt for Project: " + r.getProjectName());
            System.out.println("🏠 Flat Type          : " + r.getInvoice().getFlatType());
            System.out.println("💵 Amount Paid        : $" + String.format("%.2f", r.getInvoice().getAmount()));
            PaymentMethod method = r.getInvoice().getMethod();
            System.out.println("💳 Payment Method     : " + (method != null ? method : "Not selected"));
            System.out.println("📅 Date               : " + r.getInvoice().getDate());
        }
    }

    /**
     * Container class that groups an applicant with a scanner instance.
     */
    private static class ApplicantContext {
        Applicant applicant;
        Scanner scanner;

        public ApplicantContext(Applicant applicant, Scanner scanner) {
            this.applicant = applicant;
            this.scanner = scanner;
        }
    }

    /**
     * Prompts the applicant to change their password using the AuthService.
     */
    private static void changePassword(ApplicantContext ctx) {
        if (AuthService.changePassword(ctx.applicant, ctx.scanner)) {
            System.exit(0);
        }
    }
}
