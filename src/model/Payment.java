package src.model;

import java.time.LocalDate;

public class Payment {
    protected int paymentId;
    protected double amount;
    protected LocalDate date;
    protected String method;
    protected String status;

    public Payment(int paymentId, double amount, LocalDate date, String method, String status) {
        this.paymentId = paymentId;
        this.amount = amount;
        this.date = date;
        this.method = method;
        this.status = status;
    }

    public boolean processPayment() {
        this.status = "Processed";
        System.out.println("Processing payment of $" + amount + " via " + method);
        return true;
    }

    public boolean refundPayment() {
        this.status = "Refunded";
        System.out.println("Refunded payment of $" + amount);
        return true;
    }

    public String generateInvoice() {
        return "Invoice ID: " + paymentId + " | Amount: $" + amount + " | Date: " + date;
    }

    // Getters
}
