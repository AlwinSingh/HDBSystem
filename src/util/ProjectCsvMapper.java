package src.util;

import src.model.*;
import java.time.LocalDate;
import java.util.*;

public class ProjectCsvMapper {

    private static String safeTrim(String value) {
        return value != null ? value.trim() : "";
    }

    private static int safeInt(String value, int defaultValue) {
        try {
            return !safeTrim(value).isEmpty() ? Integer.parseInt(value.trim()) : defaultValue;
        } catch (Exception e) {
            return defaultValue;
        }
    }

    private static double safeDouble(String value, double defaultValue) {
        try {
            return !safeTrim(value).isEmpty() ? Double.parseDouble(value.trim()) : defaultValue;
        } catch (Exception e) {
            return defaultValue;
        }
    }

    private static LocalDate safeDate(String value, LocalDate defaultDate) {
        try {
            return !safeTrim(value).isEmpty() ? LocalDate.parse(value.trim()) : defaultDate;
        } catch (Exception e) {
            return defaultDate;
        }
    }

    public static Project fromCsvRow(Map<String, String> row) {
        String name = safeTrim(row.get("Project Name"));
        String neighborhood = safeTrim(row.get("Neighborhood"));

        int units2Room = safeInt(row.get("Number of units for Type 1"), 0);
        int units3Room = safeInt(row.get("Number of units for Type 2"), 0);
        int officerSlots = safeInt(row.get("Officer Slot"), 0);

        double price2Room = safeDouble(row.get("Price2Room"), 0);
        double price3Room = safeDouble(row.get("Price3Room"), 0);

        boolean visible = Boolean.parseBoolean(safeTrim(row.getOrDefault("Visible", "false")));

        LocalDate openDate = safeDate(row.get("OpenDate"), LocalDate.now());
        LocalDate closeDate = safeDate(row.get("CloseDate"), LocalDate.now());

        int locationId = safeInt(row.get("LocationId"), 0);
        String district = safeTrim(row.get("District"));
        String town = safeTrim(row.get("Town"));
        String address = safeTrim(row.get("Address"));
        double lat = safeDouble(row.get("Latitude"), 0);
        double lng = safeDouble(row.get("Longitude"), 0);

        ProjectLocation location = new ProjectLocation(locationId, district, town, address, lat, lng);

        Project p = new Project(name, neighborhood, openDate, closeDate, officerSlots, units2Room, units3Room, location);
        p.setPrice2Room(price2Room);
        p.setPrice3Room(price3Room);
        p.setVisible(visible);

        // Load Manager object
        String managerNric = safeTrim(row.getOrDefault("ManagerNRIC", ""));
        String managerName = safeTrim(row.getOrDefault("ManagerName", ""));

        if (!managerNric.isEmpty()) {
            HDBManager manager = ManagerCsvMapper.findByNric(managerNric);
            if (manager == null) {
                manager = new HDBManager(managerNric, "", managerName, 0, "");
            }
            p.setManager(manager);
        }

        // Officer NRICs
        String officerStr = safeTrim(row.get("OfficerNRICs"));
        if (!officerStr.isEmpty()) {
            Set<String> officerSet = new HashSet<>(Arrays.asList(officerStr.split("\\s+")));
            p.setOfficerNRICs(officerSet);
        }

        // Applicant NRICs
        String applicantStr = safeTrim(row.get("ApplicantNRICs"));
        if (!applicantStr.isEmpty()) {
            Set<String> applicantSet = new HashSet<>(Arrays.asList(applicantStr.split("\\s+")));
            p.setApplicantNRICs(applicantSet);
        }

        return p;
    }

    public static Map<String, String> toCsvRow(Project p) {
        Map<String, String> row = new LinkedHashMap<>();

        row.put("Project Name", p.getProjectName());
        row.put("Neighborhood", p.getNeighborhood());
        row.put("Number of units for Type 1", String.valueOf(p.getRemainingFlats("2-Room")));
        row.put("Number of units for Type 2", String.valueOf(p.getRemainingFlats("3-Room")));
        row.put("Officer Slot", String.valueOf(p.getOfficerSlots()));
        row.put("Price2Room", String.valueOf(p.getPrice2Room()));
        row.put("Price3Room", String.valueOf(p.getPrice3Room()));
        row.put("Visible", String.valueOf(p.isVisible()));
        row.put("OpenDate", p.getOpenDate().toString());
        row.put("CloseDate", p.getCloseDate().toString());

        ProjectLocation loc = p.getLocation();
        row.put("LocationId", String.valueOf(loc.getLocationId()));
        row.put("District", loc.getDistrict());
        row.put("Town", loc.getTown());
        row.put("Address", loc.getAddress());
        row.put("Latitude", String.valueOf(loc.getLat()));
        row.put("Longitude", String.valueOf(loc.getLng()));

        // Store manager NRIC and name
        if (p.getManager() != null) {
            row.put("ManagerNRIC", p.getManager().getNric());
            row.put("ManagerName", p.getManager().getName());
        } else {
            row.put("ManagerNRIC", "");
            row.put("ManagerName", "");
        }

        row.put("OfficerNRICs", String.join(" ", p.getOfficerNRICs()));
        row.put("ApplicantNRICs", String.join(" ", p.getApplicantNRICs()));

        return row;
    }

    public static List<Project> loadAll(String csvPath) {
        List<Map<String, String>> raw = CsvUtil.read(csvPath);
        List<Project> projects = new ArrayList<>();
        for (Map<String, String> row : raw) {
            try {
                projects.add(fromCsvRow(row));
            } catch (Exception e) {
                System.out.println("❌ Error parsing project row: " + row);
                e.printStackTrace();
            }
        }
        return projects;
    }

    public static void saveAll(String csvPath, List<Project> projects) {
        List<Map<String, String>> rows = new ArrayList<>();
        for (Project p : projects) {
            rows.add(toCsvRow(p));
        }
        CsvUtil.write(csvPath, rows);
    }
}
