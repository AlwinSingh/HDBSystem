package src.util;

import src.model.Amenities;
import src.repository.AmenitiesRepository;

import java.util.*;

/**
 * Utility class for loading, saving, appending, and updating amenities
 * in the CSV file defined by {@link FilePath#AMENITIES_LIST_FILE}.
 */
public class AmenitiesCsvMapper implements AmenitiesRepository {

    private static final String CSV = FilePath.AMENITIES_LIST_FILE;

    /**
     * Loads all amenities from the CSV file into a list of Amenities objects.
     *
     * @return List of amenities.
     */
    public List<Amenities> loadAll() {
        List<Map<String,String>> rows = CsvUtil.read(CSV);
        List<Amenities> out = new ArrayList<>();

        for (Map<String,String> r : rows) {
            int    id          = Integer.parseInt(r.getOrDefault("AmenityID","0"));
            String projectName = r.getOrDefault("Project Name","");
            String type        = r.getOrDefault("Type","");
            String name        = r.getOrDefault("Name","");
            double dist        = Double.parseDouble(r.getOrDefault("Distance","0"));

            out.add(new Amenities(id, type, name, dist, projectName));
        }
        return out;
    }

    /**
     * Saves a full list of amenities to the CSV, overwriting existing data.
     *
     * @param all The list of amenities to save.
     */
    public void saveAll(List<Amenities> all) {
        List<Map<String,String>> rows = new ArrayList<>();
        for (Amenities a : all) {
            Map<String,String> m = new LinkedHashMap<>();
            m.put("Project Name", a.getProjectName());
            m.put("AmenityID",    String.valueOf(a.getAmenityId()));
            m.put("Type",         a.getType());
            m.put("Name",         a.getName());
            m.put("Distance",     String.valueOf(a.getDistance()));
            rows.add(m);
        }
        CsvUtil.write(CSV, rows);
    }

    /**
     * Appends a new amenity to the CSV file.
     *
     * @param amenity The amenity to add.
     */
    public void add(Amenities amenity) {
        CsvUtil.append(CSV, toCsvRow(amenity));
    }

    /**
     * Converts an Amenity object to a CSV row format.
     *
     * @param a The amenity.
     * @return Map representing the CSV row.
     */
    private static Map<String, String> toCsvRow(Amenities a) {
        Map<String, String> m = new LinkedHashMap<>();
        m.put("Project Name", a.getProjectName());
        m.put("AmenityID",    String.valueOf(a.getAmenityId()));
        m.put("Type",         a.getType());
        m.put("Name",         a.getName());
        m.put("Distance",     String.valueOf(a.getDistance()));
        return m;
    }

    /**
     * Updates an existing amenity by matching its ID and replacing its data.
     *
     * @param updatedAmenity The updated amenity object.
     */
    public void update(Amenities updatedAmenity) {
        List<Amenities> all = loadAll();
        for (int i = 0; i < all.size(); i++) {
            if (all.get(i).getAmenityId() == updatedAmenity.getAmenityId()) {
                all.set(i, updatedAmenity);
                break;
            }
        }
        saveAll(all);
    }
}
