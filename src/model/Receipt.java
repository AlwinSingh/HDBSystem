package src.model;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Represents a receipt issued for a completed payment.
 * Contains applicant info, project details, and an embedded invoice.
 */
public class Receipt {
    private final String applicantName;
    private final String applicantNRIC;
    private final int age;
    private final String maritalStatus;
    private final String projectName;
    private final String neighborhood;
    private final String flatTypeBooked;
    private final Invoice invoice;

    private String receiptId; // 🔄 Removed final
    private LocalDate issuedDate;

    /**
     * Constructs a new Receipt based on applicant and invoice details.
     *
     * @param applicantName   Applicant's full name.
     * @param applicantNRIC   Applicant's NRIC.
     * @param age             Applicant's age.
     * @param maritalStatus   Applicant's marital status.
     * @param projectName     Name of the project booked.
     * @param neighborhood    Project's neighborhood.
     * @param flatTypeBooked  Chosen flat type.
     * @param invoice         Associated invoice.
     */
    public Receipt(String applicantName, String applicantNRIC, int age, String maritalStatus,
                   String projectName, String neighborhood, String flatTypeBooked, Invoice invoice) {
        this.applicantName = applicantName;
        this.applicantNRIC = applicantNRIC;
        this.age = age;
        this.maritalStatus = maritalStatus;
        this.projectName = projectName;
        this.neighborhood = neighborhood;
        this.flatTypeBooked = flatTypeBooked;
        this.invoice = invoice;
        this.receiptId = generateReceiptId();
        this.issuedDate = LocalDate.now();
    }

    /**
     * Generates a unique receipt ID using UUID.
     *
     * @return A formatted receipt ID string.
     */
    private String generateReceiptId() {
        return "R-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    /**
     * Returns a multi-line string representation of this receipt.
     *
     * @return Formatted string containing all receipt details.
     */
    @Override
    public String toString() {
        return "📄 RECEIPT #" + receiptId + "\n" +
               "Issued Date  : " + issuedDate + "\n" +
               "Name         : " + applicantName + "\n" +
               "NRIC         : " + applicantNRIC + "\n" +
               "Age          : " + age + "\n" +
               "Status       : " + maritalStatus + "\n" +
               "Project      : " + projectName + "\n" +
               "Neighborhood : " + neighborhood + "\n" +
               "Flat Type    : " + flatTypeBooked + "\n" +
               "Amount Paid  : $" + invoice.getAmount() + "\n" +
               "Date Paid    : " + invoice.getDate() + "\n" +
               "Method       : " + invoice.getMethod() + "\n" +
               "Invoice No.  : " + invoice.getPaymentId() + "\n" +
               "Payment Stat : " + invoice.getStatus();
    }

    // === Getters (for CSV use or service layer) ===
    public String getReceiptId() {
        return receiptId;
    }

    public LocalDate getIssuedDate() {
        return issuedDate;
    }

    public String getApplicantName() {
        return applicantName;
    }

    public String getApplicantNRIC() {
        return applicantNRIC;
    }

    public String getProjectName() {
        return projectName;
    }

    public String getFlatTypeBooked() {
        return flatTypeBooked;
    }

    public Invoice getInvoice() {
        return invoice;
    }

    // === Setters for deserialization ===

    /**
     * Sets the date this receipt was issued.
     * Used during CSV loading/deserialization.
     *
     * @param issuedDate Date to assign.
     */
    public void setIssuedDate(LocalDate issuedDate) {
        this.issuedDate = issuedDate;
    }

    /**
     * Sets the receipt ID manually — typically used during CSV deserialization.
     *
     * @param receiptId The receipt identifier string.
     */
    public void setReceiptId(String receiptId) {
        this.receiptId = receiptId;
    }
}
