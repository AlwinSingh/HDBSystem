package src.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ProjectTimeline {
    private int timelineId;
    private List<String> events;

    public ProjectTimeline() {
        this.events = new ArrayList<>();
    }

    public void addEvent(String event, LocalDate date) {
        events.add(date + ": " + event);
    }

    public List<String> getTimeline() {
        return events;
    }
}

