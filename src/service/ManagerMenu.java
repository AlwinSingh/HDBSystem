package src.service;

import java.util.*;
import src.interfaces.*;
import src.model.*;

/**
 * Displays the CLI dashboard for HDB managers and handles routing to various services.
 */
public class ManagerMenu {
    private final IManagerProjectService projectService;
    private final IManagerOfficerApprovalService officerService;
    private final IManagerApplicantApprovalService applicantService;
    private final IManagerReportService reportService;
    private final IManagerEnquiryService enquiryService;
    private final IManagerFeedbackService feedbackService;

    public ManagerMenu(
        IManagerProjectService projectService,
        IManagerOfficerApprovalService officerService,
        IManagerApplicantApprovalService applicantService,
        IManagerReportService reportService,
        IManagerEnquiryService enquiryService,
        IManagerFeedbackService feedbackService
    ) {
        this.projectService = projectService;
        this.officerService = officerService;
        this.applicantService = applicantService;
        this.reportService = reportService;
        this.enquiryService = enquiryService;
        this.feedbackService = feedbackService;
    }

    /**
     * Launches the manager dashboard and routes user input to the appropriate service methods.
     *
     * @param manager The logged-in HDBManager.
     */
    public void show(HDBManager manager) {
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
                case "1" -> projectService.createProject(manager, sc);
                case "2" -> projectService.editProject(manager, sc);
                case "3" -> projectService.deleteProject(manager, sc);
                case "4" -> projectService.toggleVisibility(manager, sc);
                case "5" -> projectService.viewAllProjectsWithFilter(sc);
                case "6" -> projectService.viewMyProjects(manager);
                case "7" -> officerService.viewOfficerRegistrations(manager);
                case "8" -> officerService.handleOfficerApproval(manager, sc);
                case "9" -> applicantService.viewApplicantApplications(manager);
                case "10" -> applicantService.handleApplicantApproval(manager, sc);
                case "11" -> applicantService.handleWithdrawalRequests(manager, sc);
                case "12" -> reportService.generateReports(manager, sc);
                case "13" -> enquiryService.showEnquiryOptions(manager, sc);
                case "14" -> feedbackService.viewAndResolveFeedback(manager, sc);
                case "15" -> FeedbackAnalyticsService.generateManagerAnalytics(manager);
                case "16" -> {
                    if (AuthService.changePassword(manager, sc)) return;
                }
                case "0" -> {
                    manager.logout();
                    return;
                }
                default -> System.out.println("❌ Invalid input. Please try again.");
            }
        }
    }
}
