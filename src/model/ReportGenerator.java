package src.model;

import src.util.*;
import java.util.*;
import java.util.stream.Collectors;

public class ReportGenerator {

    public List<Report> generateAllReports() {
        List<Applicant> applicants = ApplicantCsvMapper.loadAll();
        List<Invoice> invoices = InvoiceCsvMapper.loadAll();
        List<Receipt> receipts = ReceiptCsvMapper.loadAll();
        List<Project> allProjects = ProjectCsvMapper.loadAll();  // 🔥 Load complete projects
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
    

    public List<Report> generateReportsByProject(String projectName) {
        return generateAllReports().stream()
            .filter(r -> r.getProjectName().equalsIgnoreCase(projectName))
            .collect(Collectors.toList());
    }

    public List<Report> generateReportsByManager(HDBManager manager) {
        List<Project> allProjects = ProjectCsvMapper.loadAll();

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
