package src.model;

import java.util.ArrayList;
import java.util.List;

public class SocialMediaIntegration {
    private int integrationId;
    private String platform;
    private String apiKey;

    public void postUpdate(String message) {
        System.out.println("Posted to " + platform + ": " + message);
    }

    public List<String> getRecentPosts() {
        // Stub only
        return new ArrayList<>();
    }
}

