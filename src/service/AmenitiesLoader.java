package src.service;

import src.model.Amenities;
import src.util.CsvUtil;
import src.util.FilePath;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AmenitiesLoader {

    public static List<Amenities> loadAmenitiesByProject(String projectName) {
        List<Amenities> amenities = new ArrayList<>();
        List<Map<String,String>> rows = CsvUtil.read(FilePath.AMENITIES_LIST_FILE);

        for (Map<String,String> row : rows) {
            String proj = row.getOrDefault("Project Name", "").trim();
            if (!proj.equalsIgnoreCase(projectName)) continue;

            try {
                int    amenityId = Integer.parseInt(row.getOrDefault("AmenityID",  "0").trim());
                String type      = row.getOrDefault("Type",       "").trim();
                String name      = row.getOrDefault("Name",       "").trim();
                double distance  = Double.parseDouble(row.getOrDefault("Distance", "0").trim());

                // <-- pass 'proj' (not undefined 'projName' or 'proj')
                amenities.add(new Amenities(amenityId, type, name, distance, proj));
            } catch (Exception e) {
                System.err.println("⚠️ Skipped bad amenity row: " + row + " due to " + e.getMessage());
            }
        }
        return amenities;
    }
}
