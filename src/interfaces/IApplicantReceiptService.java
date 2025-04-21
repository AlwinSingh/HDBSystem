package src.interfaces;

import src.model.Applicant;
import src.model.Receipt;

import java.util.List;

/**
 * Lets applicants view receipts from their past payments.
 */
public interface IApplicantReceiptService {
    /**
     * Gets all receipts for this applicant.
     */
    List<Receipt> getReceiptsByApplicant(Applicant applicant);
}
