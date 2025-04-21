package src.service;

import src.model.Report;
import src.model.ReportGenerator;
import src.repository.ReportRepository;
import src.util.ReceiptCsvMapper;
import src.util.ReportCsvMapper;

import java.time.LocalDate;
import java.util.List;


/**
 * Service class for handling report generation, filtering, and display.
 * Interacts with {@link ReportCsvMapper} and {@link ReportGenerator} to manage booking reports.
 */
public class ReportService {
    private static final ReportRepository reportRepository = new ReportCsvMapper();
    private static List<Report> reports = reportRepository.loadAll();

    /**
     * Generates reports for all bookings and saves them to the CSV.
     * Overwrites existing report data in memory and file.
     */
    public static void generateAndSaveReports() {
        ReportGenerator generator = new ReportGenerator();
        reports = generator.generateAllReports();
        reportRepository.saveAll(reports);
        System.out.println("✅ Reports generated and saved.");
    }

    /**
     * Returns all reports currently loaded in memory.
     *
     * @return List of {@link Report} objects.
     */
    public static List<Report> getAllReports() {
        return reports;
    }

    /**
     * Filters reports by project name.
     *
     * @param projectName The project name to search for.
     * @return List of reports for that project.
     */
    public static List<Report> getReportsByProject(String projectName) {
        return reports.stream()
                .filter(r -> r.getProjectName().equalsIgnoreCase(projectName))
                .toList();
    }

    /**
     * Filters reports by flat type (e.g., 2-Room, 3-Room).
     *
     * @param flatType The flat type to filter by.
     * @return List of matching reports.
     */
    public static List<Report> getReportsByFlatType(String flatType) {
        return reports.stream()
            .filter(r -> r.getFlatTypeBooked().equalsIgnoreCase(flatType))
            .toList();
    }

    /**
     * Filters reports by payment status (e.g., Processed, Awaiting Payment).
     *
     * @param status Payment status to filter by.
     * @return List of matching reports.
     */
    public static List<Report> getReportsByPaymentStatus(String status) {
        return reports.stream()
            .filter(r -> r.getPaymentStatus().equalsIgnoreCase(status))
            .toList();
    }

    /**
     * Filters reports within a booking date range.
     *
     * @param start Start date (inclusive).
     * @param end   End date (inclusive).
     * @return List of matching reports.
     */
    public static List<Report> getReportsByBookingDateRange(LocalDate start, LocalDate end) {
        return reports.stream()
            .filter(r -> r.getBookingDate() != null)
            .filter(r -> !r.getBookingDate().isBefore(start) && !r.getBookingDate().isAfter(end))
            .toList();
    }

    /**
     * Prints all available reports to the console.
     * If no reports exist, shows a message.
     */
    public static void printAllReports() {
        if (reports.isEmpty()) {
            System.out.println("📭 No reports available.");
            return;
        }

        for (Report r : reports) {
            System.out.println("\n" + r);
        }
    }

    /**
     * Prints a list of filtered reports to the console.
     *
     * @param filteredReports The list of reports to display.
     */
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
