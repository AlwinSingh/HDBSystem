// File: Enquiry.java
package src.model;

import java.util.ArrayList;
import java.util.List;

public class Enquiry {
    private static int enquiryCounter = 1;

    private int enquiryId;
    private String content;
    private User createdBy;
    private Project relatedProject;
    private List<String> replies;

    public Enquiry(int enquiryId, String content, User createdBy, Project relatedProject) {
        this.enquiryId = enquiryId;
        this.content = content;
        this.createdBy = createdBy;
        this.relatedProject = relatedProject;
        this.replies = new ArrayList<>();
    }
    
    public int getEnquiryId() {
        return enquiryId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public Project getRelatedProject() {
        return relatedProject;
    }

    public List<String> getReplies() {
        return replies;
    }

    public void addReply(String reply) {
        replies.add(reply);
    }

    public void editContent(String newContent) {
        this.content = newContent;
    }

    public void deleteEnquiry() {
        // Logic for removal from database/service can be implemented in the EnquiryService
        System.out.println("Enquiry deleted: ID " + enquiryId);
    }
}
