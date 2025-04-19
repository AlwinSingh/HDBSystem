package src.service;

import src.model.Payment;
import src.util.PaymentCsvMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PaymentService {
    private static List<Payment> payments = new ArrayList<>();

    static {
        payments = PaymentCsvMapper.loadAll();  // Load once into memory
    }

    // Append new payment to disk and in-memory list
    public static void addPayment(Payment payment) {
        payments.add(payment);
        PaymentCsvMapper.append(payment);
    }

    // Retrieve latest in-memory list
    public static List<Payment> getAllPayments() {
        return payments;
    }

    // Get next ID from memory (or fallback to reload if needed)
    public static int getNextPaymentId() {
        return payments.stream()
            .mapToInt(Payment::getPaymentId)
            .max()
            .orElse(0) + 1;
    }

    // Get payments by NRIC if method exists
    public static List<Payment> getPaymentsByNRIC(String nric) {
        return payments.stream()
                .filter(p -> {
                    try {
                        return p.getClass().getMethod("getApplicantNRIC") != null
                                && ((String) p.getClass().getMethod("getApplicantNRIC").invoke(p)).equalsIgnoreCase(nric);
                    } catch (Exception e) {
                        return false;
                    }
                })
                .collect(Collectors.toList());
    }

    // Update payment in both CSV and memory
    public static void updatePayment(Payment updated) {
        PaymentCsvMapper.update(updated);  // Write to CSV
        for (int i = 0; i < payments.size(); i++) {
            if (payments.get(i).getPaymentId() == updated.getPaymentId()) {
                payments.set(i, updated);  // Update cache
                break;
            }
        }
    }
}
