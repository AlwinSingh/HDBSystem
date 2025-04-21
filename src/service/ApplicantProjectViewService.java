package src.service;

import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import src.interfaces.IApplicantProjectViewService;
import src.model.Amenities;
import src.model.Applicant;
import src.model.Project;

/**
 * Helps applicants view and filter available housing projects.
 * 
 * Shows project details, supports filtering by district, neighborhood, and flat type.
 */
public class ApplicantProjectViewService implements IApplicantProjectViewService {
    private static final ApplicantEligibilityService eligibilityService = new ApplicantEligibilityService();
    private static String filterNeighborhood = null;
    private static String filterDistrict = null;
    private static String filterFlatType = null;

    /**
     * Displays key information about the selected project in a user-friendly format.
     *
     * @param p         The project to display.
     * @param applicant The applicant viewing the project.
     */
    @Override
    public void displayProjectDetails(Project p, Applicant applicant) {
        System.out.println("────────────────────────────");
        System.out.println("🏠 Project Name      : " + p.getProjectName());
        System.out.println("📍 Location          : " + p.getNeighborhood());
        System.out.println("🏙️ District & Town   : " + p.getLocation().getDistrict() + ", " + p.getLocation().getTown());
        System.out.println("🗺️ Address           : " + p.getLocation().getAddress());
        System.out.println("📅 Application Period: " + p.getOpenDate() + " to " + p.getCloseDate());
        System.out.println("🏢 2-Room Units      : " + p.getRemainingFlats("2-Room") + " ($" + p.getPrice2Room() + ")");

        if (applicant.getMaritalStatus().equalsIgnoreCase("Married")) {
            System.out.println("🏢 3-Room Units      : " + p.getRemainingFlats("3-Room") + " ($" + p.getPrice3Room() + ")");
        }

        if (!p.getAmenities().isEmpty()) {
            System.out.println("🏞️ Nearby Amenities:");
            for (Amenities a : p.getAmenities()) {
                System.out.println("   - " + a.toString());
            }
        }
        System.out.println();
    }

    /**
     * Displays and filters eligible projects, allowing the user to apply filter options.
     *
     * @param applicant The current applicant.
     * @param sc        Scanner for user input.
     */
    @Override
    public void handleViewEligibleProjects(Applicant applicant, Scanner sc) {
        while (true) {
            List<Project> filtered = getFilteredEligibleProjects(
                    applicant,
                    filterNeighborhood,
                    filterDistrict,
                    filterFlatType
            );

            System.out.println("\n📋 Eligible Open Projects:");
            if (filtered.isEmpty()) {
                System.out.println("❌ No eligible projects found for current filters.");
            } else {
                for (Project p : filtered) {
                    displayProjectDetails(p, applicant);
                }
            }

            System.out.println("\n===== 🔧 Filter Options =====");
            System.out.println(" [1] Apply Filter");
            System.out.println(" [2] Clear Filters");
            System.out.println(" [0] Back");
            System.out.print("➡️ Enter your choice: ");
            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1" -> applyProjectFilters(sc);
                case "2" -> {
                    filterNeighborhood = null;
                    filterDistrict = null;
                    filterFlatType = null;
                    System.out.println("✅ Filters cleared.");
                }
                case "0" -> {
                    System.out.println("🔙 Returning to dashboard...");
                    return;
                }
                default -> System.out.println("❌ Invalid input.");
            }
        }
    }

    /**
     * Prompts the user to input project filter options.
     *
     * @param sc Scanner for input.
     */
    public void applyProjectFilters(Scanner sc) {
        System.out.print("🏘️  Neighborhood [" + optional(filterNeighborhood) + "]: ");
        String n = sc.nextLine().trim();
        if (!n.isBlank()) filterNeighborhood = n;

        System.out.print("🏙️  District [" + optional(filterDistrict) + "]: ");
        String d = sc.nextLine().trim();
        if (!d.isBlank()) filterDistrict = d;

        System.out.print("🏢 Flat Type (2-Room / 3-Room) [" + optional(filterFlatType) + "]: ");
        String f = sc.nextLine().trim();
        if (!f.isBlank()) filterFlatType = f;
    }

    /**
     * Returns a user-friendly placeholder for filter prompts.
     */
    private static String optional(String value) {
        return value == null ? "Any" : value;
    }

    /**
     * Gets all projects that the applicant is currently eligible for.
     *
     * @param applicant The applicant in question.
     * @return A list of eligible projects.
     */
    @Override
    public List<Project> getEligibleProjects(Applicant applicant) {
        return ProjectLoader.loadProjects().stream()
                .filter(p -> p != null && p.getProjectName() != null)
                .filter(Project::isVisible)
                .filter(p -> eligibilityService.isEligible(applicant, p))
                .collect(Collectors.toList());
    }

    /**
     * Returns eligible projects based on optional filters.
     */
    @Override
    public List<Project> getFilteredEligibleProjects(
        Applicant applicant,
        String neighborhood,
        String district,
        String flatType
    ) 
    {
    return ProjectLoader.loadProjects().stream()
            .filter(p -> p != null && p.getProjectName() != null)
            .filter(Project::isVisible)
            .filter(p -> eligibilityService.isEligible(applicant, p))
            .filter(p -> neighborhood == null || p.getNeighborhood().equalsIgnoreCase(neighborhood))
            .filter(p -> district == null || p.getLocation().getDistrict().equalsIgnoreCase(district))
            .filter(p -> flatType == null || p.getRemainingFlats(flatType) > 0)
            .sorted(Comparator.comparing(Project::getProjectName))
            .toList();
    }

}
