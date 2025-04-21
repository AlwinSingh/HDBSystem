package src.interfaces;

import src.model.Applicant;
import src.model.Project;

/**
 * Interface for validating applicant eligibility for a housing project.
 */
public interface IApplicantEligibilityService {

    /**
     * Determines if the applicant is eligible to apply for the given project.
     *
     * @param applicant The applicant.
     * @param project   The housing project.
     * @return True if the applicant is eligible; false otherwise.
     */
    boolean isEligible(Applicant applicant, Project project);
}
