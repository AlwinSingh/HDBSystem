package src.model;

import java.time.LocalDate;

public class MarketingMaterial {
    private int materialId;
    private String title;
    private String content;
    private LocalDate releaseDate;

    public void updateMaterial(String newContent) {
        this.content = newContent;
    }

    public String getMaterialSummary() {
        return title + " - " + content.substring(0, Math.min(50, content.length())) + "...";
    }
}

