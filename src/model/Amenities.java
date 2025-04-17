package src.model;

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
}
