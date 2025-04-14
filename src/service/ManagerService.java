package src.service;

import src.model.Manager;
import src.model.Project;
import src.model.Officer;
import src.model.Applicant;
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

    // ---------- PROJECT OPERATIONS ---------- //

    public void viewAllProjects(Manager manager) {
        Map<String, Project> projects = projectService.getAllProjects();
        if (projects.isEmpty()) {
            System.out.println("📭 No projects found.");
            return;
        }
        System.out.println("=== All Projects ===");
        for (Project p : projects.values()) {
            System.out.printf("Project: %s, Location: %s, Units(2-Room): %d, Units(3-Room): %d%n",
                    p.getName(), p.getNeighbourhood(), p.getTwoRoomUnits(), p.getThreeRoomUnits());
            System.out.printf("Application Period: %s to %s, Visibility: %s, Managed by: %s%n",
                    p.getOpenDate(), p.getCloseDate(), p.isVisible(), p.getManagerName());
            System.out.println();
        }
    }

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
        Project project = new Project(name, neighbourhood, twoRoomUnits, twoRoomPrice, threeRoomUnits, threeRoomPrice,
                openDate, closeDate, manager.getName(), officerSlots, new ArrayList<>(), manager.getNric(), new ArrayList<>(), new ArrayList<>(), isVisible);
        projectService.getAllProjects().put(name, project);
        boolean saved = CSVWriter.saveNewProject(project, "data/ProjectList.csv");
        System.out.println(saved ? "✅ Project created successfully!" : "❌ Failed to create project!");
    }

    public void editProject(Manager manager, String currentProjectName) {
        Project project = projectService.getProjectByName(currentProjectName);
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
            System.out.println("=== Edit Project: " + project.getName() + " ===");
            System.out.println("1. Rename project");
            System.out.println("2. Update unit counts & prices");
            System.out.println("3. Change application open/close dates");
            System.out.println("4. Toggle visibility (Current: " + project.isVisible() + ")");
            System.out.println("0. Save and exit");
            int choice = InputValidator.getInt("Enter your choice: ");
            switch (choice) {
                case 1 -> {
                    String newName = InputValidator.getNonEmptyString("Enter new project name: ");
                    if (projectService.getProjectByName(newName) != null) {
                        System.out.println("⚠️ Project name already exists.");
                    } else {
                        String oldName = project.getName();
                        projectService.getAllProjects().remove(currentProjectName);
                        project.setName(newName);
                        projectService.getAllProjects().put(newName, project);
                        currentProjectName = newName;
                        boolean saveSuccessful = CSVWriter.saveRenameProject(oldName, newName, "data/ProjectList.csv");
                        System.out.println(saveSuccessful ? "✅ Project renamed." : "❌ Failed to rename project.");
                    }
                }
                case 2 -> {
                    System.out.print("New 2-Room units: ");
                    project.setTwoRoomUnits(Integer.parseInt(sc.nextLine().trim()));
                    System.out.print("New 2-Room price: ");
                    project.setTwoRoomPrice(Double.parseDouble(sc.nextLine().trim()));
                    System.out.print("New 3-Room units: ");
                    project.setThreeRoomUnits(Integer.parseInt(sc.nextLine().trim()));
                    System.out.print("New 3-Room price: ");
                    project.setThreeRoomPrice(Double.parseDouble(sc.nextLine().trim()));
                    boolean saveSuccessful = CSVWriter.saveProject(project, "data/ProjectList.csv");
                    System.out.println(saveSuccessful ? "✅ Unit counts/prices updated." : "❌ Failed to update project.");
                }
                case 3 -> {
                    System.out.print("New open date (YYYY-MM-DD): ");
                    project.setOpenDate(LocalDate.parse(sc.nextLine().trim()));
                    System.out.print("New close date (YYYY-MM-DD): ");
                    project.setCloseDate(LocalDate.parse(sc.nextLine().trim()));
                    boolean saveSuccessful = CSVWriter.saveProject(project, "data/ProjectList.csv");
                    System.out.println(saveSuccessful ? "✅ Dates updated." : "❌ Failed to update project.");
                }
                case 4 -> {
                    project.setVisibility(!project.isVisible());
                    System.out.println("✅ Visibility set to: " + project.isVisible());
                    boolean saveSuccessful = CSVWriter.saveProject(project, "data/ProjectList.csv");
                    System.out.println(saveSuccessful ? "✅ Visibility updated." : "❌ Failed to update project.");
                }
                case 0 -> {
                    boolean saveSuccessful = CSVWriter.saveProject(project, "data/ProjectList.csv");
                    System.out.println(saveSuccessful ? "✅ Changes saved." : "❌ Failed to save changes.");
                    return;
                }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    // ---------- Applicant Application Approval ---------- //

    public void handleApplicationApprovalCLI(Manager manager) {
        List<Applicant> pendingApps = userService.getAllApplicants().values().stream()
                .filter(a -> a.getApplicationStatus().equalsIgnoreCase("PENDING"))
                .collect(Collectors.toList());
        if (pendingApps.isEmpty()) {
            System.out.println("No pending applicant applications.");
            return;
        }
        System.out.println("=== Pending Applicant Applications ===");
        for (int i = 0; i < pendingApps.size(); i++) {
            Applicant a = pendingApps.get(i);
            System.out.printf("%d. %s (%s)%n", i + 1, a.getName(), a.getNric());
        }
    }

    public void approveOrRejectApplication(String applicantNRIC, boolean approve) {
        Applicant applicant = userService.getApplicantByNric(applicantNRIC);
        if (applicant == null) {
            System.out.println("❌ Applicant not found.");
            return;
        }
        if (!applicant.getApplicationStatus().equalsIgnoreCase("PENDING")) {
            System.out.println("⚠️ Applicant's application has already been processed.");
            return;
        }
        if (approve) {
            applicant.setApplicationStatus("SUCCESSFUL");
            System.out.println("✅ Applicant " + applicantNRIC + " approved.");
        } else {
            applicant.setApplicationStatus("UNSUCCESSFUL");
            System.out.println("❌ Applicant " + applicantNRIC + " rejected.");
        }
        CSVWriter.updateApplicant(applicant, "data/ApplicantList.csv");
    }

    public boolean hasPendingApplicantApplications(Manager manager) {
        List<Applicant> pendingApps = userService.getAllApplicants().values().stream()
                .filter(a -> a.getApplicationStatus().equalsIgnoreCase("PENDING"))
                .collect(Collectors.toList());
        return !pendingApps.isEmpty();
    }

    // ---------- Applicant Withdrawal Approval ---------- //

    public void handleWithdrawalCLI(Manager manager) {
        List<Applicant> withdrawalRequests = userService.getAllApplicants().values().stream()
                .filter(a -> a.getApplicationStatus().equalsIgnoreCase("WITHDRAWAL_REQUESTED"))
                .collect(Collectors.toList());
        if (withdrawalRequests.isEmpty()) {
            System.out.println("No pending withdrawal requests.");
            return;
        }
        System.out.println("=== Pending Withdrawal Requests ===");
        for (int i = 0; i < withdrawalRequests.size(); i++) {
            Applicant a = withdrawalRequests.get(i);
            System.out.printf("%d. %s (%s)%n", i + 1, a.getName(), a.getNric());
        }
    }

    public void approveOrRejectWithdrawal(String applicantNRIC, boolean approve) {
        Applicant applicant = userService.getApplicantByNric(applicantNRIC);
        if (applicant == null) {
            System.out.println("❌ Applicant not found.");
            return;
        }
        if (!applicant.getApplicationStatus().equalsIgnoreCase("WITHDRAWAL_REQUESTED")) {
            System.out.println("⚠️ Withdrawal request already processed or invalid.");
            return;
        }
        if (approve) {
            applicant.setApplicationStatus("UNSUCCESSFUL");
            System.out.println("✅ Withdrawal approved for applicant " + applicantNRIC + ".");
        } else {
            System.out.println("❌ Withdrawal request rejected for applicant " + applicantNRIC + ".");
        }
        CSVWriter.updateApplicant(applicant, "data/ApplicantList.csv");
    }

    public boolean hasPendingWithdrawalRequests(Manager manager) {
        List<Applicant> pendingWithdrawals = userService.getAllApplicants().values().stream()
                .filter(a -> a.getApplicationStatus().equalsIgnoreCase("WITHDRAWAL_REQUESTED"))
                .collect(Collectors.toList());
        return !pendingWithdrawals.isEmpty();
    }

    // ---------- Officer Registration Operations ---------- //

    public void viewOfficerRegistrations(Manager manager) {
        Map<String, Officer> officers = userService.getAllOfficers();
        Map<String, Project> projects = projectService.getAllProjects();
        boolean found = false;
        System.out.println("=== Pending Officer Registrations for Your Projects ===");
        for (Officer officer : officers.values()) {
            if (!Officer.RegistrationStatusType.PENDING.name().equalsIgnoreCase(officer.getRegistrationStatus()))
                continue;
            String assignedProject = officer.getAssignedProjectName();
            if (assignedProject == null || assignedProject.isBlank())
                continue;
            Project project = projects.get(assignedProject);
            if (project == null)
                continue;
            if (!manager.getNric().equalsIgnoreCase(project.getManagerNRIC()))
                continue;
            found = true;
            System.out.printf("Officer: %s | NRIC: %s%n", officer.getName(), officer.getNric());
            System.out.printf("Assigned Project: %s%n", assignedProject);
            System.out.printf("Available Officer Slots: %d%n", project.getOfficerSlot() - project.getOfficerNRICs().size());
        }
        if (!found) {
            System.out.println("No pending officer registrations for your projects.");
        }
    }

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
            if (project.getOfficerNRICs().size() >= project.getOfficerSlot()) {
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
        CSVWriter.updateOfficer(officer, "data/OfficerList.csv");
        CSVWriter.saveProject(project, "data/ProjectList.csv");
    }

        // Helper method to check if there are pending officer registrations for projects managed by this manager.
        public boolean hasPendingOfficerRegistrations(Manager manager) {
            Map<String, Officer> officers = userService.getAllOfficers();
            Map<String, Project> projects = projectService.getAllProjects();
            for (Officer officer : officers.values()) {
                if (!Officer.RegistrationStatusType.PENDING.name().equalsIgnoreCase(officer.getRegistrationStatus()))
                    continue;
                String assignedProject = officer.getAssignedProjectName();
                if (assignedProject == null || assignedProject.isBlank())
                    continue;
                Project project = projects.get(assignedProject);
                if (project == null)
                    continue;
                if (manager.getNric().equalsIgnoreCase(project.getManagerNRIC()))
                    return true;
            }
            return false;
        }
    

    // ---------- Utility ---------- //

    public Map<String, Project> getProjectsByManagerNric(String managerNric) {
        Map<String, Project> allProjects = projectService.getAllProjects();
        Map<String, Project> filteredProjects = allProjects.entrySet().stream()
                .filter(entry -> managerNric.equalsIgnoreCase(entry.getValue().getManagerNRIC()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        if (filteredProjects.isEmpty()) {
            System.out.println("⚠️ You have no projects assigned to you.");
        } else {
            System.out.println("=== Your Projects ===");
            filteredProjects.values().forEach(p -> System.out.println("• " + p.getName() + " (" + p.getNeighbourhood() + ")"));
        }
        return filteredProjects;
    }
}
