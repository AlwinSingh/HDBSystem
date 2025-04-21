package src.service;

import java.util.List;
import java.util.Scanner;

import src.model.Amenities;
import src.model.HDBOfficer;
import src.model.Project;
import src.util.AmenitiesCsvMapper;

public class OfficerAmenityService {

    /**
     * Adds a new amenity to the officer’s assigned project or updates an existing one.
     *
     * @param officer The logged-in officer.
     * @param sc      Scanner for input.
     */
    public static void addOrUpdateAmenity(HDBOfficer officer, Scanner sc) {
        if (!canManageAmenities(officer)) {
            System.out.println("❌ Access denied. Officer registration status must be APPROVED to manage amenities.");
            return;
        }
    
        manageAmenityInteraction(officer.getAssignedProject(), sc);
    } 

    public static boolean canManageAmenities(HDBOfficer officer) {
        return "APPROVED".equalsIgnoreCase(officer.getRegistrationStatus())
            && officer.getAssignedProject() != null;
    }

    public static void manageAmenityInteraction(Project project, Scanner sc) {
        System.out.println("\n🏗️ Managing amenities for " + project.getProjectName());
        System.out.print("Would you like to (A)dd or (U)pdate an amenity? ");
        String action = sc.nextLine().trim().toUpperCase();

        switch (action) {
            case "A" -> addAmenity(project, sc);
            case "U" -> updateAmenity(project, sc);
            default -> System.out.println("❌ Invalid option. Please choose A or U.");
        }
    }

    private static void addAmenity(Project project, Scanner sc) {
        int nextId = AmenitiesCsvMapper.loadAll().stream()
            .map(Amenities::getAmenityId)
            .max(Integer::compareTo)
            .orElse(0) + 1;

        System.out.print("Type (e.g. MRT, Clinic): ");
        String type = sc.nextLine().trim();

        System.out.print("Name: ");
        String name = sc.nextLine().trim();

        System.out.print("Distance (km): ");
        double dist = getDoubleInput(sc);

        Amenities newAmenity = new Amenities(nextId, type, name, dist, project.getProjectName());
        AmenitiesCsvMapper.add(newAmenity);
        System.out.println("✅ Amenity added (ID=" + nextId + ").");
    }

    private static void updateAmenity(Project project, Scanner sc) {
        List<Amenities> amenities = AmenitiesCsvMapper.loadAll().stream()
            .filter(a -> a.getProjectName().equalsIgnoreCase(project.getProjectName()))
            .toList();

        if (amenities.isEmpty()) {
            System.out.println("📭 No amenities found for this project.");
            return;
        }

        System.out.println("\n📋 Existing Amenities:");
        for (Amenities a : amenities) {
            System.out.printf("ID: %d | Type: %s | Name: %s | Distance: %.2f km\n",
                a.getAmenityId(), a.getType(), a.getName(), a.getDistance());
        }

        System.out.print("Enter Amenity ID to update: ");
        int idToUpdate;
        try {
            idToUpdate = Integer.parseInt(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid ID.");
            return;
        }

        Amenities target = amenities.stream()
            .filter(a -> a.getAmenityId() == idToUpdate)
            .findFirst()
            .orElse(null);

        if (target == null) {
            System.out.println("❌ Amenity ID not found for this project.");
            return;
        }

        System.out.printf("Current Type [%s]: ", target.getType());
        String type = sc.nextLine().trim();
        if (!type.isEmpty()) target.setType(type);

        System.out.printf("Current Name [%s]: ", target.getName());
        String name = sc.nextLine().trim();
        if (!name.isEmpty()) target.setName(name);

        System.out.printf("Current Distance [%.2f]: ", target.getDistance());
        String distInput = sc.nextLine().trim();
        if (!distInput.isEmpty()) {
            try {
                target.setDistance(Double.parseDouble(distInput));
            } catch (NumberFormatException e) {
                System.out.println("❌ Invalid distance. Update skipped.");
                return;
            }
        }

        AmenitiesCsvMapper.update(target);
        System.out.println("✅ Amenity updated.");
    }

    private static double getDoubleInput(Scanner sc) {
        while (true) {
            try {
                return Double.parseDouble(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.print("❌ Invalid number. Please enter again: ");
            }
        }
    }

}
