package src.model;

import java.time.LocalDate;

/**
 * Represents a reply message to an enquiry in the BTO system.
 * Stores the content, responder information, timestamp, and a role label.
 */

public class EnquiryReply {
    private int replyId;
    private String content;
    private LocalDate timestamp;
    private User responder;
    private String responderRole;

    /**
     * Constructs a new EnquiryReply.
     *
     * @param replyId   Unique ID for this reply.
     * @param content   Text content of the reply.
     * @param responder The user who sent the reply (e.g., officer or manager).
     */
    public EnquiryReply(int replyId, String content, User responder) {
        this.replyId = replyId;
        this.content = content;
        this.responder = responder;
        this.timestamp = LocalDate.now();
        this.responderRole = (responder instanceof HDBManager) ? "Manager" : "Officer";
    }

    /**
     * Gets the ID of this reply.
     * @return Reply ID.
     */
    public int getReplyId() {
        return replyId;
    }

    /**
     * Gets the content of the reply.
     * @return The reply message.
     */
    public String getContent() {
        return content;
    }

    /**
     * Gets the timestamp when the reply was created.
     * @return Date of reply.
     */
    public LocalDate getTimestamp() {
        return timestamp;
    }

    /**
     * Gets the user who submitted the reply.
     * @return Responder user.
     */
    public User getResponder() {
        return responder;
    }

    /**
     * Gets the responder's role (Officer or Manager).
     * @return Role string.
     */
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
        return String.format("%s (%s): %s", responder.getName(), responderRole, content);
    }
}
