package src.service;

import src.model.Enquiry;
import src.model.Project;
import src.model.User;

import java.util.*;

public class EnquiryService {
    private final Map<Integer, Enquiry> enquiries = new HashMap<>();
    private int enquiryCounter = 1;

    /**
     * Create a new enquiry
     */
    public Enquiry createEnquiry(User user, Project project, String content) {
        Enquiry enquiry = new Enquiry(enquiryCounter++, content, user, project);
        enquiries.put(enquiry.getEnquiryId(), enquiry);
        return enquiry;
    }

    /**
     * Edit an existing enquiry’s content
     */
    public boolean editEnquiry(int enquiryId, String newContent, User user) {
        Enquiry e = enquiries.get(enquiryId);
        if (e == null || !e.getCreatedBy().getNric().equals(user.getNric())) return false;

        e.editContent(newContent);
        return true;
    }

    /**
     * Delete an enquiry (only by creator)
     */
    public boolean deleteEnquiry(int enquiryId, User user) {
        Enquiry e = enquiries.get(enquiryId);
        if (e == null || !e.getCreatedBy().getNric().equals(user.getNric())) return false;

        enquiries.remove(enquiryId);
        return true;
    }

    /**
     * Add a reply to an enquiry
     */
    public boolean replyToEnquiry(int enquiryId, String replyContent) {
        Enquiry e = enquiries.get(enquiryId);
        if (e == null) return false;

        e.addReply(replyContent);
        return true;
    }

    /**
     * Get all enquiries made by a user
     */
    public List<Enquiry> getEnquiriesByUser(User user) {
        List<Enquiry> result = new ArrayList<>();
        for (Enquiry e : enquiries.values()) {
            if (e.getCreatedBy().getNric().equals(user.getNric())) {
                result.add(e);
            }
        }
        return result;
    }

    /**
     * Get all enquiries related to a project
     */
    public List<Enquiry> getEnquiriesByProject(Project project) {
        List<Enquiry> result = new ArrayList<>();
        for (Enquiry e : enquiries.values()) {
            if (e.getRelatedProject().getName().equals(project.getName())) {
                result.add(e);
            }
        }
        return result;
    }

    /**
     * Retrieve enquiry by ID
     */
    public Enquiry getEnquiryById(int enquiryId) {
        return enquiries.get(enquiryId);
    }
}
