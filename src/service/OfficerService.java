package src.service;

import src.model.Officer;
import src.model.Project;
import src.model.Applicant;
import src.util.CSVWriter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class OfficerService {

    private final ProjectService projectService;
    private final UserService userService;

    public OfficerService(ProjectService projectService, UserService userService) {
        this.projectService = projectService;
        this.userService = userService;
    }

    // 1. Officer requests to register for a project
    public boolean registerForProject(Officer officer, String projectName) {
        // If their registration status is not empty AND their status is NOT REJECTED then they are not allowed to apply...
        // Officers that were rejected, can try again to apply but pending/approved are NOT allowed

        if (!officer.getRegistrationStatus().isBlank() &&
                !officer.getRegistrationStatus().equalsIgnoreCase("REJECTED")) {
            System.out.println("⚠️ You have already registered or been approved/rejected.");
            return false;
        }

        Project project = projectService.getProjectByName(projectName);
        if (project == null) {
            System.out.println("❌ Project not found.");
            return false;
        }

        // Check if project has space for more officers
        int maxSlots = project.getOfficerSlot(); // Assuming this method exists
        if (project.getOfficerNRICs().size() >= maxSlots) {
            System.out.println("⚠️ This project has reached its maximum officer capacity.");
            return false;
        }

        // Register the officer
        officer.setAssignedProjectName(projectName);
        officer.setRegistrationStatus("PENDING");

        CSVWriter.updateOfficer(officer, "data/OfficerList.csv");

        System.out.println("✅ Registration submitted for project: " + projectName);
        return true;
    }


    // 2. View assigned project details
    public void viewAssignedProject(Officer officer) {
        String assignedProject = officer.getAssignedProjectName();
        String status = officer.getRegistrationStatus();

        if (assignedProject == null || assignedProject.isBlank()) {
            System.out.println("⚠️ You have not registered for any project.");
            return;
        }

        if (!status.equalsIgnoreCase("APPROVED")) {
            System.out.println("⚠️ Your registration is still " + status + " for " + assignedProject + ". You can only view the project after approval.");
            return;
        }

        Project project = projectService.getProjectByName(assignedProject);
        if (project == null) {
            System.out.println("❌ Assigned project not found.");
            return;
        }

        // Todo: Maybe include the LIST OF APPLICANTS for the project?
        System.out.println("=== Assigned Project Details ===");
        System.out.println("Project Name: " + project.getName());
        System.out.println("Location: " + project.getNeighbourhood());
        System.out.println("2-Room Units: " + project.getTwoRoomUnits() + " @ $" + project.getTwoRoomPrice());
        System.out.println("3-Room Units: " + project.getThreeRoomUnits() + " @ $" + project.getThreeRoomPrice());
        System.out.println("Application Window: " + project.getOpenDate() + " to " + project.getCloseDate());
        System.out.println("Visibility: " + project.isVisible());
    }


    // 3. View applicant NRICs for assigned project
    // Alwin: I have implemented this such that it segregates it by application status
    // PENDING, SUCCESSFUL and UNSUCCESSFUL are all SHOWN but grouped together
    public void viewApplicantList(Officer officer) {
        String projectName = officer.getAssignedProjectName();
        String status = officer.getRegistrationStatus();

        if (projectName == null || projectName.isBlank()) {
            System.out.println("⚠️ You are not assigned to any project.");
            return;
        }

        if (!status.equalsIgnoreCase(Officer.RegistrationStatusType.APPROVED.name())) {
            System.out.println("⚠️ You can only view applicants after your registration is approved.");
            return;
        }

        Project project = projectService.getProjectByName(projectName);
        if (project == null) {
            System.out.println("❌ Project not found.");
            return;
        }

        List<String> nricList = project.getApplicantNRICs();
        if (nricList.isEmpty()) {
            System.out.println("📭 No applicants have applied yet.");
            return;
        }

        // Group applicants by application status
        // Alwin: Please take note that LinkedHashMap uses more memory but will retain its iteration order over HashMap, for this use case, a LinkedHashMap is a lot better...do not change, thank you
        // HashMap ordering: PENDING SUCCESFUL UNSUCCESSFUL OR COULD BE UNSUCCESSFUL PENDING SUCCESSFUL, THE ORDER IS DETERMINED BY INTERNAL HASHING
        // LinkedHashMap ordering: PENDING SUCCESSFUL UNSUCCESSFUL, THIS ORDER IS FIXED ALWAYS.
        // An alternative to using any additional memory / hashmaps would be to just print and filter but i want to make this as dynamic as possible...
        Map<String, List<Applicant>> grouped = new LinkedHashMap<>();
        grouped.put(Applicant.AppStatusType.PENDING.name(), new ArrayList<>());
        grouped.put(Applicant.AppStatusType.SUCCESSFUL.name(), new ArrayList<>());
        grouped.put(Applicant.AppStatusType.UNSUCCESSFUL.name(), new ArrayList<>());

        for (String nric : nricList) {
            Applicant applicant = userService.getApplicantByNric(nric);
            if (applicant == null) continue;

            // Ensure the applicant actually applied to this officer's assigned project else just SKIP
            if (!projectName.equalsIgnoreCase(applicant.getAppliedProjectName())) continue;

            String appStatus = applicant.getApplicationStatus();
            if (!grouped.containsKey(appStatus)) {
                grouped.put(appStatus, new ArrayList<>());
            }

            grouped.get(appStatus).add(applicant);
        }

        // Display grouped applicants
        for (String group : List.of(Applicant.AppStatusType.PENDING.name(), Applicant.AppStatusType.SUCCESSFUL.name(), Applicant.AppStatusType.UNSUCCESSFUL.name())) {
            List<Applicant> list = grouped.get(group);
            if (list.isEmpty()) continue;

            System.out.println("\n=== " + group + " Applicants ===");
            for (Applicant applicant : list) {
                System.out.printf("- %s (%s)\n", applicant.getNric(), applicant.getName());
            }
        }
    }

    // 4. Approve or reject a specific applicant
    /*
        Officer must be approved
        Applicant must be in the assigned project
        Applicant's application status must be PENDING
     */
    public boolean handleApplication(Project project, String applicantNRIC, boolean approve) {
        Applicant applicant = userService.getApplicantByNric(applicantNRIC);
        if (applicant == null) {
            System.out.println("❌ Applicant not found.");
            return false;
        }

        String status = applicant.getApplicationStatus();
        if (!status.equalsIgnoreCase(Applicant.AppStatusType.PENDING.name())) {
            System.out.println("⚠️ Applicant has already been processed (" + status + ").");
            return false;
        }

        if (approve) {
            applicant.setApplicationStatus(Applicant.AppStatusType.SUCCESSFUL.name());
            System.out.println("✅ Applicant " + applicantNRIC + " approved.");
        } else {
            project.getApplicantNRICs().remove(applicant.getNric());
            applicant.setApplicationStatus(Applicant.AppStatusType.UNSUCCESSFUL.name());
            System.out.println("❌ Applicant " + applicantNRIC + " rejected.");
        }

        CSVWriter.saveApplicants(userService.getAllApplicants(), "data/ApplicantList.csv");
        CSVWriter.saveProject(project, "data/ProjectList.csv");

        return true;
    }

    // 5. Mark a specific applicant as having successfully booked a flat
    /*
        Applicant must be:
        Already APPROVED/SUCCESSFUL
        Not yet SUCCESSFUL
        Project must have units remaining for the selected flat type

        Finally, set status to BOOKED
     */
    public boolean bookFlat(String applicantNRIC) {
        Applicant applicant = userService.getApplicantByNric(applicantNRIC);
        if (applicant == null) {
            System.out.println("❌ Applicant not found.");
            return false;
        }

        if (!applicant.getApplicationStatus().equalsIgnoreCase(Applicant.AppStatusType.SUCCESSFUL.name())) {
            System.out.println("⚠️ Only APPROVED applicants can book a flat.");
            return false;
        }

        String projectName = applicant.getAppliedProjectName();
        Project project = projectService.getProjectByName(projectName);
        if (project == null) {
            System.out.println("❌ Project not found.");
            return false;
        }

        String flatType = applicant.getFlatTypeApplied();
        if (flatType.equalsIgnoreCase("2-Room")) {
            if (project.getTwoRoomUnits() == 0) {
                System.out.println("❌ No more 2-Room flats available.");
                return false;
            }
            project.setTwoRoomUnits(project.getTwoRoomUnits() - 1);
        } else if (flatType.equalsIgnoreCase("3-Room")) {
            if (project.getThreeRoomUnits() == 0) {
                System.out.println("❌ No more 3-Room flats available.");
                return false;
            }
            project.setThreeRoomUnits(project.getThreeRoomUnits() - 1);
        } else {
            System.out.println("❌ Unknown flat type: " + flatType);
            return false;
        }

        applicant.setApplicationStatus(Applicant.AppStatusType.BOOKED.name());
        System.out.println("🏠 Flat booked successfully for " + applicantNRIC);

        CSVWriter.saveApplicants(userService.getAllApplicants(), "data/ApplicantList.csv");
        //CSVWriter.saveProjects(projectService.getAllProjects(), "data/ProjectList.csv");

        return true;
    }


    // 6. Generate receipt for a booked applicant
    // Applicant must have status = BOOKED
    public void generateReceipt(String applicantNRIC) {
        Applicant applicant = userService.getApplicantByNric(applicantNRIC);
        if (applicant == null) {
            System.out.println("❌ Applicant not found.");
            return;
        }

        if (!applicant.getApplicationStatus().equalsIgnoreCase(Applicant.AppStatusType.BOOKED.name())) {
            System.out.println("⚠️ Receipt can only be generated for booked applicants.");
            return;
        }

        String projectName = applicant.getAppliedProjectName();
        Project project = projectService.getProjectByName(projectName);
        if (project == null) {
            System.out.println("❌ Project not found.");
            return;
        }

        String flatType = applicant.getFlatTypeApplied();
        double price = flatType.equalsIgnoreCase("2-Room") ? project.getTwoRoomPrice() : project.getThreeRoomPrice();

        System.out.println("======= Booking Receipt =======");
        System.out.println("Applicant: " + applicant.getName() + " (" + applicant.getNric() + ")");
        System.out.println("Project: " + project.getName());
        System.out.println("Flat Type: " + flatType);
        System.out.println("Price: $" + price);
        System.out.println("Booking Status: " + applicant.getApplicationStatus());
        System.out.println("==================================");
    }
}