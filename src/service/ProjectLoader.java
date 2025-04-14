package src.service;

import src.model.Project;
import src.util.CsvUtil;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ProjectLoader {

    public static List<Project> loadProjects() {
        List<Project> projects = new ArrayList<>();
        List<Map<String, String>> rows = CsvUtil.read("data/ProjectList.csv");

        for (Map<String, String> row : rows) {
            try {
                Project p = new Project(); // using empty constructor

                p.setProjectName(row.get("Project Name"));
                p.setNeighborhood(row.get("Neighborhood"));

                // ✅ Parse unit counts and officer slots
                int flats2Room = Integer.parseInt(row.get("Number of units for Type 1"));
                int flats3Room = Integer.parseInt(row.get("Number of units for Type 2"));
                int officerSlots = Integer.parseInt(row.get("Officer Slot"));
                double price2Room = Double.parseDouble(row.get("Selling price for Type 1"));
                double price3Room = Double.parseDouble(row.get("Selling price for Type 2"));

                p.setPrice2Room(price2Room);
                p.setPrice3Room(price3Room);
                p.setAvailableFlats2Room(flats2Room);
                p.setAvailableFlats3Room(flats3Room);
                p.setOfficerSlots(officerSlots);

                // ✅ Parse dates using proper formatter (M/d/yyyy)
                try {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy");
                    p.setOpenDate(LocalDate.parse(row.get("Application opening date").trim(), formatter));
                    p.setCloseDate(LocalDate.parse(row.get("Application closing date").trim(), formatter));
                } catch (Exception e) {
                    System.out.println("⚠️ Date parse error for project: " + row.get("Project Name"));
                    System.out.println("⛔ Raw: " + row.get("Application opening date") + " to " + row.get("Application closing date"));
                }

                // ✅ Set visibility
                String vis = row.get("Visibility");
                p.setVisible(vis != null && vis.trim().equalsIgnoreCase("true"));

                projects.add(p);

            } catch (Exception e) {
                System.out.println("❌ Error reading row: " + row);
                e.printStackTrace();
            }
        }

        return projects;
    }
}
