package src.util;

import src.model.*;
import src.service.UserService;

import java.io.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class CSVWriter {
    /*
    This class is to UPDATE the CSVs accordingly

    ApplicantList.csv
    - Auto updated during load via CSVReader to include the new columns: FlatTypeApplied, AppliedProjectName, ApplicationStatus
    - Updated after applying, withdrawing or booking

    OfficerList.csv
    - Auto updated during load via CSVReader to include the new columns: AssignedProject, RegistrationStatus
    - Updated only when officer registers OR gets approved/rejected by the HDB Manager

    ManagerList.csv
    - Auto updated during load via CSVReader to include the new columns: ProjectsCreated (Comma-separated list in case got more than 1 project? but have to look up ProjectList.csv to see which is the ACTIVE project being handled...)
    - Updated whenever a project is updated, closed, opened, etc

    ProjectList.csv (Used as a 'relational' file, because all 3 users will be linked to at least ONE project)
    - Auto updated whenever a CRUD operation is performed to Projects
    - New columns: Visibility, OfficerNRICs, ApplicantNRICs
     */

    /**
     * Adds any missing columns to the CSV by rewriting the file.
     * Existing rows are preserved and filled with "" for new columns.
     */
    public static void addMissingColumns(String filePath, List<String> requiredHeaders, List<String> existingHeaders, List<Map<String, String>> rows) {
        Set<String> existingSet = new HashSet<>(existingHeaders);
        List<String> updatedHeaders = new ArrayList<>(existingHeaders);

        for (String required : requiredHeaders) {
            if (!existingSet.contains(required)) {
                updatedHeaders.add(required);
            }
        }

        // Update all rows with empty strings for missing fields
        for (Map<String, String> row : rows) {
            for (String header : updatedHeaders) {
                row.putIfAbsent(header, "");
            }
        }

        // Rewrite the CSV with updated headers and rows
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            writer.println(String.join(",", updatedHeaders));
            for (Map<String, String> row : rows) {
                List<String> values = new ArrayList<>();
                for (String header : updatedHeaders) {
                    values.add(row.getOrDefault(header, ""));
                }
                writer.println(String.join(",", values));
            }
        } catch (IOException e) {
            System.err.println("❌ Failed to update CSV with new columns: " + filePath);
            e.printStackTrace();
        }
    }

    /**
     * Updates the ManagerNRIC and OfficerNRICs fields for a specific project in the ProjectList CSV.
     */
    public static void updateProjectNRICs(String filePath, String projectName, String managerNRIC, List<String> officerNRICs) {
        List<Map<String, String>> updatedRows = new ArrayList<>();
        List<String> headers = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String headerLine = reader.readLine();
            if (headerLine == null) return;

            headers = Arrays.asList(headerLine.split(","));
            String line;

            while ((line = reader.readLine()) != null) {
                String[] values = line.split(",", -1);
                Map<String, String> row = new LinkedHashMap<>();
                for (int i = 0; i < headers.size(); i++) {
                    row.put(headers.get(i), i < values.length ? values[i] : "");
                }

                // If this is the target project, update the NRICs
                if (row.get("Project Name").equals(projectName)) {
                    row.put("ManagerNRIC", managerNRIC);
                    row.put("OfficerNRICs", String.join(",", officerNRICs));
                }

                updatedRows.add(row);
            }

        } catch (IOException e) {
            System.err.println("❌ Failed to read project CSV: " + filePath);
            e.printStackTrace();
            return;
        }

        // Write updated content back
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            writer.println(String.join(",", headers));
            for (Map<String, String> row : updatedRows) {
                List<String> values = new ArrayList<>();
                for (String header : headers) {
                    values.add(row.getOrDefault(header, ""));
                }
                writer.println(String.join(",", values));
            }
        } catch (IOException e) {
            System.err.println("❌ Failed to write updated project CSV: " + filePath);
            e.printStackTrace();
        }
    }

    public static void saveApplicants(Map<String, Applicant> applicants, String filePath) {
        List<String> headers = List.of(
                "NRIC", "Password", "Name", "Age", "Marital Status",
                "FlatTypeApplied", "AppliedProjectName", "ApplicationStatus"
        );

        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            writer.println(String.join(",", headers));

            for (Applicant applicant : applicants.values()) {
                List<String> row = List.of(
                        applicant.getNric(),
                        applicant.getPassword(),
                        applicant.getName(),
                        String.valueOf(applicant.getAge()),
                        applicant.getMaritalStatus(),
                        defaultStr(applicant.getFlatTypeApplied()),
                        defaultStr(applicant.getAppliedProjectName()),
                        defaultStr(applicant.getApplicationStatus())
                );

                writer.println(String.join(",", row));
            }
        } catch (IOException e) {
            System.err.println("❌ Failed to save applicants to CSV: " + filePath);
            e.printStackTrace();
        }
    }

    /**
     * Saves the full list of projects back to ProjectList.csv.
     */
    public static void saveProjects(Map<String, Project> projects, String filePath) {
        List<String> headers = List.of(
                "Project Name", "Neighborhood",
                "Type 1", "Number of units for Type 1", "Selling price for Type 1",
                "Type 2", "Number of units for Type 2", "Selling price for Type 2",
                "Application opening date", "Application closing date",
                "Manager", "Officer Slot", "Officer",
                "ManagerNRIC", "OfficerNRICs", "ApplicantNRICs", "Visibility"
        );

        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            writer.println(String.join(",", headers));

            for (Project project : projects.values()) {
                List<String> row = new ArrayList<>();
                row.add(project.getName());
                row.add(project.getNeighbourhood());
                row.add("2-Room"); // Type 1
                row.add(String.valueOf(project.getTwoRoomUnits()));
                row.add(String.valueOf(project.getTwoRoomPrice()));
                row.add("3-Room"); // Type 2
                row.add(String.valueOf(project.getThreeRoomUnits()));
                row.add(String.valueOf(project.getThreeRoomPrice()));

                DateTimeFormatter legacyFormat = DateTimeFormatter.ofPattern("M/d/yyyy");
                row.add(project.getOpenDate().format(legacyFormat));
                row.add(project.getCloseDate().format(legacyFormat));

                row.add(project.getManagerName()); // Manager name

                row.add(String.valueOf(project.getOfficerSlot())); // Officer Slot
                row.add(String.join(",", project.getOfficerNames()));


                // New fields
                row.add(project.getManagerNRIC());
                row.add(String.join(",", project.getOfficerNRICs()));
                row.add(String.join(",", project.getApplicantNRICs()));

                row.add(String.valueOf(project.isVisible()));
                writer.println(row.stream()
                        .map(CSVWriter::escapeCSV)
                        .collect(Collectors.joining(",")));
            }

        } catch (IOException e) {
            System.err.println("❌ Failed to save projects to CSV: " + filePath);
            e.printStackTrace();
        }
    }

    /* This SAVES ONE PROJECT back to the CSV */
    public static boolean saveProject(Project project, String filePath) {
        List<Map<String, String>> rows = CSVReader.readCSV(filePath, List.of(
                "Project Name", "Neighborhood",
                "Type 1", "Number of units for Type 1", "Selling price for Type 1",
                "Type 2", "Number of units for Type 2", "Selling price for Type 2",
                "Application opening date", "Application closing date",
                "Manager", "Officer Slot", "Officer",
                "ManagerNRIC", "OfficerNRICs", "ApplicantNRICs", "Visibility"
        ));

        boolean found = false;
        DateTimeFormatter legacyFormat = DateTimeFormatter.ofPattern("M/d/yyyy");

        for (Map<String, String> row : rows) {
            if (row.get("Project Name").equals(project.getName())) {
                row.put("Neighborhood", project.getNeighbourhood());
                row.put("Type 1", "2-Room");
                row.put("Number of units for Type 1", String.valueOf(project.getTwoRoomUnits()));
                row.put("Selling price for Type 1", String.valueOf(project.getTwoRoomPrice()));
                row.put("Type 2", "3-Room");
                row.put("Number of units for Type 2", String.valueOf(project.getThreeRoomUnits()));
                row.put("Selling price for Type 2", String.valueOf(project.getThreeRoomPrice()));
                row.put("Application opening date", project.getOpenDate().format(legacyFormat));
                row.put("Application closing date", project.getCloseDate().format(legacyFormat));
                row.put("Manager", project.getManagerName());
                row.put("Officer Slot", String.valueOf(project.getOfficerSlot()));
                row.put("Officer", String.join(",", project.getOfficerNames()));
                row.put("ManagerNRIC", project.getManagerNRIC());
                row.put("OfficerNRICs", String.join(",", project.getOfficerNRICs()));
                row.put("ApplicantNRICs", String.join(",", project.getApplicantNRICs()));
                row.put("Visibility", String.valueOf(project.isVisible()));
                found = true;
                break;
            }
        }

        if (!found) {
            System.err.println("⚠️ Project not found in CSV for update: " + project.getName());
            return false;
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            List<String> headers = List.of(
                    "Project Name", "Neighborhood",
                    "Type 1", "Number of units for Type 1", "Selling price for Type 1",
                    "Type 2", "Number of units for Type 2", "Selling price for Type 2",
                    "Application opening date", "Application closing date",
                    "Manager", "Officer Slot", "Officer",
                    "ManagerNRIC", "OfficerNRICs", "ApplicantNRICs", "Visibility"
            );
            writer.println(String.join(",", headers));

            for (Map<String, String> row : rows) {
                List<String> values = new ArrayList<>();
                for (String header : headers) {
                    values.add(escapeCSV(row.getOrDefault(header, "")));
                }
                writer.println(String.join(",", values));
            }

            return true;
        } catch (IOException e) {
            System.err.println("❌ Failed to save updated project to CSV: " + filePath);
            e.printStackTrace();
            return false;
        }
    }

    /* This ADDS ONE PROJECT back to the CSV */
    public static boolean appendProject(Project project, String filePath) {
        List<String> headers = List.of(
                "Project Name", "Neighborhood",
                "Type 1", "Number of units for Type 1", "Selling price for Type 1",
                "Type 2", "Number of units for Type 2", "Selling price for Type 2",
                "Application opening date", "Application closing date",
                "Manager", "Officer Slot", "Officer",
                "ManagerNRIC", "OfficerNRICs", "ApplicantNRICs", "Visibility"
        );

        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath, true))) {
            List<String> row = List.of(
                    project.getName(),
                    project.getNeighbourhood(),
                    "2-Room",
                    String.valueOf(project.getTwoRoomUnits()),
                    String.valueOf(project.getTwoRoomPrice()),
                    "3-Room",
                    String.valueOf(project.getThreeRoomUnits()),
                    String.valueOf(project.getThreeRoomPrice()),
                    project.getOpenDate().toString(),
                    project.getCloseDate().toString(),
                    project.getManagerName(),
                    String.valueOf(project.getOfficerSlot()),
                    String.join(",", project.getOfficerNames()),
                    defaultStr(project.getManagerNRIC()),
                    String.join(",", project.getOfficerNRICs()),
                    String.join(",", project.getApplicantNRICs()),
                    String.valueOf(project.isVisible())
            );

            writer.println(row.stream().map(CSVWriter::escapeCSV).collect(Collectors.joining(",")));
            System.out.println("✅ Project appended to CSV: " + filePath);
            return true;
        } catch (IOException e) {
            System.err.println("❌ Failed to append project to CSV: " + filePath);
            e.printStackTrace();
            return false;
        }
    }

    /* This updates ONE MANAGER back to the CSV */
    public static boolean updateManager(Manager updatedManager, String filePath) {
        List<Map<String, String>> rows = CSVReader.readCSV(filePath, List.of(
                "Name", "NRIC", "Age", "Marital Status", "Password", "ProjectsCreated"
        ));

        for (Map<String, String> row : rows) {
            if (row.get("NRIC").equals(updatedManager.getNric())) {
                System.out.println("DEBUG CSVWRITER.JAVA >> Manager NRIC: " + updatedManager.getNric());
                System.out.println("DEBUG CSVWRITER.JAVA >> Projects created: " + String.join(",", updatedManager.getProjectsCreated()));

                row.put("Name", updatedManager.getName());
                row.put("NRIC", updatedManager.getNric());
                row.put("Age", String.valueOf(updatedManager.getAge()));
                row.put("Marital Status", updatedManager.getMaritalStatus());
                row.put("Password", updatedManager.getPassword());
                row.put("ProjectsCreated", String.join(",", updatedManager.getProjectsCreated()));
                break;
            }
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            List<String> headers = List.of(
                    "NRIC", "Password", "Name", "Age", "Marital Status", "ProjectsCreated"
            );
            writer.println(String.join(",", headers));

            for (Map<String, String> row : rows) {
                List<String> values = new ArrayList<>();
                for (String header : headers) {
                    values.add(escapeCSV(row.getOrDefault(header, "")));
                }
                writer.println(String.join(",", values));
            }

            return true;
        } catch (IOException e) {
            System.err.println("❌ Failed to update manager in CSV: " + filePath);
            e.printStackTrace();
            return false;
        }
    }


    /* This updates ONE OFFICER back to the CSV */
    public static void updateOfficer(Officer updatedOfficer, String filePath) {
        List<Map<String, String>> rows = CSVReader.readCSV(filePath, List.of(
                "Name", "NRIC", "Age", "Marital Status", "Password",
                "AssignedProject", "RegistrationStatus"
        ));

        for (Map<String, String> row : rows) {
            if (row.get("NRIC").equals(updatedOfficer.getNric())) {
                System.out.println("DEBUG CSVWRITER.JAVA >> Officer NRIC: " + updatedOfficer.getNric());
                System.out.println("DEBUG CSVWRITER.JAVA >> Officer assigned project name: '" + updatedOfficer.getAssignedProjectName() + "'");

                row.put("Name", updatedOfficer.getName());
                row.put("NRIC", updatedOfficer.getNric());
                row.put("Age", String.valueOf(updatedOfficer.getAge()));
                row.put("Marital Status", updatedOfficer.getMaritalStatus());
                row.put("Password", updatedOfficer.getPassword());
                row.put("AssignedProject", updatedOfficer.getAssignedProjectName());
                row.put("RegistrationStatus", updatedOfficer.getRegistrationStatus());
                break;
            }
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            List<String> headers = List.of(
                    "NRIC", "Password", "Name", "Age", "Marital Status", "AssignedProject", "RegistrationStatus"
            );
            writer.println(String.join(",", headers));

            for (Map<String, String> row : rows) {
                List<String> values = new ArrayList<>();
                for (String header : headers) {
                    values.add(escapeCSV(row.getOrDefault(header, "")));
                }
                writer.println(String.join(",", values));
            }
        } catch (IOException e) {
            System.err.println("❌ Failed to update officer in CSV: " + filePath);
            e.printStackTrace();
        }
    }

    /* This saves the list of officers back to the CSV */
    public static void saveOfficers(Map<String, Officer> officers, String filePath) {
        List<String> headers = List.of(
                "Name", "NRIC", "Age", "Marital Status", "Password",
                "AssignedProject", "RegistrationStatus"
        );

        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            writer.println(String.join(",", headers));

            for (Officer officer : officers.values()) {
                List<String> row = List.of(
                        officer.getName(),
                        officer.getNric(),
                        String.valueOf(officer.getAge()),
                        officer.getMaritalStatus(),
                        officer.getPassword(),
                        defaultStr(officer.getAssignedProjectName()),
                        defaultStr(officer.getRegistrationStatus())
                );

                writer.println(row.stream().map(CSVWriter::escapeCSV).collect(Collectors.joining(",")));
            }
        } catch (IOException e) {
            System.err.println("❌ Failed to save officers to CSV: " + filePath);
            e.printStackTrace();
        }
    }

    /* This saves the managers back to the CSV */
    public static void saveManagers(Map<String, Manager> managers, String filePath) {
        List<String> headers = List.of(
                "Name", "NRIC", "Age", "Marital Status", "Password",
                "AssignedProject", "RegistrationStatus"
        );

        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            writer.println(String.join(",", headers));

            for (Manager manager : managers.values()) {
                List<String> row = List.of(
                        manager.getNric(),
                        manager.getPassword(),
                        manager.getName(),
                        defaultStr(String.join(",", manager.getProjectsCreated()))
                );
                writer.println(row.stream().map(CSVWriter::escapeCSV).collect(Collectors.joining(",")));
            }
        } catch (IOException e) {
            System.err.println("❌ Failed to save managers to CSV: " + filePath);
            e.printStackTrace();
        }
    }

    /* This saves ONLY THE USER PASSWORD back to the CSV */
    public static boolean updateUserPassword(User user) {
        String filePath;
        List<String> headers;
        String role;

        if (user instanceof Applicant) {
            filePath = "data/ApplicantList.csv";
            headers = List.of("Name", "NRIC", "Age", "Marital Status", "Password",
                    "FlatTypeApplied", "AppliedProjectName", "ApplicationStatus");
            role = "Applicant";
        } else if (user instanceof Officer) {
            filePath = "data/OfficerList.csv";
            headers = List.of("Name", "NRIC", "Age", "Marital Status", "Password",
                    "AssignedProject", "RegistrationStatus");
            role = "Officer";
        } else if (user instanceof Manager) {
            filePath = "data/ManagerList.csv";
            headers = List.of("Name", "NRIC", "Age", "Marital Status", "Password",
                    "ProjectsCreated");
            role = "Manager";
        } else {
            System.err.println("❌ Unknown user type.");
            return false;
        }

        List<Map<String, String>> rows = CSVReader.readCSV(filePath, headers);

        boolean updated = false;
        for (Map<String, String> row : rows) {
            if (row.get("NRIC").equals(user.getNric())) {
                row.put("Password", user.getPassword());
                updated = true;
                break;
            }
        }

        if (!updated) {
            System.err.println("❌ Failed to locate " + role + " in CSV: " + user.getNric());
            return false;
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            writer.println(String.join(",", headers));
            for (Map<String, String> row : rows) {
                List<String> values = new ArrayList<>();
                for (String header : headers) {
                    values.add(escapeCSV(row.getOrDefault(header, "")));
                }
                writer.println(String.join(",", values));
            }
            return true;
        } catch (IOException e) {
            System.err.println("❌ Failed to update " + role + " CSV.");
            e.printStackTrace();
            return false;
        }
    }


    // A utility method to ensure null strings are treated consistently by defaulting to "" or its value...
    private static String defaultStr(String value) {
        return value == null ? "" : value;
    }

    // Only used if you need to escapeCSV explicity such as when u want to END the row completely to a new line of data...
    private static String escapeCSV(String value) {
        if (value == null) return "";
        if (value.contains(",")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
