package src.util;

import src.model.Invoice;
import src.model.PaymentMethod;

import java.time.LocalDate;
import java.util.*;
import static src.util.CsvUtil.*;

public class InvoiceCsvMapper {
    private static final String CSV_PATH = FilePath.INVOICE_LIST_FILE;

    public static List<Invoice> loadAll() {
        List<Map<String, String>> rows = read(CSV_PATH);
        List<Invoice> list = new ArrayList<>();

        for (Map<String, String> row : rows) {
            try {
                int paymentId = Integer.parseInt(row.get("PaymentID"));
                double amount = Double.parseDouble(row.get("Amount"));
                LocalDate date = LocalDate.parse(row.get("GeneratedDate"));
                String methodRaw = row.get("Method");
                String status = row.get("Status");
                String applicantNRIC = row.get("ApplicantNRIC");
                String projectName = row.get("ProjectName");
                String flatType = row.get("FlatType");

                // Convert string to PaymentMethod enum
                PaymentMethod method = Arrays.stream(PaymentMethod.values())
                        .filter(pm -> pm.toString().equalsIgnoreCase(methodRaw))
                        .findFirst()
                        .orElseThrow(() -> new IllegalArgumentException("Invalid payment method: " + methodRaw));

                list.add(new Invoice(paymentId, amount, date, method, status, applicantNRIC, projectName, flatType));
            } catch (Exception ignored) {
                ignored.printStackTrace(); // for debugging purposes
            }
        }
        return list;
    }

    public static void saveAll(List<Invoice> invoices) {
        List<Map<String, String>> rows = new ArrayList<>();

        for (Invoice i : invoices) {
            Map<String, String> row = new LinkedHashMap<>();
            row.put("InvoiceID", String.valueOf(i.getPaymentId()));
            row.put("PaymentID", String.valueOf(i.getPaymentId()));
            row.put("ApplicantNRIC", i.getApplicantNRIC());
            row.put("ProjectName", i.getProjectName());
            row.put("FlatType", i.getFlatType());
            row.put("Amount", String.valueOf(i.getAmount()));
            row.put("Method", i.getMethodLabel());  // Use label from PaymentMethod enum
            row.put("Status", i.getStatus());
            row.put("GeneratedDate", i.getDate().toString());
            rows.add(row);
        }

        write(CSV_PATH, rows);
    }
}
