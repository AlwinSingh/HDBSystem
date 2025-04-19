package src.util;

import src.model.Amenities;
import java.util.*;

public class AmenitiesCsvMapper {
    private static final String CSV = FilePath.AMENITIES_LIST_FILE;

    public static List<Amenities> loadAll() {
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

    public static void saveAll(List<Amenities> all) {
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

    public static void add(Amenities amenity) {
        CsvUtil.append(CSV, toCsvRow(amenity));
    }

    private static Map<String, String> toCsvRow(Amenities a) {
        Map<String, String> m = new LinkedHashMap<>();
        m.put("Project Name", a.getProjectName());
        m.put("AmenityID",    String.valueOf(a.getAmenityId()));
        m.put("Type",         a.getType());
        m.put("Name",         a.getName());
        m.put("Distance",     String.valueOf(a.getDistance()));
        return m;
    }

    public static void update(Amenities updatedAmenity) {
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
