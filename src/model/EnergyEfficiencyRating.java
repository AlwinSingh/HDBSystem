package src.model;

import java.time.LocalDate;

public class EnergyEfficiencyRating {
    private int ratingId;
    private double ratingValue;
    private LocalDate assessmentDate;

    public double calculateRating() {
        return Math.round(Math.random() * 5.0 * 10) / 10.0; // Mock logic
    }

    public String getRatingReport() {
        return "Rating: " + ratingValue + " stars (Assessed: " + assessmentDate + ")";
    }
}

