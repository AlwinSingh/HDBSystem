package src.model;

import java.util.List;
import java.util.ArrayList;

public class ProjectLocation {
    private int locationId;
    private String district;
    private String town;
    private String address;

    public ProjectLocation(int locationId, String district, String town, String address) {
        this.locationId = locationId;
        this.district = district;
        this.town = town;
        this.address = address;
    }

    public String getFullAddress() {
        return address + ", " + town + ", " + district;
    }

    public List<String> getNearbyTransport() {
        // TODO: Stub - return list of nearby transport options
        return new ArrayList<>();
    }

    // Getters and Setters
}
