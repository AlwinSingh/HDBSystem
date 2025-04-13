package src;

import src.model.User;
import src.service.ProjectService;
import src.service.UserService;
import src.util.ConsoleUtils;
import src.util.InputValidator;

import java.util.ArrayList;
import java.util.List;

public class Main {
    private static UserService userService = null;
    private static ProjectService projectService = null;

    public static void main(String[] args) {
        userService = new UserService(); // Load from CSV....
        projectService = new ProjectService(userService);

        ConsoleUtils.clear();
        ConsoleUtils.slowPrint("===== Welcome to the BTO Management System =====", 60);
        ConsoleUtils.lineBreak();

        while (true) {
            String nric = InputValidator.getNonEmptyString("\nEnter NRIC (or type EXIT): ");
            if (nric.equalsIgnoreCase("EXIT")) break;

            String password = InputValidator.getNonEmptyString("Enter Password: ");

            User user = null;
            user = userService.authenticateUser(nric, password); // Authenticate against CSV records that were loaded in...

            if (user != null) {
                ConsoleUtils.clear();
                System.out.println("Login successful. Welcome, " + user.getClass().getSimpleName() + " " + user.getName() + "!");
                ConsoleUtils.lineBreak();
                user.showMenu(projectService, userService); // ConsoleUtils us used inside menus too
                ConsoleUtils.clear(); // Clear screen after logout
            } else {
                System.out.println("❌ Invalid NRIC or Password.");
            }
        }

        ConsoleUtils.slowPrint("System shutting down. Goodbye!", 20);
    }
}
