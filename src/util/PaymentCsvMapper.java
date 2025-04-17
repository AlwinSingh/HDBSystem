package src.util;

import src.model.Payment;
import java.time.LocalDate;
import java.util.*;
import static src.util.CsvUtil.*;

public class PaymentCsvMapper {
    private static final String CSV_PATH = "data/PaymentList.csv";

    public static List<Payment> loadAll() {
        List<Map<String, String>> rows = read(CSV_PATH);
        List<Payment> list = new ArrayList<>();
        for (Map<String, String> row : rows) {
            try {
                int paymentId = Integer.parseInt(row.get("PaymentID"));
                double amount = Double.parseDouble(row.get("Amount"));
                LocalDate date = LocalDate.parse(row.get("Date"));
                String method = row.get("Method");
                String status = row.get("Status");
                list.add(new Payment(paymentId, amount, date, method, status));
            } catch (Exception ignored) {}
        }
        return list;
    }

    public static void saveAll(List<Payment> payments) {
        List<Map<String, String>> rows = new ArrayList<>();
        rows.add(createHeaderRow());

        for (Payment p : payments) {
            Map<String, String> row = new LinkedHashMap<>();
            row.put("PaymentID", String.valueOf(p.getPaymentId()));
            row.put("Amount", String.valueOf(p.getAmount()));
            row.put("Date", p.getDate().toString());
            row.put("Method", p.getMethod());
            row.put("Status", p.getStatus());
            rows.add(row);
        }

        write(CSV_PATH, rows);
    }

    private static Map<String, String> createHeaderRow() {
        Map<String, String> header = new LinkedHashMap<>();
        header.put("PaymentID", "PaymentID");
        header.put("Amount", "Amount");
        header.put("Date", "Date");
        header.put("Method", "Method");
        header.put("Status", "Status");
        return header;
    }
}