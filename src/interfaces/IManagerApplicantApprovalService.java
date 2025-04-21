package src.interfaces;

import src.model.HDBManager;

import java.util.Scanner;

/**
 * Interface defining manager-level services for reviewing and processing
 * applicant approvals and withdrawal requests.
 */
public interface IManagerApplicantApprovalService {

    /**
     * Displays all applicant applications tied to the manager's projects.
     *
     * @param manager The logged-in manager.
     */
    void viewApplicantApplications(HDBManager manager);

    /**
     * Allows the manager to approve or reject applicant submissions for their projects.
     *
     * @param manager The manager.
     * @param sc      Scanner for user input.
     */
    void handleApplicantApproval(HDBManager manager, Scanner sc);

    /**
     * Processes withdrawal requests made by applicants for the manager's projects.
     *
     * @param manager The logged-in manager.
     * @param sc      Scanner for user input.
     */
    void handleWithdrawalRequests(HDBManager manager, Scanner sc);
}
