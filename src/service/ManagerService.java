package src.service;

import src.model.Manager;
import src.model.Project;
import src.model.Officer;
import src.util.CSVWriter;
import src.util.InputValidator;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class ManagerService {

    private final ProjectService projectService;
    private final UserService userService;

    public ManagerService(ProjectService projectService, UserService userService) {
        this.projectService = projectService;
        this.userService = userService;
    }

    // Option 1: View all projects
    public void viewAllProjects(Manager manager) {
        Map<String, Project> projects = projectService.getAllProjects();
        if (projects.isEmpty()) {
            System.out.println("📭 No projects found.");
            return;
        }

        System.out.println("=== All Projects ===");
        for (Project p : projects.values()) {
            System.out.printf("📌 %s (%s)\n", p.getName(), p.getNeighbourhood());
            System.out.printf("   2-Room: %d units @ $%f\n", p.getTwoRoomUnits(), p.getTwoRoomPrice());
            System.out.printf("   3-Room: %d units @ $%f\n", p.getThreeRoomUnits(), p.getThreeRoomPrice());
            System.out.printf("   Open: %s to %s\n", p.getOpenDate(), p.getCloseDate());
            System.out.printf("   Visibility: %s | Manager: %s\n", p.isVisible(), p.getManagerName() + (p.getManagerName().equals(manager.getName()) ? " (You)" : ""));
            System.out.println();
        }
    }

    // Option 2: Create new project
    public void createProject(Manager manager) {
        Scanner sc = new Scanner(System.in);

        System.out.println("=== Create New Project ===");
        String name = InputValidator.getNonEmptyString("Enter project name: ");

        if (projectService.getProjectByName(name) != null) {
            System.out.println("⚠️ Project with this name already exists.");
            return;
        }

        String neighbourhood = InputValidator.getNonEmptyString("Enter neighbourhood: ");

        int twoRoomUnits = InputValidator.getInt("Enter number of 2-Room units: ");
        double twoRoomPrice = InputValidator.getDouble("Enter price for 2-Room flat: ");

        int threeRoomUnits = InputValidator.getInt("Enter number of 3-Room units: ");
        double threeRoomPrice = InputValidator.getDouble("Enter price for 3-Room flat: ");

        LocalDate openDate = InputValidator.getDate("Enter open date (YYYY-MM-DD): ");
        LocalDate closeDate = InputValidator.getDate("Enter close date (YYYY-MM-DD): ");

        LocalDate today = LocalDate.now();
        boolean isVisible = (today.isEqual(openDate) || today.isAfter(openDate)) &&
                (today.isBefore(closeDate) || today.isEqual(closeDate));


        int officerSlots = InputValidator.getIntInRange("Enter number of officer slots: ", 1, 10);

        Project project = new Project(name, neighbourhood, twoRoomUnits, twoRoomPrice, threeRoomUnits, threeRoomPrice, openDate, closeDate, manager.getName(), officerSlots, null, manager.getNric(), null, null, isVisible);

        // Add to service and persist
        projectService.getAllProjects().put(name, project);
        boolean createdProjectSuccessfully = CSVWriter.saveNewProject(project, "data/ProjectList.csv");
        System.out.println(createdProjectSuccessfully ? "✅ Project created successfully!" : "❌ Failed to create project!");
    }

    // Option 3: Edit existing project (rename, visibility, dates, units, prices)
    public void editProject(Manager manager, String currentProjectName) {
        Project project = projectService.getProjectByName(currentProjectName);
        Project projectCopyOriginal = project;

        if (project == null) {
            System.out.println("❌ Project not found.");
            return;
        }

        if (!manager.getNric().equalsIgnoreCase(project.getManagerNRIC())) {
            System.out.println("❌ You are not authorized to edit this project.");
            return;
        }

        Scanner sc = new Scanner(System.in);

        while (true) {
            int choice;

            System.out.println("=== Edit Project: " + project.getName() + " ===");
            System.out.println("1. Rename project");
            System.out.println("2. Update unit counts & prices");
            System.out.println("3. Change application open/close dates");
            System.out.println("4. Toggle visibility (" + project.isVisible() + ")");
            System.out.println("0. Save and return");

            try {
                choice = InputValidator.getInt("Enter your choice: ");
            } catch (NumberFormatException e) {
                choice = -1; // keep loop going safely
            }

            switch (choice) {
                case 1 -> {
                    System.out.print("Enter new project name: ");
                    String newName = sc.nextLine().trim();
                    if (projectService.getProjectByName(newName) != null) {
                        System.out.println("⚠️ Project name already exists.");
                    } else {
                        String oldName = project.getName();
                        projectService.getAllProjects().remove(currentProjectName);
                        project.setName(newName);
                        projectService.getAllProjects().put(newName, project);
                        currentProjectName = newName;
                        boolean saveSuccessful = CSVWriter.saveRenameProject(oldName, newName,"data/ProjectList.csv");
                        System.out.println(saveSuccessful ? "✅ Project renamed.\n" : "❌ Failed to save project!");
                    }
                }
                case 2 -> {
                    System.out.print("New 2-Room units: ");
                    project.setTwoRoomUnits(Integer.parseInt(sc.nextLine()));

                    System.out.print("New 2-Room price: ");
                    project.setTwoRoomPrice(Integer.parseInt(sc.nextLine()));

                    System.out.print("New 3-Room units: ");
                    project.setThreeRoomUnits(Integer.parseInt(sc.nextLine()));

                    System.out.print("New 3-Room price: ");
                    project.setThreeRoomPrice(Integer.parseInt(sc.nextLine()));

                    boolean saveSuccessful = CSVWriter.saveProject(project, "data/ProjectList.csv");
                    System.out.println(saveSuccessful ? "✅ Unit and prices updated.\n" : "❌ Failed to save project!");
                }
                case 3 -> {
                    System.out.print("New open date (YYYY-MM-DD): ");
                    project.setOpenDate(LocalDate.parse(sc.nextLine()));

                    System.out.print("New close date (YYYY-MM-DD): ");
                    project.setCloseDate(LocalDate.parse(sc.nextLine()));

                    boolean saveSuccessful = CSVWriter.saveProject(project, "data/ProjectList.csv");
                    System.out.println(saveSuccessful ? "✅ Dates updated.\n" : "❌ Failed to save project!");
                }
                case 4 -> {
                    project.setVisibility(!project.isVisible());
                    System.out.println("✅ Visibility set to: " + project.isVisible() + "\n");

                    boolean saveSuccessful = CSVWriter.saveProject(project, "data/ProjectList.csv");
                    System.out.println(saveSuccessful ? "✅ Visibility updated.\n" : "❌ Failed to save project!");
                }
                case 0 -> {
                    // Save and exit
                    boolean saveSuccessful = CSVWriter.saveProject(project, "data/ProjectList.csv");
                    System.out.println(saveSuccessful ? "✅ Changes saved.\n" : "❌ Failed to save the project, reverting project info to original.\n");

                    if (!saveSuccessful) {
                        project = projectCopyOriginal;
                    }

                    return;
                }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    // Option 4: View pending officer registrations for approval
    public void viewOfficerRegistrations(Manager manager) {
        Map<String, Officer> officers = userService.getAllOfficers();
        Map<String, Project> projects = projectService.getAllProjects();

        boolean found = false;

        System.out.println("=== Pending Officer Registrations for Your Projects ===");

        for (Officer officer : officers.values()) {
            // Skip if not pending
            if (!Officer.RegistrationStatusType.PENDING.name().equalsIgnoreCase(officer.getRegistrationStatus())) continue;

            String assignedProject = officer.getAssignedProjectName();
            if (assignedProject == null || assignedProject.isBlank()) continue;

            Project project = projects.get(assignedProject);
            if (project == null) continue;

            // Skip if the project isn't owned by this manager
            if (!manager.getNric().equalsIgnoreCase(project.getManagerNRIC())) continue;

            found = true;

            System.out.printf("👤 Name: %s | NRIC: %s\n", officer.getName(), officer.getNric());
            System.out.printf("   Assigned Project: %s\n", assignedProject);
            System.out.printf("   Available Officer Slots: %d\n", project.getOfficerSlot() - project.getOfficerNRICs().size());
        }

        if (!found) {
            System.out.println("📭 No pending officer registrations for your projects.");
        }
    }

    // Option 5: Approve/Reject officers by NRIC
    public void approveOrRejectOfficer(String officerNRIC, boolean approve, Manager manager) {
        Officer officer = userService.getOfficerByNric(officerNRIC);
        if (officer == null) {
            System.out.println("❌ Officer not found.");
            return;
        }

        if (!"PENDING".equalsIgnoreCase(officer.getRegistrationStatus())) {
            System.out.println("⚠️ Officer is not pending approval.");
            return;
        }

        String projectName = officer.getAssignedProjectName();
        if (projectName == null || projectName.isBlank()) {
            System.out.println("❌ Officer has no assigned project.");
            return;
        }

        Project project = projectService.getProjectByName(projectName);
        if (project == null) {
            System.out.println("❌ Assigned project does not exist.");
            return;
        }

        if (!project.getManagerNRIC().equalsIgnoreCase(manager.getNric())) {
            System.out.println("❌ You are not the manager in charge of this project.");
            return;
        }

        if (approve) {
            if (project.getOfficerNRICs().size() == project.getOfficerSlot()) {
                System.out.println("❌ Cannot approve officer. No available officer slots in project.");
                return;
            }

            officer.setRegistrationStatus(Officer.RegistrationStatusType.APPROVED.name());
            project.getOfficerNRICs().add(officer.getNric());
            System.out.println("✅ Officer approved and added to project.");
        } else {
            officer.setRegistrationStatus(Officer.RegistrationStatusType.REJECTED.name());
            officer.setAssignedProjectName(null);
            System.out.println("❌ Officer rejected.");
        }

        // Save updated data
        CSVWriter.updateOfficer(officer, "data/OfficerList.csv");
        CSVWriter.saveProject(project, "data/ProjectList.csv");
    }

    public Map<String,Project> getProjectsByManagerNric(String managerNric) {
        Map<String, Project> allProjects = projectService.getAllProjects();

        System.out.println("=== Your Projects ===");
        boolean found = false;

        for (Project project : allProjects.values()) {
            if (managerNric.equals(project.getManagerNRIC())) {
                found = true;
                System.out.println("• " + project.getName() + " (" + project.getNeighbourhood() + ")");
            }
        }

        if (!found) {
            System.out.println("⚠️ You have no projects assigned to you.");
        }

        Map<String, Project> filteredProjects = allProjects.entrySet().stream()
                .filter(entry -> managerNric.equals(entry.getValue().getManagerNRIC()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        return filteredProjects;

    }
}
