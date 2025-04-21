package src.interfaces;

import src.model.HDBOfficer;
import src.model.Project;

/**
 * Interface for project view functionalities available to HDB officers.
 * <p>
 * Adheres to the Interface Segregation Principle by exposing only
 * operations related to viewing and summarizing project details.
 * </p>
 */
public interface IOfficerProjectViewService {

    /**
     * Displays the full details of the officer's assigned project,
     * including amenities, location, availability, and pricing.
     *
     * @param officer The logged-in officer.
     */
    void viewAssignedProjectDetails(HDBOfficer officer);

    /**
     * Generates a summary string containing all essential project
     * information in a human-readable format.
     *
     * @param project The project to summarize.
     * @param officer The officer requesting the summary.
     * @return A summary string for display.
     */
    String getProjectSummary(Project project, HDBOfficer officer);
}
