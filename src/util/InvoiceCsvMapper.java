package src.util;

import src.model.Invoice;
import java.time.LocalDate;
import java.util.*;
import static src.util.CsvUtil.*;

public class InvoiceCsvMapper {
    private static final String CSV_PATH = "data/InvoiceList.csv";

    public static List<Invoice> loadAll() {
        List<Map<String, String>> rows = read(CSV_PATH);
        List<Invoice> list = new ArrayList<>();

        for (Map<String, String> row : rows) {
            try {
                int paymentId = Integer.parseInt(row.get("PaymentID"));
                double amount = Double.parseDouble(row.get("Amount"));
                LocalDate date = LocalDate.parse(row.get("GeneratedDate"));
                String method = row.get("Method");
                String status = row.get("Status");
                String applicantNRIC = row.get("ApplicantNRIC");
                String projectName = row.get("ProjectName");
                String flatType = row.get("FlatType");

                list.add(new Invoice(paymentId, amount, date, method, status, applicantNRIC, projectName, flatType));
            } catch (Exception ignored) {}
        }
        return list;
    }

    public static void saveAll(List<Invoice> invoices) {
        List<Map<String, String>> rows = new ArrayList<>();

        rows.add(createHeaderRow()); // manually add header row first

        for (Invoice i : invoices) {
            Map<String, String> row = new LinkedHashMap<>();
            row.put("InvoiceID", String.valueOf(i.getPaymentId()));
            row.put("PaymentID", String.valueOf(i.getPaymentId()));
            row.put("ApplicantNRIC", i.getApplicantNRIC());
            row.put("ProjectName", i.getProjectName());
            row.put("FlatType", i.getFlatType());
            row.put("Amount", String.valueOf(i.getAmount()));
            row.put("Method", i.getMethod());
            row.put("Status", i.getStatus());
            row.put("GeneratedDate", i.getDate().toString());
            rows.add(row);
        }

        write(CSV_PATH, rows);
    }

    private static Map<String, String> createHeaderRow() {
        Map<String, String> header = new LinkedHashMap<>();
        header.put("InvoiceID", "InvoiceID");
        header.put("PaymentID", "PaymentID");
        header.put("ApplicantNRIC", "ApplicantNRIC");
        header.put("ProjectName", "ProjectName");
        header.put("FlatType", "FlatType");
        header.put("Amount", "Amount");
        header.put("Method", "Method");
        header.put("Status", "Status");
        header.put("GeneratedDate", "GeneratedDate");
        return header;
    }
}
