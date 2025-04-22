package src.service;

import src.model.Feedback;
import src.model.HDBManager;
import src.model.Project;
import src.repository.FeedbackRepository;
import src.util.FeedbackCsvMapper;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Provides functionality for submitting, retrieving, filtering, and resolving feedback.
 */
public class FeedbackService {

    private static final FeedbackRepository feedbackRepository = new FeedbackCsvMapper();
    private static List<Feedback> feedbackList = feedbackRepository.loadAll();

    /**
     * Submits new feedback from an applicant for a specific project.
     *
     * @param applicantNRIC The NRIC of the applicant.
     * @param content       The feedback message.
     * @param projectName   The name of the project the feedback is for.
     */
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
        feedbackRepository.saveAll(feedbackList);
        System.out.println("✅ Feedback submitted successfully!");
    }

    /**
     * Retrieves all feedback submitted to projects managed by the given manager.
     *
     * @param manager The HDB manager.
     * @return A list of feedback entries.
     */
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

    /**
     * Gets unresolved feedback entries for the manager's projects.
     *
     * @param manager The HDB manager.
     * @return List of feedback with "Pending" status.
     */
    public static List<Feedback> getUnresolvedByManager(HDBManager manager) {
        return getFeedbackByManager(manager).stream()
                .filter(f -> Feedback.STATUS_PENDING.equalsIgnoreCase(f.getStatus()))
                .collect(Collectors.toList());
    }

    /**
     * Retrieves feedback submitted by a specific applicant within the manager's scope.
     *
     * @param manager       The manager.
     * @param applicantNRIC NRIC of the applicant.
     * @return Matching feedback entries.
     */
    public static List<Feedback> getFeedbackByApplicant(HDBManager manager, String applicantNRIC) {
        return getFeedbackByManager(manager).stream()
                .filter(f -> f.getApplicantNRIC() != null && f.getApplicantNRIC().equalsIgnoreCase(applicantNRIC))
                .collect(Collectors.toList());
    }

    /**
     * Retrieves feedback submitted by a specific applicant (no manager filtering).
     *
     * @param applicantNRIC NRIC of the applicant.
     * @return List of their feedback.
     */
    public static List<Feedback> getFeedbackByApplicant(String applicantNRIC) {
        refresh();
        return feedbackList.stream()
                .filter(f -> f.getApplicantNRIC() != null && f.getApplicantNRIC().equalsIgnoreCase(applicantNRIC))
                .collect(Collectors.toList());
    }

    /**
     * Filters feedback entries by resolver name within the manager's scope.
     *
     * @param manager      The manager.
     * @param resolverName Name of the resolver/admin.
     * @return List of feedback resolved by that user.
     */
    public static List<Feedback> getFeedbackByResolver(HDBManager manager, String resolverName) {
        return getFeedbackByManager(manager).stream()
                .filter(f -> f.getResolverName() != null && f.getResolverName().equalsIgnoreCase(resolverName))
                .collect(Collectors.toList());
    }

    /**
     * Filters feedback submitted within a date range, scoped to the manager's projects.
     *
     * @param manager The manager.
     * @param start   Start date (inclusive).
     * @param end     End date (inclusive).
     * @return Feedback submitted within that range.
     */
    public static List<Feedback> getFeedbackBySubmittedDateRange(HDBManager manager, LocalDate start, LocalDate end) {
        return getFeedbackByManager(manager).stream()
                .filter(f -> f.getSubmittedDate() != null)
                .filter(f -> !f.getSubmittedDate().isBefore(start) && !f.getSubmittedDate().isAfter(end))
                .collect(Collectors.toList());
    }

    /**
     * Prints a list of feedback in readable format.
     *
     * @param list The list of feedback to print.
     */
    public static void printFeedbackList(List<Feedback> list) {
        if (list == null || list.isEmpty()) {
            System.out.println("📭 No feedback to display.");
            return;
        }
        for (Feedback f : list) {
            System.out.println("\n" + f);
        }
    }

    /**
     * Marks a feedback entry as resolved and logs the resolving admin's name.
     *
     * @param id         The feedback ID.
     * @param adminName  The admin resolving the feedback.
     * @return True if resolved successfully; false otherwise.
     */
    public static boolean resolveFeedback(int id, String adminName) {
        refresh();
        for (Feedback f : feedbackList) {
            if (f.getFeedbackId() == id && Feedback.STATUS_PENDING.equalsIgnoreCase(f.getStatus())) {
                f.setStatus(Feedback.STATUS_RESOLVED);
                f.setResolverName(adminName);
                f.setResolvedDate(LocalDate.now());
                feedbackRepository.saveAll(feedbackList);
                System.out.println("✅ Feedback #" + id + " marked as resolved by " + adminName);
                return true;
            }
        }
        System.out.println("❌ Feedback not found or already resolved.");
        return false;
    }

    /**
     * Saves the in-memory feedback list to the CSV file.
     */
    public static void saveAll() {
        feedbackRepository.saveAll(feedbackList);
    }

    /**
     * Reloads the feedback list from CSV to ensure it's up to date.
     */
    public static void refresh() {
        feedbackList = feedbackRepository.loadAll();
    }
}
