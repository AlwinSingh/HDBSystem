package src.model;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class SessionManager {
    private Map<String, Session> activeSessions;

    public SessionManager() {
        activeSessions = new HashMap<>();
    }

    public Session createSession(User user) {
        String sessionId = "S" + System.currentTimeMillis(); // simple ID generation
        Session session = new Session(sessionId, user, LocalDate.now().plusDays(1));
        activeSessions.put(sessionId, session);
        System.out.println("Session created for: " + user.getName());
        return session;
    }

    public boolean validateSession(String sessionId) {
        if (!activeSessions.containsKey(sessionId)) return false;
        return activeSessions.get(sessionId).validateSession();
    }

    public void endSession(String sessionId) {
        if (activeSessions.containsKey(sessionId)) {
            activeSessions.get(sessionId).endSession();
            activeSessions.remove(sessionId);
        }
    }
}

