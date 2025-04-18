package src.service;

import src.model.Feedback;
import src.model.HDBManager;
import src.model.Project;
import src.util.FeedbackCsvMapper;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class FeedbackService {

    private static List<Feedback> feedbackList = FeedbackCsvMapper.loadAll();

    // === SUBMIT ===
    public static void submitFeedback(String applicantNRIC, String content, String projectName) {
        refresh();
        int nextId = feedbackList.stream().mapToInt(Feedback::getFeedbackId).max().orElse(0) + 1;
        Feedback newFeedback = new Feedback(
            nextId,
            applicantNRIC,
            content,
            Feedback.STATUS_PENDING,
            LocalDate.now(),
            null,
            null,
            projectName
        );
        feedbackList.add(newFeedback);
        FeedbackCsvMapper.saveAll(feedbackList);
        System.out.println("✅ Feedback submitted successfully!");
    }

    // === GET ALL (RESTRICTED) ===
    public static List<Feedback> getFeedbackByManager(HDBManager manager) {
        refresh();
        Set<String> myProjects = ProjectLoader.loadProjects().stream()
            .filter(p -> p.getManager() != null &&
                         p.getManager().getNric().equalsIgnoreCase(manager.getNric()))
            .map(Project::getProjectName)
            .collect(Collectors.toSet());

        return feedbackList.stream()
            .filter(f -> f.getProjectName() != null && myProjects.contains(f.getProjectName()))
            .collect(Collectors.toList());
    }

    // === GET UNRESOLVED (Scoped) ===
    public static List<Feedback> getUnresolvedByManager(HDBManager manager) {
        return getFeedbackByManager(manager).stream()
            .filter(f -> Feedback.STATUS_PENDING.equalsIgnoreCase(f.getStatus()))
            .collect(Collectors.toList());
    }

    // === FILTERS (Scoped) ===
    public static List<Feedback> getFeedbackByApplicant(HDBManager manager, String applicantNRIC) {
        return getFeedbackByManager(manager).stream()
            .filter(f -> f.getApplicantNRIC() != null && f.getApplicantNRIC().equalsIgnoreCase(applicantNRIC))
            .collect(Collectors.toList());
    }


    public static List<Feedback> getFeedbackByApplicant(String applicantNRIC) {
        refresh();
        return feedbackList.stream()
                .filter(f -> f.getApplicantNRIC() != null && f.getApplicantNRIC().equalsIgnoreCase(applicantNRIC))
                .collect(Collectors.toList());
    }


    public static List<Feedback> getFeedbackByResolver(HDBManager manager, String resolverName) {
        return getFeedbackByManager(manager).stream()
            .filter(f -> f.getResolverName() != null && f.getResolverName().equalsIgnoreCase(resolverName))
            .collect(Collectors.toList());
    }

    public static List<Feedback> getFeedbackBySubmittedDateRange(HDBManager manager, LocalDate start, LocalDate end) {
        return getFeedbackByManager(manager).stream()
            .filter(f -> f.getSubmittedDate() != null)
            .filter(f -> !f.getSubmittedDate().isBefore(start) && !f.getSubmittedDate().isAfter(end))
            .collect(Collectors.toList());
    }

    // === PRINT ===
    public static void printFeedbackList(List<Feedback> list) {
        if (list == null || list.isEmpty()) {
            System.out.println("📭 No feedback to display.");
            return;
        }
        for (Feedback f : list) {
            System.out.println("\n" + f);
        }
    }

    // === RESOLVE ===
    public static boolean resolveFeedback(int id, String adminName) {
        refresh();
        for (Feedback f : feedbackList) {
            if (f.getFeedbackId() == id && Feedback.STATUS_PENDING.equalsIgnoreCase(f.getStatus())) {
                f.setStatus(Feedback.STATUS_RESOLVED);
                f.setResolverName(adminName);
                f.setResolvedDate(LocalDate.now());
                FeedbackCsvMapper.saveAll(feedbackList);
                System.out.println("✅ Feedback #" + id + " marked as resolved by " + adminName);
                return true;
            }
        }
        System.out.println("❌ Feedback not found or already resolved.");
        return false;
    }

    // === SAVE / RELOAD ===
    public static void saveAll() {
        FeedbackCsvMapper.saveAll(feedbackList);
    }

    public static void refresh() {
        feedbackList = FeedbackCsvMapper.loadAll();
    }
}
