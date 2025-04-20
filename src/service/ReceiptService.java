package src.service;

import src.model.Receipt;
import src.util.ReceiptCsvMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ReceiptService {
    private static List<Receipt> receipts = new ArrayList<>();

    static {
        receipts = ReceiptCsvMapper.loadAll();
    }

    /**
     * Adds a new receipt to the in-memory list and appends it to the CSV.
     *
     * @param receipt The receipt to be added.
     */
    public static void addReceipt(Receipt receipt) {
        receipts.add(receipt);
        ReceiptCsvMapper.append(receipt); // Efficient: Append only the new one
    }


    /**
     * Retrieves all receipts from the CSV file.
     *
     * @return List of all receipts in the system.
     */
    public static List<Receipt> getAllReceipts() {
        return ReceiptCsvMapper.loadAll(); // or your in-memory list
    }


    /**
     * Retrieves receipts submitted by a specific applicant.
     *
     * @param nric Applicant's NRIC.
     * @return List of matching receipts.
     */
    public static List<Receipt> getReceiptsByNRIC(String nric) {
        return receipts.stream()
                .filter(r -> r.getApplicantNRIC().equalsIgnoreCase(nric))
                .collect(Collectors.toList());
    }

    /**
     * Retrieves receipts associated with a given project.
     *
     * @param projectName The name of the project.
     * @return List of receipts for the project.
     */
    public static List<Receipt> getReceiptsByProject(String projectName) {
        return receipts.stream()
                .filter(r -> r.getProjectName().equalsIgnoreCase(projectName))
                .collect(Collectors.toList());
    }

    /**
     * Finds a receipt based on a given invoice ID.
     *
     * @param invoiceId The ID of the invoice.
     * @return Matching receipt or null if not found.
     */
    public static Receipt findByInvoiceId(int invoiceId) {
        return ReceiptCsvMapper.loadAll().stream()
            .filter(r -> r.getInvoice().getPaymentId() == invoiceId)
            .findFirst()
            .orElse(null);
    }
    
}
