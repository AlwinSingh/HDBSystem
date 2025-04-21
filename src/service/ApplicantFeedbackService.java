package src.service;

import src.interfaces.IApplicantFeedbackService;
import src.model.Applicant;

/**
 * Lets applicants submit feedback for the project they applied to.
 *
 * This class checks if the applicant has an active application
 * before allowing feedback to be sent.
 */
public class ApplicantFeedbackService implements IApplicantFeedbackService {
    /**
     * Submits a feedback message for the applicant’s current project.
     *
     * @return True if feedback was accepted; false if applicant has no active project.
     */
    @Override
    public boolean submitFeedback(Applicant applicant, String message) {
        if (applicant.getApplication() == null || applicant.getApplication().getProject() == null) {
            return false;
        }
        String projectName = applicant.getApplication().getProject().getProjectName();
        FeedbackService.submitFeedback(applicant.getNric(), message, projectName);
        return true;
    }

}
