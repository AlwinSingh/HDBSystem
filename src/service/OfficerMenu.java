package src.service;

import java.util.*;
import src.model.*;

/**
 * CLI menu interface for logged-in HDB Officers.
 * Provides access to registration, project management, invoicing, receipt generation,
 * amenity/location updates, enquiry replies, and account operations.
 *
 * Handles user navigation and routes actions to the appropriate OfficerService or AuthService logic.
 */

public class OfficerMenu {

    /**
     * Displays the officer dashboard menu and routes each valid menu option to its function.
     * 
     * Supported options:
     * <ul>
     *   <li>View registration status, browse and register for projects</li>
     *   <li>View project details, book flats, and generate receipts</li>
     *   <li>Update project location and amenities</li>
     *   <li>View and respond to enquiries</li>
     *   <li>Change password, switch to applicant view, or log out</li>
     * </ul>
     *
     * @param officer The currently logged-in HDB officer.
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
                case "1" -> OfficerRegistrationService.viewOfficerRegistrationStatus(officer);
                case "2" -> OfficerRegistrationService.browseAndFilterProjects(sc);
                case "3" -> OfficerRegistrationService.registerForProject(officer, sc);
                case "4" -> OfficerProjectViewService.viewAssignedProjectDetails(officer);
                case "5" -> OfficerBookingService.bookFlat(officer, sc);
                case "6" -> OfficerReceiptService.generateReceipt(officer, sc);
                case "7" -> OfficerLocationService.updateLocation(officer, sc);
                case "8" -> OfficerAmenityService.addOrUpdateAmenity(officer, sc);
                case "9" -> OfficerEnquiryService.handleEnquiries(officer, sc);
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
