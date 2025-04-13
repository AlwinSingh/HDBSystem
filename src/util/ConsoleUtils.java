package src.util;

import java.util.Scanner;

/**
 * Utility class for CLI-related actions (clearing screen, pausing, etc)
 */
public class ConsoleUtils {

    /*
     * Clears the console screen by printing several empty lines
     * Works reliably in IDEs and CLI terminals
     */
    public static void clear() {
        for (int i = 0; i < 30; i++) {
            System.out.println();
        }
    }

    /* Pauses the console until the user presses Enter */
    public static void pause() {
        System.out.println("\nPress Enter to continue...");
        new Scanner(System.in).nextLine();
    }

    /* Prints a horizontal divider line */
    public static void lineBreak() {
        System.out.println("--------------------------------------------------");
    }

    /**
     * Slowly prints text character-by-character (for dramatic effect)
     * @param text The text to print
     * @param delayMillis Time between characters (in ms)
     */
    public static void slowPrint(String text, int delayMillis) {
        for (char ch : text.toCharArray()) {
            System.out.print(ch);
            try {
                // Alwin: This pauses the thread for x milliseconds, it isn't good if you need async nature for a background task, please create another thread...as main thread is blocked
                Thread.sleep(delayMillis);
            } catch (InterruptedException ignored) {}
        }
        System.out.println();
    }
}
