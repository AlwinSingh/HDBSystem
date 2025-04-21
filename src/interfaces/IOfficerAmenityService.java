package src.interfaces;

import src.model.HDBOfficer;
import java.util.Scanner;

/**
 * Interface for managing amenities by HDB officers.
 * <p>
 * This interface defines operations specific to the management of project amenities,
 * ensuring separation of responsibilities and adherence to the Interface Segregation Principle (ISP).
 * </p>
 */
public interface IOfficerAmenityService {

    /**
     * Adds a new amenity or updates an existing amenity for the officer’s assigned project.
     * <p>
     * Only officers with APPROVED registration and an assigned project are allowed to perform this operation.
     * </p>
     *
     * @param officer The logged-in HDB officer performing the operation.
     * @param sc      Scanner for reading user input.
     */
    void addOrUpdateAmenity(HDBOfficer officer, Scanner sc);

    /**
     * Checks whether the officer is eligible to manage amenities.
     * <p>
     * An officer is eligible if their registration status is APPROVED and they are assigned to a project.
     * </p>
     *
     * @param officer The HDB officer to check.
     * @return True if the officer can manage amenities; false otherwise.
     */
    boolean canManageAmenities(HDBOfficer officer);
}
