package src.service;

import java.time.LocalDate;

import src.model.Applicant;
import src.model.Project;

public class ApplicantEligibilityService {
    
    /**
     * Checks if an applicant is eligible to apply for a given project.
     *
     * @param applicant The applicant in question.
     * @param project   The project being considered.
     * @return True if the applicant meets the age and marital criteria; false otherwise.
     */
    public static boolean isEligible(Applicant applicant, Project project) {
        String status = applicant.getMaritalStatus();
        int age = applicant.getAge();

        boolean withinDateRange = !LocalDate.now().isBefore(project.getOpenDate())
                && !LocalDate.now().isAfter(project.getCloseDate());

        if (status == null || !withinDateRange) return false;

        return (status.equalsIgnoreCase("Single") && age >= 35)
                || (status.equalsIgnoreCase("Married") && age >= 21);
    }

}
