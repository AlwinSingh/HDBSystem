package src;

import java.util.Scanner;
import src.model.*;
import src.service.ApplicantMenu;
import src.service.AuthService;
import src.service.ManagerMenu;
import src.service.OfficerMenu;
import src.service.RegistrationService;
import src.util.InputValidator;

/**
 * Entry point of the BTO Application System.
 * Presents login and registration options and routes users based on their role.
 */
public class Main {
    public static final Scanner sc = new Scanner(System.in);

    /**
     * Starts the main menu loop for the BTO system.
     * Allows login, registration, or exit.
     *
     * @param args Not used.
     */
    public static void main(String[] args) {
        while (true) {
            System.out.println("\n===== BTO Application System =====");
            System.out.println("1. Login");
            System.out.println("2. Register as Applicant");
            System.out.println("0. Exit");
            System.out.print("➡️ Enter your choice: ");
            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1" -> handleLogin(sc);
                case "2" -> RegistrationService.registerApplicant(sc);
                case "0" -> {
                    System.out.println("👋 Goodbye!");
                    return;
                }
                default -> System.out.println("❌ Invalid choice. Try again.");
            }
        }
    }

    /**
     * Handles login flow for all user roles (Applicant, Officer, Manager).
     * Verifies credentials and navigates to the appropriate dashboard.
     *
     * @param sc Scanner for user input.
     */
    private static void handleLogin(Scanner sc) {
        System.out.println("\n🔐 Login");
    
        System.out.print("Enter NRIC: ");
        String nric = sc.nextLine().trim();
        if (!nric.matches("^[ST]\\d{7}[A-Z]$")) {
            System.out.println("❌ Invalid NRIC format. Must be like S1234567A.");
            return;
        }
    
        System.out.print("Enter Password: ");
        String password = sc.nextLine();
    
        User user = AuthService.authenticate(nric, password);
    
        if (user == null) {
            System.out.println("❌ Invalid credentials.");
            return;
        }
    
        System.out.println("\n✅ Welcome, " + user.getName());
    
        if (user instanceof HDBManager manager) {
            System.out.println("🔓 Logged in as HDB Manager (" + manager.getNric() + ")");
            ManagerMenu.show(manager);
        } else if (user instanceof HDBOfficer officer) {
            boolean hasProject = officer.getAssignedProject() != null;
            boolean hasPendingStatus = officer.getRegistrationStatus() != null;
    
            if (!hasProject && !hasPendingStatus) {
                System.out.println("🔓 Logged in as HDB Officer (" + officer.getNric() + ")");
                System.out.println("You are an HDB Officer and also eligible to apply as an Applicant.");
                System.out.println("1. Access Officer Dashboard");
                System.out.println("2. Access Applicant Dashboard");
                System.out.println("0. Logout");
                int roleChoice = InputValidator.getIntInRange("➡️ Enter your choice: ", 0, 2);
    
                switch (roleChoice) {
                    case 1 -> OfficerMenu.show(officer);
                    case 2 -> ApplicantMenu.show(officer);
                    case 0 -> {
                        System.out.println("👋 Logging out...");
                        return;
                    }
                }
            } else {
                OfficerMenu.show(officer);
            }
        } else if (user instanceof Applicant applicant) {
            System.out.println("🔓 Logged in as Applicant (" + applicant.getNric() + ")");
            ApplicantMenu.show(applicant);
        }
    }   

}
