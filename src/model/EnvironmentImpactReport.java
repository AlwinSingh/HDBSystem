package src.model;

import java.time.LocalDate;

public class EnvironmentImpactReport {
    private int reportId;
    private String impactDetails;
    private LocalDate dateGenerated;

    public String generateImpactReport() {
        return "Environmental Impact: " + impactDetails + " (Generated on: " + dateGenerated + ")";
    }
}
