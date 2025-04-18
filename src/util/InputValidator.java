package src.util;

import java.util.Scanner;

public class InputValidator {

    private static final Scanner sc = new Scanner(System.in); // Shared static scanner

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

    public static int getPositiveInt(String prompt) {
        return getIntInRange(prompt, 1, Integer.MAX_VALUE);
    }

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

    public static String getNonEmptyString(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = sc.nextLine().trim();
            if (!input.isEmpty()) return input;
            System.out.println("❌ Input cannot be empty.");
        }
    }
}
