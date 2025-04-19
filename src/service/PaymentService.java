package src.service;

import src.model.Payment;
import src.util.PaymentCsvMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PaymentService {
    private static List<Payment> payments = new ArrayList<>();

    static {
        payments = PaymentCsvMapper.loadAll();
    }

    // Create and save a new payment
    public static void addPayment(Payment payment) {
        payments.add(payment);
        PaymentCsvMapper.append(payment); // Efficient CSV write
    }
    

    // Retrieve all payments
    public static List<Payment> getAllPayments() {
        return payments;
    }

    // Retrieve payments by NRIC (if available in subclass)
    public static List<Payment> getPaymentsByNRIC(String nric) {
        return payments.stream()
                .filter(p -> {
                    try {
                        // Only if it's a subclass that has NRIC
                        return p.getClass().getMethod("getApplicantNRIC") != null
                                && ((String) p.getClass().getMethod("getApplicantNRIC").invoke(p)).equalsIgnoreCase(nric);
                    } catch (Exception e) {
                        return false;
                    }
                })
                .collect(Collectors.toList());
    }

    // Retrieve latest payment ID for incremental generation
    public static int getNextPaymentId() {
        return PaymentCsvMapper.loadAll().stream()
            .mapToInt(Payment::getPaymentId)
            .max()
            .orElse(0) + 1;
    }

    public static void updatePayment(Payment updated) {
        PaymentCsvMapper.update(updated); // persist to disk
    
        for (int i = 0; i < payments.size(); i++) {
            if (payments.get(i).getPaymentId() == updated.getPaymentId()) {
                payments.set(i, updated); // update in-memory
                break;
            }
        }
    }
    
}
