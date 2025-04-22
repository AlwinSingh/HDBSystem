package src.model;

import src.repository.ApplicantRepository;
import src.repository.InvoiceRepository;
import src.repository.ProjectRepository;
import src.repository.ReceiptRepository;
import src.util.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Generates booking reports based on applications, invoices, and receipts.
 * Supports filtering by project or manager.
 */
public class ReportGenerator {
    private static final ApplicantRepository applicantRepository = new ApplicantCsvMapper();
    private static final InvoiceRepository invoiceRepository = new InvoiceCsvMapper();
    private static final ProjectRepository projectRepository = new ProjectCsvMapper();
    private static final ReceiptRepository receiptRepository = new ReceiptCsvMapper();
    /**
     * Generates reports for all applicants who have successfully booked flats.
     * Each report includes invoice and optional receipt details.
     *
     * @return A list of report entries.
     */
    public List<Report> generateAllReports() {
        List<Applicant> applicants = applicantRepository.loadAll();
        List<Invoice> invoices = invoiceRepository.loadAll();
        List<Receipt> receipts = receiptRepository.loadAll();
        List<Project> allProjects = projectRepository.loadAll();  // 🔥 Load complete projects
        List<Report> reports = new ArrayList<>();
    
        for (Applicant a : applicants) {
            Application app = a.getApplication();
            if (app == null || !"BOOKED".equalsIgnoreCase(app.getStatus())) continue;
    
            Project appProject = app.getProject();
            if (appProject != null) {
                Project full = allProjects.stream()
                    .filter(p -> p.getProjectName().equalsIgnoreCase(appProject.getProjectName()))
                    .findFirst()
                    .orElse(null);
    
                if (full != null) app.setProject(full);
            }
    
            Invoice inv = invoices.stream()
                .filter(i -> i.getApplicantNRIC().equalsIgnoreCase(a.getNric()))
                .findFirst()
                .orElse(null);
    
            if (inv == null) continue;
    
            Receipt receipt = receipts.stream()
                .filter(r -> r.getApplicantNRIC().equalsIgnoreCase(a.getNric()))
                .findFirst()
                .orElse(null);
    
            reports.add(new Report(
                a.getName(),
                a.getNric(),
                a.getAge(),
                a.getMaritalStatus(),
                app.getProject().getProjectName(),
                app.getFlatType(),
                app.getFlatPrice(), // ✅ Now correctly returns the price
                app.getStatus(),
                inv.getStatus(),
                inv.getDate(),
                receipt != null ? receipt.getReceiptId() : null
            ));
        }
    
        return reports;
    }

    /**
     * Filters reports for a specific project name.
     *
     * @param projectName The project to filter by.
     * @return A list of reports belonging to the specified project.
     */
    public List<Report> generateReportsByProject(String projectName) {
        return generateAllReports().stream()
            .filter(r -> r.getProjectName().equalsIgnoreCase(projectName))
            .collect(Collectors.toList());
    }

    /**
     * Filters reports based on projects assigned to a specific HDB manager.
     *
     * @param manager The manager whose projects should be included.
     * @return A list of reports across the manager's projects.
     */
    public List<Report> generateReportsByManager(HDBManager manager) {
        List<Project> allProjects = projectRepository.loadAll();

        // Collect projects handled by this manager
        Set<String> projectNames = allProjects.stream()
            .filter(p -> p.getManager() != null && p.getManager().getNric().equalsIgnoreCase(manager.getNric()))
            .map(Project::getProjectName)
            .collect(Collectors.toSet());

        return generateAllReports().stream()
            .filter(r -> projectNames.contains(r.getProjectName()))
            .collect(Collectors.toList());
    }
}
