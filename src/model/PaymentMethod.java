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

}
