package src.model;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Provides analytics and breakdowns on a list of feedback.
 * Includes counts, filters, and grouping logic.
 */
public class FeedbackAnalytics {

    private List<Feedback> feedbackList;

    public FeedbackAnalytics(List<Feedback> feedbackList) {
        this.feedbackList = feedbackList;
    }

    /**
     * Filters feedback to only include those related to a manager’s assigned project.
     *
     * @param manager The manager to filter by.
     * @return Feedback list belonging to that project.
     */
    public List<Feedback> getFeedbackByManager(HDBManager manager) {
        return feedbackList.stream()
            .filter(f -> f.getProjectName() != null && f.getProjectName().equalsIgnoreCase(manager.getAssignedProject().getProjectName()))
            .collect(Collectors.toList());
    }

    /**
     * Returns the total number of feedback entries.
     */
    public long countTotal() {
        return feedbackList.size();
    }

    /**
     * Returns the number of feedback entries marked as resolved.
     */
    public long countResolved() {
        return feedbackList.stream()
                .filter(f -> Feedback.STATUS_RESOLVED.equalsIgnoreCase(f.getStatus()))
                .count();
    }

    /**
     * Returns the number of feedback entries that are still pending.
     */
    public long countUnresolved() {
        return feedbackList.stream()
                .filter(f -> Feedback.STATUS_PENDING.equalsIgnoreCase(f.getStatus()))
                .count();
    }

    /**
     * Groups feedback by status and returns counts per status.
     */
    public Map<String, Long> groupByStatus() {
        return feedbackList.stream()
                .collect(Collectors.groupingBy(Feedback::getStatus, Collectors.counting()));
    }

    /**
     * Groups feedback by applicant NRIC and returns counts per applicant.
     */
    public Map<String, Long> groupByApplicant() {
        return feedbackList.stream()
                .collect(Collectors.groupingBy(Feedback::getApplicantNRIC, Collectors.counting()));
    }
}
