package src.service;

import java.util.Scanner;

import src.interfaces.IOfficerLocationService;
import src.model.HDBOfficer;
import src.model.Project;
import src.util.ProjectCsvMapper;


/**
 * Provides functionality for officers to update the location details
 * of their assigned HDB project, including address and geolocation.
 */
public class OfficerLocationService implements IOfficerLocationService {

    /**
     * Allows the officer to update the physical address and geolocation of their assigned project.
     *
     * @param officer The logged-in officer.
     * @param sc      Scanner for input.
     */
    public void updateLocation(HDBOfficer officer, Scanner sc) {
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

    /**
     * Checks if the officer is eligible to update project location.
     *
     * @param officer The HDB officer.
     * @return True if officer is approved and has a project assigned.
     */
    public boolean canUpdateLocation(HDBOfficer officer) {
        return "APPROVED".equalsIgnoreCase(officer.getRegistrationStatus())
            && officer.getAssignedProject() != null;
    }

    /**
     * Interactively updates the address, district, town, and coordinates of the given project.
     *
     * @param p  The project whose location is to be updated.
     * @param sc Scanner to capture input.
     * @return True if update succeeds; false on coordinate parsing error.
     */
    public boolean updateProjectLocation(Project p, Scanner sc) {
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
