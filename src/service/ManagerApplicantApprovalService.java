package src.service;

import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

import src.interfaces.IManagerApplicantApprovalService;
import src.model.Applicant;
import src.model.HDBManager;
import src.model.Project;
import src.repository.ApplicantRepository;
import src.repository.ProjectRepository;
import src.util.ApplicantCsvMapper;
import src.util.CsvUtil;
import src.util.FilePath;
import src.util.ProjectCsvMapper;

/**
 * Provides services for HDB managers to review, approve/reject applicant submissions,
 * and handle applicant withdrawal requests for their projects.
 */
public class ManagerApplicantApprovalService implements IManagerApplicantApprovalService {
    private static final ProjectRepository projectRepository = new ProjectCsvMapper();
    private static final ApplicantRepository applicantRepository = new ApplicantCsvMapper();

    /**
     * Displays all applicant submissions tied to the manager's projects.
     *
     * @param manager The logged-in manager.
     */
    @Override
    public void viewApplicantApplications(HDBManager manager) {
        List<Map<String, String>> applicants = CsvUtil.read(FilePath.APPLICANT_LIST_FILE);
        List<Map<String, String>> projects = CsvUtil.read(FilePath.PROJECT_LIST_FILE);
    
        Set<String> myProjectNames = projects.stream()
            .filter(p -> manager.getNric().equalsIgnoreCase(p.get("ManagerNRIC")))
            .map(p -> p.get("Project Name"))
            .collect(Collectors.toSet());
    
        if (myProjectNames.isEmpty()) {
            System.out.println("❌ You haven’t created any projects yet.");
            return;
        }
    
        List<Map<String, String>> relevantApplicants = applicants.stream()
            .filter(a -> myProjectNames.contains(a.getOrDefault("AppliedProjectName", "")))
            .toList();
    
        if (relevantApplicants.isEmpty()) {
            System.out.println("📭 No applicant applications for your projects.");
            return;
        }
    
        System.out.println("\n📄 Applicant Applications for Your Projects:");
        for (int i = 0; i < relevantApplicants.size(); i++) {
            Map<String, String> app = relevantApplicants.get(i);
            System.out.printf("\n[%d] %s (NRIC: %s)\n", i + 1, app.get("Name"), app.get("NRIC"));
            System.out.println("   🏘 Project: " + app.get("AppliedProjectName"));
            System.out.println("   🏠 Flat Type: " + app.get("FlatTypeApplied"));
            System.out.println("   📊 Status: " + app.get("ApplicationStatus"));
            System.out.println("   🎂 Age: " + app.get("Age"));
            System.out.println("   💍 Marital Status: " + app.get("Marital Status"));
        }
    }

    /**
     * Approves or rejects applicant submissions for the manager’s projects.
     *
     * @param manager The manager.
     * @param sc      Scanner for user input.
     */
    @Override
    public void handleApplicantApproval(HDBManager manager, Scanner sc) {
        List<Applicant> applicants = applicantRepository.loadAll();
        List<Project> projects = projectRepository.loadAll();

        Set<String> myProjectNames = projects.stream()
            .filter(p -> p.getManager() != null && p.getManager().getNric().equalsIgnoreCase(manager.getNric()))
            .map(Project::getProjectName)
            .collect(Collectors.toSet());

        List<Applicant> pendingApps = applicants.stream()
            .filter(a -> a.getApplication() != null)
            .filter(a -> myProjectNames.contains(a.getApplication().getProject().getProjectName()))
            .filter(a -> Applicant.AppStatusType.PENDING.name().equalsIgnoreCase(a.getApplication().getStatus()))
            .toList();

        if (pendingApps.isEmpty()) {
            System.out.println("📭 No pending applicant applications.");
            return;
        }

        for (int i = 0; i < pendingApps.size(); i++) {
            Applicant a = pendingApps.get(i);
            System.out.printf("[%d] %s (%s), Project: %s, Flat: %s\n", i + 1,
                a.getName(), a.getNric(),
                a.getApplication().getProject().getProjectName(),
                a.getApplication().getFlatType());
        }

        System.out.print("Select applicant to process (0 to cancel): ");
        int choice;
        try {
            choice = Integer.parseInt(sc.nextLine());
            if (choice == 0) return;
            if (choice < 1 || choice > pendingApps.size()) throw new Exception();
        } catch (Exception e) {
            System.out.println("❌ Invalid selection.");
            return;
        }

        Applicant selectedApp = pendingApps.get(choice - 1);
        String flatType = selectedApp.getApplication().getFlatType();
        String projName = selectedApp.getApplication().getProject().getProjectName();

        Project project = projects.stream()
            .filter(p -> p.getProjectName().equalsIgnoreCase(projName))
            .findFirst()
            .orElse(null);

        if (project == null) {
            System.out.println("❌ Project not found.");
            return;
        }

        System.out.print("Approve or Reject this application? (A/R): ");
        String decision = sc.nextLine().trim().toUpperCase();

        switch (decision) {
            case "A" -> {
                if (!approveApplicant(selectedApp, project, flatType)) return;
            }
            case "R" -> {
                System.out.print("Confirm rejection for " + selectedApp.getName() + " (Y/N): ");
                if (!sc.nextLine().trim().equalsIgnoreCase("Y")) {
                    System.out.println("❌ Rejection cancelled.");
                    return;
                }
                selectedApp.getApplication().setStatus(Applicant.AppStatusType.UNSUCCESSFUL.name());
                System.out.println("❌ Application rejected.");
            }
            default -> {
                System.out.println("❌ Invalid input. Use A or R.");
                return;
            }
        }

        applicantRepository.update(selectedApp);
        projectRepository.updateProject(project);
    }

    /**
     * Attempts to approve an applicant if the selected flat type has available units.
     * If successful, it updates the project and applicant records.
     *
     * @param applicant The applicant to approve.
     * @param project   The project to reserve a flat in.
     * @param flatType  The flat type to be reserved.
     * @return True if the application was approved, false if no units are available.
     */
    
    private boolean approveApplicant(Applicant applicant, Project project, String flatType) {
        int available = flatType.equalsIgnoreCase("2-Room")
            ? project.getAvailableFlats2Room()
            : project.getAvailableFlats3Room();

        if (available <= 0) {
            System.out.println("❌ No units left for this flat type.");
            return false;
        }

        if (flatType.equalsIgnoreCase("2-Room")) {
            project.setAvailableFlats2Room(available - 1);
        } else {
            project.setAvailableFlats3Room(available - 1);
        }

        applicant.getApplication().setStatus(Applicant.AppStatusType.SUCCESSFUL.name());
        System.out.println("✅ Application approved and flat reserved.");
        return true;
    }

    /**
     * Allows the manager to approve or reject pending withdrawal requests
     * made by applicants under their assigned projects.
     *
     * @param manager The logged-in HDB manager.
     * @param sc      The scanner for user input.
     */

    @Override
    public void handleWithdrawalRequests(HDBManager manager, Scanner sc) {
        List<Applicant> applicants = applicantRepository.loadAll();
        List<Project> projects = projectRepository.loadAll();
    
        Set<String> myProjects = projects.stream()
            .filter(p -> p.getManager() != null && p.getManager().getNric().equalsIgnoreCase(manager.getNric()))
            .map(Project::getProjectName)
            .collect(Collectors.toSet());
    
        List<Applicant> withdrawals = applicants.stream()
            .filter(a -> a.getApplication() != null)
            .filter(a -> Applicant.AppStatusType.WITHDRAW_REQUESTED.name().equalsIgnoreCase(a.getApplication().getStatus()))
            .filter(a -> myProjects.contains(a.getApplication().getProject().getProjectName()))
            .toList();
    
        if (withdrawals.isEmpty()) {
            System.out.println("📭 No pending withdrawal requests.");
            return;
        }
    
        for (int i = 0; i < withdrawals.size(); i++) {
            Applicant a = withdrawals.get(i);
            System.out.printf("[%d] %s (NRIC: %s) — Project: %s — Flat: %s\n", i + 1,
                a.getName(), a.getNric(),
                a.getApplication().getProject().getProjectName(),
                a.getApplication().getFlatType());
        }
    
        System.out.print("Select applicant to process (0 to cancel): ");
        int index;
        try {
            index = Integer.parseInt(sc.nextLine());
            if (index == 0) return;
            if (index < 1 || index > withdrawals.size()) throw new Exception();
        } catch (Exception e) {
            System.out.println("❌ Invalid selection.");
            return;
        }
    
        Applicant selected = withdrawals.get(index - 1);
    
        System.out.print("Approve or Reject withdrawal? (A/R): ");
        String decision = sc.nextLine().trim().toUpperCase();
    
        switch (decision) {
            case "A" -> {
                selected.getApplication().setStatus(Applicant.AppStatusType.WITHDRAWAL_APPROVED.name());
                System.out.println("✅ Withdrawal approved.");
            }
            case "R" -> {
                selected.getApplication().setStatus(Applicant.AppStatusType.WITHDRAWAL_REJECTED.name());
                System.out.println("❌ Withdrawal rejected.");
            }
            default -> {
                System.out.println("❌ Invalid input.");
                return;
            }
        }
    
        applicantRepository.update(selected);
    }

}
