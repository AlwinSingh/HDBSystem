package src.model;
import java.time.LocalDate;

public class Invoice extends Payment {
    public Invoice(int paymentId, double amount, LocalDate date, String method, String status) {
        super(paymentId, amount, date, method, status);
    }

    public String generateInvoice() {
        return "Invoice #" + paymentId + " - $" + amount + " via " + method + " on " + date;
    }
}
