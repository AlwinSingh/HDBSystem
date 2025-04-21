package src.service;

import java.util.List;
import java.util.Scanner;
import src.interfaces.IOfficerRegistrationService;
import src.model.Application;
import src.model.HDBOfficer;
import src.model.Project;
import src.repository.OfficerRepository;
import src.util.OfficerCsvMapper;
import src.util.ProjectCsvMapper;


/**
 * Provides functionality for HDB Officers to handle registration-related tasks.
 * This includes checking registration status, browsing available projects, and registering for one.
 */
public class OfficerRegistrationService implements IOfficerRegistrationService {
    private static final OfficerRepository officerRepository = new OfficerCsvMapper();
    /**
     * Displays the officer's current registration status and their assigned project's details.
     *
     * @param officer The logged-in HDB officer.
     */
    public void viewOfficerRegistrationStatus(HDBOfficer officer) {
        System.out.println("🔍 Officer Registration Overview");
        String status = officer.getRegistrationStatus();
        System.out.println("   📄 Registration Status : " + (status != null ? status : "N/A"));

        Project p = officer.getAssignedProject();
        if (p == null) {
            System.out.println("   🛑 No assigned project.");
            return;
        }

        System.out.printf("""
            \s  🏢 Project Name        : %s
            \s  📍 Neighborhood        : %s
            \s  🗓️  Application Period : %s to %s
            \s  🧍 Officer Slots       : %d
            \s  🏠 2-Room Units Left   : %d  ($%.2f)
            \s  🏠 3-Room Units Left   : %d  ($%.2f)
            \s  👀 Public Visibility   : %s
            """,
            p.getProjectName(),
            p.getNeighborhood(),
            p.getOpenDate(), p.getCloseDate(),
            p.getOfficerSlots(),
            p.getRemainingFlats("2-Room"), p.getPrice2Room(),
            p.getRemainingFlats("3-Room"), p.getPrice3Room(),
            p.isVisible() ? "Yes ✅" : "No ❌"
        );
    }


    /**
     * Registers an officer to a selected project, checking for duplication with existing application or assignment.
     *
     * @param officer         The officer attempting to register.
     * @param selectedProject The project to register for.
     * @return True if registration succeeds, false otherwise.
     */
    public boolean registerForProject(HDBOfficer officer, Project selectedProject) {
        // Officer is already assigned to a project
        if (officer.getAssignedProject() != null) {
            System.out.println("❌ You are already registered to handle a project.");
            return false;
        }
    
        Application app = officer.getApplication();
        if (app != null) {
            String appliedProjectName = app.getProject().getProjectName();
            if (appliedProjectName.equalsIgnoreCase(selectedProject.getProjectName())) {
                System.out.println("❌ You have already applied to this project as an applicant.");
                return false;
            }
        }
    
        boolean registered = officer.registerToHandleProject(selectedProject);
        if (registered) {
            officerRepository.updateOfficer(officer);
            System.out.println("✅ Registration submitted for project: " + selectedProject.getProjectName());
        }
    
        return registered;
    }
    
    /**
     * Returns a list of projects that are publicly visible and not yet registered to the officer.
     *
     * @param officer The officer browsing projects.
     * @return A list of available projects.
     */
    public List<Project> getAvailableProjectsForOfficer(HDBOfficer officer) {
        return ProjectCsvMapper.loadAll().stream()
            .filter(Project::isVisible)
            .filter(p -> !p.getOfficerNRICs().contains(officer.getNric()))
            .toList();
    }

    /**
     * Displays open projects and allows filtering by neighborhood, district, or flat type.
     *
     * @param sc Scanner to receive user input.
     */
    public void browseAndFilterProjects(Scanner sc) {
        System.out.println("\n🔍 Browse & Filter Available Projects (for reference only)");
    
        List<Project> all = ProjectCsvMapper.loadAll();
        if (all.isEmpty()) {
            System.out.println("📭 No projects found.");
            return;
        }
    
        class Filter { // inner mutable holder
            String name = null;
            String neighborhood = null;
            Integer minSlots = null;
            Boolean visible = null;
        }
    
        Filter filter = new Filter();
    
        while (true) {
            List<Project> filtered = all.stream()
                .filter(p -> filter.name == null || p.getProjectName().toLowerCase().contains(filter.name.toLowerCase()))
                .filter(p -> filter.neighborhood == null || p.getNeighborhood().toLowerCase().contains(filter.neighborhood.toLowerCase()))
                .filter(p -> filter.minSlots == null || p.getOfficerSlots() >= filter.minSlots)
                .filter(p -> filter.visible == null || p.isVisible() == filter.visible)
                .toList();
    
            System.out.println("\n📋 Filtered Projects (" + filtered.size() + "):");
            if (filtered.isEmpty()) {
                System.out.println("📭 No matching projects.");
            } else {
                for (int i = 0; i < filtered.size(); i++) {
                    Project p = filtered.get(i);
                    System.out.printf("[%d] 📌 %s (%s) | Slots: %d | Visible: %s%n",
                        i + 1, p.getProjectName(), p.getNeighborhood(), p.getOfficerSlots(),
                        p.isVisible() ? "Yes" : "No");
                }
            }
    
            System.out.println("\n🔧 Filter Options:");
            System.out.println(" [1] Filter by Project Name");
            System.out.println(" [2] Filter by Neighborhood");
            System.out.println(" [3] Filter by Min Officer Slots");
            System.out.println(" [4] Filter by Visibility (true/false)");
            System.out.println(" [5] Clear All Filters");
            System.out.println(" [0] Back");
            System.out.print("➡️ Enter your choice: ");
            String choice = sc.nextLine().trim();
    
            switch (choice) {
                case "1" -> {
                    System.out.print("🔤 Enter partial project name: ");
                    String input = sc.nextLine().trim();
                    filter.name = input.isEmpty() ? null : input;
                }
                case "2" -> {
                    System.out.print("📍 Enter neighborhood: ");
                    String input = sc.nextLine().trim();
                    filter.neighborhood = input.isEmpty() ? null : input;
                }
                case "3" -> {
                    System.out.print("👥 Enter minimum officer slots: ");
                    try {
                        filter.minSlots = Integer.parseInt(sc.nextLine().trim());
                    } catch (Exception e) {
                        filter.minSlots = null;
                    }
                }
                case "4" -> {
                    System.out.print("👀 Show visible only? (true/false): ");
                    String vis = sc.nextLine().trim().toLowerCase();
                    filter.visible = vis.equals("true") ? true : vis.equals("false") ? false : null;
                }
                case "5" -> {
                    filter.name = filter.neighborhood = null;
                    filter.minSlots = null;
                    filter.visible = null;
                    System.out.println("✅ Filters cleared.");
                }
                case "0" -> {
                    System.out.println("🔙 Returning...");
                    return;
                }
                default -> System.out.println("❌ Invalid input. Try again.");
            }
        }
    }

    /**
     * Allows an officer to register interest in a project.
     * Registration status will be marked as PENDING and saved to CSV.
     *
     * @param officer The officer attempting to register.
     * @param sc      Scanner for input.
     */
    public void registerForProject(HDBOfficer officer, Scanner sc) {
        if (officer.getAssignedProject() != null) {
            System.out.println("✅ You are already registered to project: " +
                officer.getAssignedProject().getProjectName());
            return;
        }
    
        List<Project> available = getAvailableProjectsForOfficer(officer);
    
        if (available.isEmpty()) {
            System.out.println("❌ No visible projects available.");
            return;
        }
    
        System.out.println("\n📋 Available Projects:");
        for (int i = 0; i < available.size(); i++) {
            System.out.printf("[%d] %s (%s)\n", i + 1, available.get(i).getProjectName(), available.get(i).getNeighborhood());
        }
    
        System.out.print("Choose project number to register: ");
        try {
            int idx = Integer.parseInt(sc.nextLine().trim()) - 1;
            if (idx < 0 || idx >= available.size()) throw new IndexOutOfBoundsException();
    
            Project selected = available.get(idx);
            boolean registered = registerForProject(officer, selected);
            if (registered) {
                System.out.println("✅ Registration submitted.");
            } else {
                System.out.println("❌ Could not register. Check your current assignment or application status.");
            }
    
        } catch (Exception e) {
            System.out.println("❌ Invalid selection.");
        }
    }

}
