package src.model;

import java.time.LocalDate;
import java.util.*;

public class Project {
    private String projectName;
    private String neighborhood;
    private LocalDate openDate;
    private LocalDate closeDate;
    private boolean isVisible;
    private double price2Room;
    private double price3Room;
    private int officerSlots;
    private int availableFlats2Room;
    private int availableFlats3Room;
    private ProjectLocation location;
    private List<Amenities> amenities;
    private List<HDBOfficer> officers;
    private List<Enquiry> enquiries;
    private HDBManager manager;

    private Set<String> officerNRICs = new HashSet<>();
    private Set<String> applicantNRICs = new HashSet<>();

    public Project() {
        this.amenities = new ArrayList<>();
        this.officers = new ArrayList<>();
        this.enquiries = new ArrayList<>();
    }

    public Project(String projectName, String neighborhood, LocalDate openDate, LocalDate closeDate,
                   int officerSlots, int flats2Room, int flats3Room, ProjectLocation location) {
        this.projectName = projectName;
        this.neighborhood = neighborhood;
        this.openDate = openDate;
        this.closeDate = closeDate;
        this.isVisible = false;
        this.officerSlots = officerSlots;
        this.availableFlats2Room = flats2Room;
        this.availableFlats3Room = flats3Room;
        this.location = location;
        this.amenities = new ArrayList<>();
        this.officers = new ArrayList<>();
        this.enquiries = new ArrayList<>();
    }

    // Getters
    public String getProjectName() {
        return projectName;
    }

    public String getNeighborhood() {
        return neighborhood;
    }

    public LocalDate getOpenDate() {
        return openDate;
    }

    public LocalDate getCloseDate() {
        return closeDate;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public double getPrice2Room() {
        return price2Room;
    }

    public double getPrice3Room() {
        return price3Room;
    }

    public int getOfficerSlots() {
        return officerSlots;
    }

    public int getRemainingFlats(String flatType) {
        return switch (flatType) {
            case "2-Room" -> availableFlats2Room;
            case "3-Room" -> availableFlats3Room;
            default -> 0;
        };
    }

    public ProjectLocation getLocation() {
        return location;
    }

    public List<Amenities> getAmenities() {
        return amenities;
    }

    public List<HDBOfficer> getOfficers() {
        return officers;
    }

    public List<Enquiry> getEnquiries() {
        return enquiries;
    }

    public HDBManager getManager() {
        return this.manager;
    }

    public Set<String> getOfficerNRICs() {
        return officerNRICs;
    }

    public Set<String> getApplicantNRICs() {
        return applicantNRICs;
    }

    // Setters
    public void setProjectName(String name) {
        this.projectName = name;
    }

    public void setNeighborhood(String n) {
        this.neighborhood = n;
    }

    public void setOpenDate(LocalDate d) {
        this.openDate = d;
    }

    public void setCloseDate(LocalDate d) {
        this.closeDate = d;
    }

    public void setVisible(boolean v) {
        this.isVisible = v;
    }

    public void setOfficerSlots(int officerSlots) {
        this.officerSlots = officerSlots;
    }

    public void setAvailableFlats2Room(int availableFlats2Room) {
        this.availableFlats2Room = availableFlats2Room;
    }

    public void setAvailableFlats3Room(int availableFlats3Room) {
        this.availableFlats3Room = availableFlats3Room;
    }

    public void setPrice2Room(double price) {
        this.price2Room = price;
    }

    public void setPrice3Room(double price) {
        this.price3Room = price;
    }

    public void setLocation(ProjectLocation location) {
        this.location = location;
    }

    public void setAmenities(List<Amenities> amenities) {
        this.amenities = amenities;
    }

    public void setOfficers(List<HDBOfficer> officers) {
        this.officers = officers;
    }

    public void setEnquiries(List<Enquiry> enquiries) {
        this.enquiries = enquiries;
    }

    public void setManager(HDBManager manager) {
        this.manager = manager;
    }

    public void setOfficerNRICs(Set<String> officerNRICs) {
        this.officerNRICs = officerNRICs;
    }

    public void setApplicantNRICs(Set<String> applicantNRICs) {
        this.applicantNRICs = applicantNRICs;
    }

    // Helper methods
    public void openProject() {
        this.isVisible = true;
    }

    public void closeProject() {
        this.isVisible = false;
    }

    public void addOfficer(HDBOfficer o) {
        if (officers.size() < officerSlots) {
            officers.add(o);
        }
    }

    public void removeOfficer(HDBOfficer o) {
        officers.remove(o);
    }

    public void decrementFlatCount(String flatType) {
        if (flatType.equals("2-Room")) {
            availableFlats2Room = Math.max(0, availableFlats2Room - 1);
        } else if (flatType.equals("3-Room")) {
            availableFlats3Room = Math.max(0, availableFlats3Room - 1);
        }
    }

    public void addOfficerNric(String nric) {
        officerNRICs.add(nric);
    }

    public void removeOfficerNric(String nric) {
        officerNRICs.remove(nric);
    }

    public void addApplicantNric(String nric) {
        applicantNRICs.add(nric);
    }

    public void removeApplicantNric(String nric) {
        applicantNRICs.remove(nric);
    }
}
