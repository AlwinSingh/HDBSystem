package src.model;

import java.util.List;
import java.util.ArrayList;

public class ProjectLocation {
    private int locationId;
    private String district;
    private String town;
    private String address;
    private double lat;
    private double lng;

    public ProjectLocation(int locationId, String district, String town, String address, double lat, double lng) {
        this.locationId = locationId;
        this.district = district;
        this.town = town;
        this.address = address;
        this.lat = lat;
        this.lng = lng;
    }

    public String getFullAddress() {
        return address + ", " + town + ", " + district;
    }

    public List<String> getNearbyTransport() {
        // Stub - future implementation
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
