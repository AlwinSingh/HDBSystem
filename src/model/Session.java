package src.model;

import java.time.LocalDate;

public class Session {
    private String sessionId;
    private User user;
    private LocalDate loginTime;
    private LocalDate expiryTime;

    public Session(String sessionId, User user, LocalDate expiryTime) {
        this.sessionId = sessionId;
        this.user = user;
        this.loginTime = LocalDate.now();
        this.expiryTime = expiryTime;
    }

    public boolean validateSession() {
        return LocalDate.now().isBefore(expiryTime);
    }

    public void endSession() {
        System.out.println("Session " + sessionId + " ended.");
    }

    // Getters
}

