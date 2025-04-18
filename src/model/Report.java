package src.model;

import java.time.LocalDate;

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

    @Override
    public String toString() {
        return "👤 " + applicantName + " (" + applicantNRIC + "), " + age + " y/o, " + maritalStatus + "\n"
             + "🏠 Project: " + projectName + " | Flat Type: " + flatTypeBooked + " | Price: $" + flatPrice + "\n"
             + "📦 Booking: " + bookingStatus + " | 💰 Payment: " + paymentStatus
             + (bookingDate != null ? " | 📅 Date: " + bookingDate : "")
             + (receiptId != null ? " | 🧾 Receipt ID: " + receiptId : "");
    }
}
