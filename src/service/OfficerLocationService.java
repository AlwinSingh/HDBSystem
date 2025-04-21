package src.service;

import java.util.Scanner;

import src.model.HDBOfficer;
import src.model.Project;
import src.util.ProjectCsvMapper;

public class OfficerLocationService {

    /**
     * Allows the officer to update the physical address and geolocation of their assigned project.
     *
     * @param officer The logged-in officer.
     * @param sc      Scanner for input.
     */
    public static void updateLocation(HDBOfficer officer, Scanner sc) {
        if (!canUpdateLocation(officer)) {
            System.out.println("❌ Access denied. Officer registration must be APPROVED and a project must be assigned.");
            return;
        }

        Project p = officer.getAssignedProject();
        boolean success = updateProjectLocation(p, sc);
        if (success) {
            System.out.println("✅ Location updated.");
        }
    }

    public static boolean canUpdateLocation(HDBOfficer officer) {
        return "APPROVED".equalsIgnoreCase(officer.getRegistrationStatus())
            && officer.getAssignedProject() != null;
    }

    public static boolean updateProjectLocation(Project p, Scanner sc) {
        if (p == null) return false;

        System.out.println("\n✏️  Update location for " + p.getProjectName());

        System.out.printf("Current District [%s]: ", p.getLocation().getDistrict());
        String input = sc.nextLine().trim();
        if (!input.isEmpty()) p.getLocation().setDistrict(input);

        System.out.printf("Current Town     [%s]: ", p.getLocation().getTown());
        input = sc.nextLine().trim();
        if (!input.isEmpty()) p.getLocation().setTown(input);

        System.out.printf("Current Address  [%s]: ", p.getLocation().getAddress());
        input = sc.nextLine().trim();
        if (!input.isEmpty()) p.getLocation().setAddress(input);

        try {
            System.out.printf("Current Latitude [%.6f]: ", p.getLocation().getLat());
            input = sc.nextLine().trim();
            if (!input.isEmpty()) p.getLocation().setLat(Double.parseDouble(input));

            System.out.printf("Current Longitude[%.6f]: ", p.getLocation().getLng());
            input = sc.nextLine().trim();
            if (!input.isEmpty()) p.getLocation().setLng(Double.parseDouble(input));
        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid coordinates – update aborted.");
            return false;
        }

        ProjectCsvMapper.updateProject(p);
        return true;
    }

}
