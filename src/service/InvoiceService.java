package src.service;


import src.model.Invoice;
import src.util.InvoiceCsvMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class InvoiceService {
    private static List<Invoice> invoices = new ArrayList<>();

    static {
        invoices = InvoiceCsvMapper.loadAll();
    }

    // Append new invoice to file + in-memory
    public static void addInvoice(Invoice invoice) {
        InvoiceCsvMapper.append(invoice);     // Only write the new invoice
        invoices.add(invoice);                // Update in-memory cache
    }

    // Return cached list (used for filtering, views)
    public static List<Invoice> getAllInvoices() {
        return invoices;
    }

    // Get all invoices related to specific applicant
    public static List<Invoice> getInvoicesByNRIC(String applicantNRIC) {
        return invoices.stream()
            .filter(i -> i.getApplicantNRIC().equalsIgnoreCase(applicantNRIC))
            .collect(Collectors.toList());
    }

    // Generate next unique invoice ID
    public static int getNextInvoiceId() {
        return invoices.stream()
            .mapToInt(Invoice::getPaymentId)
            .max()
            .orElse(0) + 1;
    }

    // Update invoice entry (efficient overwrite of one record)
    public static void updateInvoice(Invoice updated) {
        InvoiceCsvMapper.update(updated); // Write to disk
        for (int i = 0; i < invoices.size(); i++) {
            if (invoices.get(i).getPaymentId() == updated.getPaymentId()) {
                invoices.set(i, updated);  // Update in-memory cache
                break;
            }
        }
    }
}
