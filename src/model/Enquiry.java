package src.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents an enquiry made by an applicant about a project.
 * Includes content, status, project info, and reply history.
 */
public class Enquiry {
    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_OPEN = "OPEN";
    public static final String STATUS_CLOSED = "CLOSED";
    public static final String STATUS_DELETED = "DELETED";

    private int enquiryId;
    private String content;
    private String status;
    private String applicantNric;
    private String applicantName;
    private String projectName;
    private final List<EnquiryReply> replies;

    public Enquiry(int enquiryId, String content, String status, String applicantNric, String applicantName, String projectName) {
        this.enquiryId = enquiryId;
        this.content = content;
        this.status = (status != null) ? status : STATUS_OPEN;
        this.applicantNric = applicantNric;
        this.applicantName = applicantName;
        this.projectName = projectName;
        this.replies = new ArrayList<>();
    }

    // === Business Logic ===

    /**
     * Adds a reply to the enquiry and closes the enquiry automatically.
     *
     * @param content   The reply message.
     * @param responder The user responding (officer or manager).
     */
    public void addReply(String content, User responder) {
        int nextId = replies.size() + 1;
        replies.add(new EnquiryReply(nextId, content, responder)); 
        close();
    }

    /**
     * Adds an already-created reply and closes the enquiry.
     *
     * @param reply A reply object.
     */
    public void addReply(EnquiryReply reply) {
        replies.add(reply);
        close(); // Auto-close on reply
    }

    public void editContent(String newContent) {
        this.content = newContent;
    }

    /**
     * Marks the enquiry as deleted, clears content and replies.
     */
    public void delete() {
        this.content = "[deleted]";
        this.status = STATUS_DELETED;
        this.replies.clear();
    }

    /**
     * Marks the enquiry as closed.
     */
    public void close() {
        this.status = STATUS_CLOSED;
    }

    public boolean isClosed() {
        return STATUS_CLOSED.equalsIgnoreCase(status);
    }

    public boolean isOpen() {
        return STATUS_OPEN.equalsIgnoreCase(status);
    }

    // === Getters ===

    public int getEnquiryId() {
        return enquiryId;
    }

    public String getContent() {
        return content;
    }

    public String getStatus() {
        return status;
    }

    public String getApplicantNric() {
        return applicantNric;
    }

    public String getApplicantName() {
        return applicantName;
    }

    public String getProjectName() {
        return projectName;
    }

    public List<EnquiryReply> getReplies() {
        return replies;
    }

    // === Setters ===

    public void setStatus(String status) {
        this.status = status;
    }

    // === CSV / Debug Formatting ===

    /**
     * Returns a pipe-delimited string of all replies in formatted text.
     *
     * @return Combined replies for display or export.
     */
    public String getFormattedReplies() {
        return replies.stream()
            .map(r -> r.getResponder().getName() + ": " + r.getContent())
            .collect(Collectors.joining(" | "));
    }
}
