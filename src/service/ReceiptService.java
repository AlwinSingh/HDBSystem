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

    // Add a new receipt and persist it
    public static void addReceipt(Receipt receipt) {
        receipts.add(receipt);
        ReceiptCsvMapper.append(receipt); // Efficient: Append only the new one
    }
    

    // Get all receipts
    public static List<Receipt> getAllReceipts() {
        return ReceiptCsvMapper.loadAll(); // or your in-memory list
    }
    

    // Get receipts by applicant NRIC
    public static List<Receipt> getReceiptsByNRIC(String nric) {
        return receipts.stream()
                .filter(r -> r.getApplicantNRIC().equalsIgnoreCase(nric))
                .collect(Collectors.toList());
    }

    // Get receipts by project name
    public static List<Receipt> getReceiptsByProject(String projectName) {
        return receipts.stream()
                .filter(r -> r.getProjectName().equalsIgnoreCase(projectName))
                .collect(Collectors.toList());
    }

    public static Receipt findByInvoiceId(int invoiceId) {
        return ReceiptCsvMapper.loadAll().stream()
            .filter(r -> r.getInvoice().getPaymentId() == invoiceId)
            .findFirst()
            .orElse(null);
    }
    
}
