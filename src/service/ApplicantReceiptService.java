package src.service;

import java.util.Comparator;
import java.util.List;

import src.interfaces.IApplicantReceiptService;
import src.model.Applicant;
import src.model.Receipt;

/**
 * Handles viewing of payment receipts by applicants.
 * 
 * Shows all receipts related to the applicant's transactions.
 */
public class ApplicantReceiptService implements IApplicantReceiptService{

    /**
     * Gets all receipts for the given applicant.
     *
     * @param applicant The logged-in applicant.
     * @return List of receipts sorted by invoice ID.
     */
    @Override
    public List<Receipt> getReceiptsByApplicant(Applicant applicant) {
        return ReceiptService.getAllReceipts().stream()
                .filter(r -> r.getApplicantNRIC().equalsIgnoreCase(applicant.getNric()))
                .sorted(Comparator.comparing(r -> r.getInvoice().getPaymentId()))
                .toList();
    }

}
