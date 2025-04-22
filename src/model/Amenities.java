package src.model;

/**
 * Represents a nearby amenity (e.g., MRT station, school, mall) associated with an HDB project.
 * Contains attributes such as amenity type, name, distance from the project, and project linkage.
 */

public class Amenities {
    private int amenityId;
    private String type;
    private String name;
    private double distance; // in km
    private String projectName;

    /**
     * Constructs a new Amenity object.
     *
     * @param amenityId   Unique identifier for the amenity.
     * @param type        Type of the amenity (e.g., MRT, School).
     * @param name        Name of the amenity.
     * @param distance    Distance from the project in kilometers.
     * @param projectName The name of the project this amenity is linked to.
     */

    public Amenities(int amenityId,String type,String name,double distance,String projectName) {
        this.amenityId   = amenityId;
        this.type        = type;
        this.name        = name;
        this.distance    = distance;
        this.projectName = projectName;
    }

    /**
     * Gets the ID of the amenity.
     *
     * @return Amenity ID.
     */
    public int getAmenityId() {
        return amenityId;
    }

    /**
     * Gets the type of the amenity.
     *
     * @return Amenity type (e.g., MRT, School).
     */
    public String getType() {
        return type;
    }

    /**
     * Gets the name of the amenity.
     *
     * @return Name of the amenity.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the distance from the project.
     *
     * @return Distance in kilometers.
     */
    public double getDistance() {
        return distance;
    }


    /**
     * Updates the project name associated with the amenity.
     * @param projectName The project name to link this amenity to.
     */

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
        if (distance < 0) {
            throw new IllegalArgumentException("Distance cannot be negative.");
        }
        this.distance = distance;
    }

    /**
     * Returns a formatted string representation of the amenity for display.
     *
     * @return Amenities Details
     */

    @Override
    public String toString() {
        return String.format("🏞️ Amenity Details:\n - Type    : %s\n - Name    : %s\n - Distance: %.2f km", type, name, distance);
    }
}
