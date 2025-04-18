package src.service;

import src.model.Report;
import src.model.ReportGenerator;
import src.util.ReportCsvMapper;

import java.time.LocalDate;
import java.util.List;

public class ReportService {

    private static List<Report> reports = ReportCsvMapper.loadAll();

    // Generate reports and persist them
    public static void generateAndSaveReports() {
        ReportGenerator generator = new ReportGenerator();
        reports = generator.generateAllReports();
        ReportCsvMapper.saveAll(reports);
        System.out.println("✅ Reports generated and saved.");
    }

    // Get all reports
    public static List<Report> getAllReports() {
        return reports;
    }

    // Optional: filter by project
    public static List<Report> getReportsByProject(String projectName) {
        return reports.stream()
                .filter(r -> r.getProjectName().equalsIgnoreCase(projectName))
                .toList();
    }

    // Filter by flat type (e.g., 2-Room, 3-Room)
    public static List<Report> getReportsByFlatType(String flatType) {
        return reports.stream()
            .filter(r -> r.getFlatTypeBooked().equalsIgnoreCase(flatType))
            .toList();
    }

    // Filter by payment status (e.g., Processed, Awaiting Payment)
    public static List<Report> getReportsByPaymentStatus(String status) {
        return reports.stream()
            .filter(r -> r.getPaymentStatus().equalsIgnoreCase(status))
            .toList();
    }

    // Filter by booking date range
    public static List<Report> getReportsByBookingDateRange(LocalDate start, LocalDate end) {
        return reports.stream()
            .filter(r -> r.getBookingDate() != null)
            .filter(r -> !r.getBookingDate().isBefore(start) && !r.getBookingDate().isAfter(end))
            .toList();
    }

    // Optional: pretty print
    public static void printAllReports() {
        if (reports.isEmpty()) {
            System.out.println("📭 No reports available.");
            return;
        }

        for (Report r : reports) {
            System.out.println("\n" + r);
        }
    }

    public static void printReports(List<Report> filteredReports) {
        if (filteredReports == null || filteredReports.isEmpty()) {
            System.out.println("📭 No reports matched the selected filter.");
            return;
        }
    
        for (Report r : filteredReports) {
            System.out.println("\n" + r);
        }
    }
    
}
