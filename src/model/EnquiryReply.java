package src.model;

import java.time.LocalDate;

/**
 * Represents a reply to an enquiry, including responder info, timestamp, and content.
 */
public class EnquiryReply {
    private int replyId;
    private String content;
    private LocalDate timestamp;
    private User responder;
    private String responderRole;

    public EnquiryReply(int replyId, String content, User responder) {
        this.replyId = replyId;
        this.content = content;
        this.responder = responder;
        this.timestamp = LocalDate.now();
        this.responderRole = (responder instanceof HDBManager) ? "Manager" : "Officer";
    }
    // === Business Logic ===

    /**
     * Updates the reply content and refreshes the timestamp to now.
     *
     * @param newContent Updated reply text.
     */
    public void editReply(String newContent) {
        this.content = newContent;
        this.timestamp = LocalDate.now(); // update timestamp on edit
    }

    /**
     * Marks the reply content as deleted.
     */
    public void deleteReply() {
        this.content = "[deleted]";
    }

    // === Getters ===
    public int getReplyId() {
        return replyId;
    }

    public String getContent() {
        return content;
    }

    public LocalDate getTimestamp() {
        return timestamp;
    }

    public User getResponder() {
        return responder;
    }

    public String getResponderRole() {
        return responderRole;
    }
    

    // === Optional: toString for CSV/debugging ===
    /**
     * Returns a string representation of the reply for debugging or CSV use.
     *
     * @return Name and content string.
     */
    @Override
    public String toString() {
        return responder.getName() + ": " + content;
    }
}
