package src.model;

public enum PaymentMethod {
    PAYNOW("PayNow"),
    CREDIT_CARD("Credit Card"),
    BANK_TRANSFER("Bank Transfer");

    private final String label;

    PaymentMethod(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }

    public static boolean isValid(String input) {
        for (PaymentMethod method : PaymentMethod.values()) {
            if (method.label.equalsIgnoreCase(input)) {
                return true;
            }
        }
        return false;
    }
}
