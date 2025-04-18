package src.util;

import src.model.Invoice;
import src.model.Receipt;

import java.time.LocalDate;
import java.util.*;
import static src.util.CsvUtil.*;

public class ReceiptCsvMapper {
    private static final String CSV_PATH = FilePath.RECEIPT_LIST_FILE;

    public static List<Receipt> loadAll() {
    List<Map<String, String>> rows = read(CSV_PATH);
    List<Receipt> list = new ArrayList<>();

    for (Map<String, String> row : rows) {
        try {
            String applicantName = row.get("ApplicantName");
            String applicantNRIC = row.get("ApplicantNRIC");
            String projectName = row.get("ProjectName");
            String flatType = row.get("FlatTypeBooked");
            String receiptId = row.get("ReceiptID");

            double amountPaid = Double.parseDouble(row.get("AmountPaid"));
            String method = row.get("Method");
            String status = row.get("Status");
            int invoiceId = Integer.parseInt(row.get("InvoiceID"));
            LocalDate issuedDate = LocalDate.parse(row.get("IssuedDate"));

            // You must reconstruct the Invoice manually
            Invoice invoice = new Invoice(invoiceId, amountPaid, issuedDate, method, status, applicantNRIC, projectName, flatType);

            // Construct the Receipt
            Receipt receipt = new Receipt(applicantName, applicantNRIC, 0, "", projectName, "", flatType, invoice);
            receipt.setIssuedDate(issuedDate);  // optional if constructor already sets it
            receipt.setReceiptId(receiptId);    // optional if constructor already sets it

            list.add(receipt);
        } catch (Exception ignored) {}
    }
    return list;
}


    public static void saveAll(List<Receipt> receipts) {
        List<Map<String, String>> rows = new ArrayList<>();

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
}