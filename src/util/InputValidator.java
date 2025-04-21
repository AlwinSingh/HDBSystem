package src.util;

import java.util.Scanner;

public class InputValidator {

    private static final Scanner sc = new Scanner(System.in); // Shared static scanner

    /**
     * Prompts the user until a valid integer within the specified range is entered.
     *
     * @param prompt Message to display.
     * @param min    Minimum acceptable value.
     * @param max    Maximum acceptable value.
     * @return Validated integer within range.
     */
    public static int getIntInRange(String prompt, int min, int max) {
        int choice;

        while (true) {
            System.out.print(prompt);
            try {
                choice = Integer.parseInt(sc.nextLine().trim());
                if (choice >= min && choice <= max) {
                    return choice;
                } else {
                    System.out.println("❌ Please enter a number between " + min + " and " + max + ".");
                }
            } catch (NumberFormatException e) {
                System.out.println("❌ Invalid input. Please enter a valid number.");
            }
        }
    }

    /**
     * Prompts for a positive integer (greater than 0).
     *
     * @param prompt Message to display.
     * @return Validated positive integer.
     */
    public static int getPositiveInt(String prompt) {
        return getIntInRange(prompt, 1, Integer.MAX_VALUE);
    }

    /**
     * Prompts for a positive double (greater than 0).
     *
     * @param prompt Message to display.
     * @return Validated positive double.
     */
    public static double getPositiveDouble(String prompt) {
        double value;

        while (true) {
            System.out.print(prompt);
            try {
                value = Double.parseDouble(sc.nextLine().trim());
                if (value > 0) return value;
                System.out.println("❌ Please enter a positive number.");
            } catch (NumberFormatException e) {
                System.out.println("❌ Invalid input. Please enter a valid number.");
            }
        }
    }

    /**
     * Prompts the user until a non-empty string is entered.
     *
     * @param prompt Message to display.
     * @return Non-empty trimmed string.
     */
    public static String getNonEmptyString(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = sc.nextLine().trim();
            if (!input.isEmpty()) return input;
            System.out.println("❌ Input cannot be empty.");
        }
    }

        /**
     * Prompts the user until they enter a valid double value.
     *
     * @param sc Scanner for input.
     * @return The parsed double value.
     */
    public static double getDoubleInput(Scanner sc) {
        while (true) {
            try {
                return Double.parseDouble(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.print("❌ Invalid number. Please enter again: ");
            }
        }
    }
}
