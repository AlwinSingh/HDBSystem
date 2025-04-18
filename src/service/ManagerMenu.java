package src.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import src.model.*;
import src.util.ApplicantCsvMapper;
import src.util.CsvUtil;
import src.util.EnquiryCsvMapper;
import src.util.FilePath;
import src.util.ProjectCsvMapper;

public class ManagerMenu {

    public static void show(HDBManager manager) {
        Scanner sc = new Scanner(System.in);
    
        while (true) {
            System.out.println("\n===== 🧠 HDB Manager Dashboard =====");
            System.out.println("Welcome, Manager " + manager.getName());
    
            System.out.println("\n🏗️ Project Management");
            System.out.println(" [1] ➕ Create Project");
            System.out.println(" [2] ✏️ Edit Project");
            System.out.println(" [3] ❌ Delete Project");
            System.out.println(" [4] 🔁 Toggle Visibility");
    
            System.out.println("\n📊 Project Viewing");
            System.out.println(" [5] 🌐 View All Projects");
            System.out.println(" [6] 📂 View My Projects");
    
            System.out.println("\n🧑‍💼 Officer Applications");
            System.out.println(" [7] 📋 View Officer Registrations");
            System.out.println(" [8] ✅/❌ Approve/Reject Officer");
    
            System.out.println("\n👥 Applicant Management");
            System.out.println(" [9] 📄 View Applications");
            System.out.println(" [10] ✅/❌ Approve/Reject Applications");
            System.out.println(" [11] 🔄 Handle Withdrawal Requests");
    
            System.out.println("\n📈 Reporting");
            System.out.println(" [12] 📊 Generate Booking Reports");
    
            System.out.println("\n🗃 Enquiries and Feedback");
            System.out.println(" [13] 📬 View & Reply to Enquiries");
            System.out.println(" [14] 📝 View & Resolve Feedback");
            System.out.println(" [15] 📊 View Feedback Analytics");
    
            System.out.println("\n [16] 🔒 Change Password");
            System.out.println(" [0] Logout");
            System.out.print("➡️ Enter your choice: ");
    
            String choice = sc.nextLine().trim();
    
            switch (choice) {
                case "1" -> createProject(manager, sc);
                case "2" -> editProject(manager, sc);
                case "3" -> deleteProject(manager, sc);
                case "4" -> toggleVisibility(manager, sc);
    
                case "5" -> viewAllProjects();
                case "6" -> viewMyProjects(manager);
    
                case "7" -> viewOfficerRegistrations(manager);
                case "8" -> handleOfficerApproval(manager, sc);
    
                case "9"  -> viewApplicantApplications(manager);
                case "10" -> handleApplicantApproval(manager, sc);
                case "11" -> handleWithdrawalRequests(manager, sc);
    
                case "12" -> generateReports(manager, sc);
    
                case "13" -> showEnquiryOptions(manager, sc);          // ✅ updated label and method
                case "14" -> viewAndResolveFeedback(manager, sc);
                case "15" -> FeedbackAnalyticsService.generateManagerAnalytics(manager);
    
                case "16" -> AuthService.changePassword(manager, sc);
    
                case "0" -> {
                    AuthService.logout();
                    return;
                }
    
                default -> System.out.println("❌ Invalid input. Please try again.");
            }
        }
    }
    

    private static void createProject(HDBManager manager, Scanner sc) {
        System.out.println("\n📌 Create New Project");
    
        List<Project> allProjects = ProjectCsvMapper.loadAll();
    
        System.out.print("Enter project name: ");
        String name = sc.nextLine().trim();
        if (name.isBlank()) {
            System.out.println("❌ Project name cannot be empty.");
            return;
        }
    
        // Check uniqueness
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
    
        // Use dummy location with just neighborhood for now
        ProjectLocation location = new ProjectLocation(0, "", neighborhood, "", 0, 0);
    
        Project newProject = new Project(name, neighborhood, openDate, closeDate, officerSlots, units2, units3, location);
        newProject.setPrice2Room(price2);
        newProject.setPrice3Room(price3);
        newProject.openProject();
        newProject.setManager(manager); // ensure Manager NRIC + Name is captured
    
        allProjects.add(newProject);
        ProjectCsvMapper.saveAll(allProjects);
    
        System.out.println("✅ Project created and saved successfully!");
    }
    
    private static void editProject(HDBManager manager, Scanner sc) {
        List<Project> allProjects = ProjectCsvMapper.loadAll();
        List<Project> myProjects = allProjects.stream()
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
    
            // Ensure manager is retained
            if (p.getManager() == null) {
                p.setManager(manager);
            }
    
            ProjectCsvMapper.saveAll(allProjects);
            System.out.println("✅ Project updated successfully.");
    
        } catch (Exception e) {
            System.out.println("❌ Invalid input. Edit aborted.");
        }
    } 
    

    private static void deleteProject(HDBManager manager, Scanner sc) {
        List<Project> allProjects = ProjectCsvMapper.loadAll();
        List<Project> myProjects = allProjects.stream()
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
    
        allProjects.remove(selected);
        ProjectCsvMapper.saveAll(allProjects);
        System.out.println("✅ Project deleted successfully.");
    }
    
    
    private static void toggleVisibility(HDBManager manager, Scanner sc) {
        List<Project> allProjects = ProjectCsvMapper.loadAll();
        List<Project> myProjects = allProjects.stream()
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
    
        ProjectCsvMapper.saveAll(allProjects);
        System.out.printf("✅ Visibility updated. New visibility: %s\n", selected.isVisible());
    }
    
    private static void viewAllProjects() {
        List<Project> projects = ProjectCsvMapper.loadAll();
    
        if (projects.isEmpty()) {
            System.out.println("❌ No projects found.");
            return;
        }
    
        System.out.println("\n📋 All Projects in the System:");
        for (int i = 0; i < projects.size(); i++) {
            displayProjectDetails(projects.get(i), i + 1);
        }
    }
    
    private static void viewMyProjects(HDBManager manager) {
        List<Project> allProjects = ProjectCsvMapper.loadAll();
    
        List<Project> myProjects = allProjects.stream()
            .filter(p -> p.getManager() != null && p.getManager().getNric().equalsIgnoreCase(manager.getNric()))
            .toList();
    
        if (myProjects.isEmpty()) {
            System.out.println("❌ You haven’t created any projects yet.");
            return;
        }
    
        System.out.println("\n📋 Your Projects:");
        for (int i = 0; i < myProjects.size(); i++) {
            displayProjectDetails(myProjects.get(i), i + 1);
        }
    }

    private static void displayProjectDetails(Project p, int index) {
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
    
    
    private static void viewOfficerRegistrations(HDBManager manager) {
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
    
    
    private static void handleOfficerApproval(HDBManager manager, Scanner sc) {
        List<Map<String, String>> officerList = CsvUtil.read(FilePath.OFFICER_LIST_FILE);
        List<Map<String, String>> projectList = CsvUtil.read(FilePath.PROJECT_LIST_FILE);

        // find pending officers in the officer list
        List<Map<String, String>> pendingOfficers = officerList.stream()
            .filter(o -> HDBOfficer.RegistrationStatusType.PENDING
                .name().equalsIgnoreCase(o.getOrDefault("RegistrationStatus", "")))
            .toList();

        if (pendingOfficers.isEmpty()) {
            System.out.println("📭 No pending officer registrations.");
            return;
        }
    
        System.out.println("\n📋 Pending Officer Registrations:");
        for (int i = 0; i < pendingOfficers.size(); i++) {
            Map<String, String> o = pendingOfficers.get(i);
            System.out.printf("[%d] %s (%s) – Project: %s\n", i + 1,
                    o.get("Name"), o.get("NRIC"), o.get("AssignedProject"));
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
    
        Map<String, String> officer = pendingOfficers.get(index);
        String officerNRIC = officer.get("NRIC");
        String officerName = officer.get("Name");
        String assignedProject = officer.get("AssignedProject");
    
        Map<String, String> project = projectList.stream()
            .filter(p -> p.get("Project Name").equalsIgnoreCase(assignedProject))
            .filter(p -> manager.getNric().equalsIgnoreCase(p.get("ManagerNRIC")))
            .findFirst()
            .orElse(null);
    
        if (project == null) {
            System.out.println("❌ You are not the assigned manager for this project.");
            return;
        }
    
        System.out.print("Approve or Reject? (A/R): ");
        String decision = sc.nextLine().trim().toUpperCase();
    
        switch (decision) {
            case "A" -> approveOfficer(officer, officerNRIC, officerName, project);
            case "R" -> rejectOfficer(officer, officerName, sc);
            default -> System.out.println("❌ Invalid input. Use A or R.");
        }
    
        CsvUtil.write(FilePath.OFFICER_LIST_FILE, officerList);
        CsvUtil.write(FilePath.PROJECT_LIST_FILE, projectList);
    }
    
    private static void approveOfficer(Map<String, String> officer, String nric, String name, Map<String, String> project) {
        int slots = Integer.parseInt(project.getOrDefault("Officer Slot", "0"));
        if (slots <= 0) {
            System.out.println("❌ No officer slots remaining.");
            return;
        }
    
        project.put("Officer Slot", String.valueOf(slots - 1));
    
        Set<String> nricSet = new LinkedHashSet<>(Arrays.asList(project.getOrDefault("OfficerNRICs", "").trim().split("\\s+")));
        nricSet.add(nric);
        nricSet.remove("");
        project.put("OfficerNRICs", String.join(" ", nricSet));
    
        Set<String> nameSet = new LinkedHashSet<>(Arrays.asList(project.getOrDefault("Officer", "").trim().split("\\s+")));
        nameSet.add(name);
        nameSet.remove("");
        project.put("Officer", String.join(" ", nameSet));
    
        officer.put("RegistrationStatus", HDBOfficer.RegistrationStatusType.APPROVED.name());
        System.out.println("✅ Officer approved and added to project.");
    }
    
    private static void rejectOfficer(Map<String, String> officer, String name, Scanner sc) {
        System.out.print("Confirm rejection for Officer " + name + " (Y/N): ");
        if (!sc.nextLine().trim().equalsIgnoreCase("Y")) {
            System.out.println("❌ Rejection cancelled.");
            return;
        }
        officer.put("RegistrationStatus", HDBOfficer.RegistrationStatusType.REJECTED.name());
        officer.put("AssignedProject", "");
        System.out.println("❌ Officer registration rejected.");
    }
    
    private static void viewApplicantApplications(HDBManager manager) {
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
    
    private static void handleApplicantApproval(HDBManager manager, Scanner sc) {
        List<Applicant> applicants = ApplicantCsvMapper.loadAll();
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
    
        ApplicantCsvMapper.updateApplicant(selectedApp);
        ProjectCsvMapper.saveAll(projects);
    }
    
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
    
    
    private static void handleWithdrawalRequests(HDBManager manager, Scanner sc) {
        List<Map<String, String>> applicants = CsvUtil.read(FilePath.APPLICANT_LIST_FILE);
        List<Map<String, String>> projects = CsvUtil.read(FilePath.PROJECT_LIST_FILE);
    
        Set<String> myProjects = projects.stream()
            .filter(p -> manager.getNric().equalsIgnoreCase(p.get("ManagerNRIC")))
            .map(p -> p.get("Project Name"))
            .collect(Collectors.toSet());
    
        List<Map<String, String>> withdrawals = applicants.stream()
            .filter(a -> "WITHDRAWAL_REQUESTED".equalsIgnoreCase(a.get("ApplicationStatus")))
            .filter(a -> myProjects.contains(a.get("AppliedProjectName")))
            .toList();
    
        if (withdrawals.isEmpty()) {
            System.out.println("📭 No pending withdrawal requests.");
            return;
        }
    
        for (int i = 0; i < withdrawals.size(); i++) {
            Map<String, String> a = withdrawals.get(i);
            System.out.printf("[%d] %s (NRIC: %s) — Project: %s — Flat: %s\n", i + 1,
                    a.get("Name"), a.get("NRIC"),
                    a.get("AppliedProjectName"), a.get("FlatTypeApplied"));
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
    
        Map<String, String> selected = withdrawals.get(index - 1);
        System.out.print("Approve or Reject withdrawal? (A/R): ");
        String decision = sc.nextLine().trim().toUpperCase();
    
        if (decision.equals("A")) {
            selected.put("ApplicationStatus", Applicant.AppStatusType.WITHDRAWAL_APPROVED.name());
            System.out.println("✅ Withdrawal approved.");
        } else if (decision.equals("R")) {
            selected.put("ApplicationStatus", Applicant.AppStatusType.WITHDRAWAL_REJECTED.name());
            System.out.println("❌ Withdrawal rejected.");
        } else {
            System.out.println("❌ Invalid input.");
        }
    
        CsvUtil.write(FilePath.APPLICANT_LIST_FILE, applicants);
    }
    
    private static void generateReports(HDBManager manager, Scanner sc) {
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
    
    private static void showEnquiryOptions(HDBManager manager, Scanner sc) {
        while (true) {
            System.out.println("\n📨 Enquiry Options");
            System.out.println(" [1] View All Enquiries");
            System.out.println(" [2] Reply to Enquiries for My Projects");
            System.out.println(" [0] Back");
            System.out.print("➡️ Enter your choice: ");
    
            String choice = sc.nextLine().trim();
            switch (choice) {
                case "1" -> viewAllEnquiries();
                case "2" -> handleManagerEnquiries(manager, sc);
                case "0" -> {
                    System.out.println("🔙 Returning to manager menu...");
                    return;
                }
                default -> System.out.println("❌ Invalid input.");
            }
        }
    }

    private static void viewAllEnquiries() {
        List<Enquiry> all = EnquiryCsvMapper.loadAll();
        if (all.isEmpty()) {
            System.out.println("📭 No enquiries in the system.");
            return;
        }
    
        System.out.println("\n📋 All Enquiries:");
        for (Enquiry e : all) {
            System.out.printf("📨 Enquiry #%d | Applicant: %s (%s) | Project: %s | Status: %s\n",
                e.getEnquiryId(), e.getApplicantName(), e.getApplicantNric(), e.getProjectName(), e.getStatus());
            System.out.println("📣 " + e.getContent());
    
            if (!e.getReplies().isEmpty()) {
                System.out.println("💬 Replies:");
                for (EnquiryReply r : e.getReplies()) {
                    System.out.printf("   - [%s] %s: %s\n", r.getTimestamp(), r.getResponderRole(), r.getContent());
                }
            }
            System.out.println("────────────────────────────────────────────");
        }
    }
    
    private static void handleManagerEnquiries(HDBManager manager, Scanner sc) {
        List<Enquiry> all = EnquiryCsvMapper.loadAll();
    
        Set<String> managedProjects = ProjectCsvMapper.loadAll().stream()
            .filter(p -> p.getManager() != null && p.getManager().getNric().equalsIgnoreCase(manager.getNric()))
            .map(Project::getProjectName)
            .collect(Collectors.toSet());
    
        List<Enquiry> myEnquiries = all.stream()
            .filter(e -> managedProjects.contains(e.getProjectName()))
            .filter(e -> Enquiry.STATUS_PENDING.equalsIgnoreCase(e.getStatus()))
            .toList();
    
        if (myEnquiries.isEmpty()) {
            System.out.println("📭 No open enquiries for your projects.");
            return;
        }
    
        System.out.println("\n📬 Enquiries:");
        for (int i = 0; i < myEnquiries.size(); i++) {
            Enquiry e = myEnquiries.get(i);
            System.out.printf("[%d] %s (%s): %s\n", i + 1, e.getApplicantName(), e.getApplicantNric(), e.getContent());
        }
    
        System.out.print("Choose enquiry to reply (0 to cancel): ");
        try {
            int choice = Integer.parseInt(sc.nextLine());
            if (choice == 0) return;
            if (choice < 1 || choice > myEnquiries.size()) throw new IndexOutOfBoundsException();
    
            Enquiry selected = myEnquiries.get(choice - 1);
            System.out.print("Enter reply: ");
            String reply = sc.nextLine().trim();
    
            selected.addReply(reply, manager); // NEW REPLY HANDLING
            EnquiryCsvMapper.saveAll(all);
            System.out.println("✅ Reply sent and enquiry marked as CLOSED.");
    
        } catch (Exception e) {
            System.out.println("❌ Invalid selection.");
        }
    }

    private static void viewAndResolveFeedback(HDBManager manager, Scanner sc) {
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
