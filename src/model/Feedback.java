package src.model;

import java.time.LocalDate;

public class Feedback {
    private int feedbackId;
    private String content;
    private int rating; // 1 to 5
    private LocalDate dateSubmitted;

    public Feedback(int feedbackId, String content, int rating) {
        this.feedbackId = feedbackId;
        this.content = content;
        this.rating = rating;
        this.dateSubmitted = LocalDate.now();
    }

    public int getRating() {
        return rating;
    }
    
    public String getContent() {
        return content;
    }
    

    public void submitFeedback() {
        // Simulate storing feedback
        System.out.println("Feedback submitted: " + content);
    }

    public void editFeedback(String newContent) {
        this.content = newContent;
    }

    public void deleteFeedback() {
        this.content = "[deleted]";
    }

    // Getters
}
