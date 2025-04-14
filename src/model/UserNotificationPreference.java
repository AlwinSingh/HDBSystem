package src.model;

import java.util.List;
import java.util.ArrayList;

public class UserNotificationPreference {
    private int preferenceId;
    private List<String> preferredTypes; // e.g., ["Booking", "Enquiry", "Policy"]
    private String frequency; // e.g., "Daily", "Weekly", "Instant"

    public UserNotificationPreference(int preferenceId) {
        this.preferenceId = preferenceId;
        this.preferredTypes = new ArrayList<>();
        this.frequency = "Instant";
    }

    public void updatePreferences(List<String> newTypes, String newFrequency) {
        this.preferredTypes = newTypes;
        this.frequency = newFrequency;
    }

    public String getPreferences() {
        return "Types: " + String.join(", ", preferredTypes) + "; Frequency: " + frequency;
    }

    // Getters and Setters
}

