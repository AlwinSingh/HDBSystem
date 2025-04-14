package src.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class InputValidator {
    // Use one static scanner for the entire class to avoid resource leaks.
    private static final Scanner sc = new Scanner(System.in);
    private static final DateTimeFormatter[] dateFormats = new DateTimeFormatter[]{
            DateTimeFormatter.ofPattern("dd-MM-yyyy")
    };

    /**
     * Returns the internal Scanner instance to be reused by other utilities.
     */
    public static Scanner getScanner() {
        return sc;
    }

    public static int getInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = sc.nextLine();
            try {
                return Integer.parseInt(input.trim());
            } catch (NumberFormatException e) {
                System.out.println("❌ Please enter a valid integer.");
            }
        }
    }

    public static int getIntInRange(String prompt, int min, int max) {
        while (true) {
            int value = getInt(prompt);
            if (value >= min && value <= max) return value;
            System.out.printf("❌ Please enter a number between %d and %d.\n", min, max);
        }
    }

    public static double getDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = sc.nextLine();
            try {
                return Double.parseDouble(input.trim());
            } catch (NumberFormatException e) {
                System.out.println("❌ Please enter a valid number (e.g. 350000 or 499999.99).");
            }
        }
    }

    public static double getDoubleInRange(String prompt, double min, double max) {
        while (true) {
            double value = getDouble(prompt);
            if (value >= min && value <= max) return value;
            System.out.printf("❌ Please enter a value between %.2f and %.2f.\n", min, max);
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

    public static boolean getYesNo(String prompt) {
        while (true) {
            System.out.print(prompt + " (Y/N): ");
            String input = sc.nextLine().trim().toUpperCase();
            if (input.equals("Y")) return true;
            if (input.equals("N")) return false;
            System.out.println("❌ Please enter Y or N.");
        }
    }

    public static LocalDate getDate(String prompt) {
        while (true) {
            System.out.print(prompt + " (dd-MM-yyyy): ");
            String input = sc.nextLine().trim();
            for (DateTimeFormatter format : dateFormats) {
                try {
                    return LocalDate.parse(input, format);
                } catch (DateTimeParseException ignored) {}
            }
            System.out.println("❌ Invalid date format. Use dd-MM-yyyy.");
        }
    }
}
