package src.util;

import java.util.*;
import src.model.HDBOfficer;
import src.model.Project;

public class OfficerCsvMapper {

    public static HDBOfficer fromCsvRow(Map<String, String> row, List<Project> allProjects) {
        String nric = row.getOrDefault("NRIC", "").trim();
        String password = row.getOrDefault("Password", "").trim();
        String name = row.getOrDefault("Name", "").trim();
        int age = Integer.parseInt(row.getOrDefault("Age", "0").trim());
        String maritalStatus = row.getOrDefault("Marital Status", "Single").trim();

        HDBOfficer officer = new HDBOfficer(nric, password, name, age, maritalStatus);

        // Load registration status
        String status = row.getOrDefault("RegistrationStatus", "").trim();
        if (!status.isBlank()) {
            officer.setRegistrationStatus(status);
        }

        // Assign project by name
        String assignedProject = row.getOrDefault("AssignedProject", "").trim();
        if (!assignedProject.isBlank()) {
            officer.setAssignedProjectByName(assignedProject, allProjects);
        }

        return officer;
    }

    public static Map<String, String> toCsvRow(HDBOfficer officer) {
        Map<String, String> row = new LinkedHashMap<>();
        row.put("NRIC", officer.getNric());
        row.put("Password", officer.getPassword());
        row.put("Name", officer.getName());
        row.put("Age", String.valueOf(officer.getAge()));
        row.put("Marital Status", officer.getMaritalStatus());

        row.put("RegistrationStatus", officer.getRegistrationStatus() != null ? officer.getRegistrationStatus() : "");
        row.put("AssignedProject", officer.getAssignedProject() != null ? officer.getAssignedProject().getProjectName() : "");

        return row;
    }

    public static List<HDBOfficer> loadAll(List<Project> allProjects) {
        List<Map<String, String>> rows = CsvUtil.read(FilePath.OFFICER_LIST_FILE);
        List<HDBOfficer> officers = new ArrayList<>();
        for (Map<String, String> row : rows) {
            officers.add(fromCsvRow(row, allProjects)); // ✅ FIXED
        }
        return officers;
    }
    
    public static void updateOfficer(HDBOfficer updatedOfficer) {
        List<Map<String, String>> rows = CsvUtil.read(FilePath.OFFICER_LIST_FILE);
    
        for (Map<String, String> row : rows) {
            if (row.getOrDefault("NRIC", "").equalsIgnoreCase(updatedOfficer.getNric())) {
                // update everything, not just password
                row.put("Password",            updatedOfficer.getPassword());
                row.put("RegistrationStatus",  updatedOfficer.getRegistrationStatus() != null? updatedOfficer.getRegistrationStatus(): "");
                row.put("AssignedProject",     updatedOfficer.getAssignedProject() != null? updatedOfficer.getAssignedProject().getProjectName(): "");
                break;
            }
        }
    
        CsvUtil.write(FilePath.OFFICER_LIST_FILE, rows);
    }

    public static void saveAll(List<HDBOfficer> officers) {
        List<Map<String, String>> rows = new ArrayList<>();
        for (HDBOfficer o : officers) {
            rows.add(toCsvRow(o));
        }
        CsvUtil.write(FilePath.OFFICER_LIST_FILE, rows);
    }
}
