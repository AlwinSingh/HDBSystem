package src.service;

import src.model.Enquiry;
import src.model.Project;
import src.model.User;
import src.util.CSVReader;
import src.util.CSVWriter;
import src.util.FilePath;

import java.util.*;
import java.util.stream.Collectors;

public class EnquiryService {

    private List<Enquiry> enquiries;
    private int nextEnquiryId;

    private final ProjectService projectService;
    private final UserService userService;

    public EnquiryService(ProjectService projectService, UserService userService) {
        this.projectService = projectService;
        this.userService = userService;
        this.enquiries = new ArrayList<>();
        this.nextEnquiryId = 1;
        loadEnquiries(); // Load from CSV on init
    }

    public Enquiry createEnquiry(String content, User createdBy, Project relatedProject) {
        Enquiry enquiry = new Enquiry(nextEnquiryId++, content, createdBy, relatedProject);
        enquiries.add(enquiry);
        saveEnquiries(); // Persist after creation
        return enquiry;
    }

    public List<Enquiry> getAllEnquiries() {
        return new ArrayList<>(enquiries);
    }

    public List<Enquiry> getEnquiriesForUser(String userNric) {
        return enquiries.stream()
                .filter(e -> e.getCreatedByNric().equals(userNric))
                .collect(Collectors.toList());
    }

    public Enquiry getEnquiryById(int id) {
        return enquiries.stream().filter(e -> e.getEnquiryId() == id).findFirst().orElse(null);
    }

    public boolean editEnquiry(int enquiryId, String newContent) {
        Enquiry enquiry = getEnquiryById(enquiryId);
        if (enquiry != null) {
            enquiry.editContent(newContent);
            saveEnquiries();
            return true;
        }
        return false;
    }

    public boolean deleteEnquiry(int enquiryId) {
        Enquiry enquiry = getEnquiryById(enquiryId);
        if (enquiry != null) {
            enquiries.remove(enquiry);
            saveEnquiries();
            return true;
        }
        return false;
    }

    public boolean replyToEnquiry(int enquiryId, String replyContent, User responder) {
        Enquiry enquiry = getEnquiryById(enquiryId);
        if (enquiry != null) {
            enquiry.addReply(replyContent, responder);
            saveEnquiries();
            return true;
        }
        return false;
    }

    private void saveEnquiries() {
        CSVWriter.saveEnquiries(enquiries, FilePath.ENQUIRY_LIST_FILE);
    }

    private void loadEnquiries() {
        enquiries.clear();
        List<Map<String, String>> rows = CSVReader.readCSV(FilePath.ENQUIRY_LIST_FILE,
                List.of("EnquiryId", "Content", "CreatedBy", "Project", "Replies"));

        int maxId = 0;
        for (Map<String, String> row : rows) {
            try {
                int id = Integer.parseInt(row.get("EnquiryId"));
                String content = row.get("Content");
                String createdByNric = row.get("CreatedBy");
                String projectName = row.get("Project");
                String repliesRaw = row.getOrDefault("Replies", "");

                Project relatedProject = projectService.getProjectByName(projectName);
                User createdBy = userService.getApplicantByNric(createdByNric);
                if (createdBy == null) continue;

                List<String> replies = repliesRaw.isEmpty() ? new ArrayList<>() :
                        Arrays.asList(repliesRaw.split("\\|"));

                Enquiry enquiry = new Enquiry(id, content, createdByNric, relatedProject, replies);
                enquiry.setCreatedBy(createdBy);
                enquiries.add(enquiry);
                maxId = Math.max(maxId, id);
            } catch (Exception e) {
                System.err.println("❌ Error parsing enquiry row: " + row);
            }
        }

        this.nextEnquiryId = maxId + 1;
    }
}
