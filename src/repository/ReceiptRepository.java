package src.repository;

import src.model.Receipt;
import java.util.List;

/**
 * Repository interface for managing {@link Receipt} data storage and retrieval operations.
 * Follows the Repository pattern for abstracting CSV or other data source interactions.
 */
public interface ReceiptRepository {

    /**
     * Loads all receipt records from the underlying data source.
     *
     * @return List of all {@link Receipt} objects stored.
     */
    List<Receipt> loadAll();

    /**
     * Saves a complete list of receipts, replacing existing records in the data source.
     *
     * @param receipts The full list of {@link Receipt} objects to save.
     */
    void saveAll(List<Receipt> receipts);

    /**
     * Appends a single new receipt record to the data source.
     *
     * @param receipt The {@link Receipt} object to append.
     */
    void append(Receipt receipt);
}
