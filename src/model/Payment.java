package src.model;

import java.time.LocalDate;

public class Payment {
    protected int paymentId;
    protected double amount;
    protected LocalDate date;
    protected String method; // e.g., PayNow, Credit Card
    protected String status; // e.g., "Processed", "Refunded", "Pending"

    public Payment(int paymentId, double amount, LocalDate date, String method, String status) {
        this.paymentId = paymentId;
        this.amount = amount;
        this.date = date;
        this.method = method;
        this.status = status;
    }

    public boolean processPayment() {
        if (!"Pending".equalsIgnoreCase(status)) return false;
        this.status = "Processed";
        return true;
    }

    public boolean refundPayment() {
        if (!"Processed".equalsIgnoreCase(status)) return false;
        this.status = "Refunded";
        return true;
    }

    // === Getters and Setters ===
    public int getPaymentId() { return paymentId; }
    public double getAmount() { return amount; }
    public LocalDate getDate() { return date; }
    public String getMethod() { return method; }
    public String getStatus() { return status; }

    public void setMethod(String method) {
        this.method = method;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String toSummary() {
        return "Payment #" + paymentId + " | $" + amount + " | " + date + " | " + method + " | Status: " + status;
    }
}
