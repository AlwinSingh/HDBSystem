package src.service;

import src.model.Applicant;
import src.model.Manager;
import src.model.Officer;
import src.model.User;
import src.util.CSVReader;

import java.util.*;

/**
 * Service class for loading and managing users from CSV files.
 */
public class UserService {
    private final Map<String, Applicant> applicants = new HashMap<>();
    private final Map<String, Officer> officers = new HashMap<>();
    private final Map<String, Manager> managers = new HashMap<>();

    public UserService() {
        loadApplicants();
        loadOfficers();
        loadManagers();
    }

    private void loadApplicants() {
        List<String> requiredHeaders = List.of("NRIC", "Password", "Name", "Age", "Marital Status","FlatTypeApplied","AppliedProjectName","ApplicationStatus");
        Map<String, Map<String, String>> data =
                CSVReader.readCSVByKey("data/ApplicantList.csv", requiredHeaders, "NRIC");

        for (Map.Entry<String, Map<String, String>> entry : data.entrySet()) {
            Map<String, String> row = entry.getValue();
            try {
                Applicant a = new Applicant(
                        row.get("NRIC"),
                        row.get("Password"),
                        row.get("Name"),
                        Integer.parseInt(row.get("Age")),
                        row.get("Marital Status"),
                        row.getOrDefault("FlatTypeApplied", ""),
                        row.getOrDefault("AppliedProjectName", ""),
                        row.getOrDefault("ApplicationStatus", "").toUpperCase().trim()
                );
                applicants.put(a.getNric(), a);
            } catch (Exception e) {
                System.err.println("⚠️ Failed to parse applicant: " + row);
            }
        }
    }

    private void loadOfficers() {
        List<String> requiredHeaders = List.of("NRIC", "Password", "Name", "Age", "Marital Status", "AssignedProject", "RegistrationStatus");
        Map<String, Map<String, String>> data =
                CSVReader.readCSVByKey("data/OfficerList.csv", requiredHeaders, "NRIC");

        for (Map.Entry<String, Map<String, String>> entry : data.entrySet()) {
            Map<String, String> row = entry.getValue();
            try {
                Officer o = new Officer(
                        row.get("NRIC"),
                        row.get("Password"),
                        row.get("Name"),
                        Integer.parseInt(row.get("Age")),
                        row.get("Marital Status")
                );
                officers.put(o.getNric(), o);
            } catch (Exception e) {
                System.err.println("⚠️ Failed to parse officer: " + row);
            }
        }
    }

    private void loadManagers() {
        List<String> requiredHeaders = List.of("NRIC", "Password", "Name", "Age", "Marital Status", "ProjectsCreated");
        Map<String, Map<String, String>> data =
                CSVReader.readCSVByKey("data/ManagerList.csv", requiredHeaders, "NRIC");

        for (Map.Entry<String, Map<String, String>> entry : data.entrySet()) {
            Map<String, String> row = entry.getValue();
            try {
                Manager m = new Manager(
                        row.get("NRIC"),
                        row.get("Password"),
                        row.get("Name"),
                        Integer.parseInt(row.get("Age")),
                        row.get("Marital Status")
                );
                managers.put(m.getNric(), m);
            } catch (Exception e) {
                System.err.println("⚠️ Failed to parse manager: " + row);
            }
        }
    }

    /**
     * Attempts to authenticate a user from any role by NRIC and password.
     * Todo: Hash password
     */
    public User authenticateUser(String nric, String password) {
        if (officers.containsKey(nric) && officers.get(nric).checkPassword(password)) {
            return officers.get(nric);
        }
        if (managers.containsKey(nric) && managers.get(nric).checkPassword(password)) {
            return managers.get(nric);
        }
        if (applicants.containsKey(nric) && applicants.get(nric).checkPassword(password)) {
            return applicants.get(nric);
        }
        return null;
    }

    public Map<String, Applicant> getAllApplicants() {
        return applicants;
    }

    public Map<String, Officer> getAllOfficers() {
        return officers;
    }

    public Map<String, Manager> getAllManagers() {
        return managers;
    }

    /* FOLLOWING METHODS ARE TO GET USER BY NRIC */
    public Applicant getApplicantByNric(String nric) {
        return applicants.getOrDefault(nric, null);
    }

    public Officer getOfficerByNric(String nric) {
        return officers.getOrDefault(nric, null);
    }

    public Manager getManagerByNric(String nric) {
        return managers.getOrDefault(nric, null);
    }

    /* FOLLOWING METHODS ARE TO GET USER BY NAME */
    public Applicant getApplicantByName(String name) {
        for (Applicant applicant : applicants.values()) {
            if (applicant.getName().equalsIgnoreCase(name)) return applicant;
        }
        return null;
    }

    public Manager getManagerByName(String name) {
        for (Manager manager : managers.values()) {
            if (manager.getName().equalsIgnoreCase(name)) return manager;
        }
        return null;
    }

    public Officer getOfficerBName(String name) {
        for (Officer officer : officers.values()) {
            if (officer.getName().equalsIgnoreCase(name)) return officer;
        }
        return null;
    }
}
