package src.util;

import src.model.Payment;
import src.model.PaymentMethod;

import java.time.LocalDate;
import java.util.*;
import static src.util.CsvUtil.*;

public class PaymentCsvMapper {
    private static final String CSV_PATH = FilePath.PAYMENT_LIST_FILE;

    /**
     * Loads all payments from the CSV file and converts them to Payment objects.
     *
     * @return List of payments.
     */
    public static List<Payment> loadAll() {
        List<Map<String, String>> rows = read(CSV_PATH);
        List<Payment> list = new ArrayList<>();

        for (Map<String, String> row : rows) {
            try {
                String idStr = row.get("PaymentID");
                if (idStr == null || idStr.isBlank()) throw new NumberFormatException("Missing PaymentID");

                int paymentId = Integer.parseInt(idStr.trim());
                double amount = Double.parseDouble(row.get("Amount"));
                LocalDate date = LocalDate.parse(row.get("Date"));
                String methodRaw = row.get("Method");
                String status = row.get("Status");

                PaymentMethod method = Arrays.stream(PaymentMethod.values())
                        .filter(m -> m.name().equalsIgnoreCase(methodRaw != null ? methodRaw : ""))
                        .findFirst()
                        .orElseThrow(() -> new IllegalArgumentException("Invalid payment method: " + methodRaw));

                list.add(new Payment(paymentId, amount, date, method, status));
            } catch (Exception ignored) {
                ignored.printStackTrace(); // helpful for debugging load errors
            }
        }

        return list;
    }

    /**
     * Saves the full list of payments to the CSV file, overwriting existing data.
     *
     * @param payments List of Payment objects.
     */
    public static void saveAll(List<Payment> payments) {
        List<Map<String, String>> rows = payments.stream()
            .map(PaymentCsvMapper::toCsvRow)
            .toList();

        write(CSV_PATH, rows);
    }

    /**
     * Appends a new payment record to the CSV.
     *
     * @param payment Payment object to append.
     */
    public static void append(Payment payment) {
        CsvUtil.append(CSV_PATH, toCsvRow(payment));
    }

    /**
     * Updates an existing payment by ID and persists the change to CSV.
     *
     * @param updated The updated Payment object.
     */
    public static void update(Payment updated) {
        List<Payment> all = loadAll();
        for (int i = 0; i < all.size(); i++) {
            if (all.get(i).getPaymentId() == updated.getPaymentId()) {
                all.set(i, updated);
                break;
            }
        }
        saveAll(all);  // Persist the updated list
    }

    /**
     * Converts a Payment object into a CSV-compatible row.
     *
     * @param p The payment object.
     * @return Map representing a single CSV row.
     */
    private static Map<String, String> toCsvRow(Payment p) {
        Map<String, String> row = new LinkedHashMap<>();
        row.put("PaymentID", String.valueOf(p.getPaymentId()));
        row.put("Amount", String.valueOf(p.getAmount()));
        row.put("Date", p.getDate().toString());
        row.put("Method", p.getMethodLabel());
        row.put("Status", p.getStatus());
        return row;
    }
}
