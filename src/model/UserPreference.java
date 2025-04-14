package src.model;

public class UserPreference {
    private int preferenceId;
    private String preferredFlatType;
    private double budget;
    private String preferredDistrict;
    private String additionalNotes;

    public void updatePreference(UserPreference pref) {
        this.preferredFlatType = pref.preferredFlatType;
        this.budget = pref.budget;
        this.preferredDistrict = pref.preferredDistrict;
        this.additionalNotes = pref.additionalNotes;
    }

    public String getPreferenceDetails() {
        return "Type: " + preferredFlatType + ", Budget: $" + budget + ", Area: " + preferredDistrict;
    }
}
