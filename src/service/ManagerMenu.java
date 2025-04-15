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

    private static void toggleVisibility(HDBManager manager, Scanner sc) {}
    private static void viewAllProjects() {}
    private static void viewMyProjects(HDBManager manager) {}
    private static void viewOfficerRegistrations(HDBManager manager) {}
    private static void handleOfficerApproval(HDBManager manager, Scanner sc) {}
    private static void viewApplicantApplications(HDBManager manager) {}
    private static void handleApplicantApproval(HDBManager manager, Scanner sc) {}
    private static void handleWithdrawalRequests(HDBManager manager, Scanner sc) {}
    private static void generateReports(HDBManager manager, Scanner sc) {}
}
