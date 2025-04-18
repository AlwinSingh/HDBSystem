package src.service;

import java.util.List;
import java.util.Map;
import java.util.Scanner;
import src.model.*;
import src.util.*;

public class AuthService {

    public static User authenticate(String nric, String password) {
        // 1. Try ApplicantList
        List<Map<String, String>> applicants = CsvUtil.read(FilePath.APPLICANT_LIST_FILE);
        for (Map<String, String> row : applicants) {
            if (row.get("NRIC").equalsIgnoreCase(nric) && row.get("Password").equals(password)) {
                Applicant applicant = new Applicant(
                        row.get("NRIC"),
                        row.get("Password"),
                        row.get("Name"),
                        Integer.parseInt(row.get("Age")),
                        row.get("Marital Status")
                );

                // Load application if exists
                String projectName = row.get("AppliedProjectName");
                String flatType = row.get("FlatTypeApplied");
                String status = row.get("ApplicationStatus");

                if (projectName != null && !projectName.isBlank() && status != null && !status.isBlank()) {
                    List<Project> allProjects = ProjectLoader.loadProjects();
                    Project matched = allProjects.stream()
                            .filter(p -> p.getProjectName().equalsIgnoreCase(projectName))
                            .findFirst()
                            .orElse(null);

                    if (matched != null) {
                        Application app = new Application(applicant, matched, status, flatType);
                        applicant.setApplication(app);
                    }
                }

                return applicant;
            }
        }

        // 2. Try OfficerList
        List<Map<String, String>> officers = CsvUtil.read(FilePath.OFFICER_LIST_FILE);
        for (Map<String, String> row : officers) {
            if (row.get("NRIC").equalsIgnoreCase(nric) && row.get("Password").equals(password)) {
                HDBOfficer officer = new HDBOfficer(
                        row.get("NRIC"),
                        row.get("Password"),
                        row.get("Name"),
                        Integer.parseInt(row.get("Age")),
                        row.get("Marital Status")
                );

                // Load registration details
                String regStatus = row.get("RegistrationStatus");
                String assignedProject = row.get("AssignedProject");

                officer.setRegistrationStatus((regStatus != null && !regStatus.isBlank()) ? regStatus : null);

                if (assignedProject != null && !assignedProject.isBlank()) {
                    List<Project> allProjects = ProjectLoader.loadProjects();
                    officer.setAssignedProjectByName(assignedProject, allProjects);
                }

                return officer;
            }
        }

        // 3. Try ManagerList
        List<Map<String, String>> managers = CsvUtil.read(FilePath.MANAGER_LIST_FILE);
        for (Map<String, String> row : managers) {
            if (row.get("NRIC").equalsIgnoreCase(nric) && row.get("Password").equals(password)) {
                return new HDBManager(
                        row.get("NRIC"),
                        row.get("Password"),
                        row.get("Name"),
                        Integer.parseInt(row.get("Age")),
                        row.get("Marital Status")
                );
            }
        }

        return null;
    }
    public static boolean changePassword(User user, Scanner sc) {
        System.out.print("Enter current password: ");
        String current = sc.nextLine().trim();
        if (!user.getPassword().equals(current)) {
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
        return true;
    }
}
