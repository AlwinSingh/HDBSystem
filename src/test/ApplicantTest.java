package src.test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import src.model.Applicant;
import src.model.User;
import src.service.ApplicantMenu;
import src.service.AuthService;

/**
 * Test script to simulate an officer as a applicant's experience navigating around the menu.
 */
public class ApplicantTest {

    public static void main(String[] args) {
        InputStream originalIn = System.in;

        String applicantNRIC = "T2109876H";
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
        "",             // Skip district price
        "",             // Skip 2room/3room price
        "2",            // Clear filters
        "0",             // Return to main menu
        //
        "2",
        "2",
        "Y",
        "3",
        "4",
        "Y",
        "5",
        "6",
        //enquiry service
        "7",
        "1",
        "1",
        "How many floors are there",
        "2",
        "3",
        "1",
        "What are the prices?",
        "4",
        "1",
        "Y",
        "2",
        "0",
        //Feedback service
        "8",
        "1",
        "Great Job",
        "2",
        "0",
        //Change password
        "9",
        "password",
        "superhero");
        

        System.setIn(new ByteArrayInputStream(inputSequence.getBytes()));
        ApplicantMenu.show(a);
        System.setIn(originalIn);

        System.out.println("\n=== ✅ Applicant Filter Test Complete ===");
    }
}
