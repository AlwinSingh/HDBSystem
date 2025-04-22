package src.interfaces;

import src.model.Applicant;
import src.model.Invoice;
import src.model.PaymentMethod;

import java.util.List;

/**
 * Lets applicants view and pay their invoices.
 */
public interface IApplicantInvoiceService {
    /**
     * Gets all unpaid invoices for this applicant.
     */
    List<Invoice> getUnpaidInvoices(Applicant applicant);

    /**
     * Pays an invoice using the selected method.
     */
    void processInvoicePayment(Applicant applicant, Invoice invoice, PaymentMethod method);

    /**
     * Checks if an invoice is still unpaid.
     */
    boolean isInvoiceUnpaid(Invoice invoice);
}
