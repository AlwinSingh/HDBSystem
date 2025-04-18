package src.service;

import src.model.Report;
import src.model.ReportGenerator;
import src.util.ReportCsvMapper;
import src.util.FilePath;

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
}
