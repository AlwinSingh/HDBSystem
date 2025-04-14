package src;

import src.model.User;
import src.service.ProjectService;
import src.service.UserService;
import src.util.ConsoleUtils;
import src.util.InputValidator;

public class Main {

    private static UserService userService;
    private static ProjectService projectService;

    public static void main(String[] args) {
        // Load users and projects from CSV files
        userService = new UserService();
        projectService = new ProjectService(userService);

        // Clear the screen and show a welcome banner
        ConsoleUtils.clear();
        showWelcomeBanner();

        // Main login loop
        while (true) {
            String nric = InputValidator.getNonEmptyString("\nEnter NRIC (or type EXIT to quit): ");
            if (nric.equalsIgnoreCase("EXIT")) {
                break;
            }
            String password = InputValidator.getNonEmptyString("Enter Password: ");

            User user = userService.authenticateUser(nric, password);
            if (user != null) {
                ConsoleUtils.clear();
                System.out.println("Login successful. Welcome, " 
                        + user.getClass().getSimpleName() + " " + user.getName() + "!");
                ConsoleUtils.lineBreak();
                user.showMenu(projectService, userService);
                ConsoleUtils.clear(); // Clear the screen after logout
            } else {
                System.out.println("❌ Invalid NRIC or Password. Please try again.");
            }
        }
        
        // Shutdown message
        ConsoleUtils.clear();
        ConsoleUtils.slowPrint("System shutting down. Goodbye!", 20);
    }
    
    /**
     * Displays a welcome banner with a little flair.
     */
    private static void showWelcomeBanner() {
        String banner =
                "\n**************************************************\n" +
                "*                                                *\n" +
                "*       Welcome to the BTO Management System     *\n" +
                "*                                                *\n" +
                "**************************************************\n";
        // SlowPrint prints each character with a delay (adjust delay as needed)
        ConsoleUtils.slowPrint(banner, 5);
        ConsoleUtils.lineBreak();
    }
}
