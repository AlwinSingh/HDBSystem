package src.util;

import src.model.Receipt;
import java.util.*;
import static src.util.CsvUtil.*;

public class ReceiptCsvMapper {
    private static final String CSV_PATH = "data/ReceiptList.csv";

    public static List<Receipt> loadAll() {
        List<Map<String, String>> rows = read(CSV_PATH);
        List<Receipt> list = new ArrayList<>();
        for (Map<String, String> row : rows) {
            try {
                // Load manually if needed, skip here
            } catch (Exception ignored) {}
        }
        return list;
    }

    public static void saveAll(List<Receipt> receipts) {
        List<Map<String, String>> rows = new ArrayList<>();
        rows.add(createHeaderRow());

        for (Receipt r : receipts) {
            Map<String, String> row = new LinkedHashMap<>();
            row.put("ReceiptID", r.getReceiptId());
            row.put("ApplicantName", r.getApplicantName());
            row.put("ApplicantNRIC", r.getApplicantNRIC());
            row.put("ProjectName", r.getProjectName());
            row.put("FlatTypeBooked", r.getFlatTypeBooked());
            row.put("IssuedDate", r.getIssuedDate().toString());
            row.put("InvoiceID", String.valueOf(r.getInvoice().getPaymentId()));
            row.put("AmountPaid", String.valueOf(r.getInvoice().getAmount()));
            row.put("Method", r.getInvoice().getMethod());
            row.put("Status", r.getInvoice().getStatus());
            rows.add(row);
        }

        write(CSV_PATH, rows);
    }

    private static Map<String, String> createHeaderRow() {
        Map<String, String> header = new LinkedHashMap<>();
        header.put("ReceiptID", "ReceiptID");
        header.put("ApplicantName", "ApplicantName");
        header.put("ApplicantNRIC", "ApplicantNRIC");
        header.put("ProjectName", "ProjectName");
        header.put("FlatTypeBooked", "FlatTypeBooked");
        header.put("IssuedDate", "IssuedDate");
        header.put("InvoiceID", "InvoiceID");
        header.put("AmountPaid", "AmountPaid");
        header.put("Method", "Method");
        header.put("Status", "Status");
        return header;
    }
}