package src;

import src.model.Applicant;
import src.model.Officer;
import src.model.Manager;
import src.model.User;
import src.service.ProjectService;
import src.service.UserService;
import src.util.ConsoleUtils;
import src.util.InputValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static final boolean DEBUG_MODE = false;

    private static List<Applicant> applicants = new ArrayList<>();
    private static List<Officer> officers = new ArrayList<>();
    private static List<Manager> managers = new ArrayList<>();
    
    private static UserService userService = null;
    private static ProjectService projectService = null;

    public static void main(String[] args) {
        if (DEBUG_MODE) { // Load from program rather than CSV for testing purposes...
            preloadTestUsers(); // Simulate CSV loading
        } else {
            // Load from CSV...
            userService = new UserService(); // Load from CSV....
            projectService = new ProjectService(userService);
        }

        ConsoleUtils.clear();
        ConsoleUtils.slowPrint("===== Welcome to the BTO Management System =====", 60);
        ConsoleUtils.lineBreak();

        while (true) {
            String nric = InputValidator.getNonEmptyString("\nEnter NRIC (or type EXIT): ");
            if (nric.equalsIgnoreCase("EXIT")) break;

            String password = InputValidator.getNonEmptyString("Enter Password: ");

            User user = null;

            if (DEBUG_MODE) {
                user = authenticateTestUsers(nric, password); // Authenticate against internal dummy records that were loaded in by the program...
            } else {
                user = userService.authenticateUser(nric, password); // Authenticate against CSV records that were loaded in...
            }

            if (user != null) {
                ConsoleUtils.clear();
                System.out.println("Login successful. Welcome, " + user.getName() + "!");
                ConsoleUtils.lineBreak();
                user.showMenu(projectService, userService); // ConsoleUtils us used inside menus too
                ConsoleUtils.clear(); // Clear screen after logout
            } else {
                System.out.println("❌ Invalid NRIC or Password.");
            }
        }

        ConsoleUtils.slowPrint("System shutting down. Goodbye!", 20);
    }

    private static void preloadTestUsers() {
        applicants.add(new Applicant("S1111111A", "password", "Alice", 36, "Single", "", "", ""));
        officers.add(new Officer("T2222222B", "password", "Ben", 29, "Married"));
        managers.add(new Manager("S3333333C", "password", "Charlie", 40, "Married"));
    }

    private static User authenticateTestUsers(String nric, String password) {
        for (Officer o : officers) {
            if (o.getNric().equalsIgnoreCase(nric) && o.checkPassword(password)) {
                return o;
            }
        }

        for (Manager m : managers) {
            if (m.getNric().equalsIgnoreCase(nric) && m.checkPassword(password)) {
                return m;
            }
        }

        for (Applicant a : applicants) {
            if (a.getNric().equalsIgnoreCase(nric) && a.checkPassword(password)) {
                return a;
            }
        }

        return null;
    }
}
