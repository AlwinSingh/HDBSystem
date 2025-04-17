package src.model;

import java.time.LocalDate;
import java.util.UUID;

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

    private String generateReceiptId() {
        return "R-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    public void generatePDF() {
        System.out.println("🧾 PDF receipt generated for " + applicantName);
    }

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
    public void setIssuedDate(LocalDate issuedDate) {
        this.issuedDate = issuedDate;
    }

    public void setReceiptId(String receiptId) {
        this.receiptId = receiptId;
    }
}
