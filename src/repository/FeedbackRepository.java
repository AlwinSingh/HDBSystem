package src.repository;

import src.model.Feedback;
import java.util.List;

/**
 * Repository interface for accessing and managing {@link Feedback} data.
 */
public interface FeedbackRepository {

    /**
     * Loads all feedback entries from the data source.
     *
     * @return A list of all {@link Feedback} records.
     */
    List<Feedback> loadAll();

    /**
     * Persists the entire list of feedbacks to the data source, replacing any existing content.
     *
     * @param feedbacks The list of feedback entries to save.
     */
    void saveAll(List<Feedback> feedbacks);
}
