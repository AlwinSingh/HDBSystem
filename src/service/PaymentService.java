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
    /**
     * Adds a new payment to both in-memory list and CSV storage.
     *
     * @param payment The payment to be added.
     */
    public static void addPayment(Payment payment) {
        payments.add(payment);
        PaymentCsvMapper.append(payment);
    }

    /**
     * Retrieves the full list of payments currently loaded in memory.
     *
     * @return List of all payments.
     */
    public static List<Payment> getAllPayments() {
        return payments;
    }

    /**
     * Generates the next unique payment ID based on the highest existing one.
     *
     * @return A new unique payment ID.
     */
    public static int getNextPaymentId() {
        return payments.stream()
            .mapToInt(Payment::getPaymentId)
            .max()
            .orElse(0) + 1;
    }

    /**
     * Retrieves all payments associated with a specific applicant's NRIC.
     * Uses reflection to invoke getApplicantNRIC() if present.
     *
     * @param nric The applicant's NRIC.
     * @return List of matching payments.
     */
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

    /**
     * Updates an existing payment in both memory and persistent CSV.
     *
     * @param updated The updated Payment object.
     */
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
