package src.model;

import java.time.LocalDate;

public class Payment {
    protected int paymentId;
    protected double amount;
    protected LocalDate date;
    protected PaymentMethod method;  // Enum instead of String
    protected String status;

    public enum PaymentStatusType {
        PROCESSED,
        REFUNDED,
        PENDING
    }

    public Payment(int paymentId, double amount, LocalDate date, PaymentMethod method, String status) {
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
    public PaymentMethod getMethod() { return method; }
    public String getMethodLabel() { return method.toString(); }
    public String getStatus() { return status; }

    public void setMethod(PaymentMethod method) {
        this.method = method;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String toSummary() {
        return "Payment #" + paymentId + " | $" + amount + " | " + date + " | " + method + " | Status: " + status;
    }
}
