package src.model;

import java.time.LocalDate;
import java.util.*;

/**
 * Represents an HDB housing project. Encapsulates project information including pricing,
 * flat availability, officer assignments, application periods, and public visibility.
 */
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

    /**
     * Default constructor initializes empty amenity/officer/enquiry lists.
     */
    public Project() {
        this.amenities = new ArrayList<>();
        this.officers = new ArrayList<>();
        this.enquiries = new ArrayList<>();
    }

    /**
     * Constructs a Project with all required fields.
     *
     * @param projectName  Name of the project.
     * @param neighborhood Neighborhood name.
     * @param openDate     Project open date.
     * @param closeDate    Project close date.
     * @param officerSlots Max number of officers.
     * @param flats2Room   Total 2-room units.
     * @param flats3Room   Total 3-room units.
     * @param location     Project location.
     */
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

    /**
     * Returns remaining units for the specified flat type.
     *
     * @param flatType "2-Room" or "3-Room".
     * @return Number of units available.
     */
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

    public int getAvailableFlats2Room() {
        return availableFlats2Room;
    }
    
    public int getAvailableFlats3Room() {
        return availableFlats3Room;
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

    /**
     * Marks the project as open (visible to public).
     */
    public void openProject() {
        this.isVisible = true;
    }

    /**
     * Marks the project as closed (hidden from public view).
     */
    public void closeProject() {
        this.isVisible = false;
    }

    /**
     * Adds an officer to the project if slots are available.
     */
    public void addOfficer(HDBOfficer o) {
        if (officers.size() < officerSlots) {
            officers.add(o);
        }
    }

    /**
     * Removes the specific officer from the project.
     */
    public void removeOfficer(HDBOfficer o) {
        officers.remove(o);
    }

    /**
     * Reduces the number of available units for a flat type by 1 (if above 0).
     *
     * @param flatType The flat type to decrement (e.g., "2-Room").
     */
    public void decrementFlatCount(String flatType) {
        if (flatType.equals("2-Room")) {
            availableFlats2Room = Math.max(0, availableFlats2Room - 1);
        } else if (flatType.equals("3-Room")) {
            availableFlats3Room = Math.max(0, availableFlats3Room - 1);
        }
    }

    /**
     * Adds an officer's NRIC to the project.
     *
     * @param nric Officer's NRIC.
     */
    public void addOfficerNric(String nric) {
        officerNRICs.add(nric);
    }

    /**
     * Removes an officer's NRIC from the project.
     *
     * @param nric Officer's NRIC.
     */
    public void removeOfficerNric(String nric) {
        officerNRICs.remove(nric);
    }

    /**
     * Adds an applicant's NRIC to the project.
     *
     * @param nric Applicant's NRIC.
     */
    public void addApplicantNric(String nric) {
        applicantNRICs.add(nric);
    }

    /**
     * Removes an applicant's NRIC from the project.
     *
     * @param nric Applicant's NRIC.
     */
    public void removeApplicantNric(String nric) {
        applicantNRICs.remove(nric);
    }
}
