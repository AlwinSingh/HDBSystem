package src.service;

import java.util.*;
import java.util.stream.Collectors;
import src.model.*;
import src.util.ApplicantCsvMapper;
import src.util.AmenitiesCsvMapper;
import src.util.EnquiryCsvMapper;
import src.util.OfficerCsvMapper;
import src.util.PaymentCsvMapper;
import src.util.ProjectCsvMapper;

public class OfficerMenu {

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
            System.out.printf(" [10] 🔑 Change Password");
    
            if (officer.getRegistrationStatus() == null ||
                officer.getRegistrationStatus().equalsIgnoreCase("REJECTED")) {
                System.out.printf("   [11] 🔁 Switch to Applicant Dashboard%n");
            }
    
            System.out.printf("   [0] 🚪 Logout%n");
    
            System.out.print("\n➡️ Enter your choice: ");
            String choice = sc.nextLine().trim();
    
            switch (choice) {
                case "1" -> viewRegistrationStatus(officer);
                case "2" -> OfficerService.browseAndFilterProjects(sc); // ✅ NEW METHOD
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
                    if (officer.getRegistrationStatus() == null ||
                        officer.getRegistrationStatus().equalsIgnoreCase("REJECTED")) {
                        System.out.println("🔁 Switching to Applicant Dashboard...");
                        ApplicantMenu.show(officer);
                        return;
                    } else {
                        System.out.println("❌ You are not eligible to access the Applicant dashboard.");
                    }
                }
                case "0" -> {
                    AuthService.logout();
                    return;
                }
                default -> System.out.println("❌ Invalid input. Please try again.");
            }
        }
    } 
    
    private static void viewRegistrationStatus(HDBOfficer officer) {
        officer.viewOfficerRegistrationStatus();
    }

}
