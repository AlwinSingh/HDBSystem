package src.service;

import java.util.Map;
import java.util.Scanner;
import src.model.*;
import src.util.*;

/**
 * Handles authentication and account-related services for Applicants, Officers, and Managers.
 */
public class AuthService {

    /**
     * Authenticates a user by verifying NRIC and password across all user types.
     *
     * @param nric     The user's NRIC.
     * @param password The user's password.
     * @return A matching User object if credentials are valid, or null if not.
     */
    public static User authenticate(String nric, String password) {
        // 1. Try ApplicantList
        for (Map<String, String> row : CsvUtil.read(FilePath.APPLICANT_LIST_FILE)) {
            Applicant applicant = new Applicant(
                    row.get("NRIC"),
                    row.get("Password"),
                    row.get("Name"),
                    Integer.parseInt(row.get("Age")),
                    row.get("Marital Status")
            );

            if (!applicant.login(password)) continue;

            String projectName = row.get("AppliedProjectName");
            String flatType    = row.get("FlatTypeApplied");
            String status      = row.get("ApplicationStatus");

            if (projectName != null && !projectName.isBlank() && status != null && !status.isBlank()) {
                Project matched = ProjectLoader.loadProjects().stream()
                        .filter(p -> p.getProjectName().equalsIgnoreCase(projectName))
                        .findFirst()
                        .orElse(null);

                if (matched != null) {
                    applicant.setApplication(new Application(applicant, matched, status, flatType));
                }
            }

            return applicant;
        }

        // 2. Try OfficerList
        for (Map<String, String> row : CsvUtil.read(FilePath.OFFICER_LIST_FILE)) {
            HDBOfficer officer = new HDBOfficer(
                    row.get("NRIC"),
                    row.get("Password"),
                    row.get("Name"),
                    Integer.parseInt(row.get("Age")),
                    row.get("Marital Status")
            );

            if (!officer.login(password)) continue;

            String regStatus = row.get("RegistrationStatus");
            String project   = row.get("AssignedProject");

            officer.setRegistrationStatus(regStatus != null && !regStatus.isBlank() ? regStatus : null);

            if (project != null && !project.isBlank()) {
                officer.setAssignedProjectByName(project, ProjectLoader.loadProjects());
            }

            return officer;
        }

        // 3. Try ManagerList
        for (Map<String, String> row : CsvUtil.read(FilePath.MANAGER_LIST_FILE)) {
            HDBManager manager = new HDBManager(
                    row.get("NRIC"),
                    row.get("Password"),
                    row.get("Name"),
                    Integer.parseInt(row.get("Age")),
                    row.get("Marital Status")
            );

            if (manager.login(password)) return manager;
        }

        return null;
    }

    /**
     * Allows the user to change their password after verifying the current one.
     *
     * @param user The logged-in user.
     * @param sc   Scanner for input.
     * @return True if password was changed successfully; false otherwise.
     */
    public static boolean changePassword(User user, Scanner sc) {
        System.out.print("Enter current password: ");
        String current = sc.nextLine().trim();
        if (!user.login(current)) {
            System.out.println("❌ Incorrect current password.");
            return false;
        }

        System.out.print("Enter new password: ");
        String newPass = sc.nextLine().trim();
        if (newPass.isEmpty()) {
            System.out.println("❌ Password cannot be empty.");
            return false;
        }

        user.setPassword(newPass);

        if (user instanceof HDBManager) {
            ManagerCsvMapper.updateManager((HDBManager) user);
        } else if (user instanceof HDBOfficer) {
            OfficerCsvMapper.updateOfficer((HDBOfficer) user);
        } else if (user instanceof Applicant) {
            ApplicantCsvMapper.updateApplicant((Applicant) user);
        }

        System.out.println("✅ Password changed successfully.");
        user.logout(); // use instance method
        return true;
    }
}
