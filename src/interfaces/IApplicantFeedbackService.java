package src.interfaces;

import src.model.Applicant;

/**
 * Interface for managing feedback submissions from applicants.
 */
public interface IApplicantFeedbackService {

    /**
     * Submits feedback for the applicant's currently applied project.
     *
     * @param applicant The applicant submitting feedback.
     * @param message   The feedback message.
     * @return True if feedback is accepted, false if no valid project found.
     */
    boolean submitFeedback(Applicant applicant, String message);
}
