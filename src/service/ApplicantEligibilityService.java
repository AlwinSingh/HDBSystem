package src.service;

import java.time.LocalDate;

import src.interfaces.IApplicantEligibilityService;
import src.model.Applicant;
import src.model.Project;

/**
 * Service responsible for validating an applicant's eligibility to apply for a housing project.
 * This class is stateless and only provides a utility method.
 */
public class ApplicantEligibilityService implements IApplicantEligibilityService {
    
    /**
     * Checks if an applicant is eligible to apply for a given project.
     *
     * @param applicant The applicant in question.
     * @param project   The project being considered.
     * @return True if the applicant meets the age and marital criteria; false otherwise.
     */
    @Override
    public boolean isEligible(Applicant applicant, Project project) {
        String status = applicant.getMaritalStatus();
        int age = applicant.getAge();

        boolean withinDateRange = !LocalDate.now().isBefore(project.getOpenDate())
                && !LocalDate.now().isAfter(project.getCloseDate());

        if (status == null || !withinDateRange) return false;

        return (status.equalsIgnoreCase("Single") && age >= 35)
                || (status.equalsIgnoreCase("Married") && age >= 21);
    }

}
