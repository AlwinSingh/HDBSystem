package src.service;

import java.util.List;
import java.util.Scanner;

import src.model.Applicant;
import src.model.HDBOfficer;
import src.model.Invoice;
import src.model.Payment;
import src.model.Project;
import src.model.Receipt;
import src.repository.ApplicantRepository;
import src.util.ApplicantCsvMapper;
import src.util.ProjectCsvMapper;

/**
 * Provides functionality for HDB Officers to manage receipts for applicants' payments.
 * Responsible for validating eligibility, generating receipts, and updating payment records.
 */
public class OfficerReceiptService {
    private static final ApplicantRepository applicantRepository = new ApplicantCsvMapper();

    /**
     * Generates a receipt for a payment made by an applicant within the officer’s project.
     * Requires an existing invoice marked as "Awaiting Receipt".
     *
     * @param officer The logged-in officer.
     * @param sc      Scanner for input.
     */
    public static void generateReceipt(HDBOfficer officer, Scanner sc) {
        if (!"APPROVED".equalsIgnoreCase(officer.getRegistrationStatus())) {
            System.out.println("❌ Access denied. Officer registration status must be APPROVED to generate receipts.");
            return;
        }
    
        List<Invoice> awaitingReceipts = OfficerInvoiceService.getInvoicesAwaitingReceipt(officer);
    
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
            boolean success = OfficerReceiptService.generateReceiptForInvoice(officer, selectedInvoice);
    
            if (!success) {
                System.out.println("❌ Receipt generation failed.");
            }
    
        } catch (Exception e) {
            System.out.println("❌ Invalid input.");
        }
    }

    /**
     * Generates a receipt for a specified invoice and updates related records.
     *
     * @param officer The officer issuing the receipt.
     * @param invoice The invoice to generate a receipt for.
     * @return True if receipt generation is successful; false otherwise.
     */
    public static boolean generateReceiptForInvoice(HDBOfficer officer, Invoice invoice) {
        if (!invoice.getProjectName().equalsIgnoreCase(officer.getAssignedProject().getProjectName())) {
            System.out.println("❌ You are not authorized to issue receipts for this project.");
            return false;
        }

        Applicant applicant = findApplicantByNRIC(invoice.getApplicantNRIC());
        if (applicant == null || applicant.getApplication() == null) {
            System.out.println("❌ Applicant or application not found.");
            return false;
        }

        Project fullProject = findFullProjectByName(invoice.getProjectName());
        if (fullProject != null) {
            applicant.getApplication().setProject(fullProject);
        }

        Receipt receipt = officer.generateReceipt(
            applicant.getApplication(),
            invoice.getPaymentId(),
            invoice.getMethod()
        );

        ReceiptService.addReceipt(receipt);

        invoice.setStatus(Payment.PaymentStatusType.PROCESSED.name());
        InvoiceService.updateInvoice(invoice);

        Payment payment = PaymentService.getAllPayments().stream()
            .filter(p -> p.getPaymentId() == invoice.getPaymentId())
            .findFirst()
            .orElse(null);

        if (payment != null) {
            payment.setStatus(Payment.PaymentStatusType.PROCESSED.name());
            PaymentService.updatePayment(payment);
        }

        System.out.println("✅ Receipt generated:\n" + receipt);
        return true;
    }

    /**
     * Helper method to retrieve an applicant by NRIC.
     *
     * @param nric The NRIC of the applicant.
     * @return The matching Applicant, or null if not found.
     */
    public static Applicant findApplicantByNRIC(String nric) {
    return applicantRepository.loadAll().stream()
        .filter(a -> a.getNric().equalsIgnoreCase(nric))
        .findFirst()
        .orElse(null);
    }

    /**
     * Helper method to retrieve a full project object by name.
     *
     * @param projectName The name of the project.
     * @return The matching Project, or null if not found.
     */
    public static Project findFullProjectByName(String projectName) {
        return ProjectCsvMapper.loadAll().stream()
            .filter(p -> p.getProjectName().equalsIgnoreCase(projectName))
            .findFirst()
            .orElse(null);
    }

}
