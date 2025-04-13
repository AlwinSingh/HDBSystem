package src.service;

import src.model.Officer;
import src.model.Project;
import src.model.Applicant;
import src.util.CSVWriter;
import src.util.InputValidator;

import java.time.LocalDate;
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

    private void printVacantProjects(ProjectService projectService) {
        Map<String, Project> allProjects = projectService.getAllProjects();

        System.out.println("=== Vacant Projects (Open & Visible) ===");
        boolean found = false;

        LocalDate today = LocalDate.now();

        for (Project project : allProjects.values()) {
            // Only include projects that are:
            // 1. Visible
            // 2. Application window is open
            // 3. Officer slots are not fully filled
            boolean isVisible = project.isVisible();
            boolean isOpen = (today.isEqual(project.getOpenDate()) || today.isAfter(project.getOpenDate())) &&
                    (today.isBefore(project.getCloseDate()) || today.isEqual(project.getCloseDate()));
            int assigned = project.getOfficerNRICs().size();
            int allowed = project.getOfficerSlot();

            if (isVisible && isOpen && assigned < allowed) {
                found = true;
                System.out.printf("• %s (%s) — %d Vacant Officer Slot%s\n\n",
                        project.getName(),
                        project.getNeighbourhood(),
                        allowed - assigned,
                        (allowed - assigned == 1 ? "" : "s")
                );
            }
        }

        if (!found) {
            System.out.println("⚠️ No projects currently open and available for officer registration.\n");
        }
    }

    // 1. Officer requests to register for a project
    public boolean registerForProject(Officer officer) {
        // If their registration status is not empty AND their status is NOT REJECTED then they are not allowed to apply...
        // Officers that were rejected, can try again to apply but pending/approved are NOT allowed

        if (officer.getAssignedProjectName() != null && !officer.getAssignedProjectName().isEmpty()) {
            System.out.println("⚠️ You have already applied for project " + officer.getAssignedProjectName());
            return false;
        }

        //Request for project name
        printVacantProjects(projectService);
        String projectName = InputValidator.getNonEmptyString("Enter project name to register: ");

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
    public boolean handleApplication(Officer officer) {
        if (officer.getAssignedProjectName() == null || officer.getAssignedProjectName().isEmpty()) {
            System.out.println("⚠️ You have not applied for any project.");
            return false;
        }

        if (!officer.getRegistrationStatus().equalsIgnoreCase(Officer.RegistrationStatusType.APPROVED.name())) {
            System.out.println("⚠️ Your registration status has not been approved for " + officer.getAssignedProjectName());
            return false;
        }

        Project project = projectService.getProjectByName(officer.getAssignedProjectName());

        if (project == null) {
            System.out.println("❌ Project not found.");
            return false;
        }

        List<Applicant> pendingApplicants = new ArrayList<Applicant>();

        for (int i = 0; i < project.getApplicantNRICs().size(); i++) {
            Applicant applicant = userService.getApplicantByNric(project.getApplicantNRICs().get(i));
            if (applicant.getApplicationStatus().equalsIgnoreCase(Applicant.AppStatusType.PENDING.name())) {
                pendingApplicants.add(applicant);
            }
        }

        if (pendingApplicants.isEmpty()) {
            System.out.println("⚠️ You have no applicants to approve/reject for " + officer.getAssignedProjectName() + ".");
            return false;
        }

        System.out.println("Applicant List:");
        for (int i = 0; i < pendingApplicants.size(); i++) {
            System.out.println((i+1) + ". " + pendingApplicants.get(i).getName() + " (" + pendingApplicants.get(i).getNric() + ")");
        }

        String applicantNRIC = InputValidator.getNonEmptyString("Enter applicant NRIC to approve/reject: ");

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

        boolean approve = InputValidator.getYesNo("Approve this applicant?");

        if (approve) {
            applicant.setApplicationStatus(Applicant.AppStatusType.SUCCESSFUL.name());
            System.out.println("✅ Applicant " + applicantNRIC + " approved.");
        } else {
            project.getApplicantNRICs().remove(applicant.getNric());
            applicant.setApplicationStatus(Applicant.AppStatusType.UNSUCCESSFUL.name());
            System.out.println("❌ Applicant " + applicantNRIC + " rejected.");
        }

        CSVWriter.updateApplicant(applicant, "data/ApplicantList.csv");
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
    public boolean bookFlat(Officer officer) {
        if (officer.getAssignedProjectName() == null || officer.getAssignedProjectName().isEmpty()) {
            System.out.println("⚠️ You have not applied for any project.");
            return false;
        }

        if (!officer.getRegistrationStatus().equalsIgnoreCase(Officer.RegistrationStatusType.APPROVED.name())) {
            System.out.println("⚠️ Your registration status has not been approved for " + officer.getAssignedProjectName());
            return false;
        }

        Project project = projectService.getProjectByName(officer.getAssignedProjectName());

        if (project == null) {
            System.out.println("❌ Project not found.");
            return false;
        }

        List<Applicant> approvedApplicants = new ArrayList<Applicant>();

        for (int i = 0; i < project.getApplicantNRICs().size(); i++) {
            Applicant applicant = userService.getApplicantByNric(project.getApplicantNRICs().get(i));
            if (applicant.getApplicationStatus().equalsIgnoreCase(Applicant.AppStatusType.SUCCESSFUL.name())) {
                approvedApplicants.add(applicant);
            }
        }

        if (approvedApplicants.isEmpty()) {
            System.out.println("⚠️ You have no approved applicants to set as booked for " + officer.getAssignedProjectName() + ".");
            return false;
        }

        System.out.println("Approved Applicant List:");
        for (int i = 0; i < approvedApplicants.size(); i++) {
            System.out.println((i+1) + ". " + approvedApplicants.get(i).getName() + " (" + approvedApplicants.get(i).getNric() + ")");
        }

        System.out.println("Applicant List:");
        for (int i = 0; i < project.getApplicantNRICs().size(); i++) {
            Applicant projectApplicant = userService.getApplicantByNric(project.getApplicantNRICs().get(i));

            if (projectApplicant.getApplicationStatus().equalsIgnoreCase(Applicant.AppStatusType.PENDING.name())) {
                System.out.println((i+1) + ". " + projectApplicant.getName() + " (" + projectApplicant.getNric() + ")");
            }
        }

        String applicantNRIC = InputValidator.getNonEmptyString("Enter applicant NRIC to approve/reject: ");

        Applicant applicant = userService.getApplicantByNric(applicantNRIC);
        if (applicant == null) {
            System.out.println("❌ Applicant not found.");
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

        CSVWriter.updateApplicant(applicant, "data/ApplicantList.csv");

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