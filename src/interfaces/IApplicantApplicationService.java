package src.interfaces;

import java.util.Scanner;
import src.model.Applicant;
import src.model.Project;

/**
 * Interface for applicant application management in the BTO system.
 * Handles applying for projects, submitting applications, and withdrawals.
 */
public interface IApplicantApplicationService {

    /**
     * Guides the applicant through selecting and applying to a project.
     *
     * @param applicant The applicant applying.
     * @param sc        Scanner for user input.
     */
    void applyForProject(Applicant applicant, Scanner sc);

    /**
     * Submits an application for the given project and flat type.
     *
     * @param applicant The applicant submitting the application.
     * @param project   The selected project.
     * @param flatType  The flat type (2-Room or 3-Room).
     * @return True if submission was successful.
     */
    boolean submitApplication(Applicant applicant, Project project, String flatType);

    /**
     * Checks if the applicant is allowed to withdraw.
     *
     * @param applicant The applicant to check.
     * @return True if withdrawal is allowed.
     */
    boolean canWithdraw(Applicant applicant);

    /**
     * Submits a withdrawal request for the applicant.
     *
     * @param applicant The applicant who wants to withdraw.
     */
    void submitWithdrawalRequest(Applicant applicant);
}
