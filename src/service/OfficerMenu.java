package src.service;

import java.util.*;
import src.model.*;

/**
 * Displays the HDB Officer dashboard and handles routing to officer-specific actions.
 */
public class OfficerMenu {

    /**
     * Launches the officer dashboard and processes user input to access various services.
     *
     * @param officer The logged-in officer.
     */
    public static void show(HDBOfficer officer) {
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n===== 🧑‍💼 HDB Officer Dashboard =====");
            System.out.println("Welcome, Officer " + officer.getName());

            System.out.println("\n📋 Registration");
            System.out.printf(" [1] 📝 View Status           [2] 🔍 Browse & Filter Projects%n");
            System.out.printf(" [3] 🏗️ Register for Project%n");

            System.out.println("\n📂 Project");
            System.out.printf(" [4] 📄 View Details          [5] 🏠 Book Flat for Applicant%n");
            System.out.printf(" [6] 🧾 Generate Receipt      [7] 📍 Update Location%n");
            System.out.printf(" [8] ➕ Add Amenity%n");

            System.out.println("\n📬 Enquiries");
            System.out.printf(" [9] 💬 View & Reply to Enquiries%n");

            System.out.println("\n🔐 Account");
            System.out.printf(" [10] 🔑 Change Password   [11] 🔁 Switch to Applicant Dashboard%n");
            System.out.printf(" [0] 🚪 Logout%n");

            System.out.print("\n➡️ Enter your choice: ");
            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1" -> OfficerService.viewOfficerRegistrationStatus(officer);
                case "2" -> OfficerService.browseAndFilterProjects(sc);
                case "3" -> OfficerService.registerForProject(officer, sc);
                case "4" -> OfficerService.viewAssignedProjectDetails(officer);
                case "5" -> OfficerService.bookFlat(officer, sc);
                case "6" -> OfficerService.generateReceipt(officer, sc);
                case "7" -> OfficerService.updateLocation(officer, sc);
                case "8" -> OfficerService.addOrUpdateAmenity(officer, sc);
                case "9" -> OfficerService.handleEnquiries(officer, sc);
                case "10" -> {
                    if (AuthService.changePassword(officer, sc)) return;
                }
                case "11" -> {
                    System.out.println("🔁 Switching to Applicant Dashboard...");
                    ApplicantMenu.show(officer);
                    return;
                }                         
                case "0" -> {
                    officer.logout();
                    return;
                }
                default -> System.out.println("❌ Invalid input. Please try again.");
            }
        }
    }
}
