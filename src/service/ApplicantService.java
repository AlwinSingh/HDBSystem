package src.service;

import src.model.*;
import src.util.ApplicantCsvMapper;
import src.util.ProjectCsvMapper;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ApplicantService {

    public static boolean isEligible(Applicant applicant, Project project) {
        String status = applicant.getMaritalStatus();
        int age = applicant.getAge();

        boolean withinDateRange = !LocalDate.now().isBefore(project.getOpenDate())
                               && !LocalDate.now().isAfter(project.getCloseDate());

        if (status == null || !withinDateRange) return false;

        return (status.equalsIgnoreCase("Single") && age >= 35)
            || (status.equalsIgnoreCase("Married") && age >= 21);
    }

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
                System.out.println("   - " + a.getAmenityDetails());
            }
        }
        System.out.println();
    }

    

    public static List<Project> getEligibleProjects(Applicant applicant) {
        return ProjectLoader.loadProjects().stream()
            .filter(p -> p != null && p.getProjectName() != null)
            .filter(Project::isVisible)
            .filter(p -> isEligible(applicant, p))
            .collect(Collectors.toList());
    }

        // 🔎 Enhanced: Filtered view for eligible projects
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


    public static boolean submitApplication(Applicant applicant, Project project, String flatType) {
        boolean success = applicant.applyForProject(project, flatType);
        if (!success) return false;

        // Persist applicant update
        ApplicantCsvMapper.updateApplicant(applicant);

        // Track applicant in the project
        project.getApplicantNRICs().add(applicant.getNric());
        ProjectCsvMapper.updateProject(project);

        return true;
    }

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
    
    public static void submitWithdrawalRequest(Applicant applicant) {
        applicant.getApplication().setStatus(Applicant.AppStatusType.WITHDRAW_REQUESTED.name());
        ApplicantCsvMapper.updateApplicant(applicant);
    }
    
    public static boolean submitFeedback(Applicant applicant, String message) {
        if (applicant.getApplication() == null || applicant.getApplication().getProject() == null) {
            return false;
        }
        String projectName = applicant.getApplication().getProject().getProjectName();
        FeedbackService.submitFeedback(applicant.getNric(), message, projectName);
        return true;
    }

    public static List<Invoice> getUnpaidInvoices(Applicant applicant) {
        return InvoiceService.getAllInvoices().stream()
                .filter(inv -> inv.getApplicantNRIC().equalsIgnoreCase(applicant.getNric()))
                .filter(inv -> !"Awaiting Receipt".equalsIgnoreCase(inv.getStatus()))
                .sorted(Comparator.comparing(Invoice::getPaymentId))
                .toList();
    }
    
    public static void processInvoicePayment(Applicant applicant, Invoice invoice, PaymentMethod method) {
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
    
    public static List<Receipt> getReceiptsByApplicant(Applicant applicant) {
        return ReceiptService.getAllReceipts().stream()
            .filter(r -> r.getApplicantNRIC().equalsIgnoreCase(applicant.getNric()))
            .sorted(Comparator.comparing(r -> r.getInvoice().getPaymentId()))
            .toList();
    }
    
}
