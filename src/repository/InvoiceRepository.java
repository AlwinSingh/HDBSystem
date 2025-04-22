package src.repository;

import src.model.Invoice;
import java.util.List;

/**
 * Interface for accessing and managing {@link Invoice} records.
 * Supports CRUD operations including load, save, append, and update.
 */
public interface InvoiceRepository {

    /**
     * Loads all invoices from the persistent CSV storage.
     *
     * @return A list of all {@link Invoice} entries.
     */
    List<Invoice> loadAll();

    /**
     * Overwrites the existing CSV data with the given list of invoices.
     *
     * @param invoices The list of {@link Invoice} entries to save.
     */
    void saveAll(List<Invoice> invoices);

    /**
     * Appends a new {@link Invoice} to the existing CSV data.
     *
     * @param invoice The invoice object to add.
     */
    void append(Invoice invoice);

    /**
     * Updates an existing invoice entry by its payment ID.
     * Replaces the old invoice record with the updated one.
     *
     * @param updated The modified invoice to persist.
     */
    void update(Invoice updated);
}
