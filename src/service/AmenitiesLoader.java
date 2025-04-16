package src.service;

import src.model.Amenities;
import src.util.CsvUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AmenitiesLoader {

    public static List<Amenities> loadAmenitiesByProject(String projectName) {
        List<Amenities> amenities = new ArrayList<>();
        List<Map<String, String>> rows = CsvUtil.read("data/ProjectAmenities.csv");

        for (Map<String, String> row : rows) {
            String projName = row.getOrDefault("Project Name", "").trim();
            if (!projName.equalsIgnoreCase(projectName)) continue;

            try {
                int amenityId = Integer.parseInt(row.getOrDefault("AmenityId", "0").trim());
                String type = row.getOrDefault("Type", "").trim();
                String name = row.getOrDefault("Name", "").trim();
                double distance = Double.parseDouble(row.getOrDefault("Distance", "0").trim());

                Amenities amenity = new Amenities(amenityId, type, name, distance);
                amenities.add(amenity);
            } catch (Exception e) {
                System.err.println("⚠️ Skipped bad amenity row: " + row + " due to " + e.getMessage());
            }
        }

        return amenities;
    }
}
