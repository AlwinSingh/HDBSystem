package src.interfaces;

import java.util.List;
import java.util.Scanner;
import src.model.HDBOfficer;
import src.model.Project;

/**
 * Interface for officer registration functionalities.
 * Covers status viewing, registration actions, and project browsing.
 */
public interface IOfficerRegistrationService {

    /**
     * Views the officer’s current registration status and assigned project details.
     *
     * @param officer The officer whose status is being viewed.
     */
    void viewOfficerRegistrationStatus(HDBOfficer officer);

    /**
     * Attempts to register an officer to the specified project.
     *
     * @param officer         The officer registering.
     * @param selectedProject The project to register for.
     * @return True if registration is successful; false otherwise.
     */
    boolean registerForProject(HDBOfficer officer, Project selectedProject);

    /**
     * Returns a list of projects that the officer is eligible to register for.
     *
     * @param officer The officer querying available projects.
     * @return List of projects available to register for.
     */
    List<Project> getAvailableProjectsForOfficer(HDBOfficer officer);

    /**
     * Displays the list of open projects and allows filtering by various criteria.
     *
     * @param sc Scanner used for user input.
     */
    void browseAndFilterProjects(Scanner sc);

    /**
     * Allows the officer to register for a selected project from the list.
     *
     * @param officer The officer registering.
     * @param sc      Scanner for input.
     */
    void registerForProject(HDBOfficer officer, Scanner sc);
}
