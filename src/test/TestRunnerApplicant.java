package src.test;

import src.model.Applicant;
import src.model.Project;
import src.service.ApplicantService;
import src.service.ProjectService;
import src.service.UserService;
import src.util.CSVWriter;

import java.util.List;

public class TestRunnerApplicant {
    public static void main(String[] args) {
        System.out.println("=== Initializing Test Environment ===");

        // Load users and projects
        UserService userService = new UserService();
        ProjectService projectService = new ProjectService(userService);
        ApplicantService applicantService = new ApplicantService(projectService, userService);

        // Select test applicant
        String testNRIC = "S1234567A"; // Replace with valid NRIC in your CSV
        Applicant applicant = userService.getApplicantByNric(testNRIC);

        if (applicant == null) {
            System.out.println("❌ Applicant not found.");
            return;
        }

        System.out.println("✅ Loaded applicant: " + applicant.getName());
        System.out.println("   Age: " + applicant.getAge());
        System.out.println("   Marital Status: " + applicant.getMaritalStatus());

        // View eligible projects
        List<Project> eligibleProjects = applicantService.getEligibleProjects(applicant);
        if (eligibleProjects.isEmpty()) {
            System.out.println("⚠️ No eligible projects found for this applicant.");
            return;
        }

        System.out.println("📋 Eligible Projects:");
        for (Project p : eligibleProjects) {
            p.displaySummary();
        }

        // Apply to the first eligible project
        Project selected = eligibleProjects.get(0);
        System.out.println("Attempting to apply to: " + selected.getName());

        for (int i = 0; i < selected.getOfficerNames().size(); i++) {
            System.out.println(selected.getOfficerNames().get(i));
        }

        boolean success = applicantService.apply(applicant, selected, "2-Room");
        if (success) {
            System.out.println("✅ Application successful!");
        } else {
            System.out.println("❌ Application failed.");
        }

        // Show updated project info
        System.out.println("\n📌 Updated Project Applicant List:");
        Project updated = projectService.getProjectByName(selected.getName());
        System.out.println(updated.getApplicantNRICs());

        // Withdraw and reapply test
        System.out.println("\n🚨 Withdrawing application...");
        applicantService.withdraw(applicant);

        System.out.println("🔁 Reapplying...");
        applicantService.apply(applicant, selected, "2-Room");

        for (int i = 0; i < updated.getOfficerNames().size(); i++) {
            System.out.println(" ::::::: TESTRUNNER "  + updated.getOfficerNames().get(i));
        }

        // (Optional) Save to CSV — Uncomment when persistence is integrated
        CSVWriter.saveProjects(projectService.getAllProjects(), "data/ProjectList.csv");

        System.out.println("\n✅ Test Complete.");
    }
}
