package src.model;

import java.util.ArrayList;
import java.util.List;

public class Enquiry {
    private int enquiryId;
    private String content;
    private User createdBy;
    private Project relatedProject;
    private List<EnquiryReply> replies;

    public Enquiry(int enquiryId, String content, User createdBy, Project relatedProject) {
        this.enquiryId = enquiryId;
        this.content = content;
        this.createdBy = createdBy;
        this.relatedProject = relatedProject;
        this.replies = new ArrayList<>();
    }

    public void addReply(String replyContent, User responder) {
        int replyId = replies.size() + 1;
        EnquiryReply reply = new EnquiryReply(replyId, replyContent, responder);
        replies.add(reply);
    }

    public void editContent(String newContent) {
        this.content = newContent;
    }

    public void deleteEnquiry() {
        this.content = "[deleted]";
        this.replies.clear(); // optional
    }

    public List<EnquiryReply> getReplies() {
        return replies;
    }

    // Getters
    public String getContent() {
        return content;
    }

    public Project getRelatedProject() {
        return relatedProject;
    }

    public User getCreatedBy() {
        return createdBy;
    }
}

