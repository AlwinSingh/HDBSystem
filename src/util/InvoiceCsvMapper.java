package src.util;

import src.model.Invoice;
import src.model.PaymentMethod;
import src.repository.InvoiceRepository;

import java.time.LocalDate;
import java.util.*;
import static src.util.CsvUtil.*;

/**
 * Utility class for handling reading and writing {@link Invoice} objects to the CSV file.
 * Provides methods to load, save, append, and update invoices.
 */
public class InvoiceCsvMapper implements InvoiceRepository {
    private static final String CSV_PATH = FilePath.INVOICE_LIST_FILE;

    /**
     * Loads all invoices from the CSV file and parses them into Invoice objects.
     *
     * @return List of {@link Invoice}
     */
    public List<Invoice> loadAll() {
        List<Map<String, String>> rows = read(CSV_PATH);
        List<Invoice> list = new ArrayList<>();

        for (Map<String, String> row : rows) {
            try {
                String paymentIdStr = row.getOrDefault("PaymentID", "").trim();
                String amountStr = row.getOrDefault("Amount", "").trim();
                String dateStr = row.getOrDefault("GeneratedDate", "").trim();
                String methodRaw = row.getOrDefault("Method", "").trim();
                String status = row.getOrDefault("Status", "").trim();
                String applicantNRIC = row.getOrDefault("ApplicantNRIC", "").trim();
                String projectName = row.getOrDefault("ProjectName", "").trim();
                String flatType = row.getOrDefault("FlatType", "").trim();

                // Skip rows missing essential info
                if (paymentIdStr.isEmpty() || amountStr.isEmpty() || dateStr.isEmpty()) {
                    continue;
                }

                int paymentId = Integer.parseInt(paymentIdStr);
                double amount = Double.parseDouble(amountStr);
                LocalDate date = LocalDate.parse(dateStr);

                PaymentMethod method = Arrays.stream(PaymentMethod.values())
                        .filter(pm -> pm.toString().equalsIgnoreCase(methodRaw))
                        .findFirst()
                        .orElse(PaymentMethod.PAYNOW);  // default fallback

                list.add(new Invoice(paymentId, amount, date, method, status, applicantNRIC, projectName, flatType));
            } catch (Exception e) {
                System.err.println("⚠️ Skipping malformed row in InvoiceList.csv: " + e.getMessage());
            }
        }
        return list;
    }

    /**
     * Saves a list of invoices to the CSV file, overwriting all existing records.
     *
     * @param invoices The invoices to write.
     */
    public void saveAll(List<Invoice> invoices) {
        List<Map<String, String>> rows = new ArrayList<>();

        for (Invoice i : invoices) {
            Map<String, String> row = new LinkedHashMap<>();
            row.put("InvoiceID", String.valueOf(i.getPaymentId()));
            row.put("PaymentID", String.valueOf(i.getPaymentId()));
            row.put("ApplicantNRIC", i.getApplicantNRIC());
            row.put("ProjectName", i.getProjectName());
            row.put("FlatType", i.getFlatType());
            row.put("Amount", String.valueOf(i.getAmount()));
            row.put("Method", i.getMethodLabel());  // from enum
            row.put("Status", i.getStatus());
            row.put("GeneratedDate", i.getDate().toString());
            rows.add(row);
        }

        write(CSV_PATH, rows);
    }

    /**
     * Appends a new invoice record to the CSV file.
     *
     * @param invoice The invoice to add.
     */
    public void append(Invoice invoice) {
        Map<String, String> row = new LinkedHashMap<>();
        row.put("InvoiceID", String.valueOf(invoice.getPaymentId()));
        row.put("PaymentID", String.valueOf(invoice.getPaymentId()));
        row.put("ApplicantNRIC", invoice.getApplicantNRIC());
        row.put("ProjectName", invoice.getProjectName());
        row.put("FlatType", invoice.getFlatType());
        row.put("Amount", String.valueOf(invoice.getAmount()));
        row.put("Method", invoice.getMethodLabel());
        row.put("Status", invoice.getStatus());
        row.put("GeneratedDate", invoice.getDate().toString());
    
        CsvUtil.append(CSV_PATH, row);
    }

    /**
     * Updates an invoice in the CSV file by replacing the matching PaymentID.
     *
     * @param updated The updated invoice to save.
     */
    public void update(Invoice updated) {
        List<Invoice> all = loadAll();
        for (int i = 0; i < all.size(); i++) {
            if (all.get(i).getPaymentId() == updated.getPaymentId()) {
                all.set(i, updated);
                break;
            }
        }
        saveAll(all);
    }
    
}
