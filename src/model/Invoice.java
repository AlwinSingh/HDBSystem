package src.model;

import java.time.LocalDate;

/**
 * Represents an invoice, extending the {@link Payment} class to include
 * housing-specific fields such as the applicant's NRIC, project name, and flat type.
 * Used to track payment-related information for BTO bookings.
 */
public class Invoice extends Payment {
    private String applicantNRIC;  // For tracking
    private String projectName;
    private String flatType;

    /**
     * Constructs an Invoice with all relevant details.
     *
     * @param paymentId     Unique ID for this invoice/payment.
     * @param amount        Total amount to be paid.
     * @param date          Date the invoice was generated.
     * @param method        Payment method used (can be null if not paid yet).
     * @param status        Current payment status (e.g., "Awaiting Payment", "Processed").
     * @param applicantNRIC NRIC of the applicant responsible for this invoice.
     * @param projectName   Name of the housing project this invoice applies to.
     * @param flatType      Flat type associated with this invoice.
     */
    public Invoice(int paymentId, double amount, LocalDate date, PaymentMethod method, String status,
                   String applicantNRIC, String projectName, String flatType) {
        super(paymentId, amount, date, method, status);
        this.applicantNRIC = applicantNRIC;
        this.projectName = projectName;
        this.flatType = flatType;
    }

    /**
     * Generates a detailed string representation of the invoice.
     *
     * @return Formatted invoice details including project, flat type, amount, and payment status.
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

    /**
     * Returns the NRIC of the applicant associated with this invoice.
     *
     * @return Applicant NRIC.
     */
    public String getApplicantNRIC() {
        return applicantNRIC;
    }

    /**
     * Returns the name of the project this invoice is for.
     *
     * @return Project name.
     */
    public String getProjectName() {
        return projectName;
    }

    /**
     * Returns the flat type (e.g., 2-Room, 3-Room) this invoice is for.
     *
     * @return Flat type.
     */
    public String getFlatType() {
        return flatType;
    }
}
