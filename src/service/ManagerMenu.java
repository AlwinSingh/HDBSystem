package src.service;

import src.model.HDBManager;
import src.util.CsvUtil;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ManagerMenu {

    public static void show(HDBManager manager) {
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n===== 🧱 HDB Manager Dashboard =====");
            System.out.println("Welcome, Manager " + manager.getName());
            System.out.println("\n🏗 Project Management");
            System.out.println("1. Create a new project");
            System.out.println("2. Edit a project");
            System.out.println("3. Delete a project");
            System.out.println("4. Toggle project visibility");

            System.out.println("\n📋 Project Viewing");
            System.out.println("5. View all projects");
            System.out.println("6. View my projects");

            System.out.println("\n🧑‍💼 Officer Registration");
            System.out.println("7. View officer registrations");
            System.out.println("8. Approve/reject officer registration");

            System.out.println("\n🧑‍💻 Applicant Applications");
            System.out.println("9. View applicant applications");
            System.out.println("10. Approve/reject applicant applications");
            System.out.println("11. Approve/reject withdrawal requests");

            System.out.println("\n📈 Reporting");
            System.out.println("12. Generate applicant booking reports");

            System.out.println("\n0. Logout");
            System.out.print("Enter your choice: ");

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
                case "9" -> viewApplicantApplications(manager);
                case "10" -> handleApplicantApproval(manager, sc);
                case "11" -> handleWithdrawalRequests(manager, sc);
                case "12" -> generateReports(manager, sc);
                case "0" -> {
                    System.out.println("👋 Logging out...");
                    return;
                }
                default -> System.out.println("❌ Invalid input. Try again.");
            }
        }
    }

    private static void createProject(HDBManager manager, Scanner sc) {
        System.out.println("\n📌 Create New Project");
    
        System.out.print("Enter project name: ");
        String name = sc.nextLine().trim();
        if (name.isBlank()) {
            System.out.println("❌ Project name cannot be empty.");
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
            System.out.print("Enter number of 2-Room units: ");
            units2 = Integer.parseInt(sc.nextLine());
            if (units2 <= 0) throw new IllegalArgumentException();
    
            System.out.print("Enter number of 3-Room units: ");
            units3 = Integer.parseInt(sc.nextLine());
            if (units3 <= 0) throw new IllegalArgumentException();
    
            System.out.print("Enter selling price for 2-Room: ");
            price2 = Double.parseDouble(sc.nextLine());
    
            System.out.print("Enter selling price for 3-Room: ");
            price3 = Double.parseDouble(sc.nextLine());
    
            System.out.print("Enter number of officer slots (max 10): ");
            officerSlots = Integer.parseInt(sc.nextLine());
            if (officerSlots <= 0 || officerSlots > 10) {
                System.out.println("❌ Officer slots must be between 1 and 10.");
                return;
            }
    
        } catch (Exception e) {
            System.out.println("❌ Invalid numeric input.");
            return;
        }
    
        System.out.print("Enter application opening date (M/d/yyyy): ");
        String openDateStr = sc.nextLine();
        System.out.print("Enter application closing date (M/d/yyyy): ");
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
    
        List<Map<String, String>> all = CsvUtil.read("data/ProjectList.csv");
    
        Map<String, String> row = new LinkedHashMap<>();
        row.put("Project Name", name);
        row.put("Neighborhood", neighborhood);
        row.put("Number of units for Type 1", String.valueOf(units2));
        row.put("Number of units for Type 2", String.valueOf(units3));
        row.put("Selling price for Type 1", String.valueOf(price2));
        row.put("Selling price for Type 2", String.valueOf(price3));
        row.put("Application opening date", openDateStr);
        row.put("Application closing date", closeDateStr);
        row.put("Officer Slot", String.valueOf(officerSlots));
        row.put("Visibility", "true");
        row.put("Manager", manager.getName());
        row.put("ManagerNRIC", manager.getNric());

    
        all.add(row);
        CsvUtil.write("data/ProjectList.csv", all);
    
        System.out.println("✅ Project created and saved successfully!");
    }
    

    private static void editProject(HDBManager manager, Scanner sc) {
        List<Map<String, String>> projects = CsvUtil.read("data/ProjectList.csv");
        List<Map<String, String>> myProjects = new ArrayList<>();
    
        for (Map<String, String> row : projects) {
            if (manager.getNric().equalsIgnoreCase(row.get("ManagerNRIC"))) {
                myProjects.add(row);
            }
        }
    
        if (myProjects.isEmpty()) {
            System.out.println("❌ You have no projects to edit.");
            return;
        }
    
        System.out.println("\n🛠 Your Projects:");
        for (int i = 0; i < myProjects.size(); i++) {
            System.out.printf("[%d] %s (%s)\n", i + 1,
                    myProjects.get(i).get("Project Name"),
                    myProjects.get(i).get("Neighborhood"));
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
    
        Map<String, String> project = myProjects.get(index);
    
        System.out.println("Leave field blank to keep current value.");
    
        System.out.print("New neighborhood [" + project.get("Neighborhood") + "]: ");
        String input = sc.nextLine();
        if (!input.isBlank()) project.put("Neighborhood", input);
    
        try {
            System.out.print("New 2-Room units [" + project.get("Number of units for Type 1") + "]: ");
            input = sc.nextLine();
            if (!input.isBlank() && Integer.parseInt(input) > 0)
                project.put("Number of units for Type 1", input);
    
            System.out.print("New 3-Room units [" + project.get("Number of units for Type 2") + "]: ");
            input = sc.nextLine();
            if (!input.isBlank() && Integer.parseInt(input) > 0)
                project.put("Number of units for Type 2", input);
    
            System.out.print("New price for 2-Room [" + project.get("Selling price for Type 1") + "]: ");
            input = sc.nextLine();
            if (!input.isBlank() && Double.parseDouble(input) >= 0)
                project.put("Selling price for Type 1", input);
    
            System.out.print("New price for 3-Room [" + project.get("Selling price for Type 2") + "]: ");
            input = sc.nextLine();
            if (!input.isBlank() && Double.parseDouble(input) >= 0)
                project.put("Selling price for Type 2", input);
    
            System.out.print("New opening date [" + project.get("Application opening date") + "]: ");
            String openDate = sc.nextLine();
            System.out.print("New closing date [" + project.get("Application closing date") + "]: ");
            String closeDate = sc.nextLine();
            if (!openDate.isBlank() && !closeDate.isBlank()) {
                DateTimeFormatter fmt = DateTimeFormatter.ofPattern("M/d/yyyy");
                LocalDate o = LocalDate.parse(openDate, fmt);
                LocalDate c = LocalDate.parse(closeDate, fmt);
                if (o.isBefore(c)) {
                    project.put("Application opening date", openDate);
                    project.put("Application closing date", closeDate);
                } else {
                    System.out.println("⚠️ Opening date must be before closing date. Keeping existing values.");
                }
            }
    
            System.out.print("New officer slots [" + project.get("Officer Slot") + "]: ");
            input = sc.nextLine();
            if (!input.isBlank()) {
                int slots = Integer.parseInt(input);
                if (slots > 0 && slots <= 10)
                    project.put("Officer Slot", input);
                else
                    System.out.println("⚠️ Officer slots must be between 1 and 10. Keeping existing value.");
            }
    
            CsvUtil.write("data/ProjectList.csv", projects);
            System.out.println("✅ Project updated successfully.");
        } catch (Exception e) {
            System.out.println("❌ Invalid input. Edit aborted.");
        }
    }

    private static void deleteProject(HDBManager manager, Scanner sc) {
        List<Map<String, String>> allProjects = CsvUtil.read("data/ProjectList.csv");
        List<Map<String, String>> myProjects = new ArrayList<>();

        for (Map<String, String> row : allProjects) {
            if (manager.getNric().equalsIgnoreCase(row.getOrDefault("ManagerNRIC", ""))) {
                myProjects.add(row);
            }
        }

        if (myProjects.isEmpty()) {
            System.out.println("❌ You have no projects to delete.");
            return;
        }

        System.out.println("\n🗑 Your Projects:");
        for (int i = 0; i < myProjects.size(); i++) {
            System.out.printf("[%d] %s (%s)\n", i + 1,
                    myProjects.get(i).get("Project Name"),
                    myProjects.get(i).get("Neighborhood"));
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

        Map<String, String> projectToDelete = myProjects.get(index);

        // Check for assigned officers or applicants
        String officers = projectToDelete.getOrDefault("OfficerNRICs", "").trim();
        String applicants = projectToDelete.getOrDefault("ApplicantNRICs", "").trim();
        if (!officers.isEmpty() || !applicants.isEmpty()) {
            System.out.println("⚠️ Project cannot be deleted because it has assigned officers or applicants.");
            System.out.println("🛑 Please remove those associations before deletion.");
            return;
        }

        System.out.print("Are you sure you want to delete this project? (Y/N): ");
        String confirm = sc.nextLine().trim();
        if (!confirm.equalsIgnoreCase("Y")) {
            System.out.println("🔙 Deletion cancelled.");
            return;
        }

        // Remove the project from the full list
        allProjects.remove(projectToDelete);
        CsvUtil.write("data/ProjectList.csv", allProjects);
        System.out.println("✅ Project deleted successfully.");
    }

    private static void toggleVisibility(HDBManager manager, Scanner sc) {
        List<Map<String, String>> allProjects = CsvUtil.read("data/ProjectList.csv");
        List<Map<String, String>> myProjects = new ArrayList<>();
    
        for (Map<String, String> row : allProjects) {
            if (manager.getNric().equalsIgnoreCase(row.getOrDefault("ManagerNRIC", ""))) {
                myProjects.add(row);
            }
        }
    
        if (myProjects.isEmpty()) {
            System.out.println("❌ You have no projects to toggle visibility.");
            return;
        }
    
        System.out.println("\n🔁 Your Projects:");
        for (int i = 0; i < myProjects.size(); i++) {
            String projectName = myProjects.get(i).get("Project Name");
            String vis = myProjects.get(i).get("Visibility");
            System.out.printf("[%d] %s - Visibility: %s\n", i + 1, projectName, vis);
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
    
        Map<String, String> selectedProject = myProjects.get(index);
        String currentVis = selectedProject.getOrDefault("Visibility", "true").trim().toLowerCase();
        String newVis = currentVis.equals("true") ? "false" : "true";
    
        System.out.printf("Are you sure you want to change visibility from %s to %s? (Y/N): ",
                currentVis, newVis);
        String confirm = sc.nextLine().trim();
        if (!confirm.equalsIgnoreCase("Y")) {
            System.out.println("🔙 Toggle cancelled.");
            return;
        }
    
        selectedProject.put("Visibility", newVis);
        CsvUtil.write("data/ProjectList.csv", allProjects);
        System.out.println("✅ Visibility updated. New visibility: " + newVis);
    }
    
    private static void viewAllProjects() {
        List<Map<String, String>> projects = CsvUtil.read("data/ProjectList.csv");
    
        if (projects.isEmpty()) {
            System.out.println("❌ No projects found.");
            return;
        }
    
        System.out.println("\n📋 All Projects in the System:");
        int count = 1;
        for (Map<String, String> row : projects) {
            System.out.printf("\n[%d] 📌 %s (%s)\n", count++, row.get("Project Name"), row.get("Neighborhood"));
            System.out.println("   🛏️ 2-Room Units: " + row.get("Number of units for Type 1"));
            System.out.println("   🛏️ 3-Room Units: " + row.get("Number of units for Type 2"));
            System.out.println("   💰 Price 2-Room: $" + row.get("Selling price for Type 1"));
            System.out.println("   💰 Price 3-Room: $" + row.get("Selling price for Type 2"));
            System.out.println("   📅 Application Period: " + row.get("Application opening date") + " to " + row.get("Application closing date"));
            System.out.println("   👨‍💼 Manager: " + row.get("Manager"));
            System.out.println("   🧍 Officer Slots: " + row.get("Officer Slot"));
            System.out.println("   👀 Visible to public: " + row.get("Visibility"));
        }
    }
    
    private static void viewMyProjects(HDBManager manager) {
        List<Map<String, String>> projects = CsvUtil.read("data/ProjectList.csv");
        String managerNRIC = manager.getNric();
    
        List<Map<String, String>> myProjects = new ArrayList<>();
        for (Map<String, String> row : projects) {
            if (managerNRIC.equalsIgnoreCase(row.get("ManagerNRIC"))) {
                myProjects.add(row);
            }
        }
    
        if (myProjects.isEmpty()) {
            System.out.println("❌ You haven't created any projects yet.");
            return;
        }
    
        System.out.println("\n📋 Your Projects:");
        int count = 1;
        for (Map<String, String> row : myProjects) {
            System.out.printf("\n[%d] 📌 %s (%s)\n", count++, row.get("Project Name"), row.get("Neighborhood"));
            System.out.println("   🛏️ 2-Room Units: " + row.get("Number of units for Type 1"));
            System.out.println("   🛏️ 3-Room Units: " + row.get("Number of units for Type 2"));
            System.out.println("   💰 Price 2-Room: $" + row.get("Selling price for Type 1"));
            System.out.println("   💰 Price 3-Room: $" + row.get("Selling price for Type 2"));
            System.out.println("   📅 Application Period: " + row.get("Application opening date") + " to " + row.get("Application closing date"));
            System.out.println("   👀 Visibility: " + row.get("Visibility"));
            System.out.println("   🧍 Officer Slots: " + row.get("Officer Slot"));
        }
    }
    
    private static void viewOfficerRegistrations(HDBManager manager) {
        List<Map<String, String>> officers = CsvUtil.read("data/OfficerList.csv");
        List<Map<String, String>> projects = CsvUtil.read("data/ProjectList.csv");
    
        String managerNRIC = manager.getNric();
        Set<String> myProjects = new HashSet<>();
    
        for (Map<String, String> project : projects) {
            if (managerNRIC.equalsIgnoreCase(project.getOrDefault("ManagerNRIC", "").trim())) {
                myProjects.add(project.getOrDefault("Project Name", "").trim());
            }
        }
    
        List<Map<String, String>> relevantOfficers = new ArrayList<>();
    
        for (Map<String, String> officer : officers) {
            String project = officer.getOrDefault("AssignedProject", "").trim();
            String status = officer.getOrDefault("RegistrationStatus", "").trim();
    
            if (!project.isEmpty() && myProjects.contains(project) && !status.isEmpty()) {
                relevantOfficers.add(officer);
            }
        }
    
        if (relevantOfficers.isEmpty()) {
            System.out.println("❌ No officer registrations found for your projects.");
            return;
        }
    
        System.out.println("\n🧾 Officer Registrations for Your Projects:");
        for (Map<String, String> row : relevantOfficers) {
            System.out.println("👤 " + row.get("Name") + " (NRIC: " + row.get("NRIC") + ")");
            System.out.println("   📌 Project: " + row.get("AssignedProject"));
            System.out.println("   📊 Status: " + row.get("RegistrationStatus"));
            System.out.println();
        }
    }
    
    
    private static void handleOfficerApproval(HDBManager manager, Scanner sc) {
        List<Map<String, String>> projectList = CsvUtil.read("data/ProjectList.csv");
        List<Map<String, String>> officerList = CsvUtil.read("data/OfficerList.csv");
    
        List<Map<String, String>> pendingOfficers = new ArrayList<>();
        for (Map<String, String> row : officerList) {
            String status = row.getOrDefault("RegistrationStatus", "");
            if ("PENDING".equalsIgnoreCase(status)) {
                pendingOfficers.add(row);
            }
        }
    
        if (pendingOfficers.isEmpty()) {
            System.out.println("📭 No pending officer registrations.");
            return;
        }
    
        System.out.println("\n📋 Pending Officer Registrations:");
        for (int i = 0; i < pendingOfficers.size(); i++) {
            Map<String, String> officer = pendingOfficers.get(i);
            System.out.printf("[%d] %s (%s), Project: %s\n", i + 1,
                    officer.get("Name"), officer.get("NRIC"), officer.get("AssignedProject"));
        }
    
        System.out.print("Select officer to process (or 0 to cancel): ");
        int choice;
        try {
            choice = Integer.parseInt(sc.nextLine());
            if (choice == 0) return;
            if (choice < 1 || choice > pendingOfficers.size()) throw new Exception();
        } catch (Exception e) {
            System.out.println("❌ Invalid choice.");
            return;
        }
    
        Map<String, String> selectedOfficer = pendingOfficers.get(choice - 1);
        String officerNRIC = selectedOfficer.get("NRIC");
        String officerName = selectedOfficer.get("Name");
        String assignedProject = selectedOfficer.get("AssignedProject");
    
        // Locate project row
        Map<String, String> projectToUpdate = null;
        for (Map<String, String> project : projectList) {
            if (project.get("Project Name").equalsIgnoreCase(assignedProject)
                    && manager.getNric().equalsIgnoreCase(project.get("ManagerNRIC"))) {
                projectToUpdate = project;
                break;
            }
        }
    
        if (projectToUpdate == null) {
            System.out.println("❌ You are not the assigned manager for the selected officer’s project.");
            return;
        }
    
        System.out.print("Approve or Reject? (A/R): ");
        String decision = sc.nextLine().trim().toUpperCase();
    
        if (decision.equals("A")) {
            System.out.print("Confirm approval for Officer " + officerName + " (Y/N): ");
            if (!sc.nextLine().trim().equalsIgnoreCase("Y")) {
                System.out.println("❌ Approval cancelled.");
                return;
            }
    
            int slots = Integer.parseInt(projectToUpdate.getOrDefault("Officer Slot", "0"));
            if (slots <= 0) {
                System.out.println("❌ No officer slots remaining.");
                return;
            }
    
            // Update slots
            projectToUpdate.put("Officer Slot", String.valueOf(slots - 1));
    
            // Update OfficerNRICs (space-separated, no duplicates)
            String nricField = projectToUpdate.getOrDefault("OfficerNRICs", "").trim();
            Set<String> nricSet = new LinkedHashSet<>(Arrays.asList(nricField.split("\\s+")));
            nricSet.add(officerNRIC);
            nricSet.remove("");
            projectToUpdate.put("OfficerNRICs", String.join(" ", nricSet));
    
            // Update Officer names
            String nameField = projectToUpdate.getOrDefault("Officer", "").trim();
            Set<String> nameSet = new LinkedHashSet<>(Arrays.asList(nameField.split("\\s+")));
            nameSet.add(officerName);
            nameSet.remove("");
            projectToUpdate.put("Officer", String.join(" ", nameSet));
    
            selectedOfficer.put("RegistrationStatus", "APPROVED");
            System.out.println("✅ Officer approved and added to project.");
    
        } else if (decision.equals("R")) {
            System.out.print("Confirm rejection for Officer " + officerName + " (Y/N): ");
            if (!sc.nextLine().trim().equalsIgnoreCase("Y")) {
                System.out.println("❌ Rejection cancelled.");
                return;
            }
    
            selectedOfficer.put("RegistrationStatus", "REJECTED");
            selectedOfficer.put("AssignedProject", "");
            System.out.println("❌ Officer registration rejected.");
        } else {
            System.out.println("❌ Invalid input. Only A or R allowed.");
            return;
        }
    
        CsvUtil.write("data/ProjectList.csv", projectList);
        CsvUtil.write("data/OfficerList.csv", officerList);
    }
    
    private static void viewApplicantApplications(HDBManager manager) {
        List<Map<String, String>> applicants = CsvUtil.read("data/ApplicantList.csv");
        List<Map<String, String>> projects = CsvUtil.read("data/ProjectList.csv");
    
        String managerNRIC = manager.getNric();
        List<Map<String, String>> myProjects = new ArrayList<>();
    
        // Get all projects created by this manager
        for (Map<String, String> p : projects) {
            if (managerNRIC.equalsIgnoreCase(p.getOrDefault("ManagerNRIC", ""))) {
                myProjects.add(p);
            }
        }
    
        if (myProjects.isEmpty()) {
            System.out.println("❌ You haven’t created any projects yet.");
            return;
        }
    
        List<Map<String, String>> relevantApplicants = new ArrayList<>();
        Set<String> myProjectNames = new HashSet<>();
        for (Map<String, String> p : myProjects) {
            myProjectNames.add(p.get("Project Name"));
        }
    
        for (Map<String, String> a : applicants) {
            String appliedProject = a.getOrDefault("AppliedProjectName", "");
            if (myProjectNames.contains(appliedProject)) {
                relevantApplicants.add(a);
            }
        }
    
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
        List<Map<String, String>> applicantList = CsvUtil.read("data/ApplicantList.csv");
        List<Map<String, String>> projectList = CsvUtil.read("data/ProjectList.csv");
    
        // Filter applicants who applied to manager's projects and are still PENDING
        List<Map<String, String>> pendingApps = new ArrayList<>();
        Set<String> managerProjects = new HashSet<>();
        for (Map<String, String> project : projectList) {
            if (manager.getNric().equalsIgnoreCase(project.getOrDefault("ManagerNRIC", ""))) {
                managerProjects.add(project.get("Project Name"));
            }
        }
    
        for (Map<String, String> app : applicantList) {
            String status = app.getOrDefault("ApplicationStatus", "").trim();
            String appliedProject = app.getOrDefault("AppliedProjectName", "").trim();
            if ("PENDING".equalsIgnoreCase(status) && managerProjects.contains(appliedProject)) {
                pendingApps.add(app);
            }
        }
    
        if (pendingApps.isEmpty()) {
            System.out.println("📭 No pending applicant applications for your projects.");
            return;
        }
    
        System.out.println("\n📋 Pending Applicant Applications:");
        for (int i = 0; i < pendingApps.size(); i++) {
            Map<String, String> app = pendingApps.get(i);
            System.out.printf("[%d] %s (NRIC: %s)\n", i + 1, app.get("Name"), app.get("NRIC"));
            System.out.println("   🏘 Project: " + app.get("AppliedProjectName"));
            System.out.println("   🏠 Flat Type: " + app.get("FlatTypeApplied"));
            System.out.println("   🎂 Age: " + app.get("Age"));
            System.out.println("   💍 Marital Status: " + app.get("Marital Status"));
        }
    
        System.out.print("Select applicant to process (or 0 to cancel): ");
        int choice;
        try {
            choice = Integer.parseInt(sc.nextLine());
            if (choice == 0) return;
            if (choice < 1 || choice > pendingApps.size()) throw new Exception();
        } catch (Exception e) {
            System.out.println("❌ Invalid selection.");
            return;
        }
    
        Map<String, String> selectedApp = pendingApps.get(choice - 1);
        String projectName = selectedApp.get("AppliedProjectName");
        String flatType = selectedApp.get("FlatTypeApplied");
    
        // Locate project for this application
        Map<String, String> projectToUpdate = null;
        for (Map<String, String> proj : projectList) {
            if (projectName.equalsIgnoreCase(proj.get("Project Name"))) {
                projectToUpdate = proj;
                break;
            }
        }
    
        if (projectToUpdate == null) {
            System.out.println("❌ Project not found.");
            return;
        }
    
        System.out.print("Approve or Reject this application? (A/R): ");
        String decision = sc.nextLine().trim().toUpperCase();
       
    
        if (decision.equals("A")) {

            System.out.print("Confirm approval for " + selectedApp.get("Name") + " (Y/N): ");
            if (!sc.nextLine().trim().equalsIgnoreCase("Y")) {
                System.out.println("❌ Action cancelled.");
                return;
            }

            // Check flat availability
            String unitKey = flatType.equalsIgnoreCase("2-Room")
                    ? "Number of units for Type 1"
                    : "Number of units for Type 2";
    
            int available = Integer.parseInt(projectToUpdate.get(unitKey));
            if (available <= 0) {
                System.out.println("❌ No more units available for this flat type.");
                return;
            }
    
            // Decrement unit count and approve
            projectToUpdate.put(unitKey, String.valueOf(available - 1));
            selectedApp.put("ApplicationStatus", "SUCCESSFUL");
            System.out.println("✅ Application approved and flat reserved.");
    
        } else if (decision.equals("R")) {
            System.out.print("Confirm rejection for " + selectedApp.get("Name") + " (Y/N): ");
            if (!sc.nextLine().trim().equalsIgnoreCase("Y")) {
                System.out.println("❌ Rejection cancelled.");
                return;
            }
            selectedApp.put("ApplicationStatus", "UNSUCCESSFUL");
            System.out.println("❌ Application rejected.");
        } else {
            System.out.println("❌ Invalid input. Only A or R allowed.");
            return;
        }
    
        // Save updates
        CsvUtil.write("data/ProjectList.csv", projectList);
        CsvUtil.write("data/ApplicantList.csv", applicantList);
    }
    
    private static void handleWithdrawalRequests(HDBManager manager, Scanner sc) {
        List<Map<String, String>> applicants = CsvUtil.read("data/ApplicantList.csv");
        List<Map<String, String>> projects = CsvUtil.read("data/ProjectList.csv");
    
        String managerNRIC = manager.getNric();
        Set<String> myProjectNames = new HashSet<>();
    
        // Get projects managed by this manager
        for (Map<String, String> p : projects) {
            if (managerNRIC.equalsIgnoreCase(p.getOrDefault("ManagerNRIC", ""))) {
                myProjectNames.add(p.get("Project Name"));
            }
        }
    
        // Filter withdrawal requests (excluding BOOKED)
        List<Map<String, String>> withdrawalRequests = new ArrayList<>();
        for (Map<String, String> app : applicants) {
            String status = app.getOrDefault("ApplicationStatus", "").trim().toUpperCase();
            String projectName = app.getOrDefault("AppliedProjectName", "").trim();
    
            if ("WITHDRAWAL_REQUESTED".equals(status) && myProjectNames.contains(projectName)) {
                withdrawalRequests.add(app);
            }
        }
    
        if (withdrawalRequests.isEmpty()) {
            System.out.println("📭 No pending withdrawal requests for your projects.");
            return;
        }
    
        System.out.println("\n📤 Withdrawal Requests:");
        for (int i = 0; i < withdrawalRequests.size(); i++) {
            Map<String, String> app = withdrawalRequests.get(i);
            System.out.printf("[%d] %s (NRIC: %s) — Project: %s — Flat Type: %s\n",
                    i + 1,
                    app.get("Name"),
                    app.get("NRIC"),
                    app.get("AppliedProjectName"),
                    app.get("FlatTypeApplied"));
        }
    
        System.out.print("Select applicant to process (0 to cancel): ");
        int choice;
        try {
            choice = Integer.parseInt(sc.nextLine());
            if (choice == 0) return;
            if (choice < 1 || choice > withdrawalRequests.size()) throw new Exception();
        } catch (Exception e) {
            System.out.println("❌ Invalid selection.");
            return;
        }
    
        Map<String, String> selected = withdrawalRequests.get(choice - 1);
        System.out.print("Approve or Reject withdrawal? (A/R): ");
        String decision = sc.nextLine().trim().toUpperCase();
    
        if (decision.equals("A")) {
            selected.put("ApplicationStatus", "WITHDRAWN");
            System.out.println("✅ Withdrawal approved.");
        } else if (decision.equals("R")) {
            selected.put("ApplicationStatus", "REJECTED");
            System.out.println("❌ Withdrawal request rejected.");
        } else {
            System.out.println("❌ Invalid choice. Use A or R.");
            return;
        }
    
        CsvUtil.write("data/ApplicantList.csv", applicants);
    }
    

    private static void generateReports(HDBManager manager, Scanner sc) {
        List<Map<String, String>> applicants = CsvUtil.read("data/ApplicantList.csv");
        List<Map<String, String>> projects = CsvUtil.read("data/ProjectList.csv");

        Set<String> myProjects = new HashSet<>();
        for (Map<String, String> p : projects) {
            if (manager.getNric().equalsIgnoreCase(p.get("ManagerNRIC"))) {
                myProjects.add(p.get("Project Name"));
            }
        }

        if (myProjects.isEmpty()) {
            System.out.println("❌ You haven’t created any projects yet.");
            return;
        }

        System.out.println("\n📑 Generate Report By:");
        System.out.println("1. All Applicants");
        System.out.println("2. Married Applicants");
        System.out.println("3. By Flat Type");
        System.out.print("Choose filter: ");
        String filter = sc.nextLine().trim();

        System.out.println("\n📄 Applicant Booking Report:");
        for (Map<String, String> a : applicants) {
            String proj = a.get("AppliedProjectName");
            String status = a.get("ApplicationStatus");
            if (!myProjects.contains(proj) || !"BOOKED".equalsIgnoreCase(status)) continue;

            boolean match = switch (filter) {
                case "1" -> true;
                case "2" -> "Married".equalsIgnoreCase(a.get("Marital Status"));
                case "3" -> {
                    System.out.print("Enter flat type (2-Room/3-Room): ");
                    String ft = sc.nextLine().trim();
                    yield ft.equalsIgnoreCase(a.get("FlatTypeApplied"));
                }
                default -> {
                    System.out.println("❌ Invalid filter.");
                    yield false;
                }
            };

            if (match) {
                System.out.println("👤 " + a.get("Name") + " (NRIC: " + a.get("NRIC") + ")");
                System.out.println("   🏘 Project: " + a.get("AppliedProjectName"));
                System.out.println("   🏠 Flat Type: " + a.get("FlatTypeApplied"));
                System.out.println("   🎂 Age: " + a.get("Age") + ", 💍 Marital Status: " + a.get("Marital Status"));
                System.out.println();
            }
        }
    }
}
