package src.service;

import src.model.Invoice;
import src.util.InvoiceCsvMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Handles invoice operations such as adding, retrieving, and updating invoices.
 * Maintains an in-memory cache to avoid repeated file reads.
 */
public class InvoiceService {
    private static List<Invoice> invoices = new ArrayList<>();

    // Load invoices from CSV at class load time
    static {
        invoices = InvoiceCsvMapper.loadAll();
    }

    /**
     * Adds a new invoice to both the CSV file and in-memory list.
     *
     * @param invoice The new invoice to add.
     */
    public static void addInvoice(Invoice invoice) {
        InvoiceCsvMapper.append(invoice);     // Write to disk
        invoices.add(invoice);                // Add to cache
    }

    /**
     * Returns all invoices currently in memory.
     *
     * @return List of all invoices in the system.
     */
    public static List<Invoice> getAllInvoices() {
        return invoices;
    }

    /**
     * Retrieves all invoices that match the given applicant's NRIC.
     *
     * @param applicantNRIC The NRIC of the applicant.
     * @return List of invoices belonging to that applicant.
     */
    public static List<Invoice> getInvoicesByNRIC(String applicantNRIC) {
        return invoices.stream()
                .filter(i -> i.getApplicantNRIC().equalsIgnoreCase(applicantNRIC))
                .collect(Collectors.toList());
    }

    /**
     * Generates the next available unique invoice ID by incrementing the highest current ID.
     *
     * @return A new unique invoice ID.
     */
    public static int getNextInvoiceId() {
        return invoices.stream()
                .mapToInt(Invoice::getPaymentId)
                .max()
                .orElse(0) + 1;
    }

    /**
     * Updates an existing invoice in both memory and file storage.
     *
     * @param updated The invoice with updated details.
     */
    public static void updateInvoice(Invoice updated) {
        InvoiceCsvMapper.update(updated); // Persist change

        for (int i = 0; i < invoices.size(); i++) {
            if (invoices.get(i).getPaymentId() == updated.getPaymentId()) {
                invoices.set(i, updated);  // Update in-memory version
                break;
            }
        }
    }
}
