package src.service;

import src.model.Feedback;
import src.model.FeedbackAnalytics;
import src.util.FeedbackCsvMapper;
import src.model.HDBManager;

import java.util.List;

public class FeedbackAnalyticsService {

    // Get all feedback from CSV
    public static List<Feedback> getAllFeedback() {
        return FeedbackCsvMapper.loadAll();
    }

    // Perform analytics on feedback data
    public static void generateAnalytics() {
        List<Feedback> feedbackList = getAllFeedback();
        FeedbackAnalytics analytics = new FeedbackAnalytics(feedbackList);

        // Print the analytics results
        System.out.println("===== Feedback Analytics =====");
        System.out.println("Total Feedback: " + analytics.countTotal());
        System.out.println("Resolved Feedback: " + analytics.countResolved());
        System.out.println("Unresolved Feedback: " + analytics.countUnresolved());
        System.out.println("Feedback Grouped by Status: " + analytics.groupByStatus());
        System.out.println("Feedback Grouped by Applicant: " + analytics.groupByApplicant());
    }

    // Print analytics for a specific manager
    public static void generateManagerAnalytics(HDBManager manager) {
        List<Feedback> feedbackList = FeedbackCsvMapper.loadAll();
        FeedbackAnalytics analytics = new FeedbackAnalytics(feedbackList);

        // Get feedback for the manager's assigned project
        List<Feedback> managerFeedback = analytics.getFeedbackByManager(manager);

        FeedbackAnalytics managerAnalytics = new FeedbackAnalytics(managerFeedback);

        // Print the analytics results for the manager's feedback
        System.out.println("===== Manager Feedback Analytics =====");
        System.out.println("Total Feedback: " + managerAnalytics.countTotal());
        System.out.println("Resolved Feedback: " + managerAnalytics.countResolved());
        System.out.println("Unresolved Feedback: " + managerAnalytics.countUnresolved());
        System.out.println("Feedback Grouped by Status: " + managerAnalytics.groupByStatus());
        System.out.println("Feedback Grouped by Applicant: " + managerAnalytics.groupByApplicant());
    }
}
