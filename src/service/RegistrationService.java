package src.service;

import java.util.Scanner;
import src.model.Applicant;
import src.repository.ApplicantRepository;
import src.util.ApplicantCsvMapper;

/**
 * Handles the registration process for new applicants.
 * This includes NRIC validation, age and marital status checks,
 * and persistent storage using CSV mapping.
 */
public class RegistrationService {
    private static final ApplicantRepository applicantRepository = new ApplicantCsvMapper();
    /**
     * Handles the full applicant registration flow.
     * Includes input validation, NRIC format check, and CSV persistence.
     *
     * @param sc Scanner for user input.
     */
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
    
        if (applicantRepository.exists(nric)) {
            System.out.println("❌ An account with this NRIC already exists.");
            return;
        }
    
        Applicant applicant = new Applicant(nric, "password", name, age, maritalStatus);
        applicantRepository.save(applicant);
        System.out.println("✅ Applicant created. Default password is: password");
    }


    /**
     * Prompts the user for input using a message and returns trimmed text.
     *
     * @param sc Scanner for input.
     * @param message Message to display.
     * @return User input as trimmed string.
     */
    private static String prompt(Scanner sc, String message) {
        System.out.print(message);
        return sc.nextLine().trim();
    }

    /**
     * Prompts the user to enter a valid integer.
     *
     * @param sc Scanner for input.
     * @param message Message to display.
     * @return Parsed integer from user.
     */
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

    /**
     * Prompts for an NRIC and checks if it matches the expected Singapore NRIC format.
     *
     * @param sc Scanner for input.
     * @return A valid NRIC string or null if invalid.
     */
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
