package src.service;

import src.model.Application;
import src.model.Invoice;
import src.model.PaymentMethod;
import src.util.InvoiceCsvMapper;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class InvoiceService {
    private static List<Invoice> invoices = new ArrayList<>();

    static {
        invoices = InvoiceCsvMapper.loadAll();
    }

    // Generate a new Invoice and store it
    public static Invoice generateInvoiceForBooking(Application app, int paymentId) {
        return new Invoice(
            paymentId,
            app.getFlatPrice(),
            LocalDate.now(),
            PaymentMethod.valueOf("UNSPECIFIED"),   // Placeholder; make sure to define this enum if needed
            "Awaiting Payment",
            app.getApplicant().getNric(),
            app.getProject().getProjectName(),
            app.getFlatType()
        );
    }

    public static void addInvoice(Invoice invoice) {
        List<Invoice> all = loadAll();
        all.add(invoice);
        InvoiceCsvMapper.saveAll(all);
    }

    public static List<Invoice> loadAll() {
        return InvoiceCsvMapper.loadAll();
    }

    public static List<Invoice> getAllInvoices() {
        return invoices;
    }

    public static List<Invoice> getInvoicesByNRIC(String applicantNRIC) {
        return invoices.stream()
            .filter(i -> i.getApplicantNRIC().equalsIgnoreCase(applicantNRIC))
            .collect(Collectors.toList());
    }

    public static int getNextInvoiceId() {
        return invoices.stream()
            .mapToInt(Invoice::getPaymentId)
            .max()
            .orElse(0) + 1;
    }

    public static void persist() {
        InvoiceCsvMapper.saveAll(invoices);
    }

    public static void saveAll(List<Invoice> invoices) {
        InvoiceCsvMapper.saveAll(invoices);
    }

    public static void updateInvoice(Invoice updated) {
        List<Invoice> all = loadAll();
        for (int i = 0; i < all.size(); i++) {
            if (all.get(i).getPaymentId() == updated.getPaymentId()) {
                all.set(i, updated);
                break;
            }
        }
        saveAll(all);
    }
}
