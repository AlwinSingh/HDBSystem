package src.service;

import src.model.Amenities;
import src.util.CsvUtil;
import src.util.FilePath;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Utility service responsible for loading amenities from a CSV file.
 * Supports filtering amenities by project name.
 */
public class AmenitiesLoader {

    /**
     * Loads all amenities associated with the specified project.
     * Each amenity is matched by the "Project Name" column in the CSV.
     *
     * @param projectName The name of the project to filter amenities for.
     * @return A list of {@link Amenities} objects linked to the specified project.
     */
    public static List<Amenities> loadAmenitiesByProject(String projectName) {
        List<Amenities> result = new ArrayList<>();
        List<Map<String, String>> rows = CsvUtil.read(FilePath.AMENITIES_LIST_FILE);

        for (Map<String, String> row : rows) {
            String currentProject = row.getOrDefault("Project Name", "").trim();
            if (!currentProject.equalsIgnoreCase(projectName)) continue;

            try {
                int amenityId = Integer.parseInt(row.getOrDefault("AmenityID", "0").trim());
                String type = row.getOrDefault("Type", "").trim();
                String name = row.getOrDefault("Name", "").trim();
                double distance = Double.parseDouble(row.getOrDefault("Distance", "0").trim());

                result.add(new Amenities(amenityId, type, name, distance, currentProject));
            } catch (Exception e) {
                System.err.println("⚠️ Skipped malformed amenity row: " + row + " due to: " + e.getMessage());
            }
        }

        return result;
    }
}
