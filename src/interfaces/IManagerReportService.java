package src.interfaces;

import java.util.Scanner;
import src.model.HDBManager;

/**
 * Interface defining report-related operations for HDB Managers.
 * Supports CLI-based generation and filtering of applicant booking reports.
 */
public interface IManagerReportService {
    
    /**
     * Launches the report dashboard for generating, viewing,
     * and filtering applicant booking reports.
     *
     * @param manager The logged-in manager.
     * @param sc      Scanner for console input.
     */
    void generateReports(HDBManager manager, Scanner sc);
}
