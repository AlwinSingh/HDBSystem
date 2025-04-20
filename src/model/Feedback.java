package src.model;

import java.time.LocalDate;

/**
 * Represents feedback submitted by an applicant about a project.
 * May be resolved by a manager or officer.
 */
public class Feedback {
    private int feedbackId;
    private String applicantNRIC;
    private String content;
    private String status; // e.g., PENDING, RESOLVED
    private LocalDate submittedDate;
    private String resolverName;      // Manager or Officer who resolved it
    private LocalDate resolvedDate;
    private String projectName;  

    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_RESOLVED = "RESOLVED";

    public Feedback(int feedbackId, String applicantNRIC, String content, String status,
                LocalDate submittedDate, String resolverName, LocalDate resolvedDate, String projectName) {
    this.feedbackId = feedbackId;
    this.applicantNRIC = applicantNRIC;
    this.content = content;
    this.status = status;
    this.submittedDate = submittedDate;
    this.resolverName = resolverName;
    this.resolvedDate = resolvedDate;
    this.projectName = projectName; // NEW
}

    // === Business Logic ===
    /**
     * Marks this feedback as resolved, setting resolver and resolved date.
     *
     * @param resolverName Name of the admin resolving it.
     */
    public void markResolved(String resolverName) {
        this.status = STATUS_RESOLVED;
        this.resolverName = resolverName;
        this.resolvedDate = LocalDate.now();
    }

    /**
     * Returns true if this feedback is marked as resolved.
     */
    public boolean isResolved() {
        return STATUS_RESOLVED.equalsIgnoreCase(status);
    }

    public boolean isPending() {
        return STATUS_PENDING.equalsIgnoreCase(status);
    }

    // === Getters ===
    public int getFeedbackId() { return feedbackId; }
    public String getApplicantNRIC() { return applicantNRIC; }
    public String getContent() { return content; }
    public String getStatus() { return status; }
    public LocalDate getSubmittedDate() { return submittedDate; }
    public String getResolverName() { return resolverName; }
    public LocalDate getResolvedDate() { return resolvedDate; }
    public String getProjectName() {return projectName;}

    // === Setters (optional for editing) ===
    public void setContent(String content) { this.content = content; }
    public void setStatus(String status) { this.status = status; }
    public void setResolverName(String resolverName) { this.resolverName = resolverName; }
    public void setResolvedDate(LocalDate resolvedDate) { this.resolvedDate = resolvedDate; }

    /**
     * Displays formatted feedback details including submission, resolution, and content.
     */
    @Override
    public String toString() {
        return "📝 Feedback #" + feedbackId + " by " + applicantNRIC + "\n"
             + "📅 Submitted: " + submittedDate + " | Status: " + status
             + (resolverName != null ? " | Resolved by: " + resolverName : "")
             + "\n📣 Content: " + content;
    }
}
