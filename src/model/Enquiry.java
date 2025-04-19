package src.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    public void addReply(String content, User responder) {
        int nextId = replies.size() + 1;
        replies.add(new EnquiryReply(nextId, content, responder)); 
        close();
    }
    
    public void addReply(EnquiryReply reply) {
        replies.add(reply);
        close(); // Auto-close on reply
    }

    public void editContent(String newContent) {
        this.content = newContent;
    }

    public void delete() {
        this.content = "[deleted]";
        this.status = STATUS_DELETED;
        this.replies.clear();
    }

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

    public void setClosed(boolean closed) {
        this.status = closed ? STATUS_CLOSED : STATUS_OPEN;
    }

    // === Compatibility Methods ===

    public Project getProject() {
        return new Project(projectName, "", null, null, 0, 0, 0, null);
    }

    public Applicant getApplicant() {
        return new Applicant(applicantNric, "", applicantName, 0, "");
    }

    // === CSV / Debug Formatting ===

    public String getFormattedReplies() {
        return replies.stream()
            .map(r -> r.getResponder().getName() + ": " + r.getContent())
            .collect(Collectors.joining(" | "));
    }
}
