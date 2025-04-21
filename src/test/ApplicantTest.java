package src.test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import src.model.Applicant;
import src.model.User;
import src.service.ApplicantMenu;
import src.service.AuthService;

/**
 * Test script to simulate an applicant's experience navigating eligible project filters.
 * Steps:
 * 1. Log in as an applicant.
 * 2. Press 1 to view eligible projects.
 * 3. Press 1 again to apply filter and type 'Clementi', then press Enter twice.
 * 4. Press 2 to clear filters.
 * 5. Press 0 to go back to the main menu.
 */
public class ApplicantTest {

    public static void main(String[] args) {
        InputStream originalIn = System.in;

        String applicantNRIC = "T7654321B";
        String password      = "password";

        System.out.println("=== 🧪 Running Applicant Filter Test ===\n");

        User user = AuthService.authenticate(applicantNRIC, password);
        if (!(user instanceof Applicant a)) {
            System.out.println("❌ Applicant login failed.");
            return;
        }

        // Simulate input sequence:
        // 1 - View eligible projects
        // 1 - Apply filter
        // Clementi (filter by neighborhood)
        // <Enter> twice to skip optional filters
        // 2 - Clear filters
        // 0 - Return to previous menu
        String inputSequence = String.join("\n",
            "1",            // View eligible projects
            "1",            // Filter by Neighborhood
            "Clementi",     // Enter neighborhood filter
            "",             // Skip min price
            "",             // Skip max price
            "2",            // Clear filters
            "0",             // Return to main menu
            "2",
            "2"
        );

        System.setIn(new ByteArrayInputStream(inputSequence.getBytes()));
        ApplicantMenu.show(a);
        System.setIn(originalIn);

        System.out.println("\n=== ✅ Applicant Filter Test Complete ===");
    }
}
