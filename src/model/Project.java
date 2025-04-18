package src.model;

import java.time.LocalDate;
import java.util.List;

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
                   String managerNRIC, List<String> officerNRICs, List<String> applicantNRICs, boolean visibility) {
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
        this.officerNRICs = officerNRICs;
        this.applicantNRICs = applicantNRICs;
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

    public void setName(String name) {
        this.name = name;
    }

    public void setTwoRoomUnits(int units2Room) {
        this.units2Room = units2Room;
    }

    public void setTwoRoomPrice(double price2Room) {
        this.price2Room = price2Room;
    }

    public void setThreeRoomUnits(int units3Room) {
        this.units3Room = units3Room;
    }

    public void setThreeRoomPrice(double price3Room) {
        this.price3Room = price3Room;
    }

    public void setOpenDate(LocalDate openDate) {
        this.openDate = openDate;
    }

    public void setCloseDate(LocalDate closeDate) {
        this.closeDate = closeDate;
    }

    public boolean isOpen() {
        LocalDate today = LocalDate.now();
        return !today.isBefore(openDate) && !today.isAfter(closeDate);
    }

    public boolean isVisible() {
        return visibility;
    }

    public void setVisibility(boolean visible) {
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

    public void addApplicant(String applicantNric) {
        if (!applicantNRICs.contains(applicantNric)) {
            applicantNRICs.add(applicantNric);
        }
    }

    public String getManagerNRIC() {
        return managerNRIC;
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

    public int getOfficerSlot() {
        return officerSlot;
    }

    public void displaySummary() {
        System.out.println("--- Project: " + name + " ---");
        System.out.println("Location: " + neighbourhood);
        System.out.println("Application Period: " + openDate + " to " + closeDate);
        System.out.println("2-Room Units Left: " + units2Room + " ($" + price2Room + ")");
        System.out.println("3-Room Units Left: " + units3Room + " ($" + price3Room + ")");
        //System.out.println("Visibility: " + (visibility ? "ON" : "OFF"));
        System.out.println("\n");
    }
}

