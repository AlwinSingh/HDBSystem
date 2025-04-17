package src;

import java.util.List;
import java.util.Scanner;
import src.model.*;
import src.service.ApplicantMenu;
import src.service.AuthService;
import src.service.ManagerMenu;
import src.service.OfficerMenu;
import src.util.ApplicantCsvMapper;

public class Main {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n===== BTO Application System =====");
            System.out.println("1. Login");
            System.out.println("2. Register as Applicant");
            System.out.println("0. Exit");
            System.out.print("Enter choice: ");

            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1" -> handleLogin(sc);
                case "2" -> handleApplicantRegistration(sc);
                case "0" -> {
                    System.out.println("👋 Goodbye!");
                    return;
                }
                default -> System.out.println("❌ Invalid choice. Try again.");
            }
        }
    }

    private static void handleLogin(Scanner sc) {
        System.out.println("\n🔐 Login");
        System.out.print("Enter NRIC: ");
        String nric = sc.nextLine();
        System.out.print("Enter Password: ");
        String password = sc.nextLine();

        User user = AuthService.authenticate(nric, password);

        if (user == null) {
            System.out.println("❌ Invalid credentials.");
            return;
        }

        System.out.println("✅ Welcome, " + user.getName());

        if (user instanceof HDBManager manager) {
            System.out.println("🔓 Logged in as HDB Manager: " + manager.getNric());
            ManagerMenu.show(manager);
        }
        else if (user instanceof HDBOfficer officer) {
            // Officer dashboard logic with dual-role access
            boolean hasProject = officer.getAssignedProject() != null;
            boolean hasPendingStatus = officer.getRegistrationStatus() != null;

            if (!hasProject && !hasPendingStatus) {
                System.out.println("\nWelcome, Officer " + officer.getName());
                System.out.println("You are an HDB Officer and also eligible to apply as an Applicant.");
                System.out.println("1. Access Officer Dashboard");
                System.out.println("2. Access Applicant Dashboard");
                System.out.println("0. Logout");
                System.out.print("Enter choice: ");
                String roleChoice = sc.nextLine().trim();

                switch (roleChoice) {
                    case "1" -> OfficerMenu.show(officer);
                    case "2" -> ApplicantMenu.show(officer);
                    default -> System.out.println("Logging out...");
                }
            } else {
                OfficerMenu.show(officer);
            }
        }
        else if (user instanceof Applicant applicant) {
            System.out.println("🔓 Logged in as Applicant: " + applicant.getNric());
            ApplicantMenu.show(applicant);
        }
    }
    private static boolean isValidNric(String nric) {
        return nric.matches("^[ST]\\d{7}[A-Z]$");
    }
    

   private static void handleApplicantRegistration(Scanner sc) {
    System.out.println("\n📝 Register New Applicant");
    System.out.print("Enter NRIC: ");
    String nric = sc.nextLine().trim().toUpperCase();

    if (!isValidNric(nric)) {
        System.out.println("❌ Invalid NRIC format. It must start with S or T, followed by 7 digits, and end with a letter.");
        return;
    }

    System.out.print("Enter Name: ");
    String name = sc.nextLine().trim();
    System.out.print("Enter Age: ");
    int age = Integer.parseInt(sc.nextLine().trim());
    System.out.print("Enter Marital Status (Single/Married): ");
    String maritalStatus = sc.nextLine().trim();

    // Check if already exists
    List<Applicant> existing = ApplicantCsvMapper.loadAll("data/ApplicantList.csv");
    boolean exists = existing.stream().anyMatch(a -> a.getNric().equalsIgnoreCase(nric));
    if (exists) {
        System.out.println("❌ An account with this NRIC already exists.");
        return;
    }

    // Create new applicant with default password
    Applicant newApplicant = new Applicant(nric, "password", name, age, maritalStatus);
    existing.add(newApplicant);
    ApplicantCsvMapper.saveAll("data/ApplicantList.csv", existing);

    System.out.println("✅ Applicant created. Default password is: password");
}

}
