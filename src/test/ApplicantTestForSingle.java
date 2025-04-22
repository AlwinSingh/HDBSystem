package src.test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import src.model.Applicant;
import src.model.User;
import src.service.ApplicantMenu;
import src.service.AuthService;

/**
 * Test script to simulate an Single > 35 years old Officer as a applicant's experience navigating around the menu.
 */
public class ApplicantTestForSingle {

    public static void main(String[] args) {
        InputStream originalIn = System.in;

        String applicantNRIC = "S1234567A";
        String password      = "password";

        System.out.println("=== 🧪 Running Applicant Filter Test ===\n");

        User user = AuthService.authenticate(applicantNRIC, password);
        if (!(user instanceof Applicant a)) {
            System.out.println("❌ Applicant login failed.");
            return;
        }

        
        String inputSequence = String.join("\n",
        "1",            // View eligible projects
        "1",
        "Clementi",
        " ",
        "2-Room",
        "2",
        "0",
        //apply for a project
        "2",
        "1", //apply for clementi
        "Y",
        "3"

        );

        System.setIn(new ByteArrayInputStream(inputSequence.getBytes()));
        ApplicantMenu.show(a);
        System.setIn(originalIn);

        System.out.println("\n=== ✅ Applicant Filter Test Complete ===");
    }
}

