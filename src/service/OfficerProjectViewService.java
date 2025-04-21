package src.service;

import java.util.List;

import src.model.Amenities;
import src.model.HDBOfficer;
import src.model.Project;
import src.model.ProjectLocation;

public class OfficerProjectViewService {
    public static List<Amenities> getProjectAmenities(Project project) {
        return project.getAmenities();
    }

    public static String getProjectSummary(Project p, HDBOfficer officer) {
        StringBuilder sb = new StringBuilder();
        ProjectLocation loc = p.getLocation();

        sb.append("🏢 Project Name       : ").append(p.getProjectName()).append("\n")
          .append("📍 Neighborhood       : ").append(p.getNeighborhood()).append("\n")
          .append("🌆 District & Town    : ").append(loc.getDistrict()).append(", ").append(loc.getTown()).append("\n")
          .append("📫 Address            : ").append(loc.getAddress()).append("\n")
          .append(String.format("🗺️ Coordinates         : %.6f, %.6f\n", loc.getLat(), loc.getLng()))
          .append("🧍 Officer Slots      : ").append(p.getOfficerSlots()).append("\n")
          .append("🏠 2-Room Units       : ").append(p.getRemainingFlats("2-Room")).append("\n")
          .append("💰 2-Room Price       : $").append(String.format("%.2f", p.getPrice2Room())).append("\n")
          .append("🏠 3-Room Units       : ").append(p.getRemainingFlats("3-Room")).append("\n")
          .append("💰 3-Room Price       : $").append(String.format("%.2f", p.getPrice3Room())).append("\n")
          .append("📅 Application Period : ").append(p.getOpenDate()).append(" to ").append(p.getCloseDate()).append("\n")
          .append("👀 Visible to Public  : ").append(p.isVisible() ? "Yes ✅" : "No ❌").append("\n")
          .append("📊 Your Registration  : ").append(officer.getRegistrationStatus());

        return sb.toString();
    }

    /**
     * Displays full details of the project the officer is assigned to.
     *
     * @param officer The logged-in officer.
     */
    public static void viewAssignedProjectDetails(HDBOfficer officer) {
        Project p = officer.getAssignedProject();
        if (p == null) {
            System.out.println("❌ No assigned project.");
            return;
        }

        // 🔄 Force-load amenities if not already set
        if (p.getAmenities() == null || p.getAmenities().isEmpty()) {
            p.setAmenities(AmenitiesLoader.loadAmenitiesByProject(p.getProjectName()));
        }

        System.out.println("\n📌 Assigned Project Details:");
        System.out.println(getProjectSummary(p, officer));

        List<Amenities> amenities = p.getAmenities();
        if (!amenities.isEmpty()) {
            System.out.println("\n🏞️ Nearby Amenities:");
            for (Amenities a : amenities) {
                System.out.println("   - " + a.toString());
            }
        }
    }

}
