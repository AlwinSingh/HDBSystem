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

    /*
      Adds any missing columns to the CSV by rewriting the file.
      Existing rows are preserved and filled with "" for new columns.
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

    /* This updates ONE APPLICANT back to the CSV */
    public static void updateApplicant(Applicant updatedApplicant, String filePath) {
        List<Map<String, String>> rows = CSVReader.readCSV(filePath, List.of(
                "NRIC", "Password", "Name", "Age", "Marital Status",
                "FlatTypeApplied", "AppliedProjectName", "ApplicationStatus"
        ));

        boolean found = false;

        for (Map<String, String> row : rows) {
            if (row.get("NRIC").equals(updatedApplicant.getNric())) {
                row.put("Password", updatedApplicant.getPassword());
                row.put("Name", updatedApplicant.getName());
                row.put("Age", String.valueOf(updatedApplicant.getAge()));
                row.put("Marital Status", updatedApplicant.getMaritalStatus());
                row.put("FlatTypeApplied", defaultStr(updatedApplicant.getFlatTypeApplied()));
                row.put("AppliedProjectName", defaultStr(updatedApplicant.getAppliedProjectName()));
                row.put("ApplicationStatus", defaultStr(updatedApplicant.getApplicationStatus()));
                found = true;
                break;
            }
        }

        if (!found) {
            System.err.println("⚠️ Applicant not found in CSV for update: " + updatedApplicant.getNric());
            return;
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            List<String> headers = List.of(
                    "NRIC", "Password", "Name", "Age", "Marital Status",
                    "FlatTypeApplied", "AppliedProjectName", "ApplicationStatus"
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
            System.err.println("❌ Failed to update applicant in CSV: " + filePath);
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

    /* This UPDATES THE PROJECT NAME back to the CSV */
    public static boolean saveRenameProject(String originalProjectName, String newProjectName, String filePath) {
        List<Map<String, String>> rows = CSVReader.readCSV(filePath, List.of("Project Name"));
        boolean found = false;

        for (Map<String, String> row : rows) {
            if (row.get("Project Name").equals(originalProjectName)) {
                row.put("Project Name", newProjectName);
                found = true;
                break;
            }
        }

        if (!found) {
            System.err.println("⚠️ Project not found for renaming: " + originalProjectName);
            return false;
        }

        // Now write back all rows, preserving original headers
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            // Use the headers from the first row to preserve order
            List<String> headers = new ArrayList<>(rows.get(0).keySet());
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
            System.err.println("❌ Failed to rename project in CSV.");
            e.printStackTrace();
            return false;
        }
    }

    /* This ADDS ONE PROJECT back to the CSV */
    public static boolean saveNewProject(Project project, String filePath) {
        List<String> headers = List.of(
                "Project Name", "Neighborhood",
                "Type 1", "Number of units for Type 1", "Selling price for Type 1",
                "Type 2", "Number of units for Type 2", "Selling price for Type 2",
                "Application opening date", "Application closing date",
                "Manager", "Officer Slot", "Officer",
                "ManagerNRIC", "OfficerNRICs", "ApplicantNRICs", "Visibility"
        );

        DateTimeFormatter legacyFormat = DateTimeFormatter.ofPattern("M/d/yyyy");

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
                    project.getOpenDate().format(legacyFormat),
                    project.getCloseDate().format(legacyFormat),
                    project.getManagerName(),
                    String.valueOf(project.getOfficerSlot()),
                    project.getOfficerNames() != null ? String.join(",", project.getOfficerNames()) : "",
                    defaultStr(project.getManagerNRIC()),
                    project.getOfficerNames() != null ? String.join(",", project.getOfficerNRICs()) : "",
                    project.getOfficerNames() != null ? String.join(",", project.getApplicantNRICs()) : "",
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

    /* This updates ONE OFFICER back to the CSV */
    public static void updateOfficer(Officer updatedOfficer, String filePath) {
        List<Map<String, String>> rows = CSVReader.readCSV(filePath, List.of(
                "Name", "NRIC", "Age", "Marital Status", "Password",
                "AssignedProject", "RegistrationStatus"
        ));

        for (Map<String, String> row : rows) {
            if (row.get("NRIC").equals(updatedOfficer.getNric())) {
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

    /* This saves ONLY THE USER PASSWORD back to the CSV */
    public static boolean updateUserPassword(User user) {
        String filePath;
        List<String> headers;
        String role;

        if (user instanceof Applicant) {
            filePath = FilePath.APPLICANT_LIST_FILE;
            headers = List.of("Name", "NRIC", "Age", "Marital Status", "Password",
                    "FlatTypeApplied", "AppliedProjectName", "ApplicationStatus");
            role = "Applicant";
        } else if (user instanceof Officer) {
            filePath = FilePath.OFFICER_LIST_FILE;
            headers = List.of("Name", "NRIC", "Age", "Marital Status", "Password",
                    "AssignedProject", "RegistrationStatus");
            role = "Officer";
        } else if (user instanceof Manager) {
            filePath = FilePath.MANAGER_LIST_FILE;
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

    /* THIS UPDATES THE PROJECTLIST.CSV HEADERS */
    public static void updateProjectHeaders(List<Map<String, String>> rows, String filePath,
                                            List<String> baseHeaders, List<String> additionalHeaders) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            List<String> allHeaders = new ArrayList<>(baseHeaders);
            allHeaders.addAll(additionalHeaders);

            writer.println(String.join(",", allHeaders));

            for (Map<String, String> row : rows) {
                List<String> values = new ArrayList<>();
                for (String header : allHeaders) {
                    values.add(escapeCSV(row.getOrDefault(header, "")));
                }
                writer.println(String.join(",", values));
            }
        } catch (IOException e) {
            System.err.println("❌ Failed to update project CSV with new headers.");
            e.printStackTrace();
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
