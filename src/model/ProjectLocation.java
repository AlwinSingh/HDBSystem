package src.model;

import java.util.List;
import java.util.ArrayList;

/**
 * Represents the geographical location of a project, including district, town,
 * address, and coordinates.
 */
public class ProjectLocation {
    private int locationId;
    private String district;
    private String town;
    private String address;
    private double lat;
    private double lng;

    /**
     * Constructs a new ProjectLocation with the specified details.
     *
     * @param locationId Unique identifier for this location.
     * @param district   Administrative district (e.g., North, Central).
     * @param town       Town or planning area (e.g., Yishun, Clementi).
     * @param address    Street-level address.
     * @param lat        Latitude coordinate.
     * @param lng        Longitude coordinate.
     */
    public ProjectLocation(int locationId, String district, String town, String address, double lat, double lng) {
        this.locationId = locationId;
        this.district = district;
        this.town = town;
        this.address = address;
        this.lat = lat;
        this.lng = lng;
    }

    /**
     * Returns a formatted address combining street, town, and district.
     *
     * @return Full address as a string.
     */
    public String getFullAddress() {
        return address + ", " + town + ", " + district;
    }

    /**
     * Placeholder for future enhancement. Intended to return nearby transport services.
     *
     * @return An empty list currently; future versions may populate it dynamically.
     */
    public List<String> getNearbyTransport() {
        return new ArrayList<>();
    }

    // Getters
    public int getLocationId() {
        return locationId;
    }

    public String getDistrict() {
        return district;
    }

    public String getTown() {
        return town;
    }

    public String getAddress() {
        return address;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    // Setters
    public void setLocationId(int locationId) {
        this.locationId = locationId;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }
}
