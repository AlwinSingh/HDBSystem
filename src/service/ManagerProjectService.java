package src.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

import src.model.HDBManager;
import src.model.Project;
import src.model.ProjectLocation;
import src.util.ProjectCsvMapper;

/**
 * Provides functionality for HDB managers to manage their housing projects.
 * Managers can create, edit, delete, toggle visibility, and view projects.
 * Also includes advanced filtering options.
 */

public class ManagerProjectService {
    
    /**
     * Guides the manager through creating a new housing project.
     * Includes validation for name uniqueness, unit counts, pricing,
     * officer slot range, and application period constraints.
     *
     * @param manager The currently logged-in HDB manager.
     * @param sc      Scanner to receive user input.
     */
    public static void createProject(HDBManager manager, Scanner sc) {
        System.out.println("\n📌 Create New Project");

        List<Project> allProjects = ProjectCsvMapper.loadAll();

        System.out.print("Enter project name: ");
        String name = sc.nextLine().trim();
        if (name.isBlank()) {
            System.out.println("❌ Project name cannot be empty.");
            return;
        }

        boolean exists = allProjects.stream().anyMatch(p -> p.getProjectName().equalsIgnoreCase(name));
        if (exists) {
            System.out.println("❌ A project with this name already exists.");
            return;
        }

        System.out.print("Enter neighborhood: ");
        String neighborhood = sc.nextLine().trim();
        if (neighborhood.isBlank()) {
            System.out.println("❌ Neighborhood cannot be empty.");
            return;
        }

        int units2, units3, officerSlots;
        double price2, price3;
        try {
            System.out.print("Number of 2-Room units: ");
            units2 = Integer.parseInt(sc.nextLine());

            System.out.print("Number of 3-Room units: ");
            units3 = Integer.parseInt(sc.nextLine());

            System.out.print("Selling price for 2-Room: ");
            price2 = Double.parseDouble(sc.nextLine());

            System.out.print("Selling price for 3-Room: ");
            price3 = Double.parseDouble(sc.nextLine());

            System.out.print("Number of officer slots (1–10): ");
            officerSlots = Integer.parseInt(sc.nextLine());

            if (units2 <= 0 || units3 <= 0 || officerSlots < 1 || officerSlots > 10) {
                System.out.println("❌ Please ensure units are positive and officer slots are between 1 and 10.");
                return;
            }
        } catch (Exception e) {
            System.out.println("❌ Invalid input. Please enter numeric values.");
            return;
        }

        System.out.print("Opening date (M/d/yyyy): ");
        String openDateStr = sc.nextLine();
        System.out.print("Closing date (M/d/yyyy): ");
        String closeDateStr = sc.nextLine();

        LocalDate openDate, closeDate;
        try {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("M/d/yyyy");
            openDate = LocalDate.parse(openDateStr, fmt);
            closeDate = LocalDate.parse(closeDateStr, fmt);
            if (!openDate.isBefore(closeDate)) {
                System.out.println("❌ Opening date must be before closing date.");
                return;
            }
        } catch (Exception e) {
            System.out.println("❌ Invalid date format. Use M/d/yyyy.");
            return;
        }

        boolean overlaps = allProjects.stream()
            .filter(p -> p.getManager() != null)
            .filter(p -> p.getManager().getNric().equalsIgnoreCase(manager.getNric()))
            .anyMatch(p -> !(closeDate.isBefore(p.getOpenDate()) || openDate.isAfter(p.getCloseDate())));

        if (overlaps) {
            System.out.println("❌ You already manage a project during this application period.");
            return;
        }

        ProjectLocation location = new ProjectLocation(0, "", neighborhood, "", 0, 0);

        Project newProject = new Project(name, neighborhood, openDate, closeDate, officerSlots, units2, units3, location);
        newProject.setPrice2Room(price2);
        newProject.setPrice3Room(price3);
        newProject.openProject();
        newProject.setManager(manager);
        ProjectCsvMapper.save(newProject);

        System.out.println("✅ Project created and saved successfully!");
    }

    /**
     * Allows the manager to edit a project they created — including dates, pricing, and slots.
     *
     * @param manager The logged-in manager.
     * @param sc      Scanner for user input.
     */
    public static void editProject(HDBManager manager, Scanner sc) {
        List<Project> myProjects = ProjectCsvMapper.loadAll().stream()
            .filter(p -> p.getManager() != null && p.getManager().getNric().equalsIgnoreCase(manager.getNric()))
            .toList();
    
        if (myProjects.isEmpty()) {
            System.out.println("❌ You have no projects to edit.");
            return;
        }
    
        System.out.println("\n🛠 Your Projects:");
        for (int i = 0; i < myProjects.size(); i++) {
            Project p = myProjects.get(i);
            System.out.printf("[%d] %s (%s)\n", i + 1, p.getProjectName(), p.getNeighborhood());
        }
    
        System.out.print("Select a project to edit: ");
        int index;
        try {
            index = Integer.parseInt(sc.nextLine()) - 1;
            if (index < 0 || index >= myProjects.size()) throw new IndexOutOfBoundsException();
        } catch (Exception e) {
            System.out.println("❌ Invalid selection.");
            return;
        }
    
        Project p = myProjects.get(index);
        System.out.println("Leave field blank to keep current value.");
    
        System.out.printf("New neighborhood [%s]: ", p.getNeighborhood());
        String input = sc.nextLine().trim();
        if (!input.isEmpty()) p.setNeighborhood(input);
    
        try {
            System.out.printf("New 2-Room units [%d]: ", p.getRemainingFlats("2-Room"));
            input = sc.nextLine().trim();
            if (!input.isEmpty()) p.setAvailableFlats2Room(Integer.parseInt(input));
    
            System.out.printf("New 3-Room units [%d]: ", p.getRemainingFlats("3-Room"));
            input = sc.nextLine().trim();
            if (!input.isEmpty()) p.setAvailableFlats3Room(Integer.parseInt(input));
    
            System.out.printf("New price for 2-Room [$%.2f]: ", p.getPrice2Room());
            input = sc.nextLine().trim();
            if (!input.isEmpty()) p.setPrice2Room(Double.parseDouble(input));
    
            System.out.printf("New price for 3-Room [$%.2f]: ", p.getPrice3Room());
            input = sc.nextLine().trim();
            if (!input.isEmpty()) p.setPrice3Room(Double.parseDouble(input));
    
            System.out.printf("New officer slots [%d]: ", p.getOfficerSlots());
            input = sc.nextLine().trim();
            if (!input.isEmpty()) {
                int slots = Integer.parseInt(input);
                if (slots >= 1 && slots <= 10) {
                    p.setOfficerSlots(slots);
                } else {
                    System.out.println("⚠️ Officer slots must be between 1 and 10.");
                }
            }
    
            System.out.printf("New opening date [%s] (M/d/yyyy): ", p.getOpenDate());
            String openDateStr = sc.nextLine().trim();
            System.out.printf("New closing date [%s] (M/d/yyyy): ", p.getCloseDate());
            String closeDateStr = sc.nextLine().trim();
    
            if (!openDateStr.isEmpty() && !closeDateStr.isEmpty()) {
                DateTimeFormatter fmt = DateTimeFormatter.ofPattern("M/d/yyyy");
                LocalDate open = LocalDate.parse(openDateStr, fmt);
                LocalDate close = LocalDate.parse(closeDateStr, fmt);
                if (open.isBefore(close)) {
                    p.setOpenDate(open);
                    p.setCloseDate(close);
                } else {
                    System.out.println("⚠️ Opening date must be before closing date.");
                }
            }
    
            if (p.getManager() == null) {
                p.setManager(manager);
            }
    
            ProjectCsvMapper.updateProject(p);
            System.out.println("✅ Project updated successfully.");
    
        } catch (Exception e) {
            System.out.println("❌ Invalid input. Edit aborted.");
        }
    }

    /**
     * Deletes a project if no officers or applicants are currently associated with it.
     * Requires confirmation before removal.
     *
     * @param manager The currently logged-in HDB manager.
     * @param sc      Scanner to receive user input.
     */

    public static void deleteProject(HDBManager manager, Scanner sc) {
        List<Project> myProjects = ProjectCsvMapper.loadAll().stream()
            .filter(p -> p.getManager() != null && p.getManager().getNric().equalsIgnoreCase(manager.getNric()))
            .toList();
    
        if (myProjects.isEmpty()) {
            System.out.println("❌ You have no projects to delete.");
            return;
        }
    
        System.out.println("\n🗑 Your Projects:");
        for (int i = 0; i < myProjects.size(); i++) {
            Project p = myProjects.get(i);
            System.out.printf("[%d] %s (%s)\n", i + 1, p.getProjectName(), p.getNeighborhood());
        }
    
        System.out.print("Select a project to delete: ");
        int index;
        try {
            index = Integer.parseInt(sc.nextLine()) - 1;
            if (index < 0 || index >= myProjects.size()) throw new IndexOutOfBoundsException();
        } catch (Exception e) {
            System.out.println("❌ Invalid selection.");
            return;
        }
    
        Project selected = myProjects.get(index);
    
        if (!selected.getOfficerNRICs().isEmpty() || !selected.getApplicantNRICs().isEmpty()) {
            System.out.println("⚠️ Project cannot be deleted because it has assigned officers or applicants.");
            System.out.println("🛑 Please remove those associations before deletion.");
            return;
        }
    
        System.out.print("Are you sure you want to delete this project? (Y/N): ");
        if (!sc.nextLine().trim().equalsIgnoreCase("Y")) {
            System.out.println("🔙 Deletion cancelled.");
            return;
        }
    
        ProjectCsvMapper.deleteProjectByName(selected.getProjectName());
        System.out.println("✅ Project deleted successfully.");
    }

    /**
     * Toggles the public visibility of a project managed by the current manager.
     *
     * @param manager The manager who owns the project.
     * @param sc      Scanner for input.
     */
    public static void toggleVisibility(HDBManager manager, Scanner sc) {
        List<Project> myProjects = ProjectCsvMapper.loadAll().stream()
            .filter(p -> p.getManager() != null && p.getManager().getNric().equalsIgnoreCase(manager.getNric()))
            .toList();
    
        if (myProjects.isEmpty()) {
            System.out.println("❌ You have no projects to toggle visibility.");
            return;
        }
    
        System.out.println("\n🔁 Your Projects:");
        for (int i = 0; i < myProjects.size(); i++) {
            Project p = myProjects.get(i);
            System.out.printf("[%d] %s - Visibility: %s\n", i + 1, p.getProjectName(), p.isVisible());
        }
    
        System.out.print("Select a project to toggle visibility: ");
        int index;
        try {
            index = Integer.parseInt(sc.nextLine()) - 1;
            if (index < 0 || index >= myProjects.size()) throw new IndexOutOfBoundsException();
        } catch (Exception e) {
            System.out.println("❌ Invalid selection.");
            return;
        }
    
        Project selected = myProjects.get(index);
        boolean current = selected.isVisible();
        boolean toggleTo = !current;
    
        System.out.printf("Are you sure you want to change visibility from %s to %s? (Y/N): ",
                current, toggleTo);
        if (!sc.nextLine().trim().equalsIgnoreCase("Y")) {
            System.out.println("🔙 Toggle cancelled.");
            return;
        }
    
        if (toggleTo) selected.openProject();
        else selected.closeProject();
    
        ProjectCsvMapper.updateProject(selected);
        System.out.printf("✅ Visibility updated. New visibility: %s\n", selected.isVisible());
    }

    /**
     * Displays basic details of a project for use in other views.
     *
     * @param p     The project.
     * @param index The display index (1-based).
     */
    public static void displayProjectDetails(Project p, int index) {
        System.out.printf("\n[%d] 📌 %s (%s)\n", index, p.getProjectName(), p.getNeighborhood());
        System.out.printf("   🛏️ 2-Room Units: %d\n", p.getRemainingFlats("2-Room"));
        System.out.printf("   🛏️ 3-Room Units: %d\n", p.getRemainingFlats("3-Room"));
        System.out.printf("   💰 Price 2-Room: $%.2f\n", p.getPrice2Room());
        System.out.printf("   💰 Price 3-Room: $%.2f\n", p.getPrice3Room());
        System.out.printf("   📅 Application Period: %s to %s\n", p.getOpenDate(), p.getCloseDate());
        System.out.printf("   👨‍💼 Manager: %s\n", p.getManager() != null ? p.getManager().getName() : "N/A");
        System.out.printf("   🧍 Officer Slots: %d\n", p.getOfficerSlots());
        System.out.printf("   👀 Visible to public: %s\n", p.isVisible() ? "Yes" : "No");
    }

    private static class ProjectFilterCriteria {
        String name = null;
        String neighborhood = null;
        Boolean visible = null;
        Integer min2Room = null;
        Integer min3Room = null;
        Integer minSlots = null;
        LocalDate start = null;
        LocalDate end = null;
    }
    
    /**
     * Displays all projects in the system and allows the manager
     * to apply dynamic filters including project name, district, visibility,
     * room availability, officer slots, and application period.
     *
     * @param sc Scanner to capture filter inputs.
     */

    public static void viewAllProjectsWithFilter(Scanner sc) {
        List<Project> allProjects = ProjectCsvMapper.loadAll();
        if (allProjects.isEmpty()) {
            System.out.println("❌ No projects found.");
            return;
        }
    
        ProjectFilterCriteria filter = new ProjectFilterCriteria();
    
        while (true) {
            List<Project> filtered = allProjects.stream()
                .filter(p -> filter.name == null || p.getProjectName().equalsIgnoreCase(filter.name))
                .filter(p -> filter.neighborhood == null || p.getNeighborhood().equalsIgnoreCase(filter.neighborhood))
                .filter(p -> filter.visible == null || p.isVisible() == filter.visible)
                .filter(p -> filter.min2Room == null || p.getRemainingFlats("2-Room") >= filter.min2Room)
                .filter(p -> filter.min3Room == null || p.getRemainingFlats("3-Room") >= filter.min3Room)
                .filter(p -> filter.minSlots == null || p.getOfficerSlots() >= filter.minSlots)
                .filter(p -> filter.start == null || !p.getOpenDate().isBefore(filter.start))
                .filter(p -> filter.end == null || !p.getCloseDate().isAfter(filter.end))
                .toList();
    
            System.out.println("\n📋 Filtered Projects (" + filtered.size() + "):");
            if (filtered.isEmpty()) System.out.println("📭 No projects match current filters.");
            else for (int i = 0; i < filtered.size(); i++) displayProjectDetails(filtered.get(i), i + 1);
    
            System.out.println("\n🔎 Filter Options");
            System.out.println(" [1] Project Name");
            System.out.println(" [2] Neighborhood");
            System.out.println(" [3] Min 2-Room Units");
            System.out.println(" [4] Min 3-Room Units");
            System.out.println(" [5] Min Officer Slots");
            System.out.println(" [6] Visibility (true/false)");
            System.out.println(" [7] Application Date Range");
            System.out.println(" [8] Clear All Filters");
            System.out.println(" [0] Back");
            System.out.print("➡️ Enter choice: ");
            String choice = sc.nextLine().trim();
    
            switch (choice) {
                case "1" -> {
                    System.out.print("Enter exact project name: ");
                    filter.name = sc.nextLine().trim();
                }
                case "2" -> {
                    System.out.print("Enter neighborhood: ");
                    filter.neighborhood = sc.nextLine().trim();
                }
                case "3" -> {
                    System.out.print("Enter minimum 2-room units: ");
                    try { filter.min2Room = Integer.parseInt(sc.nextLine().trim()); } catch (Exception e) { filter.min2Room = null; }
                }
                case "4" -> {
                    System.out.print("Enter minimum 3-room units: ");
                    try { filter.min3Room = Integer.parseInt(sc.nextLine().trim()); } catch (Exception e) { filter.min3Room = null; }
                }
                case "5" -> {
                    System.out.print("Enter minimum officer slots: ");
                    try { filter.minSlots = Integer.parseInt(sc.nextLine().trim()); } catch (Exception e) { filter.minSlots = null; }
                }
                case "6" -> {
                    System.out.print("Visible only? (true/false): ");
                    String vis = sc.nextLine().trim().toLowerCase();
                    filter.visible = vis.equals("true") ? true : vis.equals("false") ? false : null;
                }
                case "7" -> {
                    try {
                        System.out.print("Enter start date (yyyy-MM-dd): ");
                        filter.start = LocalDate.parse(sc.nextLine().trim());
                        System.out.print("Enter end date (yyyy-MM-dd): ");
                        filter.end = LocalDate.parse(sc.nextLine().trim());
                    } catch (Exception e) {
                        System.out.println("❌ Invalid date format. Filters cleared.");
                        filter.start = filter.end = null;
                    }
                }
                case "8" -> {
                    filter.name = filter.neighborhood = null;
                    filter.visible = null;
                    filter.min2Room = filter.min3Room = filter.minSlots = null;
                    filter.start = filter.end = null;
                    System.out.println("✅ Filters cleared.");
                }
                case "0" -> {
                    System.out.println("🔙 Returning...");
                    return;
                }
                default -> System.out.println("❌ Invalid input.");
            }
        }
    }

    /**
     * Returns a list of projects managed by the specified manager.
     *
     * @param manager The logged-in manager.
     * @return List of owned projects.
     */
    public static List<Project> getProjectsByManager(HDBManager manager) {
        return ProjectCsvMapper.loadAll().stream()
            .filter(p -> p.getManager() != null && p.getManager().getNric().equalsIgnoreCase(manager.getNric()))
            .toList();
    }

    /**
     * Displays all projects created by the logged-in manager.
     *
     * @param manager The manager.
     */
    public static void viewMyProjects(HDBManager manager) {
        List<Project> myProjects = getProjectsByManager(manager);
    
        if (myProjects.isEmpty()) {
            System.out.println("❌ You haven’t created any projects yet.");
            return;
        }
    
        System.out.println("\n📋 Your Projects:");
        for (int i = 0; i < myProjects.size(); i++) {
            displayProjectDetails(myProjects.get(i), i + 1);
        }
    }

}
