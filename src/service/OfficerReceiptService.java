package src.service;

import java.util.List;
import java.util.Scanner;

import src.interfaces.IOfficerInvoiceService;
import src.interfaces.IOfficerReceiptService;
import src.model.*;
import src.repository.ApplicantRepository;
import src.repository.ProjectRepository;
import src.util.ApplicantCsvMapper;
import src.util.ProjectCsvMapper;

/**
 * Service implementation for HDB officers to manage and generate receipts for invoices.
 * This class adheres to the Interface Segregation Principle by implementing IOfficerReceiptService.
 * It depends on an injected IOfficerInvoiceService to retrieve invoice data and interacts
 * with repositories to update applicant and project information.
 */
public class OfficerReceiptService implements IOfficerReceiptService {
    private static final ProjectRepository projectRepository = new ProjectCsvMapper();
    private final IOfficerInvoiceService invoiceService;
    private final ApplicantRepository applicantRepository;

    /**
     * Constructs an OfficerReceiptService with a specified invoice service.
     *
     * @param invoiceService An implementation of IOfficerInvoiceService used to retrieve invoice data.
     */
    public OfficerReceiptService(IOfficerInvoiceService invoiceService) {
        this.invoiceService = invoiceService;
        this.applicantRepository = new ApplicantCsvMapper();
    }

    /**
     * Allows an HDB officer to generate a receipt for any invoice that is awaiting receipt generation
     * under the officer’s assigned project. Presents a list of eligible invoices and prompts user input.
     *
     * @param officer The logged-in officer performing the receipt generation.
     * @param sc      Scanner to capture user input.
     */
    @Override
    public void generateReceipt(HDBOfficer officer, Scanner sc) {
        if (!"APPROVED".equalsIgnoreCase(officer.getRegistrationStatus())) {
            System.out.println("❌ Access denied. Officer registration status must be APPROVED to generate receipts.");
            return;
        }

        List<Invoice> awaitingReceipts = invoiceService.getInvoicesAwaitingReceipt(officer);
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
            boolean success = generateReceiptForInvoice(officer, selectedInvoice);
            if (!success) {
                System.out.println("❌ Receipt generation failed.");
            }

        } catch (Exception e) {
            System.out.println("❌ Invalid input.");
        }
    }

    /**
     * Issues a receipt for a specific invoice. Updates the invoice status to "PROCESSED"
     * and synchronizes the corresponding payment status.
     *
     * @param officer The officer responsible for the receipt.
     * @param invoice The invoice to process.
     * @return True if receipt was successfully generated and saved; false otherwise.
     */
    @Override
    public boolean generateReceiptForInvoice(HDBOfficer officer, Invoice invoice) {
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

        Receipt receipt = officer.generateReceipt(applicant.getApplication(), invoice.getPaymentId(), invoice.getMethod());
        ReceiptService.addReceipt(receipt);

        invoice.setStatus(Payment.PaymentStatusType.PROCESSED.name());
        InvoiceService.updateInvoice(invoice);

        Payment payment = PaymentService.getAllPayments().stream()
            .filter(p -> p.getPaymentId() == invoice.getPaymentId())
            .findFirst().orElse(null);

        if (payment != null) {
            payment.setStatus(Payment.PaymentStatusType.PROCESSED.name());
            PaymentService.updatePayment(payment);
        }

        System.out.println("✅ Receipt generated:\n" + receipt);
        return true;
    }

    /**
     * Finds and returns an applicant by their NRIC.
     *
     * @param nric The NRIC of the applicant.
     * @return The matching Applicant or null if not found.
     */
    private Applicant findApplicantByNRIC(String nric) {
        return applicantRepository.loadAll().stream()
            .filter(a -> a.getNric().equalsIgnoreCase(nric))
            .findFirst()
            .orElse(null);
    }

    /**
     * Retrieves the full project object from the repository by name.
     *
     * @param projectName The name of the project.
     * @return The full Project object or null if not found.
     */
    private Project findFullProjectByName(String projectName) {
        return projectRepository.loadAll().stream()
            .filter(p -> p.getProjectName().equalsIgnoreCase(projectName))
            .findFirst()
            .orElse(null);
    }
}
