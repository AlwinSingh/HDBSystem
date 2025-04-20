package src.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

import src.model.Applicant;
import src.model.Feedback;
import src.model.HDBManager;
import src.model.HDBOfficer;
import src.model.Project;
import src.model.ProjectLocation;
import src.repository.ApplicantRepository;
import src.util.ApplicantCsvMapper;
import src.util.CsvUtil;
import src.util.FilePath;
import src.util.OfficerCsvMapper;
import src.util.ProjectCsvMapper;

public class ManagerService {
    private static final ApplicantRepository applicantRepository = new ApplicantCsvMapper();
    /**
     * Allows the manager to create a new project, validate inputs,
     * and save it to the system.
     *
     * @param manager The logged-in HDB manager.
     * @param sc      Scanner for user input.
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
     * Allows a manager to delete one of their projects, if no officers or applicants are assigned.
     *
     * @param manager The logged-in manager.
     * @param sc      Scanner for user input.
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

    /**
     * Displays all officer registration requests tied to the manager's projects.
     *
     * @param manager The manager.
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
        List<HDBOfficer> allOfficers = OfficerCsvMapper.loadAll(allProjects);

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

                OfficerCsvMapper.updateOfficer(officer);
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
                OfficerCsvMapper.updateOfficer(officer);

                System.out.println("❌ Officer registration rejected.");
            }
            default -> System.out.println("❌ Invalid input. Use A or R.");
        }
    }

    /**
     * Displays all applicant submissions tied to the manager's projects.
     *
     * @param manager The logged-in manager.
     */
    public static void viewApplicantApplications(HDBManager manager) {
        List<Map<String, String>> applicants = CsvUtil.read(FilePath.APPLICANT_LIST_FILE);
        List<Map<String, String>> projects = CsvUtil.read(FilePath.PROJECT_LIST_FILE);
    
        Set<String> myProjectNames = projects.stream()
            .filter(p -> manager.getNric().equalsIgnoreCase(p.get("ManagerNRIC")))
            .map(p -> p.get("Project Name"))
            .collect(Collectors.toSet());
    
        if (myProjectNames.isEmpty()) {
            System.out.println("❌ You haven’t created any projects yet.");
            return;
        }
    
        List<Map<String, String>> relevantApplicants = applicants.stream()
            .filter(a -> myProjectNames.contains(a.getOrDefault("AppliedProjectName", "")))
            .toList();
    
        if (relevantApplicants.isEmpty()) {
            System.out.println("📭 No applicant applications for your projects.");
            return;
        }
    
        System.out.println("\n📄 Applicant Applications for Your Projects:");
        for (int i = 0; i < relevantApplicants.size(); i++) {
            Map<String, String> app = relevantApplicants.get(i);
            System.out.printf("\n[%d] %s (NRIC: %s)\n", i + 1, app.get("Name"), app.get("NRIC"));
            System.out.println("   🏘 Project: " + app.get("AppliedProjectName"));
            System.out.println("   🏠 Flat Type: " + app.get("FlatTypeApplied"));
            System.out.println("   📊 Status: " + app.get("ApplicationStatus"));
            System.out.println("   🎂 Age: " + app.get("Age"));
            System.out.println("   💍 Marital Status: " + app.get("Marital Status"));
        }
    }

    /**
     * Approves or rejects applicant submissions for the manager’s projects.
     *
     * @param manager The manager.
     * @param sc      Scanner for user input.
     */
    public static void handleApplicantApproval(HDBManager manager, Scanner sc) {
        List<Applicant> applicants = applicantRepository.loadAll();
        List<Project> projects = ProjectCsvMapper.loadAll();

        Set<String> myProjectNames = projects.stream()
            .filter(p -> p.getManager() != null && p.getManager().getNric().equalsIgnoreCase(manager.getNric()))
            .map(Project::getProjectName)
            .collect(Collectors.toSet());

        List<Applicant> pendingApps = applicants.stream()
            .filter(a -> a.getApplication() != null)
            .filter(a -> myProjectNames.contains(a.getApplication().getProject().getProjectName()))
            .filter(a -> Applicant.AppStatusType.PENDING.name().equalsIgnoreCase(a.getApplication().getStatus()))
            .toList();

        if (pendingApps.isEmpty()) {
            System.out.println("📭 No pending applicant applications.");
            return;
        }

        for (int i = 0; i < pendingApps.size(); i++) {
            Applicant a = pendingApps.get(i);
            System.out.printf("[%d] %s (%s), Project: %s, Flat: %s\n", i + 1,
                a.getName(), a.getNric(),
                a.getApplication().getProject().getProjectName(),
                a.getApplication().getFlatType());
        }

        System.out.print("Select applicant to process (0 to cancel): ");
        int choice;
        try {
            choice = Integer.parseInt(sc.nextLine());
            if (choice == 0) return;
            if (choice < 1 || choice > pendingApps.size()) throw new Exception();
        } catch (Exception e) {
            System.out.println("❌ Invalid selection.");
            return;
        }

        Applicant selectedApp = pendingApps.get(choice - 1);
        String flatType = selectedApp.getApplication().getFlatType();
        String projName = selectedApp.getApplication().getProject().getProjectName();

        Project project = projects.stream()
            .filter(p -> p.getProjectName().equalsIgnoreCase(projName))
            .findFirst()
            .orElse(null);

        if (project == null) {
            System.out.println("❌ Project not found.");
            return;
        }

        System.out.print("Approve or Reject this application? (A/R): ");
        String decision = sc.nextLine().trim().toUpperCase();

        switch (decision) {
            case "A" -> {
                if (!approveApplicant(selectedApp, project, flatType)) return;
            }
            case "R" -> {
                System.out.print("Confirm rejection for " + selectedApp.getName() + " (Y/N): ");
                if (!sc.nextLine().trim().equalsIgnoreCase("Y")) {
                    System.out.println("❌ Rejection cancelled.");
                    return;
                }
                selectedApp.getApplication().setStatus(Applicant.AppStatusType.UNSUCCESSFUL.name());
                System.out.println("❌ Application rejected.");
            }
            default -> {
                System.out.println("❌ Invalid input. Use A or R.");
                return;
            }
        }

        applicantRepository.update(selectedApp);
        ProjectCsvMapper.updateProject(project);
    }

    /**
     * Approves the applicant and deducts one unit of the selected flat type.
     *
     * @return True if successful; false if flats are unavailable.
     */
    private static boolean approveApplicant(Applicant applicant, Project project, String flatType) {
        int available = flatType.equalsIgnoreCase("2-Room")
            ? project.getAvailableFlats2Room()
            : project.getAvailableFlats3Room();

        if (available <= 0) {
            System.out.println("❌ No units left for this flat type.");
            return false;
        }

        if (flatType.equalsIgnoreCase("2-Room")) {
            project.setAvailableFlats2Room(available - 1);
        } else {
            project.setAvailableFlats3Room(available - 1);
        }

        applicant.getApplication().setStatus(Applicant.AppStatusType.SUCCESSFUL.name());
        System.out.println("✅ Application approved and flat reserved.");
        return true;
    }

    /**
     * Displays and resolves pending withdrawal requests from applicants.
     */
    public static void handleWithdrawalRequests(HDBManager manager, Scanner sc) {
        List<Applicant> applicants = applicantRepository.loadAll();
        List<Project> projects = ProjectCsvMapper.loadAll();
    
        Set<String> myProjects = projects.stream()
            .filter(p -> p.getManager() != null && p.getManager().getNric().equalsIgnoreCase(manager.getNric()))
            .map(Project::getProjectName)
            .collect(Collectors.toSet());
    
        List<Applicant> withdrawals = applicants.stream()
            .filter(a -> a.getApplication() != null)
            .filter(a -> Applicant.AppStatusType.WITHDRAW_REQUESTED.name().equalsIgnoreCase(a.getApplication().getStatus()))
            .filter(a -> myProjects.contains(a.getApplication().getProject().getProjectName()))
            .toList();
    
        if (withdrawals.isEmpty()) {
            System.out.println("📭 No pending withdrawal requests.");
            return;
        }
    
        for (int i = 0; i < withdrawals.size(); i++) {
            Applicant a = withdrawals.get(i);
            System.out.printf("[%d] %s (NRIC: %s) — Project: %s — Flat: %s\n", i + 1,
                a.getName(), a.getNric(),
                a.getApplication().getProject().getProjectName(),
                a.getApplication().getFlatType());
        }
    
        System.out.print("Select applicant to process (0 to cancel): ");
        int index;
        try {
            index = Integer.parseInt(sc.nextLine());
            if (index == 0) return;
            if (index < 1 || index > withdrawals.size()) throw new Exception();
        } catch (Exception e) {
            System.out.println("❌ Invalid selection.");
            return;
        }
    
        Applicant selected = withdrawals.get(index - 1);
    
        System.out.print("Approve or Reject withdrawal? (A/R): ");
        String decision = sc.nextLine().trim().toUpperCase();
    
        switch (decision) {
            case "A" -> {
                selected.getApplication().setStatus(Applicant.AppStatusType.WITHDRAWAL_APPROVED.name());
                System.out.println("✅ Withdrawal approved.");
            }
            case "R" -> {
                selected.getApplication().setStatus(Applicant.AppStatusType.WITHDRAWAL_REJECTED.name());
                System.out.println("❌ Withdrawal rejected.");
            }
            default -> {
                System.out.println("❌ Invalid input.");
                return;
            }
        }
    
        applicantRepository.update(selected);
    }

    /**
     * Allows managers to generate and view booking reports based on filters.
     *
     * @param manager The manager.
     * @param sc      Scanner for input.
     */
    public static void generateReports(HDBManager manager, Scanner sc) {
        while (true) {
            System.out.println("\n📊 Generate Applicant Booking Reports");
            System.out.println(" [1] 🔄 Generate Reports");
            System.out.println(" [2] 📋 View All Reports");
            System.out.println(" [3] 🔎 Filter by Project Name");
            System.out.println(" [4] 🏠 Filter by Flat Type");
            System.out.println(" [5] 💳 Filter by Payment Status");
            System.out.println(" [6] 📅 Filter by Booking Date Range");
            System.out.println(" [0] 🔙 Back");
            System.out.print("Enter choice: ");
            String choice = sc.nextLine().trim();
    
            switch (choice) {
                case "1" -> ReportService.generateAndSaveReports();
    
                case "2" -> {
                    if (ReportService.getAllReports().isEmpty()) {
                        System.out.println("📭 No reports available. Please generate reports first.");
                    } else {
                        ReportService.printAllReports();
                    }
                }
    
                case "3" -> {
                    if (ReportService.getAllReports().isEmpty()) {
                        System.out.println("📭 No reports available to filter. Please generate reports first.");
                        break;
                    }
                    System.out.print("Enter project name to filter: ");
                    String name = sc.nextLine().trim();
                    var filtered = ReportService.getReportsByProject(name);
                    if (filtered.isEmpty()) {
                        System.out.println("📭 No reports found for project: " + name);
                    } else {
                        ReportService.printReports(filtered);
                    }
                }
    
                case "4" -> {
                    if (ReportService.getAllReports().isEmpty()) {
                        System.out.println("📭 No reports available to filter. Please generate reports first.");
                        break;
                    }
                    System.out.print("Enter flat type to filter (e.g., 2-Room): ");
                    String type = sc.nextLine().trim();
                    var filtered = ReportService.getReportsByFlatType(type);
                    if (filtered.isEmpty()) {
                        System.out.println("📭 No reports found for flat type: " + type);
                    } else {
                        ReportService.printReports(filtered);
                    }
                }
    
                case "5" -> {
                    if (ReportService.getAllReports().isEmpty()) {
                        System.out.println("📭 No reports available to filter. Please generate reports first.");
                        break;
                    }
                    System.out.print("Enter payment status to filter (Processed/Awaiting Payment): ");
                    String status = sc.nextLine().trim();
                    var filtered = ReportService.getReportsByPaymentStatus(status);
                    if (filtered.isEmpty()) {
                        System.out.println("📭 No reports found for payment status: " + status);
                    } else {
                        ReportService.printReports(filtered);
                    }
                }
    
                case "6" -> {
                    if (ReportService.getAllReports().isEmpty()) {
                        System.out.println("📭 No reports available to filter. Please generate reports first.");
                        break;
                    }
                    try {
                        System.out.print("Enter start date (yyyy-MM-dd): ");
                        LocalDate start = LocalDate.parse(sc.nextLine().trim());
                        System.out.print("Enter end date (yyyy-MM-dd): ");
                        LocalDate end = LocalDate.parse(sc.nextLine().trim());
                        var filtered = ReportService.getReportsByBookingDateRange(start, end);
                        if (filtered.isEmpty()) {
                            System.out.println("📭 No reports found in that date range.");
                        } else {
                            ReportService.printReports(filtered);
                        }
                    } catch (Exception e) {
                        System.out.println("❌ Invalid date format.");
                    }
                }
    
                case "0" -> {
                    System.out.println("🔙 Returning to manager menu...");
                    return;
                }
    
                default -> System.out.println("❌ Invalid choice.");
            }
        }
    }

    /**
     * Shows the enquiry options available to managers, allowing them to respond.
     */
    public static void showEnquiryOptions(HDBManager manager, Scanner sc) {
        while (true) {
            System.out.println("\n📨 Enquiry Options");
            System.out.println(" [1] View All Enquiries");
            System.out.println(" [2] Reply to Enquiries for My Projects");
            System.out.println(" [0] Back");
            System.out.print("➡️ Enter your choice: ");
    
            String choice = sc.nextLine().trim();
            switch (choice) {
                case "1" -> EnquireService.viewAllEnquiries();
                case "2" -> EnquireService.replyAsManager(manager, sc);
                case "0" -> {
                    System.out.println("🔙 Returning to manager menu...");
                    return;
                }
                default -> System.out.println("❌ Invalid input.");
            }
        }
    }

    /**
     * Provides full feedback management options to managers:
     * viewing, filtering, and resolving feedback.
     */
    public static void viewAndResolveFeedback(HDBManager manager, Scanner sc) {
        while (true) {
            System.out.println("\n📝 Feedback Management");
            System.out.println(" [1] View My Feedback");
            System.out.println(" [2] View Unresolved Feedback");
            System.out.println(" [3] Filter by Applicant NRIC");
            System.out.println(" [4] Filter by Submission Date Range");
            System.out.println(" [5] Resolve Feedback");
            System.out.println(" [0] Back");
            System.out.print("➡️ Enter choice: ");

            String choice = sc.nextLine().trim();
            switch (choice) {
                case "1" -> FeedbackService.printFeedbackList(
                    FeedbackService.getFeedbackByManager(manager)
                );

                case "2" -> FeedbackService.printFeedbackList(
                    FeedbackService.getUnresolvedByManager(manager)
                );

                case "3" -> {
                    System.out.print("Enter Applicant NRIC: ");
                    String nric = sc.nextLine().trim();
                    FeedbackService.printFeedbackList(
                        FeedbackService.getFeedbackByApplicant(manager, nric)
                    );
                }

                case "4" -> {
                    try {
                        System.out.print("Start date (yyyy-MM-dd): ");
                        LocalDate start = LocalDate.parse(sc.nextLine().trim());
                        System.out.print("End date (yyyy-MM-dd): ");
                        LocalDate end = LocalDate.parse(sc.nextLine().trim());
                        FeedbackService.printFeedbackList(
                            FeedbackService.getFeedbackBySubmittedDateRange(manager, start, end)
                        );
                    } catch (Exception e) {
                        System.out.println("❌ Invalid date format.");
                    }
                }

                case "5" -> {
                    try {
                        System.out.print("Enter Feedback ID to resolve: ");
                        int id = Integer.parseInt(sc.nextLine().trim());

                        List<Feedback> myFeedback = FeedbackService.getFeedbackByManager(manager);
                        boolean belongsToManager = myFeedback.stream()
                            .anyMatch(f -> f.getFeedbackId() == id);

                        if (!belongsToManager) {
                            System.out.println("❌ You can only resolve feedback from your assigned projects.");
                            break;
                        }

                        FeedbackService.resolveFeedback(id, manager.getName());
                    } catch (Exception e) {
                        System.out.println("❌ Invalid input.");
                    }
                }

                case "0" -> {
                    System.out.println("🔙 Returning to manager menu...");
                    return;
                }

                default -> System.out.println("❌ Invalid input.");
            }
        }
    }

    

}
