package src.util;

import java.io.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import src.model.*;

/**
 * Utility class for writing and updating CSV files.
 */
public class CSVWriter {

    /**
     * Adds any missing columns to the CSV by rewriting the file.
     * Existing rows are preserved and new columns are filled with empty strings.
     */
    public static void addMissingColumns(String filePath, List<String> requiredHeaders, List<String> existingHeaders, List<Map<String, String>> rows) {
        Set<String> existingSet = new HashSet<>(existingHeaders);
        List<String> updatedHeaders = new ArrayList<>(existingHeaders);

        for (String required : requiredHeaders) {
            if (!existingSet.contains(required)) {
                updatedHeaders.add(required);
            }
        }

        // Update all rows with empty strings for missing fields.
        for (Map<String, String> row : rows) {
            for (String header : updatedHeaders) {
                row.putIfAbsent(header, "");
            }
        }

        // Rewrite the CSV with updated headers and rows.
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
     * Updates one Applicant’s information in the CSV.
     */
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

    /**
     * Saves one Project’s data back to the CSV.
     */
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

    /**
     * Updates just the project name (for renaming) in the CSV.
     */
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

        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
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

    /**
     * Appends a new project to the CSV.
     */
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
                    project.getOfficerNRICs() != null ? String.join(",", project.getOfficerNRICs()) : "",
                    project.getApplicantNRICs() != null ? String.join(",", project.getApplicantNRICs()) : "",
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

    /**
     * Updates one Officer’s information in the CSV.
     */
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

    /**
     * Updates ONLY the user password in the CSV.
     */
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

    /**
     * Updates the headers for the project CSV file.
     */
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

    // Helper method to return empty string if value is null.
    private static String defaultStr(String value) {
        return value == null ? "" : value;
    }

    // Escapes CSV values if necessary.
    public static String escapeCSV(String value) {
        if (value == null) return "";
        if (value.contains(",")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
    public static void saveEnquiries(List<Enquiry> enquiries, String filePath) {
        List<String> headers = List.of("EnquiryId", "Content", "CreatedBy", "Project", "Replies");
    
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            writer.println(String.join(",", headers));
            for (Enquiry e : enquiries) {
                List<String> row = List.of(
                        String.valueOf(e.getEnquiryId()),
                        escapeCSV(e.getContent()),
                        e.getCreatedByNric(),
                        e.getRelatedProject() != null ? e.getRelatedProject().getName() : "",
                        escapeCSV(String.join("|", e.getReplies()))
                );
                writer.println(String.join(",", row));
            }
        } catch (IOException e) {
            System.err.println("❌ Failed to save enquiries to CSV.");
            e.printStackTrace();
        }
    }
    
    
}
