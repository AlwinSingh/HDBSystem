package src.service;

import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

import src.model.HDBManager;
import src.model.HDBOfficer;
import src.model.Project;
import src.repository.OfficerRepository;
import src.util.CsvUtil;
import src.util.FilePath;
import src.util.OfficerCsvMapper;
import src.util.ProjectCsvMapper;

/**
 * Handles the approval workflow of HDB officers by project managers.
 * This includes viewing, approving, and rejecting officer registration requests.
 */
public class ManagerOfficerApprovalService {
    private static final OfficerRepository officerRepository = new OfficerCsvMapper();
    /**
     * Displays all officer registration requests associated with the manager's projects.
     *
     * @param manager The currently logged-in HDB manager.
     */
    public static void viewOfficerRegistrations(HDBManager manager) {
        List<Map<String, String>> officers = CsvUtil.read(FilePath.OFFICER_LIST_FILE);
        List<Map<String, String>> projects = CsvUtil.read(FilePath.PROJECT_LIST_FILE);

        Set<String> managerProjectNames = projects.stream()
            .filter(p -> manager.getNric().equalsIgnoreCase(p.getOrDefault("ManagerNRIC", "").trim()))
            .map(p -> p.getOrDefault("Project Name", "").trim())
            .collect(Collectors.toSet());

        List<Map<String, String>> relevantOfficers = officers.stream()
            .filter(o -> !o.getOrDefault("AssignedProject", "").isBlank())
            .filter(o -> managerProjectNames.contains(o.get("AssignedProject")))
            .filter(o -> !o.getOrDefault("RegistrationStatus", "").isBlank())
            .toList();

        if (relevantOfficers.isEmpty()) {
            System.out.println("❌ No officer registrations found for your projects.");
            return;
        }

        System.out.println("\n🧾 Officer Registrations for Your Projects:");
        for (Map<String, String> row : relevantOfficers) {
            System.out.printf("👤 %s (NRIC: %s)\n", row.get("Name"), row.get("NRIC"));
            System.out.println("   📌 Project: " + row.get("AssignedProject"));
            System.out.println("   📊 Status: " + row.get("RegistrationStatus"));
            System.out.println();
        }
    }

    /**
     * Allows the manager to approve or reject officer applications for their projects.
     *
     * @param manager The logged-in manager.
     * @param sc      Scanner for user input.
     */
    public static void handleOfficerApproval(HDBManager manager, Scanner sc) {
        List<Project> allProjects = ProjectCsvMapper.loadAll();
        List<HDBOfficer> allOfficers = officerRepository.loadAll(allProjects);

        List<HDBOfficer> pendingOfficers = allOfficers.stream()
            .filter(o -> HDBOfficer.RegistrationStatusType.PENDING.name().equalsIgnoreCase(o.getRegistrationStatus()))
            .toList();

        if (pendingOfficers.isEmpty()) {
            System.out.println("📭 No pending officer registrations.");
            return;
        }

        System.out.println("\n📋 Pending Officer Registrations:");
        for (int i = 0; i < pendingOfficers.size(); i++) {
            HDBOfficer o = pendingOfficers.get(i);
            System.out.printf("[%d] %s (%s) – Project: %s\n", i + 1,
                o.getName(), o.getNric(),
                o.getAssignedProject() != null ? o.getAssignedProject().getProjectName() : "N/A");
        }

        System.out.print("Select officer to process (or 0 to cancel): ");
        int index;
        try {
            index = Integer.parseInt(sc.nextLine()) - 1;
            if (index == -1) return;
            if (index < 0 || index >= pendingOfficers.size()) throw new Exception();
        } catch (Exception e) {
            System.out.println("❌ Invalid selection.");
            return;
        }

        HDBOfficer officer = pendingOfficers.get(index);
        Project assignedProject = officer.getAssignedProject();

        if (assignedProject == null || assignedProject.getManager() == null ||
            !assignedProject.getManager().getNric().equalsIgnoreCase(manager.getNric())) {
            System.out.println("❌ You are not the assigned manager for this project.");
            return;
        }

        System.out.print("Approve or Reject? (A/R): ");
        String decision = sc.nextLine().trim().toUpperCase();

        switch (decision) {
            case "A" -> {
                int slots = assignedProject.getOfficerSlots();
                if (slots <= 0) {
                    System.out.println("❌ No officer slots remaining.");
                    return;
                }

                assignedProject.setOfficerSlots(slots - 1);
                assignedProject.addOfficerNric(officer.getNric());
                officer.setRegistrationStatus(HDBOfficer.RegistrationStatusType.APPROVED.name());

                officerRepository.updateOfficer(officer);
                ProjectCsvMapper.updateProject(assignedProject);

                System.out.println("✅ Officer approved and added to project.");
            }
            case "R" -> {
                System.out.print("Confirm rejection for Officer " + officer.getName() + " (Y/N): ");
                if (!sc.nextLine().trim().equalsIgnoreCase("Y")) {
                    System.out.println("❌ Rejection cancelled.");
                    return;
                }

                officer.setRegistrationStatus(HDBOfficer.RegistrationStatusType.REJECTED.name());
                officer.setAssignedProject(null);
                officerRepository.updateOfficer(officer);

                System.out.println("❌ Officer registration rejected.");
            }
            default -> System.out.println("❌ Invalid input. Use A or R.");
        }
    }

}
