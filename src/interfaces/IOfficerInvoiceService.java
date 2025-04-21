package src.interfaces;

import src.model.HDBOfficer;
import src.model.Invoice;

import java.util.List;

/**
 * Interface for officer-level invoice operations.
 * <p>
 * Follows the Interface Segregation Principle by restricting functionality
 * to only retrieving invoices that are pending receipt issuance
 * for the officer's assigned project.
 * </p>
 */
public interface IOfficerInvoiceService {

    /**
     * Retrieves invoices that are marked as "Awaiting Receipt" and
     * belong to the project assigned to the officer.
     *
     * @param officer The logged-in officer.
     * @return List of invoices that need receipt generation.
     */
    List<Invoice> getInvoicesAwaitingReceipt(HDBOfficer officer);

    
}
