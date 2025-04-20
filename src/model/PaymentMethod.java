package src.model;

/**
 * Enum representing supported payment methods in the system.
 * Includes display labels and validation logic.
 */
public enum PaymentMethod {
    PAYNOW("PayNow"),
    CREDIT_CARD("Credit Card"),
    BANK_TRANSFER("Bank Transfer");

    private final String label;

    PaymentMethod(String label) {
        this.label = label;
    }

    /**
     * Returns the human-readable label for this payment method.
     */
    @Override
    public String toString() {
        return label;
    }

    /**
     * Checks if the given input matches any of the defined payment method labels.
     *
     * @param input The user input string.
     * @return True if input matches a valid method; false otherwise.
     */
    public static boolean isValid(String input) {
        for (PaymentMethod method : PaymentMethod.values()) {
            if (method.label.equalsIgnoreCase(input)) {
                return true;
            }
        }
        return false;
    }
}
