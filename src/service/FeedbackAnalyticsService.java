package src.service;

import src.model.Feedback;
import src.model.FeedbackAnalytics;
import src.model.HDBManager;
import src.model.Project;
import src.util.FeedbackCsvMapper;
import src.util.ProjectCsvMapper;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Provides analytics and insights on feedback data, both globally and per manager.
 */
public class FeedbackAnalyticsService {

    /**
     * Loads all feedback entries from the CSV file.
     *
     * @return A list of all feedback submitted in the system.
     */
    public static List<Feedback> getAllFeedback() {
        return FeedbackCsvMapper.loadAll();
    }

    /**
     * Generates and prints overall analytics on all feedback in the system,
     * regardless of manager or project.
     */
    public static void generateAnalytics() {
        List<Feedback> feedbackList = getAllFeedback();
        FeedbackAnalytics analytics = new FeedbackAnalytics(feedbackList);

        System.out.println("\n");
        System.out.println("===== Feedback Analytics =====");
        System.out.println("Total Feedback: " + analytics.countTotal());
        System.out.println("Resolved Feedback: " + analytics.countResolved());
        System.out.println("Unresolved Feedback: " + analytics.countUnresolved());
        System.out.println("Feedback Grouped by Status: " + analytics.groupByStatus());
        System.out.println("Feedback Grouped by Applicant: " + analytics.groupByApplicant());
    }

    /**
     * Generates analytics scoped to a specific manager. This includes only feedback
     * submitted for projects managed by the given HDB manager.
     *
     * @param manager The manager whose project feedback should be analyzed.
     */
    public static void generateManagerAnalytics(HDBManager manager) {
        List<Feedback> feedbackList = getAllFeedback();

        // Get project names owned by this manager
        Set<String> myProjects = ProjectCsvMapper.loadAll().stream()
                .filter(p -> p.getManager() != null &&
                        p.getManager().getNric().equalsIgnoreCase(manager.getNric()))
                .map(Project::getProjectName)
                .collect(Collectors.toSet());

        // Keep only feedback belonging to the manager's projects
        List<Feedback> managerFeedback = feedbackList.stream()
                .filter(f -> f.getProjectName() != null && myProjects.contains(f.getProjectName()))
                .collect(Collectors.toList());

        FeedbackAnalytics analytics = new FeedbackAnalytics(managerFeedback);

        System.out.println("===== Manager Feedback Analytics =====");
        System.out.println("Total Feedback: " + analytics.countTotal());
        System.out.println("Resolved Feedback: " + analytics.countResolved());
        System.out.println("Unresolved Feedback: " + analytics.countUnresolved());
        System.out.println("Feedback Grouped by Status: " + analytics.groupByStatus());
        System.out.println("Feedback Grouped by Applicant: " + analytics.groupByApplicant());
    }
}
