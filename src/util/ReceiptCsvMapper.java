package src.util;

import src.model.Invoice;
import src.model.PaymentMethod;
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
                String methodRaw = row.get("Method");
                String status = row.get("Status");
                int invoiceId = Integer.parseInt(row.get("InvoiceID"));
                LocalDate issuedDate = LocalDate.parse(row.get("IssuedDate"));

                // Convert method string to PaymentMethod enum safely
                PaymentMethod method = Arrays.stream(PaymentMethod.values())
                        .filter(pm -> pm.toString().equalsIgnoreCase(methodRaw))
                        .findFirst()
                        .orElseThrow(() -> new IllegalArgumentException("Invalid payment method: " + methodRaw));

                Invoice invoice = new Invoice(invoiceId, amountPaid, issuedDate, method, status, applicantNRIC, projectName, flatType);

                // Construct the Receipt (dummy age and status since not stored in CSV)
                Receipt receipt = new Receipt(applicantName, applicantNRIC, 0, "", projectName, "", flatType, invoice);
                receipt.setIssuedDate(issuedDate);
                receipt.setReceiptId(receiptId);

                list.add(receipt);
            } catch (Exception ignored) {
                ignored.printStackTrace(); // You can log instead of printing in production
            }
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
            row.put("Method", r.getInvoice().getMethodLabel());
            row.put("Status", r.getInvoice().getStatus());
            rows.add(row);
        }

        write(CSV_PATH, rows);
    }
}
