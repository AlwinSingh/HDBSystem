package src.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/* Fields required based on CSV and brief
name, neighbourhood
units2Room, units3Room
price2Room, price3Room
openDate, closeDate
visibility
managerNRIC
officerNRICs (comma-separated list)
Optionally: applicantNRICs

This means the following methods ARE NEEDED:
isOpen() — checks if current date is within range
hasAvailableUnits(String flatType)
bookUnit(String flatType)
toggleVisibility(boolean)
displaySummary() — for CLI display
*/

/**
 * Represents a BTO Project with limited units and application period.
 */
public class Project {
    private String name;
    private String neighbourhood;

    private int units2Room;
    private int units3Room;

    private double price2Room;
    private double price3Room;

    private LocalDate openDate;
    private LocalDate closeDate;

    private String managerName;

    private int officerSlot;
    private List<String> officerNames;

    private String managerNRIC; // A project is only assigned to 1 manager...
    private List<String> officerNRICs;
    private List<String> applicantNRICs;

    private boolean visibility;

    public Project(String name, String neighbourhood, int units2Room, double price2Room,
                   int units3Room, double price3Room, LocalDate openDate, LocalDate closeDate,
                   String managerName, int officerSlot, List<String> officerNames,
                   String managerNRIC, boolean visibility) {
        this.name = name;
        this.neighbourhood = neighbourhood;
        this.units2Room = units2Room;
        this.price2Room = price2Room;
        this.units3Room = units3Room;
        this.price3Room = price3Room;
        this.openDate = openDate;
        this.closeDate = closeDate;
        this.managerName = managerName;
        this.officerSlot = officerSlot;
        this.officerNames = officerNames;
        this.managerNRIC = managerNRIC;
        this.officerNRICs = new ArrayList<>();
        this.applicantNRICs = new ArrayList<>();
        this.visibility = visibility;
    }

    public String getName() {
        return name;
    }

    public String getNeighbourhood() {
        return neighbourhood;
    }

    public int getTwoRoomUnits() {
        return units2Room;
    }

    public int getThreeRoomUnits() {
        return units3Room;
    }

    public double getTwoRoomPrice() {
        return price2Room;
    }

    public double getThreeRoomPrice() {
        return price3Room;
    }

    public LocalDate getOpenDate() {
        return openDate;
    }

    public LocalDate getCloseDate() {
        return closeDate;
    }

    public void setTwoRoomUnits(int units2Room) {
        this.units2Room = units2Room;
    }

    public void setThreeRoomUnits(int units3Room) {
        this.units3Room = units3Room;
    }

    public boolean isOpen() {
        LocalDate today = LocalDate.now();
        return !today.isBefore(openDate) && !today.isAfter(closeDate);
    }

    public boolean isVisible() {
        return visibility;
    }

    public void toggleVisibility(boolean visible) {
        this.visibility = visible;
    }

    public boolean hasAvailableUnits(String flatType) {
        if ("2-Room".equalsIgnoreCase(flatType)) {
            return units2Room > 0;
        } else if ("3-Room".equalsIgnoreCase(flatType)) {
            return units3Room > 0;
        }
        return false;
    }

    public boolean bookUnit(String flatType) {
        if ("2-Room".equalsIgnoreCase(flatType) && units2Room > 0) {
            units2Room--;
            return true;
        } else if ("3-Room".equalsIgnoreCase(flatType) && units3Room > 0) {
            units3Room--;
            return true;
        }
        return false;
    }

    public void addOfficer(String officerNric) {
        if (!officerNRICs.contains(officerNric)) {
            officerNRICs.add(officerNric);
        }
    }

    public void addApplicant(String applicantNric) {
        if (!applicantNRICs.contains(applicantNric)) {
            applicantNRICs.add(applicantNric);
        }
    }

    public String getManagerNRIC() {
        return managerNRIC;
    }

    public void setManagerNRIC(String managerNRIC) {
        this.managerNRIC = managerNRIC;
    }

    public List<String> getOfficerNames() {
        return officerNames;
    }

    public List<String> getOfficerNRICs() {
        return officerNRICs;
    }

    public List<String> getApplicantNRICs() {
        return applicantNRICs;
    }

    public String getManagerName() {
        return managerName;
    }

    public void setManagerName(String managerName) {
        this.managerName = managerName;
    }

    public int getOfficerSlot() {
        return officerSlot;
    }

    public void displaySummary() {
        System.out.println("\n--- Project: " + name + " ---");
        System.out.println("Location: " + neighbourhood);
        System.out.println("Application Period: " + openDate + " to " + closeDate);
        System.out.println("2-Room Units Left: " + units2Room + " ($" + price2Room + ")");
        System.out.println("3-Room Units Left: " + units3Room + " ($" + price3Room + ")");
        System.out.println("Visibility: " + (visibility ? "ON" : "OFF"));
    }
}

