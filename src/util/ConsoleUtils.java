package src.util;

import java.util.Scanner;

/**
 * Utility class for CLI-related actions (clearing screen, pausing, etc)
 */
public class ConsoleUtils {

    /**
     * Clears the console screen by printing several empty lines.
     * Works reliably in IDEs and CLI terminals.
     * The other alternative which is clear2() works best in Native Command Prompts, so it isn't used in this app but i (Alwin) have included it for future-proofing
     */
    public static void clear() {
        for (int i = 0; i < 30; i++) {
            System.out.println();
        }
    }

    /* CURRENTLY NOT USED IN THE PROJECT AT ALL , THUS SET TO PACKAGE-PRIVATE */
    protected static void clear2() {
        try {
            if (System.getProperty("os.name").contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (Exception e) {
            System.out.println("Could not clear console.");
        }
    }

    /**
     * Pauses the console until the user presses Enter.
     */
    public static void pause() {
        System.out.println("\nPress Enter to continue...");
        new Scanner(System.in).nextLine();
    }

    /**
     * Prints a horizontal divider line.
     */
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
