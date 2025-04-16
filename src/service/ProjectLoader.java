package src.service;

import src.model.Project;
import src.model.Amenities;
import src.util.ProjectCsvMapper;

import java.util.List;

public class ProjectLoader {
    private static final String PROJECT_CSV_PATH = "data/ProjectList.csv";

    public static List<Project> loadProjects() {
        List<Project> projects = ProjectCsvMapper.loadAll(PROJECT_CSV_PATH);

        for (Project p : projects) {
            // Attach amenities from the separate CSV
            List<Amenities> amenities = AmenitiesLoader.loadAmenitiesByProject(p.getProjectName());
            p.setAmenities(amenities);  // This is the key line you're missing
        }

        return projects;
    }
}
