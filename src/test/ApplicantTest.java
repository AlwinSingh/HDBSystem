package src.test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import src.model.Applicant;
import src.model.User;
import src.service.ApplicantMenu;
import src.service.AuthService;

/**
 * Standalone test script to simulate a full applicant interaction flow.
 * Includes applying for a project, managing applications, paying invoices,
 * viewing receipts, submitting enquiries/feedback, and changing password.
 */
public class ApplicantTest {

    /**
     * Runs a full test scenario for an applicant using input stream simulation.
     * Automatically walks through the dashboard options and prints status updates.
     *
     * @param args Not used.
     */
    public static void main(String[] args) {
        InputStream originalIn = System.in;

        String applicantNRIC = "T7654321B";
        String password      = "password";

        System.out.println("=== 🧪 Running Applicant Test Flow ===\n");

        User user = AuthService.authenticate(applicantNRIC, password);
        if (!(user instanceof Applicant a)) {
            System.out.println("❌ Applicant login failed.");
            return;
        }

        // Simulate full dashboard flow using input injection
        System.setIn(new ByteArrayInputStream((
            "1\n" +                     // View eligible projects
            "2\n1\n2-Room\nY\n" +       // Apply to first project with confirmation
            "3\n" +                     // View application
            "4\nY\n" +                   // Request withdrawal
            "5\n1\n1\n" +               // View & Pay Invoice using PayNow
            "6\n" +                     // View Receipts
            "7\n1\nThis is a test enquiry\n0\n" +   // Submit enquiry & back
            "8\n1\nAmazing service!\n0\n" +         // Submit feedback & back
            "9\nnewpassword\nnewpassword\n" +       // Change password
            "0\n"                       // Logout
        ).getBytes()));

        ApplicantMenu.show(a);

        System.setIn(originalIn);
        System.out.println("\n=== ✅ Applicant Flow Test Complete ===");
    }
}
