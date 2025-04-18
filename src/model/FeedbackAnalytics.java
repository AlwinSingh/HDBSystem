package src.model;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FeedbackAnalytics {

    private List<Feedback> feedbackList;

    public FeedbackAnalytics(List<Feedback> feedbackList) {
        this.feedbackList = feedbackList;
    }

    // Get feedback by manager's assigned project
    public List<Feedback> getFeedbackByManager(HDBManager manager) {
        return feedbackList.stream()
            .filter(f -> f.getProjectName() != null && f.getProjectName().equalsIgnoreCase(manager.getAssignedProject().getProjectName()))
            .collect(Collectors.toList());
    }

    public long countTotal() {
        return feedbackList.size();
    }

    public long countResolved() {
        return feedbackList.stream()
                .filter(f -> Feedback.STATUS_RESOLVED.equalsIgnoreCase(f.getStatus()))
                .count();
    }

    public long countUnresolved() {
        return feedbackList.stream()
                .filter(f -> Feedback.STATUS_PENDING.equalsIgnoreCase(f.getStatus()))
                .count();
    }

    public Map<String, Long> groupByStatus() {
        return feedbackList.stream()
                .collect(Collectors.groupingBy(Feedback::getStatus, Collectors.counting()));
    }

    public Map<String, Long> groupByApplicant() {
        return feedbackList.stream()
                .collect(Collectors.groupingBy(Feedback::getApplicantNRIC, Collectors.counting()));
    }
}
