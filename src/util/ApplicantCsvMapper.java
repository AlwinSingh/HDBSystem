package src.util;

import src.model.*;
import src.repository.ApplicantRepository;

import java.util.*;

public class ApplicantCsvMapper implements ApplicantRepository {

    /**
     * Loads all applicants from the CSV file.
     *
     * @return List of applicants.
     */
    @Override
    public List<Applicant> loadAll() {
        List<Map<String, String>> rows = CsvUtil.read(FilePath.APPLICANT_LIST_FILE);
        List<Applicant> result = new ArrayList<>();
        for (Map<String, String> row : rows) {
            result.add(fromCsvRow(row));
        }
        return result;
    }

    /**
     * Updates a specific applicant entry by NRIC in the CSV.
     *
     * @param updatedApplicant The updated applicant object.
     */
    @Override
    public void update(Applicant updatedApplicant) {
        List<Applicant> all = loadAll();
        for (int i = 0; i < all.size(); i++) {
            if (all.get(i).getNric().equalsIgnoreCase(updatedApplicant.getNric())) {
                all.set(i, updatedApplicant);
                break;
            }
        }
        saveAll(all);
    }

    /**
     * Saves a new applicant to the CSV file.
     *
     * @param applicant The applicant to add.
     */

    @Override
    public void save(Applicant applicant) {
        Map<String, String> row = toCsvRow(applicant);
        CsvUtil.append(FilePath.APPLICANT_LIST_FILE, row);
    }

    /**
     * Checks if an applicant exists in the CSV file by NRIC.
     *
     * @param nric The NRIC to check.
     * @return True if applicant exists, false otherwise.
     */

    @Override
    public boolean exists(String nric) {
        return loadAll().stream()
            .anyMatch(a -> a.getNric().equalsIgnoreCase(nric));
    }

     /**
     * Saves a full list of applicants to the CSV file.
     *
     * @param applicants The list to save.
     */
    private void saveAll(List<Applicant> applicants) {
        List<Map<String, String>> rows = new ArrayList<>();
        for (Applicant a : applicants) {
            rows.add(toCsvRow(a));
        }
        CsvUtil.write(FilePath.APPLICANT_LIST_FILE, rows);
    }


    /**
     * Parsing a CSV row into an Applicant object, including application data.
     *
     * @param row The CSV row.
     * @return An Applicant object.
     */
    private Applicant fromCsvRow(Map<String, String> row) {
        String nric = row.getOrDefault("NRIC", "").trim();
        String password = row.getOrDefault("Password", "").trim();
        String name = row.getOrDefault("Name", "").trim();
        int age = Integer.parseInt(row.getOrDefault("Age", "0").trim());
        String maritalStatus = row.getOrDefault("Marital Status", "Single").trim();

        Applicant applicant = new Applicant(nric, password, name, age, maritalStatus);

        String appStatus = row.getOrDefault("ApplicationStatus", "").trim();
        String projectName = row.getOrDefault("AppliedProjectName", "").trim();
        String flatType = row.getOrDefault("FlatTypeApplied", "").trim();

        if (!appStatus.isEmpty() && !projectName.isEmpty() && !flatType.isEmpty()) {
            Project dummy = new Project();
            dummy.setProjectName(projectName);
            Application app = new Application(applicant, dummy, appStatus, flatType);
            applicant.setApplication(app);
        }

        return applicant;
    }

    /**
     * Converting an applicant and their application, if present to a CSV row.
     *
     * @param applicant The applicant.
     * @return CSV row representation.
     */
    private Map<String, String> toCsvRow(Applicant applicant) {
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
}
