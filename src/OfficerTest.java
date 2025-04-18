package src;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import src.model.HDBOfficer;
import src.model.User;
import src.service.AuthService;
import src.service.OfficerMenu;

/**
 * Automated functional test for Officer menu.
 */
public class OfficerTest {
    public static void main(String[] args) {
        InputStream originalIn = System.in;

        String officerNRIC = "T2109876H";
        String password    = "password";

        System.out.println("=== 🧪 Running Officer Test Flow ===\n");

        User user = AuthService.authenticate(officerNRIC, password);
        if (!(user instanceof HDBOfficer o)) {
            System.out.println("❌ Officer login failed.");
            return;
        }

        // Simulated flow:
        System.setIn(new ByteArrayInputStream((
            "1\n" + // View registration status
            "2\n1\n" + // Register for project (first one)
            "3\n" + // View assigned project details
            "7\nTest District\nTest Town\n123 Test St\n1.234567\n103.987654\n" + // Update location
            "8\nClinic\nSunshine Clinic\n0.5\n" + // Add amenity
            "6\n1\nWill look into it.\n" + // Enquiry response
            "4\n1\n" + // Book flat for first applicant
            "5\n1\n" + // Generate receipt for first eligible invoice
            "9\nnewofficerpass\nnewofficerpass\n" + // Change password
            "0\n" // Logout
        ).getBytes()));

        OfficerMenu.show(o);

        System.setIn(originalIn);
        System.out.println("\n=== ✅ Officer Flow Test Complete ===");
    }
}