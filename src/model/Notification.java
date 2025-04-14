package src.model;
import java.time.LocalDate;

public class Notification {
    private int notificationId;
    private String message;
    private LocalDate timestamp;
    private boolean isRead;

    public Notification(int notificationId, String message) {
        this.notificationId = notificationId;
        this.message = message;
        this.timestamp = LocalDate.now();
        this.isRead = false;
    }

    public void sendNotification() {
        System.out.println("Notification: " + message);
    }

    public void markAsRead() {
        this.isRead = true;
    }

    // Getters
}
