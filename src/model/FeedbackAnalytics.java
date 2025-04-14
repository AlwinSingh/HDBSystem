package src.model;

import java.util.List;
import java.util.ArrayList;


public class FeedbackAnalytics {
    private double averageRating;
    private List<String> commonIssues = new ArrayList<>();

    public void analyzeFeedback(List<Feedback> feedbacks) {
        if (feedbacks.isEmpty()) return;

        double total = 0;
        for (Feedback fb : feedbacks) {
            total += fb.getRating(); // ✅ via getter
            if (fb.getContent().toLowerCase().contains("delay")) {
                commonIssues.add("Delay issue");
            }
        }
        
        this.averageRating = total / feedbacks.size();
    }

    public String getAnalysisSummary() {
        return "Average Rating: " + averageRating + "\nCommon Issues: " + String.join(", ", commonIssues);
    }
}

