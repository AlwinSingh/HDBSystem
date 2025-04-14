package src.model;

import java.util.ArrayList;
import java.util.List;

public class Enquiry {
    private int enquiryId;
    private String content;
    private String createdByNric;           // used for persistence
    private transient User createdBy;       // used at runtime
    private Project relatedProject;
    private List<String> replies;

    public Enquiry(int enquiryId, String content, User createdBy, Project relatedProject) {
        this.enquiryId = enquiryId;
        this.content = content;
        this.createdBy = createdBy;
        this.createdByNric = createdBy.getNric();
        this.relatedProject = relatedProject;
        this.replies = new ArrayList<>();
    }

    // For CSV loading
    public Enquiry(int enquiryId, String content, String createdByNric, Project relatedProject, List<String> replies) {
        this.enquiryId = enquiryId;
        this.content = content;
        this.createdByNric = createdByNric;
        this.relatedProject = relatedProject;
        this.replies = replies != null ? replies : new ArrayList<>();
    }

    public int getEnquiryId() {
        return enquiryId;
    }

    public String getContent() {
        return content;
    }

    public void editContent(String content) {
        this.content = content;
    }

    public void deleteEnquiry() {
        this.content = "[DELETED]";
    }

    public void addReply(String replyContent, User responder) {
        replies.add("[" + responder.getName() + "]: " + replyContent);
    }

    public List<String> getReplies() {
        return replies;
    }

    public Project getRelatedProject() {
        return relatedProject;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public String getCreatedByNric() {
        return createdByNric;
    }

    public void setCreatedByNric(String nric) {
        this.createdByNric = nric;
    }
}
