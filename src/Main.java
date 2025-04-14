package src;

import src.model.*;
import src.service.ApplicantMenu;
import src.service.AuthService;

import java.util.Scanner;

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

        if (user instanceof Applicant applicant) {
            System.out.println("🔓 Logged in as Applicant: " + applicant.getNric());
            ApplicantMenu.show(applicant);
        } else if (user instanceof HDBOfficer officer) {
            System.out.println("🔓 Logged in as HDB Officer: " + officer.getNric());
        } else if (user instanceof HDBManager manager) {
            System.out.println("🔓 Logged in as HDB Manager: " + manager.getNric());
        }
    }

    private static void handleApplicantRegistration(Scanner sc) {
        System.out.println("\n📝 Register New Applicant");
        System.out.print("Enter NRIC: ");
        String nric = sc.nextLine();
        System.out.print("Enter Name: ");
        String name = sc.nextLine();
        System.out.print("Enter Age: ");
        int age = Integer.parseInt(sc.nextLine());
        System.out.print("Enter Marital Status (Single/Married): ");
        String maritalStatus = sc.nextLine();

        Applicant newApplicant = new Applicant(nric, "password", name, age, maritalStatus);
        System.out.println("✅ Applicant created. Default password is: password");

        // TODO: Write newApplicant to ApplicantList.csv
    }
}
