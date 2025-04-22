package src.test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import src.model.HDBOfficer;
import src.model.User;
import src.service.OfficerMenu;
import src.service.AuthService;

/**
 * Test script to simulate an officer experience navigating registering for a project.
 */
public class OfficerTest {

    public static void main(String[] args) {
        InputStream originalIn = System.in;

        String applicantNRIC = "T1234567J";
        String password      = "password";

        System.out.println("=== 🧪 Running Applicant Filter Test ===\n");

        User user = AuthService.authenticate(applicantNRIC, password);
        if (!(user instanceof HDBOfficer a)) {
            System.out.println("❌ Officer login failed.");
            return;
        }

  
        String inputSequence = String.join("\n",
        "1",            // Enter Officer Dashboard
        "1",            // View Registration Status
        "2",     // Browse & Filter Available Projects (for reference only)
        "1",             // Filter by Project Name
        "Acacia",             // Enter partial project name
        "5",            // Clear filters
        "0",             // Return to main menu
                            //
        "3",            // Register for Project
        "1",            //Available Projects, choose to register, return to main menu
        "4",            //Displays project details
        "5",            //Perform bookings
        "6",            //Generate Receipts
        "7",            //Update location
        "8",            //Add Amenity
        "9",            //View & reply to Enquiries
        "10",           //Change password
        "password",     //current password
        "password",            //new password
        "1", //relog
        "T1234567J",
        "password",
        "11"         //Applicant Dashboard
        );
        

        System.setIn(new ByteArrayInputStream(inputSequence.getBytes()));
        OfficerMenu.show(a);
        System.setIn(originalIn);

        System.out.println("\n=== ✅ Officer Filter Test Complete ===");
    }
}