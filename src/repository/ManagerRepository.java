package src.repository;

import src.model.HDBManager;
import java.util.List;

/**
 * Repository interface for managing {@link HDBManager} entities.
 * Supports CRUD operations abstracted from CSV implementation details.
 */
public interface ManagerRepository {

    /**
     * Loads all managers from the persistent data source.
     *
     * @return A list of all {@link HDBManager} entries.
     */
    List<HDBManager> loadAll();

    /**
     * Saves the entire list of managers to the data source.
     *
     * @param managers A list of managers to persist.
     */
    void saveAll(List<HDBManager> managers);

    /**
     * Searches for a manager by NRIC.
     *
     * @param nric The NRIC to search for.
     * @return The matching {@link HDBManager}, or null if not found.
     */
    HDBManager findByNric(String nric);

    /**
     * Updates an existing manager by matching NRIC and persisting the new version.
     *
     * @param updatedManager The modified manager object to persist.
     */
    void updateManager(HDBManager updatedManager);
}
