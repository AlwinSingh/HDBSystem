package src.service;

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

    /*public ProjectService() {
        loadProjects();
    }*/

    private void loadProjects() {
        // Original + new headers
        List<String> headers = List.of(
                "Project Name", "Neighborhood",               // Legacy name: Neighborhood
                "Type 1", "Number of units for Type 1",       // => 2RoomUnits
                "Selling price for Type 1",                   // => 2RoomPrice
                "Type 2", "Number of units for Type 2",       // => 3RoomUnits
                "Selling price for Type 2",                   // => 3RoomPrice
                "Application opening date",                   // => OpenDate
                "Application closing date",                   // => CloseDate
                "Manager", "Officer Slot", "Officer"         // Legacy name
                // New columns to add
               // "ManagerNRIC", "OfficerNRICs", "ApplicantNRICs", "Visibility"
        );

        Map<String, Map<String, String>> data =
                CSVReader.readCSVByKey("data/ProjectList.csv", headers, "Project Name");

        for (Map.Entry<String, Map<String, String>> entry : data.entrySet()) {
            Map<String, String> row = entry.getValue();
            try {
                String projectName = row.get("Project Name");
                String neighborhood = row.get("Neighborhood");

                // Manager Name & NRIC
                String managerName = row.getOrDefault("Manager", "").trim();
                String managerNRIC = row.getOrDefault("ManagerNRIC", "").trim();

                String officerNamesRaw = row.getOrDefault("Officer", "").trim();
                List<String> officerNames = officerNamesRaw.isBlank()
                        ? new ArrayList<>()
                        : Arrays.stream(officerNamesRaw.split(","))
                        .map(String::trim)
                        .collect(Collectors.toList());

                List<String> officerNRICs = new ArrayList<>();
                String officerNRICsRaw = row.getOrDefault("OfficerNRICs", "").trim();
                if (!officerNRICsRaw.isBlank()) {
                    officerNRICs = Arrays.stream(officerNRICsRaw.split(",")).map(String::trim).collect(Collectors.toList());
                }

                // Extract and convert
                int twoRoomUnits = Integer.parseInt(row.getOrDefault("Number of units for Type 1", "0"));
                double twoRoomPrice = Double.parseDouble(row.getOrDefault("Selling price for Type 1", "0"));
                int threeRoomUnits = Integer.parseInt(row.getOrDefault("Number of units for Type 2", "0"));
                double threeRoomPrice = Double.parseDouble(row.getOrDefault("Selling price for Type 2", "0"));

                DateTimeFormatter legacyDateFormat = DateTimeFormatter.ofPattern("M/d/yyyy");
                LocalDate openDate = LocalDate.parse(row.get("Application opening date"), legacyDateFormat);
                LocalDate closeDate = LocalDate.parse(row.get("Application closing date"), legacyDateFormat);

                int officerSlot = Integer.parseInt(row.getOrDefault("Officer Slot", "0"));

                // Convert visibility
                String visRaw = row.getOrDefault("Visibility", "true").trim();
                boolean visibility = visRaw.isBlank() ? false : Boolean.parseBoolean(visRaw);
                visibility = true;

                Project project = new Project(
                        projectName,
                        neighborhood,
                        twoRoomUnits,
                        twoRoomPrice,
                        threeRoomUnits,
                        threeRoomPrice,
                        openDate,
                        closeDate,
                        managerName,
                        officerSlot,
                        officerNames,
                        managerNRIC,
                        visibility
                );

                // Normalize manager NRIC if only if count mismatch
                if (managerName == "" && managerNRIC == "") {
                    // Close the project....this is a rubbish project with no manager assigned
                } else if (managerName != "" && managerNRIC == "") {
                    // What if manager name is there but NRIC isn't there?
                    project.setManagerNRIC(userService.getManagerByName(managerName).getNric());
                } else if (managerNRIC != "" && managerNRIC == "") {
                    // What if NRIC is there but manager name isn't there?
                    project.setManagerName(userService.getManagerByNric(managerNRIC).getName());
                }

                // Load Applicant NRICs
                String applicants = row.getOrDefault("ApplicantNRICs", "");
                if (!applicants.isBlank()) {
                    for (String nric : applicants.split(",")) {
                        project.addApplicant(nric.trim());
                    }
                }

                projects.put(project.getName(), project);

            } catch (Exception e) {
                System.err.println("⚠️ Failed to parse project: " + row);
                e.printStackTrace();
            }
        }
    }

    public Project getProjectByName(String name) {
        return projects.get(name);
    }

    public Map<String, Project> getAllProjects() {
        return projects;
    }
}
