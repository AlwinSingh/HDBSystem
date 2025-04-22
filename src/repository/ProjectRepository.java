package src.repository;

import src.model.Project;
import java.util.List;

/**
 * Repository interface for managing {@link Project} data from a data source.
 * Encapsulates data access operations such as loading, saving, updating, and deleting project records.
 */
public interface ProjectRepository {

    /**
     * Loads all project entries from the data source.
     *
     * @return List of all {@link Project} objects.
     */
    List<Project> loadAll();

    /**
     * Persists a list of projects, replacing existing records.
     *
     * @param projects The list of {@link Project} objects to be saved.
     */
    void saveAll(List<Project> projects);

    /**
     * Appends a new project to the data source.
     *
     * @param project The {@link Project} object to be added.
     */
    void save(Project project);

    /**
     * Updates an existing project record in the data source.
     *
     * @param updated The {@link Project} object containing updated values.
     */
    void updateProject(Project updated);

    /**
     * Deletes a project from the data source by matching its name.
     *
     * @param name The name of the project to delete.
     */
    void deleteProjectByName(String name);
}
