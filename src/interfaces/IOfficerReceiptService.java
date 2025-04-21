package src.interfaces;

import java.util.Scanner;
import src.model.HDBOfficer;
import src.model.Invoice;

/**
 * Interface for HDB officers to manage receipt generation and validation.
 * Part of officer-facing functionality under Interface Segregation Principle.
 */
public interface IOfficerReceiptService {

    /**
     * Initiates receipt generation for eligible invoices under the officer’s assigned project.
     *
     * @param officer The logged-in officer.
     * @param sc      Scanner to capture user input.
     */
    void generateReceipt(HDBOfficer officer, Scanner sc);

    /**
     * Processes and saves a receipt for a specific invoice after validation.
     *
     * @param officer The logged-in officer.
     * @param invoice The invoice to process.
     * @return True if successful, false otherwise.
     */
    boolean generateReceiptForInvoice(HDBOfficer officer, Invoice invoice);
}
