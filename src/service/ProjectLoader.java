package src.service;

import src.model.Project;
import src.repository.ProjectRepository;
import src.model.Amenities;
import src.util.ProjectCsvMapper;
import java.util.List;


/**
 * Utility service class responsible for loading Project data from CSV,
 * and enriching each project with its associated list of amenities.
 */
public class ProjectLoader {
    private static final ProjectRepository projectRepository = new ProjectCsvMapper();
    /**
     * Loads all projects from the CSV file via {@link ProjectCsvMapper}
     * and injects the related amenities using {@link AmenitiesLoader}.
     *
     * @return A list of {@link Project} objects with amenities populated.
     */
    public static List<Project> loadProjects() {
        List<Project> projects = projectRepository.loadAll();

        for (Project p : projects) {
            List<Amenities> amenities = AmenitiesLoader.loadAmenitiesByProject(p.getProjectName());
            p.setAmenities(amenities); 
        }

        return projects;
    }
}
