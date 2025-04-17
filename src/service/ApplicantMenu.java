package src.service;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import src.model.*;
import src.util.ApplicantCsvMapper;
import src.util.ProjectCsvMapper;

public class ApplicantMenu {

    private static final String APPLICANT_PATH = "data/ApplicantList.csv";
    private static final String PROJECT_PATH   = "data/ProjectList.csv";

    private static final Map<String, Consumer<ApplicantContext>> menuOptions = new LinkedHashMap<>() {{
        put("1", ApplicantMenu::viewEligibleProjects);
        put("2", ApplicantMenu::applyForProject);
        put("3", ApplicantMenu::viewApplication);
        put("4", ApplicantMenu::requestWithdrawal);
        put("5", ApplicantMenu::handleEnquiries);
        put("6", ctx -> viewAndPayInvoices(ctx.applicant, ctx.scanner)); // ✅ Connect menu 6 properly
        put("7", ApplicantMenu::changePassword);
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
            System.out.println("5. Enquiry Services");
            System.out.println("6. View and Pay Invoice");
            System.out.println("7. Change Password");
            if (isOfficer) System.out.println("9. Back to Officer Dashboard");
            System.out.println("0. Logout");
            System.out.print("Enter choice: ");

            String choice = sc.nextLine().trim();

            if (choice.equals("0")) {
                System.out.println("🚪 Logged out.\n");
                return;
            } else if (choice.equals("9") && isOfficer) {
                System.out.println("🔙 Returning to Officer Dashboard...");
                OfficerMenu.show((HDBOfficer) applicant);
                return;
            } else if (menuOptions.containsKey(choice)) {
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
            ApplicantCsvMapper.updateApplicant(APPLICANT_PATH, applicant);

            // also record this applicant in the project
            selected.getApplicantNRICs().add(applicant.getNric());
            ProjectCsvMapper.updateProject(PROJECT_PATH, selected);

            System.out.println("✅ Application submitted. Status: PENDING.");
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
        System.out.println("\n📄 Application Details");
        System.out.println("🏠 Project   : " + p.getProjectName() + " (" + p.getNeighborhood() + ")");
        System.out.println("🏢 Flat Type : " + app.getFlatType());
        System.out.println("📌 Status    : " + ("WITHDRAWAL_REQUESTED".equalsIgnoreCase(app.getStatus()) ?
                           "WITHDRAWAL REQUESTED (Pending review)" : app.getStatus()));
    }

    private static void requestWithdrawal(ApplicantContext ctx) {
        Scanner sc = ctx.scanner;
        Application app = ctx.applicant.getApplication();

        if (app == null) {
            System.out.println("❌ No application to withdraw.");
            return;
        }

        if ("WITHDRAWAL_REQUESTED".equalsIgnoreCase(app.getStatus())) {
            System.out.println("ℹ️ Withdrawal already requested.");
            return;
        }

        if ("BOOKED".equalsIgnoreCase(app.getStatus())) {
            System.out.println("❌ You cannot withdraw after booking.");
            return;
        }

        System.out.print("Confirm withdrawal? (Y/N): ");
        if (!sc.nextLine().trim().equalsIgnoreCase("Y")) {
            System.out.println("🔁 Withdrawal cancelled.");
            return;
        }

        app.setStatus("WITHDRAWAL_REQUESTED");
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

            System.out.print("Enter your choice: ");
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

    private static void viewAndPayInvoices(Applicant applicant, Scanner sc) {
        List<Invoice> allInvoices = InvoiceService.loadAll();
        List<Invoice> myInvoices = allInvoices.stream()
            .filter(inv -> inv.getApplicantNRIC().equalsIgnoreCase(applicant.getNric()))
            .toList();
    
        if (myInvoices.isEmpty()) {
            System.out.println("📭 No invoices found for your account.");
            return;
        }
    
        System.out.println("\n📄 Your Invoices:");
        for (int i = 0; i < myInvoices.size(); i++) {
            Invoice inv = myInvoices.get(i);
            System.out.printf("[%d] Invoice #%d | Flat: %s | Amount: $%.2f | Status: %s\n",
                i + 1, inv.getPaymentId(), inv.getFlatType(), inv.getAmount(), inv.getStatus());
        }
    
        System.out.print("Enter invoice number to pay (or 0 to cancel): ");
        try {
            int idx = Integer.parseInt(sc.nextLine().trim());
            if (idx == 0) return;
            if (idx < 1 || idx > myInvoices.size()) throw new Exception();
    
            Invoice selected = myInvoices.get(idx - 1);
            if ("Processed".equalsIgnoreCase(selected.getStatus())) {
                System.out.println("✅ This invoice has already been paid.");
                return;
            }
    
            // Payment method selection
            System.out.println("Choose payment method:");
            System.out.println("1. PayNow");
            System.out.println("2. Bank Transfer");
            System.out.println("3. Credit Card");
            System.out.print("Enter choice: ");
            int methodChoice = Integer.parseInt(sc.nextLine().trim());
            String method;
            switch (methodChoice) {
                case 1 -> method = "PayNow";
                case 2 -> method = "Bank Transfer";
                case 3 -> method = "Credit Card";
                default -> {
                    System.out.println("❌ Invalid payment method.");
                    return;
                }
            } 
            selected.setMethod(method);
            selected.setStatus("Processed");
            InvoiceService.updateInvoice(selected); // Save changes
    
            System.out.println("💸 Payment successful via " + method + "!");
        } catch (Exception e) {
            System.out.println("❌ Invalid input.");
        }
    }
    

    private static void saveApplicantUpdate(Applicant updatedApplicant) {
        List<Applicant> all = ApplicantCsvMapper.loadAll("data/ApplicantList.csv");
        for (int i = 0; i < all.size(); i++) {
            if (all.get(i).getNric().equalsIgnoreCase(updatedApplicant.getNric())) {
                all.set(i, updatedApplicant);
                break;
            }
        }
        ApplicantCsvMapper.saveAll("data/ApplicantList.csv", all);
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