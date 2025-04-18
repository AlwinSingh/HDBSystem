package src.util;

import java.util.*;
import src.model.Applicant;
import src.model.Application;
import src.model.Project;

public class ApplicantCsvMapper {

    public static Applicant fromCsvRow(Map<String, String> row) {
        String nric = row.getOrDefault("NRIC", "").trim();
        String password = row.getOrDefault("Password", "").trim();
        String name = row.getOrDefault("Name", "").trim();
        int age = Integer.parseInt(row.getOrDefault("Age", "0").trim());
        String maritalStatus = row.getOrDefault("Marital Status", "Single").trim();

        Applicant applicant = new Applicant(nric, password, name, age, maritalStatus);

        // Optional: Load application status and details if present
        String appStatus = row.getOrDefault("ApplicationStatus", "").trim();
        String appliedProject = row.getOrDefault("AppliedProjectName", "").trim();
        String flatType = row.getOrDefault("FlatTypeApplied", "").trim();

        if (!appStatus.isEmpty() && !appliedProject.isEmpty() && !flatType.isEmpty()) {
            Project dummyProject = new Project(); // Just need project name for now
            dummyProject.setProjectName(appliedProject);

            Application app = new Application(applicant, dummyProject, appStatus, flatType);
            applicant.setApplication(app);
        }
        return applicant;
    }

    public static Map<String, String> toCsvRow(Applicant applicant) {
        Map<String, String> row = new LinkedHashMap<>();
        row.put("NRIC", applicant.getNric());
        row.put("Password", applicant.getPassword());
        row.put("Name", applicant.getName());
        row.put("Age", String.valueOf(applicant.getAge()));
        row.put("Marital Status", applicant.getMaritalStatus());

        if (applicant.getApplication() != null) {
            row.put("ApplicationStatus", applicant.getApplication().getStatus());
            row.put("AppliedProjectName", applicant.getApplication().getProject().getProjectName());
            row.put("FlatTypeApplied", applicant.getApplication().getFlatType());
        } else {
            row.put("ApplicationStatus", "");
            row.put("AppliedProjectName", "");
            row.put("FlatTypeApplied", "");
        }

        return row;
    }

    public static List<Applicant> loadAll() {
        List<Map<String, String>> rawRows = CsvUtil.read(FilePath.APPLICANT_LIST_FILE);
        List<Applicant> applicants = new ArrayList<>();
        for (Map<String, String> row : rawRows) {
            applicants.add(fromCsvRow(row));
        }
        return applicants;
    }
    
    public static void updateApplicant(Applicant updatedApplicant) {
        List<Applicant> all = loadAll();
        for (int i = 0; i < all.size(); i++) {
            if (all.get(i).getNric().equalsIgnoreCase(updatedApplicant.getNric())) {
                all.set(i, updatedApplicant);
                break;
            }
        }
        saveAll(all);
    }
    
    public static void saveAll(List<Applicant> applicants) {
        List<Map<String, String>> rows = new ArrayList<>();
        for (Applicant a : applicants) {
            rows.add(toCsvRow(a));
        }
        CsvUtil.write(FilePath.APPLICANT_LIST_FILE, rows);
    }
    public static boolean exists(String nric) {
        return loadAll().stream()
            .anyMatch(a -> a.getNric().equalsIgnoreCase(nric));
    }
    
    public static void save(Applicant applicant) {
        List<Applicant> all = loadAll();
        all.add(applicant);
        saveAll(all);
    }
    
}