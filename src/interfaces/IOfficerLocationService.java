package src.interfaces;

import src.model.HDBOfficer;
import src.model.Project;

import java.util.Scanner;

/**
 * Interface for officer-level project location updates.
 * <p>
 * Follows the Interface Segregation Principle by exposing only location-specific operations
 * relevant to the officer's project responsibilities.
 * </p>
 */
public interface IOfficerLocationService {

    /**
     * Updates the location details of the officer's assigned project.
     * This includes district, town, address, and geolocation.
     *
     * @param officer The logged-in officer.
     * @param sc      Scanner for input.
     */
    void updateLocation(HDBOfficer officer, Scanner sc);

    /**
     * Checks if the officer has permission to update project location.
     * Officer must have an approved registration and assigned project.
     *
     * @param officer The officer to check.
     * @return True if eligible; false otherwise.
     */
    boolean canUpdateLocation(HDBOfficer officer);

    /**
     * Performs the interactive location update for a project.
     *
     * @param project The project to update.
     * @param sc      Scanner for user input.
     * @return True if update succeeds; false otherwise.
     */
    boolean updateProjectLocation(Project project, Scanner sc);
}
