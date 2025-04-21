package src.repository;

import src.model.Applicant;
import java.util.List;

/**
 * Repository for basic data access operations for {@link Applicant} objects.
 * Implementations interact with CSV mappers and CSV databases.
 */
public interface ApplicantRepository {
    /**
     * Loads all applicants from the data source.
     *
     * @return A list of all stored applicants.
     */
    List<Applicant> loadAll();

    /**
     * Updates an existing applicant record in the data source.
     * Typically used when an applicant's application or status has changed.
     *
     * @param applicant The applicant to update.
     */
    void update(Applicant applicant);

    /**
     * Saves a new applicant to the data source.
     * This should only be used for applicants not already present.
     *
     * @param applicant The applicant to save.
     */
    void save(Applicant applicant);

    /**
     * Checks if an applicant with the given NRIC already exists in the data source.
     *
     * @param nric The NRIC to check for.
     * @return True if the NRIC is already in use; false otherwise.
     */
    boolean exists(String nric);
}
