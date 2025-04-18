package src.service;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import src.model.*;
import src.util.ApplicantCsvMapper;
import src.util.ProjectCsvMapper;

public class ApplicantMenu {

    private static final Map<String, Consumer<ApplicantContext>> menuOptions = new LinkedHashMap<>() {{
        put("1", ApplicantMenu::viewEligibleProjects);
        put("2", ApplicantMenu::applyForProject);
        put("3", ApplicantMenu::viewApplication);
        put("4", ApplicantMenu::requestWithdrawal);
        put("5", ctx -> viewAndPayInvoices(ctx.applicant, ctx.scanner));
        put("6", ctx -> viewReceipts(ctx.applicant)); 
        put("7", ApplicantMenu::handleEnquiries);
        put("8", ctx -> ApplicantMenu.showFeedbackServices(ctx.applicant, ctx.scanner));
        put("9", ApplicantMenu::changePassword);
    }};
    

    public static void show(Applicant applicant) {
        Scanner sc = new Scanner(System.in);
        boolean isOfficer = applicant instanceof HDBOfficer;

        while (true) {
            System.out.println("\n===== 🏠 Applicant Dashboard =====");
            System.out.println("Welcome, " + applicant.getName());
            System.out.println("1. View eligible open projects");
            System.out.println("2. Apply for a project");
            System.out.println("3. View my application");
            System.out.println("4. Request withdrawal");
            System.out.println("5. View and Pay Invoice");
            System.out.println("6. View Receipts");
            System.out.println("7. Enquiry Services");
            System.out.println("8. Feedback Services");
            System.out.println("9. Change Password");
            
            if (isOfficer) System.out.println("10. Switch to Officer Dashboard");
            System.out.println("0. Logout");
            System.out.print("➡️ Enter your choice: ");

            String choice = sc.nextLine().trim();

            if (choice.equals("0")) {
                AuthService.logout();
                return;
            }  else if (menuOptions.containsKey(choice)) {
                menuOptions.get(choice).accept(new ApplicantContext(applicant, sc));
            } else {
                System.out.println("❌ Invalid input.");
            }
        }
    }

    private static void viewEligibleProjects(ApplicantContext ctx) {
        List<Project> projects = ProjectLoader.loadProjects();
        Applicant applicant = ctx.applicant;
        System.out.println("\n📋 Eligible Open Projects:");
        boolean anyShown = false;
    
        for (Project p : projects) {
            if (p != null && p.getProjectName() != null && isEligible(applicant, p) && p.isVisible()) {
                anyShown = true;
                System.out.println("────────────────────────────");
                System.out.println("🏠 Project Name      : " + p.getProjectName());
                System.out.println("📍 Location          : " + p.getNeighborhood());
                System.out.println("🏙️ District & Town   : " + p.getLocation().getDistrict() + ", " + p.getLocation().getTown());
                System.out.println("🗺️ Address           : " + p.getLocation().getAddress());
                System.out.println("📅 Application Period: " + p.getOpenDate() + " to " + p.getCloseDate());
                System.out.println("🏢 2-Room Units      : " + p.getRemainingFlats("2-Room") + " ($" + p.getPrice2Room() + ")");
                if (applicant.getMaritalStatus().equalsIgnoreCase("Married")) {
                    System.out.println("🏢 3-Room Units      : " + p.getRemainingFlats("3-Room") + " ($" + p.getPrice3Room() + ")");
                }
                if (!p.getAmenities().isEmpty()) {
                    System.out.println("🏞️ Nearby Amenities:");
                    for (Amenities a : p.getAmenities()) {
                        System.out.println("   - " + a.getAmenityDetails());
                    }
                }
                System.out.println(); // extra line break between entries
            }
        }
    
        if (!anyShown) {
            System.out.println("❌ No eligible open projects available at the moment.");
        }
    }
    

    private static boolean isEligible(Applicant applicant, Project project) {
        String status = applicant.getMaritalStatus();
        int age = applicant.getAge();
        boolean withinDateRange = !LocalDate.now().isBefore(project.getOpenDate())
                                && !LocalDate.now().isAfter(project.getCloseDate());
        return withinDateRange && status != null &&
               ((status.equalsIgnoreCase("Single") && age >= 35) ||
                (status.equalsIgnoreCase("Married") && age >= 21));
    }

    private static void applyForProject(ApplicantContext ctx) {
        Applicant applicant = ctx.applicant;
        Scanner sc = ctx.scanner;

        // 1️⃣ Prevent double‐apply
        if (applicant.getApplication() != null) {
            System.out.println("⚠️ You already have an active application for: " 
                + applicant.getApplication().getProject().getProjectName()
                + " (Status: " + applicant.getApplication().getStatus() + ")");
            return;
        }

        // 2️⃣ Build eligible list
        List<Project> eligible = ProjectLoader.loadProjects().stream()
            .filter(p -> isEligible(applicant, p) && p.isVisible())
            .collect(Collectors.toList());
        if (eligible.isEmpty()) {
            System.out.println("❌ No eligible projects available.");
            return;
        }

        // 3️⃣ Prompt choice…
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

        // 4️⃣ Flat type
        String flatType = "2-Room";
        if ("Married".equalsIgnoreCase(applicant.getMaritalStatus())) {
            System.out.print("Choose flat type (2-Room/3-Room): ");
            flatType = sc.nextLine().trim();
        }

        // 5️⃣ Confirm
        System.out.print("Submit application for " + selected.getProjectName()
            + " (" + flatType + ")? (Y/N): ");
        if (!sc.nextLine().trim().equalsIgnoreCase("Y")) {
            System.out.println("🔁 Application cancelled.");
            return;
        }

        // 6️⃣ Business logic + persistence
        boolean ok = applicant.applyForProject(selected, flatType);
        if (ok) {
            // update applicant CSV
            ApplicantCsvMapper.updateApplicant(applicant);

            // also record this applicant in the project
            selected.getApplicantNRICs().add(applicant.getNric());
            ProjectCsvMapper.updateProject(selected);

            System.out.println("✅ Application submitted. Status: " + Applicant.AppStatusType.PENDING.name() + ".");
        } else {
            System.out.println("❌ Application failed.");
        }
    }


    private static void viewApplication(ApplicantContext ctx) {
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
                System.out.println("   - " + a.getAmenityDetails());
            }
        }
    
        System.out.println("==================================");
    }
    

    private static void requestWithdrawal(ApplicantContext ctx) {
        Scanner sc = ctx.scanner;
        Application app = ctx.applicant.getApplication();

        if (app == null) {
            System.out.println("❌ No application to withdraw.");
            return;
        }

        if (Applicant.AppStatusType.WITHDRAW_REQUESTED.name().equalsIgnoreCase(app.getStatus())) {
            System.out.println("ℹ️ Withdrawal already requested.");
            return;
        }

        if (Applicant.AppStatusType.BOOKED.name().equalsIgnoreCase(app.getStatus())) {
            System.out.println("❌ You cannot withdraw after booking.");
            return;
        }

        System.out.print("Confirm withdrawal? (Y/N): ");
        if (!sc.nextLine().trim().equalsIgnoreCase("Y")) {
            System.out.println("🔁 Withdrawal cancelled.");
            return;
        }

        app.setStatus(Applicant.AppStatusType.WITHDRAW_REQUESTED.name());
        saveApplicantUpdate(ctx.applicant);
        System.out.println("✅ Withdrawal request submitted.");
    }

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
                    if (applicant.getApplication() == null || applicant.getApplication().getProject() == null) {
                        System.out.println("❌ You must have an active application to submit feedback.");
                        break;
                    }
    
                    System.out.print("✉️ Enter your feedback message: ");
                    String message = sc.nextLine().trim();
    
                    if (message.isBlank()) {
                        System.out.println("❌ Feedback cannot be empty.");
                        break;
                    }
    
                    String projectName = applicant.getApplication().getProject().getProjectName();
                    FeedbackService.submitFeedback(applicant.getNric(), message, projectName);
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
    
    
    

    private static void viewAndPayInvoices(Applicant applicant, Scanner sc) {
        List<Invoice> allInvoices = InvoiceService.loadAll();
        List<Invoice> myInvoices = allInvoices.stream()
            .filter(inv -> inv.getApplicantNRIC().equalsIgnoreCase(applicant.getNric()))
            .toList();
    
        if (myInvoices.isEmpty()) {
            System.out.println("📭 No invoices found for your account.");
            return;
        }
    
        // Filter unpaid invoices
        List<Invoice> unpaidInvoices = myInvoices.stream()
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
            if (idx < 1 || idx > unpaidInvoices.size()) throw new Exception();
    
            Invoice selected = unpaidInvoices.get(idx - 1);
    
            // Payment method selection
            System.out.println("Choose payment method:");
            System.out.println("1. PayNow");
            System.out.println("2. Bank Transfer");
            System.out.println("3. Credit Card");
            System.out.print("➡️ Enter your choice: ");
            int methodChoice = Integer.parseInt(sc.nextLine().trim());
    
            PaymentMethod method;
            switch (methodChoice) {
                case 1 -> method = PaymentMethod.PAYNOW;
                case 2 -> method = PaymentMethod.BANK_TRANSFER;
                case 3 -> method = PaymentMethod.CREDIT_CARD;
                default -> {
                    System.out.println("❌ Invalid payment method.");
                    return;
                }
            }
    
            selected.setMethod(method);
            selected.setStatus("Awaiting Receipt");
            InvoiceService.updateInvoice(selected);
    
            Payment newPayment = new Payment(
                selected.getPaymentId(),
                selected.getAmount(),
                LocalDate.now(),
                method,
                selected.getStatus()
            );
            PaymentService.addPayment(newPayment);
            System.out.println("💸 Payment successful via " + method + "!");
        } catch (Exception e) {
            System.out.println("❌ Invalid input.");
        }
    }
    
    
    
    
    private static void viewReceipts(Applicant applicant) {
        List<Receipt> allReceipts = ReceiptService.getAllReceipts();
        List<Receipt> myReceipts = allReceipts.stream()
            .filter(r -> r.getApplicantNRIC().equalsIgnoreCase(applicant.getNric()))
            .toList();
    
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
            System.out.println("💳 Payment Method     : " + r.getInvoice().getMethod());
            System.out.println("📅 Date               : " + r.getInvoice().getDate());
        }
    }
    

    private static void saveApplicantUpdate(Applicant updatedApplicant) {
        List<Applicant> all = ApplicantCsvMapper.loadAll();
        for (int i = 0; i < all.size(); i++) {
            if (all.get(i).getNric().equalsIgnoreCase(updatedApplicant.getNric())) {
                all.set(i, updatedApplicant);
                break;
            }
        }
        ApplicantCsvMapper.saveAll(all);
    }

    private static class ApplicantContext {
        Applicant applicant;
        Scanner scanner;

        public ApplicantContext(Applicant applicant, Scanner scanner) {
            this.applicant = applicant;
            this.scanner = scanner;
        }
    }
    private static void changePassword(ApplicantContext ctx) {
        AuthService.changePassword(ctx.applicant, ctx.scanner);
    }

    
    
}