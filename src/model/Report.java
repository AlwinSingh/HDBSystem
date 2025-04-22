package src.model;

import java.time.LocalDate;

/**
 * Represents a summary report entry for a booking and payment.
 * Includes key information about the applicant, flat, and transaction status.
 */
public class Report {
    private String applicantName;
    private String applicantNRIC;
    private int age;
    private String maritalStatus;
    private String projectName;
    private String flatTypeBooked;
    private double flatPrice;
    private String bookingStatus;
    private String paymentStatus;
    private LocalDate bookingDate; // optional
    private String receiptId;      // optional

    /**
     * Constructs a new report entry summarizing a flat booking.
     *
     * @param applicantName    Name of the applicant.
     * @param applicantNRIC    NRIC of the applicant.
     * @param age              Age of the applicant.
     * @param maritalStatus    Marital status of the applicant.
     * @param projectName      Name of the project booked.
     * @param flatTypeBooked   Type of flat booked (e.g., 2-Room).
     * @param flatPrice        Price of the booked flat.
     * @param bookingStatus    Booking status (e.g., BOOKED).
     * @param paymentStatus    Payment status (e.g., PROCESSED).
     * @param bookingDate      Date the booking was made (nullable).
     * @param receiptId        Receipt ID issued after payment (nullable).
     */
    public Report(String applicantName, String applicantNRIC, int age, String maritalStatus,
                  String projectName, String flatTypeBooked, double flatPrice,
                  String bookingStatus, String paymentStatus,
                  LocalDate bookingDate, String receiptId) {
        this.applicantName = applicantName;
        this.applicantNRIC = applicantNRIC;
        this.age = age;
        this.maritalStatus = maritalStatus;
        this.projectName = projectName;
        this.flatTypeBooked = flatTypeBooked;
        this.flatPrice = flatPrice;
        this.bookingStatus = bookingStatus;
        this.paymentStatus = paymentStatus;
        this.bookingDate = bookingDate;
        this.receiptId = receiptId;
    }

    public String getApplicantName() { return applicantName; }
    public String getApplicantNRIC() { return applicantNRIC; }
    public int getAge() { return age; }
    public String getMaritalStatus() { return maritalStatus; }
    public String getProjectName() { return projectName; }
    public String getFlatTypeBooked() { return flatTypeBooked; }
    public double getFlatPrice() { return flatPrice; }
    public String getBookingStatus() { return bookingStatus; }
    public String getPaymentStatus() { return paymentStatus; }
    public LocalDate getBookingDate() { return bookingDate; }
    public String getReceiptId() { return receiptId; }

    /**
     * Returns a concise formatted summary string of the report for console display.
     */
    @Override
    public String toString() {
        return "👤 " + applicantName + " (" + applicantNRIC + "), " + age + " y/o, " + maritalStatus + "\n"
             + "🏠 Project: " + projectName + " | Flat Type: " + flatTypeBooked + " | Price: $" + flatPrice + "\n"
             + "📦 Booking: " + bookingStatus + " | 💰 Payment: " + paymentStatus
             + (bookingDate != null ? " | 📅 Date: " + bookingDate : "")
             + (receiptId != null ? " | 🧾 Receipt ID: " + receiptId : "");
    }
}
