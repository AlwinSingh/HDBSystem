package src.model;

import java.time.LocalDate;

public class HousingPolicy {
    private int policyId;
    private String description;
    private LocalDate effectiveDate;
    private String criteria;

    public String getPolicyDetails() {
        return description + " (Effective: " + effectiveDate + ")";
    }

    public void updatePolicy(String desc) {
        this.description = desc;
    }
}
