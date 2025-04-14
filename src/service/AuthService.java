package src.service;

import src.model.*;
import src.util.CsvUtil;
import java.util.List;
import java.util.Map;

public class AuthService {

    public static User authenticate(String nric, String password) {
        // 1. Try ApplicantList
        List<Map<String, String>> applicants = CsvUtil.read("data/ApplicantList.csv");
        for (Map<String, String> row : applicants) {
            if (row.get("NRIC").equalsIgnoreCase(nric) && row.get("Password").equals(password)) {
                Applicant applicant = new Applicant(
                        row.get("NRIC"),
                        row.get("Password"),
                        row.get("Name"),
                        Integer.parseInt(row.get("Age")),
                        row.get("Marital Status")
                );

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
        List<Map<String, String>> officers = CsvUtil.read("data/OfficerList.csv");
        for (Map<String, String> row : officers) {
            if (row.get("NRIC").equalsIgnoreCase(nric) && row.get("Password").equals(password)) {
                return new HDBOfficer(
                        row.get("NRIC"),
                        row.get("Password"),
                        row.get("Name"),
                        Integer.parseInt(row.get("Age")),
                        row.get("Marital Status")
                );
            }
        }

        // 3. Try ManagerList
        List<Map<String, String>> managers = CsvUtil.read("data/ManagerList.csv");
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

        return null; // login failed
    }
}
