package src.service;

import src.model.Project;
import src.model.Amenities;
import src.util.ProjectCsvMapper;
import java.util.List;

public class ProjectLoader {

    /**
     * Loads all projects from the CSV file and attaches related amenities.
     *
     * @return List of all loaded Project objects.
     */
    public static List<Project> loadProjects() {
        List<Project> projects = ProjectCsvMapper.loadAll();

        for (Project p : projects) {
            // Attach amenities from the separate CSV
            List<Amenities> amenities = AmenitiesLoader.loadAmenitiesByProject(p.getProjectName());
            p.setAmenities(amenities); 
        }

        return projects;
    }
}
