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

public class FeedbackAnalyticsService {

    // Get all feedback from CSV
    public static List<Feedback> getAllFeedback() {
        return FeedbackCsvMapper.loadAll();
    }

    // General analytics for all feedback (not scoped to manager)
    public static void generateAnalytics() {
        List<Feedback> feedbackList = getAllFeedback();
        FeedbackAnalytics analytics = new FeedbackAnalytics(feedbackList);

        // Print the analytics results
        System.out.println("\n");
        System.out.println("===== Feedback Analytics =====");
        System.out.println("Total Feedback: " + analytics.countTotal());
        System.out.println("Resolved Feedback: " + analytics.countResolved());
        System.out.println("Unresolved Feedback: " + analytics.countUnresolved());
        System.out.println("Feedback Grouped by Status: " + analytics.groupByStatus());
        System.out.println("Feedback Grouped by Applicant: " + analytics.groupByApplicant());
    }

    // Manager-specific feedback analytics
    public static void generateManagerAnalytics(HDBManager manager) {
        List<Feedback> feedbackList = getAllFeedback();

        // Identify project names owned by this manager
        Set<String> myProjects = ProjectCsvMapper.loadAll().stream()
                .filter(p -> p.getManager() != null &&
                             p.getManager().getNric().equalsIgnoreCase(manager.getNric()))
                .map(Project::getProjectName)
                .collect(Collectors.toSet());

        // Filter feedback related to manager's projects
        List<Feedback> managerFeedback = feedbackList.stream()
                .filter(f -> f.getProjectName() != null && myProjects.contains(f.getProjectName()))
                .collect(Collectors.toList());

        FeedbackAnalytics analytics = new FeedbackAnalytics(managerFeedback);

        // Print the analytics results for the manager's projects
        System.out.println("===== Manager Feedback Analytics =====");
        System.out.println("Total Feedback: " + analytics.countTotal());
        System.out.println("Resolved Feedback: " + analytics.countResolved());
        System.out.println("Unresolved Feedback: " + analytics.countUnresolved());
        System.out.println("Feedback Grouped by Status: " + analytics.groupByStatus());
        System.out.println("Feedback Grouped by Applicant: " + analytics.groupByApplicant());
    }
}
