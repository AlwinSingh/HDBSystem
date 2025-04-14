package src.model;

import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

public class AuditLog {
    private int logId;
    private LocalDate timestamp;
    private User user;
    private String action;
    private String description;

    public AuditLog(int logId, User user, String action, String description) {
        this.logId = logId;
        this.timestamp = LocalDate.now();
        this.user = user;
        this.action = action;
        this.description = description;
    }

    public void recordLog() {
        System.out.println("[" + timestamp + "] " + user.getName() + ": " + action + " - " + description);
    }

    public List<AuditLog> getLogsByUser(User user) {
        // Stub only - simulate filtered logs
        return new ArrayList<>();
    }

    // Getters
}

