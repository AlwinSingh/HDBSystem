package src.model;

import java.time.LocalDate;

public class Payment {
    protected int paymentId;
    protected double amount;
    protected LocalDate date;
    protected String method; // e.g., PayNow, Credit Card
    protected String status; // e.g., Processed, Refunded, Pending

    public enum PaymentStatusType {
        PROCESSED,
        REFUNDED,
        PENDING
    }

    public Payment(int paymentId, double amount, LocalDate date, String method, String status) {
        this.paymentId = paymentId;
        this.amount = amount;
        this.date = date;
        this.method = method;
        this.status = status;
    }

    public boolean processPayment() {
        if (!PaymentStatusType.PENDING.name().equalsIgnoreCase(status)) return false;
        this.status = PaymentStatusType.PROCESSED.name();
        return true;
    }

    public boolean refundPayment() {
        if (!PaymentStatusType.PROCESSED.name().equalsIgnoreCase(status)) return false;
        this.status = PaymentStatusType.REFUNDED.name();
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
