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

    /**
     * Constructs a PaymentMethod enum with a user-friendly label.
     *
     * @param label The display name for this payment method.
     */
    PaymentMethod(String label) {
        this.label = label;
    }

    /**
     * Returns the human-readable label associated with the payment method.
     *
     * @return The label for this payment method (e.g., "PayNow").
     */
    @Override
    public String toString() {
        return label;
    }

}
