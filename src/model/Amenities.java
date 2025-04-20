package src.model;

/**
 * Represents a nearby amenity (e.g., school, mall, MRT) associated with a project.
 * Each amenity includes its type, name, distance from the project, and project link.
 */
public class Amenities {
    private int amenityId;
    private String type;
    private String name;
    private double distance; // in km
    private String projectName;

    public Amenities(int amenityId,String type,String name,double distance,String projectName) {
        this.amenityId   = amenityId;
        this.type        = type;
        this.name        = name;
        this.distance    = distance;
        this.projectName = projectName;
    }

    // Getter methods
    public int getAmenityId() {
        return amenityId;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public double getDistance() {
        return distance;
    }

    // Optional: helpful for displaying amenity information
    public String getAmenityDetails() {
        return type + ": " + name + " (" + distance + " km away)";
    }

    public String getProjectName() {
        return projectName;
    }
    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public void setType(String type) {
        this.type = type;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public void setDistance(double distance) {
        this.distance = distance;
    }
    
}
