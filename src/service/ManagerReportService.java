package src.service;

import java.time.LocalDate;
import java.util.Scanner;

import src.model.HDBManager;

public class ManagerReportService {

    /**
     * Allows managers to generate and view booking reports based on filters.
     *
     * @param manager The manager.
     * @param sc      Scanner for input.
     */
    public static void generateReports(HDBManager manager, Scanner sc) {
        while (true) {
            System.out.println("\n📊 Generate Applicant Booking Reports");
            System.out.println(" [1] 🔄 Generate Reports");
            System.out.println(" [2] 📋 View All Reports");
            System.out.println(" [3] 🔎 Filter by Project Name");
            System.out.println(" [4] 🏠 Filter by Flat Type");
            System.out.println(" [5] 💳 Filter by Payment Status");
            System.out.println(" [6] 📅 Filter by Booking Date Range");
            System.out.println(" [0] 🔙 Back");
            System.out.print("Enter choice: ");
            String choice = sc.nextLine().trim();
    
            switch (choice) {
                case "1" -> ReportService.generateAndSaveReports();
    
                case "2" -> {
                    if (ReportService.getAllReports().isEmpty()) {
                        System.out.println("📭 No reports available. Please generate reports first.");
                    } else {
                        ReportService.printAllReports();
                    }
                }
    
                case "3" -> {
                    if (ReportService.getAllReports().isEmpty()) {
                        System.out.println("📭 No reports available to filter. Please generate reports first.");
                        break;
                    }
                    System.out.print("Enter project name to filter: ");
                    String name = sc.nextLine().trim();
                    var filtered = ReportService.getReportsByProject(name);
                    if (filtered.isEmpty()) {
                        System.out.println("📭 No reports found for project: " + name);
                    } else {
                        ReportService.printReports(filtered);
                    }
                }
    
                case "4" -> {
                    if (ReportService.getAllReports().isEmpty()) {
                        System.out.println("📭 No reports available to filter. Please generate reports first.");
                        break;
                    }
                    System.out.print("Enter flat type to filter (e.g., 2-Room): ");
                    String type = sc.nextLine().trim();
                    var filtered = ReportService.getReportsByFlatType(type);
                    if (filtered.isEmpty()) {
                        System.out.println("📭 No reports found for flat type: " + type);
                    } else {
                        ReportService.printReports(filtered);
                    }
                }
    
                case "5" -> {
                    if (ReportService.getAllReports().isEmpty()) {
                        System.out.println("📭 No reports available to filter. Please generate reports first.");
                        break;
                    }
                    System.out.print("Enter payment status to filter (Processed/Awaiting Payment): ");
                    String status = sc.nextLine().trim();
                    var filtered = ReportService.getReportsByPaymentStatus(status);
                    if (filtered.isEmpty()) {
                        System.out.println("📭 No reports found for payment status: " + status);
                    } else {
                        ReportService.printReports(filtered);
                    }
                }
    
                case "6" -> {
                    if (ReportService.getAllReports().isEmpty()) {
                        System.out.println("📭 No reports available to filter. Please generate reports first.");
                        break;
                    }
                    try {
                        System.out.print("Enter start date (yyyy-MM-dd): ");
                        LocalDate start = LocalDate.parse(sc.nextLine().trim());
                        System.out.print("Enter end date (yyyy-MM-dd): ");
                        LocalDate end = LocalDate.parse(sc.nextLine().trim());
                        var filtered = ReportService.getReportsByBookingDateRange(start, end);
                        if (filtered.isEmpty()) {
                            System.out.println("📭 No reports found in that date range.");
                        } else {
                            ReportService.printReports(filtered);
                        }
                    } catch (Exception e) {
                        System.out.println("❌ Invalid date format.");
                    }
                }
    
                case "0" -> {
                    System.out.println("🔙 Returning to manager menu...");
                    return;
                }
    
                default -> System.out.println("❌ Invalid choice.");
            }
        }
    }

}
