package src.service;

import java.util.List;

import src.interfaces.IOfficerInvoiceService;
import src.model.HDBOfficer;
import src.model.Invoice;

/**
 * Provides functionality for officers to retrieve invoices that are awaiting receipt generation.
 * This service isolates invoice-related responsibilities for the officer role.
 */
public class OfficerInvoiceService implements IOfficerInvoiceService {

    /**
     * Returns a list of invoices that are marked as "Awaiting Receipt" and have not yet
     * been converted to receipts by the officer. Only invoices tied to the officer's assigned project are included.
     *
     * @param officer The logged-in HDB officer.
     * @return A list of invoices pending receipt issuance.
     */
    public List<Invoice> getInvoicesAwaitingReceipt(HDBOfficer officer) {
        return InvoiceService.getAllInvoices().stream()
            .filter(i -> "Awaiting Receipt".equalsIgnoreCase(i.getStatus()))
            .filter(i -> ReceiptService.findByInvoiceId(i.getPaymentId()) == null)
            .filter(i -> i.getProjectName().equalsIgnoreCase(officer.getAssignedProject().getProjectName()))
            .toList();
    }

}
