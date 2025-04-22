package src.service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

import src.interfaces.IApplicantInvoiceService;
import src.model.Applicant;
import src.model.Invoice;
import src.model.Payment;
import src.model.PaymentMethod;


/**
 * Handles everything related to applicant invoices.
 *
 * Includes checking unpaid invoices and making payments.
 */
public class ApplicantInvoiceService implements IApplicantInvoiceService{

    /**
     * Retrieves all unpaid invoices belonging to the applicant.
     *
     * @param applicant The applicant whose unpaid invoices are being retrieved.
     * @return List of unpaid invoices sorted by payment ID.
     */
    @Override
    public List<Invoice> getUnpaidInvoices(Applicant applicant) {
        return InvoiceService.getAllInvoices().stream()
            .filter(inv -> inv.getApplicantNRIC().equalsIgnoreCase(applicant.getNric()))
            .filter(inv -> isInvoiceUnpaid(inv)) // ✅ Fixed: call method with lambda
            .sorted(Comparator.comparing(Invoice::getPaymentId))
            .toList();
    }
    

    /**
     * Updates an invoice to reflect successful payment via the selected method.
     * Does safeguard checks to ensure payment is only done once
     * 
     * @param method The payment method chosen by the user.
     */
    @Override
    public void processInvoicePayment(Applicant applicant, Invoice invoice, PaymentMethod method) {
        if ("PROCESSED".equalsIgnoreCase(invoice.getStatus()) ||
            "Awaiting Receipt".equalsIgnoreCase(invoice.getStatus())) {
            System.out.println("❌ This invoice has already been paid or is pending receipt.");
            return;
        }
    
        invoice.setMethod(method);
        invoice.setStatus("Awaiting Receipt");
        InvoiceService.updateInvoice(invoice);
    
        int nextPaymentId = PaymentService.getNextPaymentId();
        Payment newPayment = new Payment(
                nextPaymentId,
                invoice.getAmount(),
                LocalDate.now(),
                method,
                invoice.getStatus()
        );
        PaymentService.addPayment(newPayment);
    
        System.out.println("💸 Payment successful via " + method + "!");
    }

    @Override
    public boolean isInvoiceUnpaid(Invoice invoice) {
        return !invoice.getStatus().equalsIgnoreCase("Awaiting Receipt")
            && !invoice.getStatus().equalsIgnoreCase("PROCESSED");
    }

}
