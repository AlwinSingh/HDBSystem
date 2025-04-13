package src.service;

import src.model.Manager;
import src.model.Officer;
import src.model.Project;
import src.util.CSVReader;
import src.util.CSVWriter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for loading and managing BTO projects (adapted to legacy headers).
 */
public class ProjectService {

    private final Map<String, Project> projects = new HashMap<>();
    private final UserService userService;

    public ProjectService(UserService userService) {
        this.userService = userService;
        loadProjects();
    }

    private void loadProjects() {
        List<String> baseHeaders = List.of(
                "Project Name", "Neighborhood", "Type 1", "Number of units for Type 1",
                "Selling price for Type 1", "Type 2", "Number of units for Type 2",
                "Selling price for Type 2", "Application opening date", "Application closing date",
                "Manager", "Officer Slot", "Officer"
        );

        List<String> newHeaders = List.of("ManagerNRIC", "OfficerNRICs", "ApplicantNRICs", "Visibility");

        List<Map<String, String>> rows = CSVReader.readCSV("data/ProjectList.csv", baseHeaders);
        LocalDate today = LocalDate.now();
        boolean updated = false;

        List<Map<String, String>> rowsToRemove = new ArrayList<>();

        for (Map<String, String> row : rows) {
            // === 1. Ensure new columns exist ===
            for (String header : newHeaders) {
                if (!row.containsKey(header)) {
                    row.put(header, header.equals("Visibility") ? "false" : "");
                    updated = true;
                }
            }

            // === 2. Visibility check based on date range ===
            try {
                DateTimeFormatter legacyDateFormat = DateTimeFormatter.ofPattern("M/d/yyyy");
                LocalDate openDate = LocalDate.parse(row.get("Application opening date"), legacyDateFormat);
                LocalDate closeDate = LocalDate.parse(row.get("Application closing date"), legacyDateFormat);

                boolean visible = (today.isEqual(openDate) || today.isAfter(openDate)) &&
                        (today.isBefore(closeDate) || today.isEqual(closeDate));
                row.put("Visibility", String.valueOf(visible));
                updated = true;
            } catch (Exception e) {
                System.err.println("⚠️ Could not parse open/close date for: " + row.get("Project Name"));
            }

            // === 3. Set Manager NRIC ===
            String managerName = row.get("Manager").trim();
            Manager manager = userService.getManagerByName(managerName);
            if (manager == null) {
                System.err.println("❌ Manager not found: " + managerName + " — Project will be removed.");
                rowsToRemove.add(row); // Track for deletion
                continue;
            }

            row.put("ManagerNRIC", manager.getNric());
            updated = true;

            // === 4. Set Officer NRICs ===
            String rawOfficerNames = row.get("Officer").trim();
            List<String> officerNames = Arrays.stream(rawOfficerNames.split(","))
                    .map(String::trim)
                    .collect(Collectors.toList());

            List<String> matchedOfficerNrics = new ArrayList<>();
            List<String> validOfficerNames = new ArrayList<>();

            for (String name : officerNames) {
                Officer officer = userService.getOfficerByName(name);
                if (officer != null) {
                    matchedOfficerNrics.add(officer.getNric());
                    validOfficerNames.add(name);
                } else {
                    System.err.println("⚠️ Officer not found: " + name + " — Removing from project.");
                }
            }

            // Clean up and save filtered names back
            row.put("Officer", String.join(",", validOfficerNames));
            row.put("OfficerNRICs", String.join(",", matchedOfficerNrics));
            updated = true;

            String rawApplicantNrics = row.getOrDefault("ApplicantNRICs", "").trim();
            List<String> applicantNrics = rawApplicantNrics.isEmpty() ? new ArrayList<>() :
                    Arrays.stream(rawApplicantNrics.split(",")).map(String::trim).collect(Collectors.toList());

            List<String> validApplicantNrics = new ArrayList<>();

            for (String nric : applicantNrics) {
                if (userService.getApplicantByNric(nric) != null) {
                    validApplicantNrics.add(nric);
                } else {
                    System.err.println("⚠️ Applicant NRIC not found: " + nric + " — Removing from project.");
                }
            }

            row.put("ApplicantNRICs", String.join(",", validApplicantNrics));
            updated = true;

            /* LOAD PROJECTS INTO THE HASHMAP */
            // === 5. Construct Project object ===
            try {
                String projectName = row.get("Project Name");
                String neighbourhood = row.get("Neighborhood");

                int twoRoomUnits = Integer.parseInt(row.get("Number of units for Type 1"));
                double twoRoomPrice = Double.parseDouble(row.get("Selling price for Type 1"));
                int threeRoomUnits = Integer.parseInt(row.get("Number of units for Type 2"));
                double threeRoomPrice = Double.parseDouble(row.get("Selling price for Type 2"));

                DateTimeFormatter legacyFormat = DateTimeFormatter.ofPattern("M/d/yyyy");
                LocalDate openDate = LocalDate.parse(row.get("Application opening date"), legacyFormat);
                LocalDate closeDate = LocalDate.parse(row.get("Application closing date"), legacyFormat);

                managerName = row.get("Manager");
                String managerNRIC = row.get("ManagerNRIC");
                int officerSlot = Integer.parseInt(row.get("Officer Slot"));

                officerNames = Arrays.stream(row.get("Officer").split(","))
                        .map(String::trim)
                        .collect(Collectors.toCollection(ArrayList::new));

                List<String> officerNRICs = Arrays.stream(row.get("OfficerNRICs").split(","))
                        .map(String::trim)
                        .filter(s -> !s.isBlank())
                        .collect(Collectors.toCollection(ArrayList::new));

                List<String> applicantNRICs = Arrays.stream(row.get("ApplicantNRICs").split(","))
                        .map(String::trim)
                        .filter(s -> !s.isBlank())
                        .collect(Collectors.toCollection(ArrayList::new));


                boolean visible = Boolean.parseBoolean(row.get("Visibility"));

                Project project = new Project(
                        projectName, neighbourhood,
                        twoRoomUnits, twoRoomPrice,
                        threeRoomUnits, threeRoomPrice,
                        openDate, closeDate,
                        managerName, officerSlot, officerNames,
                        managerNRIC, officerNRICs, applicantNRICs, visible
                );

                projects.put(projectName, project);
            } catch (Exception e) {
                System.err.println("❌ Failed to parse project: " + row);
                e.printStackTrace();
            }
        }

        rows.removeAll(rowsToRemove);

        if (updated) {
            System.out.println("✅ ProjectList.csv updated with visibility and NRICs.");
            CSVWriter.updateProjectHeaders(rows, "data/ProjectList.csv", baseHeaders, newHeaders);
        }
    }

    public Project getProjectByName(String name) {
        return projects.get(name);
    }

    public Map<String, Project> getAllProjects() {
        return projects;
    }
}
