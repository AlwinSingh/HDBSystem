package src.model;

import java.time.LocalDate;

public class EnquiryReply {
    private int replyId;
    private String content;
    private LocalDate timestamp;
    private User responder;

    public EnquiryReply(int replyId, String content, User responder) {
        this.replyId = replyId;
        this.content = content;
        this.responder = responder;
        this.timestamp = LocalDate.now();  // Capture the current date as the timestamp
    }

    // Getters
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

    // Operations

    /**
     * Edits the reply content.
     *
     * @param newContent the new content for the reply
     */
    public void editReply(String newContent) {
        this.content = newContent;
        System.out.println("Reply #" + replyId + " has been updated.");
    }

    /**
     * Marks this reply as deleted.
     * (This could be adjusted to remove it from the parent Enquiry if needed.)
     */
    public void deleteReply() {
        this.content = "[This reply has been deleted]";
        System.out.println("Reply #" + replyId + " has been deleted.");
    }
    
    @Override
    public String toString() {
        return "Reply #" + replyId + " by " + responder.getName() + " on " + timestamp + ": " + content;
    }
}
