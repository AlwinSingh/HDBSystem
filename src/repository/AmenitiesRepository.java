package src.repository;

import src.model.Amenities;
import java.util.List;

/**
 * Interface for performing CRUD operations on Amenities data.
 */
public interface AmenitiesRepository {

    /**
     * Loads all amenities from the CSV file.
     * 
     * @return List of amenities.
     */
    List<Amenities> loadAll();

    /**
     * Saves all amenities to the CSV file.
     * 
     * @param all The list of amenities to persist.
     */
    void saveAll(List<Amenities> all);

    /**
     * Appends a new amenity to the CSV file.
     * 
     * @param amenity The amenity to add.
     */
    void add(Amenities amenity);

    /**
     * Updates an existing amenity in the CSV file.
     * 
     * @param updated The updated amenity object.
     */
    void update(Amenities updated);
}
