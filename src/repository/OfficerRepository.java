package src.repository;

import src.model.HDBOfficer;
import src.model.Project;

import java.util.List;

/**
 * Repository interface for managing {@link HDBOfficer}
 * <p>
 * This interface provides abstraction over data operations such as loading,
 * saving, and updating officers along with their project assignments.
 * It supports reading from and writing to a CSV file
 * </p>
 */
public interface OfficerRepository {

    /**
     * Loads all HDB officers from the data source.
     *
     * @param allProjects A list of all known projects to match officer assignments.
     * @return A list of {@link HDBOfficer} objects.
     */
    List<HDBOfficer> loadAll(List<Project> allProjects);

    /**
     * Persists the given list of HDB officers to the data source,
     * replacing any existing data.
     *
     * @param officers List of {@link HDBOfficer} to be saved.
     */
    void saveAll(List<HDBOfficer> officers);

    /**
     * Updates a specific officer in the data source based on their NRIC.
     *
     * @param updatedOfficer The {@link HDBOfficer} object with updated fields.
     */
    void updateOfficer(HDBOfficer updatedOfficer);
}
