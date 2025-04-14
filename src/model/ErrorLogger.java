package src.model;

import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

public class ErrorLogger {
    private int errorId;
    private String errorMessage;
    private LocalDate timestamp;
    private String severity;

    public void logError(String errorMessage, String severity) {
        this.errorId = (int) (Math.random() * 10000); // Mock ID
        this.timestamp = LocalDate.now();
        this.errorMessage = errorMessage;
        this.severity = severity;
        System.out.println("[" + severity + "] " + errorMessage);
    }

    public List<ErrorLogger> getErrorsBySeverity(String severity) {
        // TODO: Stub - simulate filtering logic
        return new ArrayList<>();
    }
}

