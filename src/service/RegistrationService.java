package src.service;

import java.util.Scanner;
import src.model.Applicant;
import src.util.ApplicantCsvMapper;

public class RegistrationService {

    public static void registerApplicant(Scanner sc) {
        String nric = promptValidNric(sc);
        if (nric == null) return;
    
        String name = prompt(sc, "Enter Name: ");
    
        int age;
        while (true) {
            age = promptInt(sc, "Enter Age: ");
            if (age < 21 || age >100) {
                System.out.println("❌ Age must be at least 21 to register.");
                return;
            } else {
                break;
            }
        }
    
        String maritalStatus;
        while (true) {
            maritalStatus = prompt(sc, "Enter Marital Status (Single/Married): ");
            if (!maritalStatus.equalsIgnoreCase("Single") && !maritalStatus.equalsIgnoreCase("Married")) {
                System.out.println("❌ Marital status must be either 'Single' or 'Married'.");
            } else {
                break;
            }
        }
    
        if (ApplicantCsvMapper.exists(nric)) {
            System.out.println("❌ An account with this NRIC already exists.");
            return;
        }
    
        Applicant applicant = new Applicant(nric, "password", name, age, maritalStatus);
        ApplicantCsvMapper.save(applicant);
        System.out.println("✅ Applicant created. Default password is: password");
    }
    

    private static String prompt(Scanner sc, String message) {
        System.out.print(message);
        return sc.nextLine().trim();
    }

    private static int promptInt(Scanner sc, String message) {
        while (true) {
            System.out.print(message);
            try {
                return Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("❌ Please enter a valid number.");
            }
        }
    }

    private static String promptValidNric(Scanner sc) {
        System.out.print("Enter NRIC: ");
        String nric = sc.nextLine().trim().toUpperCase();
        if (!nric.matches("^[ST]\\d{7}[A-Z]$")) {
            System.out.println("❌ Invalid NRIC format. It must start with S or T, followed by 7 digits, and end with a letter.");
            return null;
        }
        return nric;
    }
}
