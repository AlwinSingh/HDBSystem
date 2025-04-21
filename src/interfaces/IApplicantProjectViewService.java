package src.interfaces;

import src.model.Applicant;
import src.model.Project;

import java.util.List;
import java.util.Scanner;

/**
 * Shows project info and lets applicants filter what they see.
 */
public interface IApplicantProjectViewService {
    /**
     * Shows a list of eligible projects with filtering options.
     */
    void handleViewEligibleProjects(Applicant applicant, Scanner sc);

    /**
     * Gets all open and eligible projects for this applicant.
     */
    List<Project> getEligibleProjects(Applicant applicant);

    /**
     * Gets eligible projects based on filters.
     */
    List<Project> getFilteredEligibleProjects(Applicant applicant, String neighborhood, String district, String flatType);

    /**
     * Shows details for one project.
     */
    void displayProjectDetails(Project p, Applicant applicant);
}
