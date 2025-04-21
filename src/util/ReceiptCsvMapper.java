package src.util;

import src.model.Invoice;
import src.model.PaymentMethod;
import src.model.Receipt;

import java.time.LocalDate;
import java.util.*;
import static src.util.CsvUtil.*;

public class ReceiptCsvMapper {
    private static final String CSV_PATH = FilePath.RECEIPT_LIST_FILE;

    /**
     * Loads all receipt records from the CSV file and constructs Receipt objects.
     * Skips rows with missing or malformed fields.
     *
     * @return List of valid Receipt objects.
     */
    public static List<Receipt> loadAll() {
        List<Map<String, String>> rows = read(CSV_PATH);
        List<Receipt> list = new ArrayList<>();

        for (Map<String, String> row : rows) {
            try {
                String applicantName = row.getOrDefault("ApplicantName", "").trim();
                String applicantNRIC = row.getOrDefault("ApplicantNRIC", "").trim();
                String projectName = row.getOrDefault("ProjectName", "").trim();
                String flatType = row.getOrDefault("FlatTypeBooked", "").trim();
                String receiptId = row.getOrDefault("ReceiptID", "").trim();

                String amountStr = row.getOrDefault("AmountPaid", "").trim();
                String methodRaw = row.getOrDefault("Method", "").trim();
                String status = row.getOrDefault("Status", "").trim();
                String invoiceIdStr = row.getOrDefault("InvoiceID", "").trim();
                String issuedDateStr = row.getOrDefault("IssuedDate", "").trim();

                // Skip if critical fields are missing
                if (amountStr.isEmpty() || invoiceIdStr.isEmpty() || issuedDateStr.isEmpty()) continue;

                double amountPaid = Double.parseDouble(amountStr);
                int invoiceId = Integer.parseInt(invoiceIdStr);
                LocalDate issuedDate = LocalDate.parse(issuedDateStr);

                PaymentMethod method = Arrays.stream(PaymentMethod.values())
                        .filter(pm -> pm.name().equalsIgnoreCase(methodRaw))
                        .findFirst()
                        .orElse(PaymentMethod.PAYNOW); // fallback method

                Invoice invoice = new Invoice(invoiceId, amountPaid, issuedDate, method, status, applicantNRIC, projectName, flatType);

                Receipt receipt = new Receipt(applicantName, applicantNRIC, 0, "", projectName, "", flatType, invoice);
                receipt.setIssuedDate(issuedDate);
                receipt.setReceiptId(receiptId);

                list.add(receipt);
            } catch (Exception e) {
                System.err.println("Skipping row in ReceiptList.csv: " + e.getMessage());
            }
        }

        return list;
    }

    /**
     * Writes the full list of receipts to the CSV file, replacing all existing records.
     *
     * @param receipts The receipts to write.
     */
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

    /**
     * Appends a single receipt record to the CSV file.
     *
     * @param r The Receipt object to append.
     */
    public static void append(Receipt r) {
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
        CsvUtil.append(FilePath.RECEIPT_LIST_FILE, row);
    }
}
