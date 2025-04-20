package src.service;

import src.model.Amenities;
import src.util.CsvUtil;
import src.util.FilePath;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Loads and filters amenities based on a given project name.
 */
public class AmenitiesLoader {

    /**
     * Returns a list of amenities that belong to the specified project.
     *
     * @param projectName The name of the project to search for.
     * @return A list of matching amenities.
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
