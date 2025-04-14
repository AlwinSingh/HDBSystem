package src.model;

import java.time.LocalDate;

public class SystemMonitor {
    private int monitorId;
    private double currentLoad;
    private int errorCount;
    private LocalDate timestamp;

    public void collectMetrics() {
        this.currentLoad = Math.random(); // Simulated load
        this.errorCount = 0; // Reset after collection
        this.timestamp = LocalDate.now();
    }

    public String generateSystemStatusReport() {
        return "System Load: " + currentLoad + "\nErrors: " + errorCount + "\nTimestamp: " + timestamp;
    }
}

