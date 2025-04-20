package src.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

import src.model.Feedback;
import src.model.HDBManager;

public class ManagerFeedbackService {

    /**
     * Provides full feedback management options to managers:
     * viewing, filtering, and resolving feedback.
     */
    public static void viewAndResolveFeedback(HDBManager manager, Scanner sc) {
        while (true) {
            System.out.println("\n📝 Feedback Management");
            System.out.println(" [1] View My Feedback");
            System.out.println(" [2] View Unresolved Feedback");
            System.out.println(" [3] Filter by Applicant NRIC");
            System.out.println(" [4] Filter by Submission Date Range");
            System.out.println(" [5] Resolve Feedback");
            System.out.println(" [0] Back");
            System.out.print("➡️ Enter choice: ");

            String choice = sc.nextLine().trim();
            switch (choice) {
                case "1" -> FeedbackService.printFeedbackList(
                    FeedbackService.getFeedbackByManager(manager)
                );

                case "2" -> FeedbackService.printFeedbackList(
                    FeedbackService.getUnresolvedByManager(manager)
                );

                case "3" -> {
                    System.out.print("Enter Applicant NRIC: ");
                    String nric = sc.nextLine().trim();
                    FeedbackService.printFeedbackList(
                        FeedbackService.getFeedbackByApplicant(manager, nric)
                    );
                }

                case "4" -> {
                    try {
                        System.out.print("Start date (yyyy-MM-dd): ");
                        LocalDate start = LocalDate.parse(sc.nextLine().trim());
                        System.out.print("End date (yyyy-MM-dd): ");
                        LocalDate end = LocalDate.parse(sc.nextLine().trim());
                        FeedbackService.printFeedbackList(
                            FeedbackService.getFeedbackBySubmittedDateRange(manager, start, end)
                        );
                    } catch (Exception e) {
                        System.out.println("❌ Invalid date format.");
                    }
                }

                case "5" -> {
                    try {
                        System.out.print("Enter Feedback ID to resolve: ");
                        int id = Integer.parseInt(sc.nextLine().trim());

                        List<Feedback> myFeedback = FeedbackService.getFeedbackByManager(manager);
                        boolean belongsToManager = myFeedback.stream()
                            .anyMatch(f -> f.getFeedbackId() == id);

                        if (!belongsToManager) {
                            System.out.println("❌ You can only resolve feedback from your assigned projects.");
                            break;
                        }

                        FeedbackService.resolveFeedback(id, manager.getName());
                    } catch (Exception e) {
                        System.out.println("❌ Invalid input.");
                    }
                }

                case "0" -> {
                    System.out.println("🔙 Returning to manager menu...");
                    return;
                }

                default -> System.out.println("❌ Invalid input.");
            }
        }
    }

}
