package src.service;

import src.model.HDBOfficer;

import java.util.Scanner;

public class OfficerGateway {

    public static void show(HDBOfficer officer) {
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n===== 🛡️ Officer Login Successful =====");
            System.out.println("Welcome, Officer " + officer.getName());
            System.out.println("You are an HDB Officer and also eligible to apply as an Applicant.\n");
            System.out.println("1. Access Officer Dashboard");
            System.out.println("2. Access Applicant Dashboard");
            System.out.println("0. Logout");
            System.out.print("Enter choice: ");

            String choice = sc.nextLine().trim();
            switch (choice) {
                case "1" -> OfficerMenu.show(officer);     // to be implemented
                case "2" -> ApplicantMenu.show(officer);   // reuse applicant logic
                case "0" -> {
                    System.out.println("👋 Logging out...");
                    return;
                }
                default -> System.out.println("❌ Invalid input. Please try again.");
            }
        }
    }
}
