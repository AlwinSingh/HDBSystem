package src.service;

import src.model.Receipt;
import src.repository.ReceiptRepository;
import src.util.ReceiptCsvMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Responsible for managing receipt operations including creation,
 * retrieval, and lookups from both in-memory and CSV storage.
 */

public class ReceiptService {
    private static final ReceiptRepository receiptRepository = new ReceiptCsvMapper();
    private static List<Receipt> receipts = new ArrayList<>();

    static {
        receipts = receiptRepository.loadAll();
    }

    /**
     * Adds a new receipt to the in-memory list and appends it to the CSV.
     *
     * @param receipt The receipt to be added.
     */
    public static void addReceipt(Receipt receipt) {
        receipts.add(receipt);
        receiptRepository.append(receipt); // Efficient: Append only the new one
    }


    /**
     * Retrieves all receipts from the CSV file.
     *
     * @return A list of all {@link Receipt} objects in the system.
     */
    public static List<Receipt> getAllReceipts() {
        return receiptRepository.loadAll(); // or your in-memory list
    }


    /**
     * Retrieves all receipts associated with a specific applicant by NRIC.
     *
     * @param nric The NRIC of the applicant.
     * @return A list of matching {@link Receipt} objects.
     */
    public static List<Receipt> getReceiptsByNRIC(String nric) {
        return receipts.stream()
                .filter(r -> r.getApplicantNRIC().equalsIgnoreCase(nric))
                .collect(Collectors.toList());
    }

    /**
     * Retrieves all receipts associated with a specific project.
     *
     * @param projectName The name of the project.
     * @return A list of {@link Receipt} objects for the specified project.
     */
    public static List<Receipt> getReceiptsByProject(String projectName) {
        return receipts.stream()
                .filter(r -> r.getProjectName().equalsIgnoreCase(projectName))
                .collect(Collectors.toList());
    }

    /**
     * Finds a receipt based on the given invoice ID.
     *
     * @param invoiceId The ID of the invoice.
     * @return The matching {@link Receipt} object, or null if not found.
     */
    public static Receipt findByInvoiceId(int invoiceId) {
        return receiptRepository.loadAll().stream()
            .filter(r -> r.getInvoice().getPaymentId() == invoiceId)
            .findFirst()
            .orElse(null);
    }
    
}
