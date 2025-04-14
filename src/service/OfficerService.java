package src.service;

import src.model.Officer;
import src.model.Project;
import src.model.Applicant;
import src.util.CSVWriter;
import src.util.InputValidator;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OfficerService {

    private final ProjectService projectService;
    private final UserService userService;

    public OfficerService(ProjectService projectService, UserService userService) {
        this.projectService = projectService;
        this.userService = userService;
    }
    
    private void printVacantProjects() {
        Map<String, Project> allProjects = projectService.getAllProjects();
        System.out.println("=== Vacant Projects (Open & Visible) ===");
        boolean found = false;
        LocalDate today = LocalDate.now();
        for (Project project : allProjects.values()) {
            boolean isVisible = project.isVisible();
            boolean isOpen = (today.isEqual(project.getOpenDate()) || today.isAfter(project.getOpenDate())) &&
                    (today.isBefore(project.getCloseDate()) || today.isEqual(project.getCloseDate()));
            int assigned = project.getOfficerNRICs().size();
            int allowed = project.getOfficerSlot();
            if (isVisible && isOpen && assigned < allowed) {
                found = true;
                System.out.printf("• %s (%s) — %d Vacant Officer Slot%s%n%n",
                        project.getName(),
                        project.getNeighbourhood(),
                        allowed - assigned,
                        (allowed - assigned == 1 ? "" : "s"));
            }
        }
        if (!found) {
            System.out.println("⚠️ No projects currently open and available for officer registration.");
        }
    }

    // ---------- Officer Registration ---------- //

    public boolean registerForProject(Officer officer) {
        if (officer.getAssignedProjectName() != null && !officer.getAssignedProjectName().isEmpty()) {
            System.out.println("⚠️ You have already applied for project " + officer.getAssignedProjectName());
            return false;
        }
        printVacantProjects();
        String projectName = InputValidator.getNonEmptyString("Enter project name to register: ");
        Project project = projectService.getProjectByName(projectName);
        if (project == null) {
            System.out.println("❌ Project not found.");
            return false;
        }
        int maxSlots = project.getOfficerSlot();
        if (project.getOfficerNRICs().size() >= maxSlots) {
            System.out.println("⚠️ This project has reached its maximum officer capacity.");
            return false;
        }
        officer.setAssignedProjectName(projectName);
        officer.setRegistrationStatus("PENDING");
        CSVWriter.updateOfficer(officer, "data/OfficerList.csv");
        System.out.println("✅ Registration submitted for project: " + projectName);
        return true;
    }
    
    // ---------- Assigned Project Details ---------- //

    public void viewAssignedProject(Officer officer) {
        String assignedProject = officer.getAssignedProjectName();
        String status = officer.getRegistrationStatus();
        if (assignedProject == null || assignedProject.isBlank()) {
            System.out.println("⚠️ You have not registered for any project.");
            return;
        }
        if (!status.equalsIgnoreCase("APPROVED")) {
            System.out.println("⚠️ Your registration is still " + status + " for " + assignedProject +
                               ". You can only view the project after approval.");
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
    
    // ---------- View Applicant List ---------- //

    public void viewApplicantList(Officer officer) {
        String projectName = officer.getAssignedProjectName();
        if (projectName == null || projectName.isBlank()) {
            System.out.println("⚠️ You are not registered for any project.");
            return;
        }
        Project project = projectService.getProjectByName(projectName);
        if (project == null) {
            System.out.println("❌ Project not found.");
            return;
        }
        List<String> applicantNRICs = project.getApplicantNRICs();
        if (applicantNRICs.isEmpty()) {
            System.out.println("📭 No applicants have applied to this project.");
            return;
        }
        System.out.println("=== Applicant List for " + projectName + " ===");
        for (String nric : applicantNRICs) {
            Applicant applicant = userService.getApplicantByNric(nric);
            if (applicant != null) {
                System.out.println("- " + applicant.getName() + " (" + applicant.getNric() + ")"
                        + " - Status: " + applicant.getApplicationStatus());
            }
        }
    }
    
    // ---------- Booking a Flat ---------- //

    public boolean bookFlat(Officer officer) {
        String projectName = officer.getAssignedProjectName();
        if (projectName == null || projectName.isBlank()) {
            System.out.println("⚠️ You are not registered for any project.");
            return false;
        }
        if (!officer.getRegistrationStatus().equalsIgnoreCase("APPROVED")) {
            System.out.println("⚠️ Your registration status has not been approved for project " + projectName);
            return false;
        }
        Project project = projectService.getProjectByName(projectName);
        if (project == null) {
            System.out.println("❌ Project not found.");
            return false;
        }
        // Gather applicants with status SUCCESSFUL (approved by Manager)
        List<Applicant> approvedApplicants = new ArrayList<>();
        for (String nric : project.getApplicantNRICs()) {
            Applicant applicant = userService.getApplicantByNric(nric);
            if (applicant != null && "SUCCESSFUL".equalsIgnoreCase(applicant.getApplicationStatus())) {
                approvedApplicants.add(applicant);
            }
        }
        if (approvedApplicants.isEmpty()) {
            System.out.println("⚠️ No approved applicants available for booking.");
            return false;
        }
        System.out.println("=== Approved Applicants ===");
        for (int i = 0; i < approvedApplicants.size(); i++) {
            System.out.printf("%d. %s (%s)%n", i + 1, approvedApplicants.get(i).getName(), approvedApplicants.get(i).getNric());
        }
        String applicantNRIC = InputValidator.getNonEmptyString("Enter applicant NRIC to book flat for: ");
        Applicant selected = userService.getApplicantByNric(applicantNRIC);
        if (selected == null || !"SUCCESSFUL".equalsIgnoreCase(selected.getApplicationStatus())) {
            System.out.println("❌ Selected applicant is not valid for booking.");
            return false;
        }
        String flatType = selected.getFlatTypeApplied();
        if ("2-Room".equalsIgnoreCase(flatType)) {
            if (project.getTwoRoomUnits() == 0) {
                System.out.println("❌ No 2-Room flats available.");
                return false;
            }
            project.setTwoRoomUnits(project.getTwoRoomUnits() - 1);
        } else if ("3-Room".equalsIgnoreCase(flatType)) {
            if (project.getThreeRoomUnits() == 0) {
                System.out.println("❌ No 3-Room flats available.");
                return false;
            }
            project.setThreeRoomUnits(project.getThreeRoomUnits() - 1);
        } else {
            System.out.println("❌ Invalid flat type: " + flatType);
            return false;
        }
        selected.setApplicationStatus("BOOKED");
        System.out.println("🏠 Flat booked successfully for applicant " + selected.getNric());
        CSVWriter.updateApplicant(selected, "data/ApplicantList.csv");
        CSVWriter.saveProject(project, "data/ProjectList.csv");
        return true;
    }
    
    // ---------- Generating a Receipt ---------- //

    public void generateReceipt(String applicantNRIC) {
        Applicant applicant = userService.getApplicantByNric(applicantNRIC);
        if (applicant == null) {
            System.out.println("❌ Applicant not found.");
            return;
        }
        if (!"BOOKED".equalsIgnoreCase(applicant.getApplicationStatus())) {
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
        double price = "2-Room".equalsIgnoreCase(flatType)
                ? project.getTwoRoomPrice()
                : project.getThreeRoomPrice();
        System.out.println("======= Booking Receipt =======");
        System.out.println("Applicant: " + applicant.getName() + " (" + applicant.getNric() + ")");
        System.out.println("Project: " + project.getName());
        System.out.println("Flat Type: " + flatType);
        System.out.println("Price: $" + price);
        System.out.println("Booking Status: " + applicant.getApplicationStatus());
        System.out.println("==================================");
    }
}
