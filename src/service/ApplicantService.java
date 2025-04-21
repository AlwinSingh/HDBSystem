package src.service;

import src.model.*;
import src.repository.ApplicantRepository;
import src.repository.ProjectRepository;
import src.util.ApplicantCsvMapper;
import src.util.ProjectCsvMapper;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * Handles applicant-related operations including applications, withdrawals,
 * feedback, invoices, payments, and receipts.
 */
public class ApplicantService {
    private static final ApplicantRepository applicantRepository = new ApplicantCsvMapper();
    private static final ProjectRepository projectRepository = new ProjectCsvMapper();
    private static String filterNeighborhood = null;
    private static String filterDistrict = null;
    private static String filterFlatType = null;

    /**
     * Checks if an applicant is eligible to apply for a given project.
     *
     * @param applicant The applicant in question.
     * @param project   The project being considered.
     * @return True if the applicant meets the age and marital criteria; false otherwise.
     */
    public static boolean isEligible(Applicant applicant, Project project) {
        String status = applicant.getMaritalStatus();
        int age = applicant.getAge();

        boolean withinDateRange = !LocalDate.now().isBefore(project.getOpenDate())
                && !LocalDate.now().isAfter(project.getCloseDate());

        if (status == null || !withinDateRange) return false;

        return (status.equalsIgnoreCase("Single") && age >= 35)
                || (status.equalsIgnoreCase("Married") && age >= 21);
    }

    /**
     * Displays key information about the selected project in a user-friendly format.
     *
     * @param p         The project to display.
     * @param applicant The applicant viewing the project.
     */
    public static void displayProjectDetails(Project p, Applicant applicant) {
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
                System.out.println("   - " + a.toString());
            }
        }
        System.out.println();
    }

    /**
     * Displays and filters eligible projects, allowing the user to apply filter options.
     *
     * @param applicant The current applicant.
     * @param sc        Scanner for user input.
     */
    public static void handleViewEligibleProjects(Applicant applicant, Scanner sc) {
        while (true) {
            List<Project> filtered = ApplicantService.getFilteredEligibleProjects(
                    applicant,
                    filterNeighborhood,
                    filterDistrict,
                    filterFlatType
            );

            System.out.println("\n📋 Eligible Open Projects:");
            if (filtered.isEmpty()) {
                System.out.println("❌ No eligible projects found for current filters.");
            } else {
                for (Project p : filtered) {
                    ApplicantService.displayProjectDetails(p, applicant);
                }
            }

            System.out.println("\n===== 🔧 Filter Options =====");
            System.out.println(" [1] Apply Filter");
            System.out.println(" [2] Clear Filters");
            System.out.println(" [0] Back");
            System.out.print("➡️ Enter your choice: ");
            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1" -> applyProjectFilters(sc);
                case "2" -> {
                    filterNeighborhood = null;
                    filterDistrict = null;
                    filterFlatType = null;
                    System.out.println("✅ Filters cleared.");
                }
                case "0" -> {
                    System.out.println("🔙 Returning to dashboard...");
                    return;
                }
                default -> System.out.println("❌ Invalid input.");
            }
        }
    }

    /**
     * Prompts the user to input project filter options.
     *
     * @param sc Scanner for input.
     */
    public static void applyProjectFilters(Scanner sc) {
        System.out.print("🏘️  Neighborhood [" + optional(filterNeighborhood) + "]: ");
        String n = sc.nextLine().trim();
        if (!n.isBlank()) filterNeighborhood = n;

        System.out.print("🏙️  District [" + optional(filterDistrict) + "]: ");
        String d = sc.nextLine().trim();
        if (!d.isBlank()) filterDistrict = d;

        System.out.print("🏢 Flat Type (2-Room / 3-Room) [" + optional(filterFlatType) + "]: ");
        String f = sc.nextLine().trim();
        if (!f.isBlank()) filterFlatType = f;
    }

    /**
     * Returns a user-friendly placeholder for filter prompts.
     */
    private static String optional(String value) {
        return value == null ? "Any" : value;
    }

    /**
     * Gets all projects that the applicant is currently eligible for.
     *
     * @param applicant The applicant in question.
     * @return A list of eligible projects.
     */
    public static List<Project> getEligibleProjects(Applicant applicant) {
        return ProjectLoader.loadProjects().stream()
                .filter(p -> p != null && p.getProjectName() != null)
                .filter(Project::isVisible)
                .filter(p -> isEligible(applicant, p))
                .collect(Collectors.toList());
    }

    /**
     * Returns eligible projects based on optional filters.
     */
    public static List<Project> getFilteredEligibleProjects(
            Applicant applicant,
            String neighborhood,
            String district,
            String flatType
    ) {
        return ProjectLoader.loadProjects().stream()
                .filter(p -> p != null && p.getProjectName() != null)
                .filter(Project::isVisible)
                .filter(p -> isEligible(applicant, p))
                .filter(p -> neighborhood == null || p.getNeighborhood().equalsIgnoreCase(neighborhood))
                .filter(p -> district == null || p.getLocation().getDistrict().equalsIgnoreCase(district))
                .filter(p -> flatType == null || p.getRemainingFlats(flatType) > 0)
                .sorted(Comparator.comparing(Project::getProjectName))
                .toList();
    }

    /**
     * Guides the applicant through applying to a project and selecting a flat type.
     */
    public static void applyForProject(Applicant applicant, Scanner sc){
    
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
    
        if (applicant.isOfficer()) {
            HDBOfficer officer = (HDBOfficer) applicant;
            Project assigned = officer.getAssignedProject();
            String status = officer.getRegistrationStatus();

            if (assigned != null &&
                assigned.getProjectName().equalsIgnoreCase(selected.getProjectName()) &&
                ("PENDING".equalsIgnoreCase(status) || "APPROVED".equalsIgnoreCase(status))) {
                System.out.println("❌ You are already handling this project as an officer.");
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
     * Applies for a selected project with the chosen flat type and saves updates to CSV.
     *
     * @return True if successful; false if already applied or any issues occur.
     */
    public static boolean submitApplication(Applicant applicant, Project project, String flatType) {
        if (applicant instanceof HDBOfficer officer) {
            boolean exists = applicantRepository.loadAll().stream()
                .anyMatch(a -> a.getNric().equalsIgnoreCase(officer.getNric()));
            if (!exists) {
                applicantRepository.save(officer);
            }
        }
    
        boolean success = applicant.applyForProject(project, flatType);
        if (!success) return false;
    
        applicantRepository.update(applicant);
    
        project.getApplicantNRICs().add(applicant.getNric());
        projectRepository.updateProject(project);
    
        return true;
    }
    
    /**
     * Checks if the applicant is allowed to withdraw from their application.
     */
    public static boolean canWithdraw(Applicant applicant) {
        Application app = applicant.getApplication();
        if (app == null) {
            System.out.println("❌ No application to withdraw.");
            return false;
        }

        String status = app.getStatus();
        if (Applicant.AppStatusType.WITHDRAW_REQUESTED.name().equalsIgnoreCase(status)) {
            System.out.println("ℹ️ Withdrawal already requested.");
            return false;
        }

        if (Applicant.AppStatusType.BOOKED.name().equalsIgnoreCase(status)) {
            System.out.println("❌ You cannot withdraw after booking.");
            return false;
        }

        return true;
    }

    /**
     * Flags the applicant's application for withdrawal and persists the change.
     */
    public static void submitWithdrawalRequest(Applicant applicant) {
        applicant.getApplication().setStatus(Applicant.AppStatusType.WITHDRAW_REQUESTED.name());
        applicantRepository.update(applicant);

    }

    /**
     * Submits a feedback message for the applicant’s current project.
     *
     * @return True if feedback was accepted; false if applicant has no active project.
     */
    public static boolean submitFeedback(Applicant applicant, String message) {
        if (applicant.getApplication() == null || applicant.getApplication().getProject() == null) {
            return false;
        }
        String projectName = applicant.getApplication().getProject().getProjectName();
        FeedbackService.submitFeedback(applicant.getNric(), message, projectName);
        return true;
    }

    /**
     * Retrieves all unpaid invoices belonging to the applicant.
     */
    public static List<Invoice> getUnpaidInvoices(Applicant applicant) {
        return InvoiceService.getAllInvoices().stream()
            .filter(inv -> inv.getApplicantNRIC().equalsIgnoreCase(applicant.getNric()))
            .filter(ApplicantService::isInvoiceUnpaid)
            .sorted(Comparator.comparing(Invoice::getPaymentId))
            .toList();
    }
    

    /**
     * Updates an invoice to reflect successful payment via the selected method.
     * Does safeguard checks to ensure payment is only done once
     * 
     * @param method The payment method chosen by the user.
     */
    public static void processInvoicePayment(Applicant applicant, Invoice invoice, PaymentMethod method) {
        if ("PROCESSED".equalsIgnoreCase(invoice.getStatus()) ||
            "Awaiting Receipt".equalsIgnoreCase(invoice.getStatus())) {
            System.out.println("❌ This invoice has already been paid or is pending receipt.");
            return;
        }
    
        invoice.setMethod(method);
        invoice.setStatus("Awaiting Receipt");
        InvoiceService.updateInvoice(invoice);
    
        int nextPaymentId = PaymentService.getNextPaymentId();
        Payment newPayment = new Payment(
                nextPaymentId,
                invoice.getAmount(),
                LocalDate.now(),
                method,
                invoice.getStatus()
        );
        PaymentService.addPayment(newPayment);
    
        System.out.println("💸 Payment successful via " + method + "!");
    }

    public static boolean isInvoiceUnpaid(Invoice invoice) {
        return !invoice.getStatus().equalsIgnoreCase("Awaiting Receipt")
            && !invoice.getStatus().equalsIgnoreCase("PROCESSED");
    }
    
    

    /**
     * Returns a list of receipts for all payments made by the applicant.
     */
    public static List<Receipt> getReceiptsByApplicant(Applicant applicant) {
        return ReceiptService.getAllReceipts().stream()
                .filter(r -> r.getApplicantNRIC().equalsIgnoreCase(applicant.getNric()))
                .sorted(Comparator.comparing(r -> r.getInvoice().getPaymentId()))
                .toList();
    }
}
