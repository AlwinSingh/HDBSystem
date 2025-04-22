package src.service;

import java.util.List;
import java.util.Scanner;

import src.interfaces.IApplicantApplicationService;
import src.model.Applicant;
import src.model.Application;
import src.model.HDBOfficer;
import src.model.Project;
import src.repository.ApplicantRepository;
import src.repository.ProjectRepository;
import src.util.ApplicantCsvMapper;
import src.util.ProjectCsvMapper;


/**
 * Handles application-related features for applicants in the BTO system.
 * 
 * - Apply for eligible projects
 * - Submit a flat type
 * - Withdraw their application if needed
 * 
 * It checks for application status and ensures applicants don’t apply twice
 * or withdraw after booking a flat.
 */
public class ApplicantApplicationService implements IApplicantApplicationService {
    ApplicantProjectViewService projectViewService = new ApplicantProjectViewService();
    private static final ApplicantRepository applicantRepository = new ApplicantCsvMapper();
    private static final ProjectRepository projectRepository = new ProjectCsvMapper();
    

    /**
     * Guides the applicant through applying to a project and selecting a flat type.
     */
    @Override
    public void applyForProject(Applicant applicant, Scanner sc){
    
        if (applicant.getApplication() != null) {
            System.out.println("⚠️ You already have an active application for: "
                    + applicant.getApplication().getProject().getProjectName()
                    + " (Status: " + applicant.getApplication().getStatus() + ")");
            return;
        }
    
        List<Project> eligible = projectViewService.getEligibleProjects(applicant);
        if (eligible.isEmpty()) {
            System.out.println("❌ No eligible projects available.");
            return;
        }
    
        for (int i = 0; i < eligible.size(); i++) {
            Project p = eligible.get(i);
            System.out.printf("[%d] %s (%s)\n", i + 1, p.getProjectName(), p.getNeighborhood());
        }
    
        System.out.print("Enter project number to apply: ");
        int choice = Integer.parseInt(sc.nextLine().trim()) - 1;
        if (choice < 0 || choice >= eligible.size()) {
            System.out.println("❌ Invalid selection.");
            return;
        }
    
        Project selected = eligible.get(choice);
    
        if (applicant instanceof HDBOfficer officer) {
            Project assigned = officer.getAssignedProject();
            String status = officer.getRegistrationStatus();
        
            if (assigned != null &&
                assigned.getProjectName().equalsIgnoreCase(selected.getProjectName()) &&
                ("PENDING".equalsIgnoreCase(status) || "APPROVED".equalsIgnoreCase(status))) {
                System.out.println("❌ You are already handling this project as an officer.");
                return;
            }
        }
        
    
        String flatType = "2-Room";
        if ("Married".equalsIgnoreCase(applicant.getMaritalStatus())) {
            System.out.print("Choose flat type (2-Room/3-Room): ");
            flatType = sc.nextLine().trim();
        }
    
        System.out.print("Submit application for " + selected.getProjectName()
                + " (" + flatType + ")? (Y/N): ");
        if (!sc.nextLine().trim().equalsIgnoreCase("Y")) {
            System.out.println("🔁 Application cancelled.");
            return;
        }
    
        boolean ok = submitApplication(applicant, selected, flatType);
        if (ok) {
            System.out.println("✅ Application submitted. Status: " + Applicant.AppStatusType.PENDING.name() + ".");
        } else {
            System.out.println("❌ Application failed.");
        }
    }

    /**
     * Applies for a selected project with the chosen flat type and saves updates to CSV.
     *
     * @return True if successful; false if already applied or any issues occur.
     */
    @Override
    public boolean submitApplication(Applicant applicant, Project project, String flatType) {
        if (applicant instanceof HDBOfficer officer) {
            boolean exists = applicantRepository.loadAll().stream()
                .anyMatch(a -> a.getNric().equalsIgnoreCase(officer.getNric()));
            if (!exists) {
                applicantRepository.save(officer);
            }
        }
    
        boolean success = applicant.applyForProject(project, flatType);
        if (!success) return false;
    
        applicantRepository.update(applicant);
    
        project.getApplicantNRICs().add(applicant.getNric());
        projectRepository.updateProject(project);
    
        return true;
    }
    
    /**
     * Checks if the applicant is allowed to withdraw from their application.
     */
    @Override
    public boolean canWithdraw(Applicant applicant) {
        Application app = applicant.getApplication();
        if (app == null) {
            System.out.println("❌ No application to withdraw.");
            return false;
        }

        String status = app.getStatus();
        if (Applicant.AppStatusType.WITHDRAW_REQUESTED.name().equalsIgnoreCase(status)) {
            System.out.println("ℹ️ Withdrawal already requested.");
            return false;
        }

        if (Applicant.AppStatusType.BOOKED.name().equalsIgnoreCase(status)) {
            System.out.println("❌ You cannot withdraw after booking.");
            return false;
        }

        return true;
    }

    /**
     * Flags the applicant's application for withdrawal and persists the change.
     */
    @Override
    public void submitWithdrawalRequest(Applicant applicant) {
        applicant.getApplication().setStatus(Applicant.AppStatusType.WITHDRAW_REQUESTED.name());
        applicantRepository.update(applicant);

    }

}
