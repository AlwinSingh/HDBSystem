package src.repository;

import src.model.Enquiry;
import java.util.List;

/**
 * Repository interface for accessing and manipulating {@link Enquiry} records.
 * Supports typical CRUD operations, adhering to the Interface Segregation Principle (ISP).
 */
public interface EnquiryRepository {

    /**
     * Loads all enquiry records from the data source.
     *
     * @return A list of all enquiries.
     */
    List<Enquiry> loadAll();

    /**
     * Saves the entire list of enquiries, replacing all existing entries in the data source.
     *
     * @param enquiries The list of enquiries to persist.
     */
    void saveAll(List<Enquiry> enquiries);

    /**
     * Appends a new enquiry to the data source.
     *
     * @param enquiry The new enquiry to be added.
     */
    void add(Enquiry enquiry);

    /**
     * Updates a specific enquiry by ID with new data.
     *
     * @param updated The updated enquiry object.
     */
    void update(Enquiry updated);
}
