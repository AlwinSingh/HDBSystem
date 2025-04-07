package src.test;

import src.model.Applicant;
import src.model.Officer;
import src.model.Project;
import src.service.OfficerService;
import src.service.ProjectService;
import src.service.UserService;
import src.util.CSVWriter;

public class TestRunnerOfficer {
    public static void main(String[] args) {
        System.out.println("=== TEST RUNNER START ===");

        UserService us = new UserService();
        ProjectService ps = new ProjectService(us);
        OfficerService os = new OfficerService(ps, us);

        // Test Officer
        Officer officer = us.getOfficerByNric("T2109876H"); // Use a valid NRIC

        System.out.println("\n--- Registering Officer ---");
        os.registerForProject(officer, "Acacia Breeze");

        officer.setRegistrationStatus("APPROVED"); // Simulate manager approval manually
        System.out.println("✅ Officer manually approved for testing.");

        os.viewAssignedProject(officer);
        os.viewApplicantList(officer);

        System.out.println("\n--- Approving Applicant S1234567A ---");
        os.handleApplication("S1234567A", true);

        System.out.println("\n--- Rejecting Applicant S2345678B ---");
        os.handleApplication("S2345678B", false);

        System.out.println("\n--- Re-approving Already Processed Applicant ---");
        os.handleApplication("S2345678B", true); // Should fail

        System.out.println("\n--- Booking Flat for Approved Applicant ---");
        os.bookFlat("S1234567A");

        System.out.println("\n--- Trying to Book Flat with Invalid NRIC ---");
        os.bookFlat("INVALID123"); // Should fail

        System.out.println("\n--- Booking Flat Again for Already Successful Applicant ---");
        os.bookFlat("S1234567A"); // Should fail

        System.out.println("\n--- Generate Receipt ---");
        os.generateReceipt("S1234567A");

        System.out.println("\n--- Generate Receipt for Unbooked Applicant ---");
        os.generateReceipt("S2345678B"); // Should fail

        // Optional: Check if CSVs updated (output info)
        Project project = ps.getProjectByName("Acacia Breeze");
        System.out.println("\nFinal 2-Room Flat Count: " + project.getTwoRoomUnits());

        Applicant a = us.getApplicantByNric("S1234567A");
        System.out.println("Applicant S1234567A Final Status: " + a.getApplicationStatus());

        System.out.println("=== TEST RUNNER END ===");
    }
}
