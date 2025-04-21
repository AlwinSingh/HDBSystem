package src.repository;

import src.model.Payment;
import java.util.List;

/**
 * Repository interface for managing {@link Payment} entities.
 * Provides methods for loading, saving, appending, and updating payment records.
 */
public interface PaymentRepository {

    /**
     * Loads all payments from the data source.
     *
     * @return List of {@link Payment} objects.
     */
    List<Payment> loadAll();

    /**
     * Saves the full list of payments to the data source.
     *
     * @param payments List of {@link Payment} objects to persist.
     */
    void saveAll(List<Payment> payments);

    /**
     * Appends a new payment to the data source.
     *
     * @param payment The new {@link Payment} to add.
     */
    void append(Payment payment);

    /**
     * Updates an existing payment in the data source.
     *
     * @param updated The updated {@link Payment} object.
     */
    void update(Payment updated);
}
