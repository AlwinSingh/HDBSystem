package src.repository;

import src.model.Report;
import java.util.List;

/**
 * Repository interface for managing {@link Report} data access operations.
 * Encapsulates loading and saving booking reports for applicants.
 */
public interface ReportRepository {

    /**
     * Loads all report entries from the data source.
     *
     * @return List of {@link Report} objects.
     */
    List<Report> loadAll();

    /**
     * Saves a list of report entries to the data source, overwriting existing records.
     *
     * @param reports The list of {@link Report} objects to save.
     */
    void saveAll(List<Report> reports);
}
