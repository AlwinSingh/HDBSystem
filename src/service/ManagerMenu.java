package src.service;

import java.util.*;
import src.model.*;

/**
 * Displays the CLI dashboard for HDB managers and handles routing to various services.
 */
public class ManagerMenu {

    /**
     * Launches the manager dashboard and routes user input to the appropriate service methods.
     *
     * @param manager The logged-in HDBManager.
     */
    public static void show(HDBManager manager) {
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n===== 🧠 HDB Manager Dashboard =====");
            System.out.println("Welcome, Manager " + manager.getName());

            System.out.println("""
    🏗️  Project Management
     [1]  ➕ Create Project           [2]  ✏️ Edit Project
     [3]  ❌ Delete Project           [4]  🔁 Toggle Visibility

    📊  Project Viewing
     [5]  🌐 View All Projects        [6]  📁 View My Projects

    👔  Officer Applications
     [7]  📋 View Registrations       [8]  ✅/❌ Approve/Reject Officers

    👥  Applicant Management
     [9]  📄 View Applications        [10] ✅/❌ Approve/Reject Applications
     [11]  🔄 Handle Withdrawal Requests

    📈  Reports
     [12]  📊 Generate Booking Reports

    📬  Enquiries & Feedback
     [13]  📬 View & Reply to Enquiries
     [14]  📝 View & Resolve Feedback
     [15]  📊 View Feedback Analytics

    🔐  Account
     [16]  🔒 Change Password         [0]  🚪 Logout
    """);

            System.out.print("➡️ Enter your choice: ");
            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1" -> ManagerService.createProject(manager, sc);
                case "2" -> ManagerService.editProject(manager, sc);
                case "3" -> ManagerService.deleteProject(manager, sc);
                case "4" -> ManagerService.toggleVisibility(manager, sc);
                case "5" -> ManagerService.viewAllProjectsWithFilter(sc);
                case "6" -> ManagerService.viewMyProjects(manager);
                case "7" -> ManagerService.viewOfficerRegistrations(manager);
                case "8" -> ManagerService.handleOfficerApproval(manager, sc);
                case "9" -> ManagerService.viewApplicantApplications(manager);
                case "10" -> ManagerService.handleApplicantApproval(manager, sc);
                case "11" -> ManagerService.handleWithdrawalRequests(manager, sc);
                case "12" -> ManagerService.generateReports(manager, sc);
                case "13" -> ManagerService.showEnquiryOptions(manager, sc);
                case "14" -> ManagerService.viewAndResolveFeedback(manager, sc);
                case "15" -> FeedbackAnalyticsService.generateManagerAnalytics(manager);
                case "16" -> {
                    if (AuthService.changePassword(manager, sc)) return;
                }
                case "0"  -> {
                    AuthService.logout();
                    return;
                }
                default -> System.out.println("❌ Invalid input. Please try again.");
            }
        }
    }
}
