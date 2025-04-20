package src.model;

import java.time.LocalDate;

/**
 * Extends Payment to include invoice-specific details such as project name, flat type,
 * and the applicant's NRIC.
 */
public class Invoice extends Payment {
    private String applicantNRIC;  // For tracking
    private String projectName;
    private String flatType;

    public Invoice(int paymentId, double amount, LocalDate date, PaymentMethod method, String status,
                   String applicantNRIC, String projectName, String flatType) {
        super(paymentId, amount, date, method, status);
        this.applicantNRIC = applicantNRIC;
        this.projectName = projectName;
        this.flatType = flatType;
    }

    /**
     * Generates a formatted invoice string showing all relevant fields.
     *
     * @return Invoice details as a string.
     */
    public String generateInvoice() {
        return String.format(
            "🧾 Invoice #%d\nApplicant: %s\nProject: %s\nFlat Type: %s\nAmount: $%.2f\nDate: %s\nMethod: %s\nStatus: %s",
            paymentId,
            applicantNRIC,
            projectName,
            flatType,
            amount,
            date,
            (method != null ? method.toString() : "N/A"),
            status
        );
    }

    public String getApplicantNRIC() {
        return applicantNRIC;
    }

    public String getProjectName() {
        return projectName;
    }

    public String getFlatType() {
        return flatType;
    }
}
