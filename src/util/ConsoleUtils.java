package src.util;

import java.util.Scanner;

/**
 * Utility class for CLI-related actions (clearing screen, pausing, etc).
 * Improvements: Reuses a static Scanner instance (shared with InputValidator) to avoid repeated object creation.
 */
public class ConsoleUtils {

    // Rather than create a new Scanner each time, we reuse the one from InputValidator.
    // Alternatively, you can define your own private static final Scanner here, if you prefer:
    // private static final Scanner scanner = new Scanner(System.in);

    /**
     * Clears the console screen by printing several empty lines.
     * Works reliably in IDEs and CLI terminals.
     */
    public static void clear() {
        // Print 30 empty lines.
        for (int i = 0; i < 30; i++) {
            System.out.println();
        }
    }

    /**
     * Pauses the console until the user presses Enter.
     */
    public static void pause() {
        System.out.println("\nPress Enter to continue...");
        // Reuse the static Scanner from InputValidator (or create your own as shown above).
        InputValidator.getScanner().nextLine();
    }

    /**
     * Prints a horizontal divider line.
     */
    public static void lineBreak() {
        System.out.println("--------------------------------------------------");
    }

    /**
     * Slowly prints text character-by-character (for dramatic effect).
     * @param text The text to print.
     * @param delayMillis Time between characters (in ms).
     */
    public static void slowPrint(String text, int delayMillis) {
        for (char ch : text.toCharArray()) {
            System.out.print(ch);
            try {
                Thread.sleep(delayMillis);
            } catch (InterruptedException ignored) {
                // Optionally: Restore the interrupted status, e.g.: Thread.currentThread().interrupt();
            }
        }
        System.out.println();
    }
}
