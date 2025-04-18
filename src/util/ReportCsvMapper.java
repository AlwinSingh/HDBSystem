package src.util;

import src.model.Report;
import java.time.LocalDate;
import java.util.*;
import static src.util.CsvUtil.*;

public class ReportCsvMapper {
    private static final String CSV_PATH = FilePath.REPORT_LIST_FILE;

    public static List<Report> loadAll() {
        List<Map<String, String>> rows = read(CSV_PATH);
        List<Report> list = new ArrayList<>();

        for (Map<String, String> row : rows) {
            try {
                String applicantName = row.get("ApplicantName");
                String applicantNRIC = row.get("ApplicantNRIC");
                int age = Integer.parseInt(row.get("Age"));
                String maritalStatus = row.get("MaritalStatus");
                String projectName = row.get("ProjectName");
                String flatTypeBooked = row.get("FlatTypeBooked");
                double flatPrice = Double.parseDouble(row.get("FlatPrice"));
                String bookingStatus = row.get("BookingStatus");
                String paymentStatus = row.get("PaymentStatus");

                LocalDate bookingDate  = row.get("BookingDate").isEmpty() ? null :LocalDate.parse(row.get("BookingDate"));
                String receiptId = row.get("ReceiptID").isEmpty() ? null:row.get("ReceiptID");

                list.add(new Report(
                    applicantName, applicantNRIC, age, maritalStatus,
                    projectName, flatTypeBooked, flatPrice,
                    bookingStatus, paymentStatus, bookingDate, receiptId
                ));
            } catch (Exception ignored) {}
        }

        return list;
    }

    public static void saveAll(List<Report> reports) {
        List<Map<String, String>> rows = new ArrayList<>();
        for (Report r : reports) {
            Map<String, String> row = new LinkedHashMap<>();
            row.put("ApplicantName", r.getApplicantName());
            row.put("ApplicantNRIC", r.getApplicantNRIC());
            row.put("Age", String.valueOf(r.getAge()));
            row.put("MaritalStatus", r.getMaritalStatus());
            row.put("ProjectName", r.getProjectName());
            row.put("FlatTypeBooked", r.getFlatTypeBooked());
            row.put("FlatPrice", String.valueOf(r.getFlatPrice()));
            row.put("BookingStatus", r.getBookingStatus());
            row.put("PaymentStatus", r.getPaymentStatus());
            row.put("BookingDate", r.getBookingDate() != null ? r.getBookingDate().toString() : "");
            row.put("ReceiptID", r.getReceiptId() != null ? r.getReceiptId() : "");
            rows.add(row);
        }

        write(CSV_PATH, rows);
    }
}
