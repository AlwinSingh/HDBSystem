package src.model;

public class Amenities {
    private int amenityId;
    private String type;
    private String name;
    private double distance; // in km

    public Amenities(int amenityId, String type, String name, double distance) {
        this.amenityId = amenityId;
        this.type = type;
        this.name = name;
        this.distance = distance;
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
}
