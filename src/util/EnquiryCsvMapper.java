package src.util;

import src.model.*;
import src.repository.EnquiryRepository;

import java.util.*;


/**
 * A CSV utility mapper for converting {@link Enquiry} objects to and from CSV rows.
 * Supports operations like load, save, update, and append.
 * Also handles parsing of embedded {@link EnquiryReply} data.
 */
public class EnquiryCsvMapper implements EnquiryRepository {

    /**
     * Parses a string safely into an integer, returning a default value if it fails.
     *
     * @param s   The string input.
     * @param def The default value.
     * @return Parsed integer or default.
     */
    private int safeParseInt(String s, int def) {
        if (s == null || s.trim().isEmpty()) return def;
        try { return Integer.parseInt(s.trim()); }
        catch (NumberFormatException e) { return def; }
    }

    /**
     * Converts a single CSV row into an {@link Enquiry} object.
     *
     * @param row The CSV row.
     * @return The reconstructed {@link Enquiry} object.
     */
    public Enquiry fromCsvRow(Map<String, String> row) {
        int id = safeParseInt(row.get("EnquiryId"), 0);
        String content = row.getOrDefault("Content", "").trim();
        String status = row.getOrDefault("Status", Enquiry.STATUS_OPEN).trim();
        String applicantNric = row.getOrDefault("ApplicantNric", "").trim();
        String applicantName = row.getOrDefault("ApplicantName", "").trim();
        String projectName = row.getOrDefault("ProjectName", "").trim();
        String repliesRaw = row.getOrDefault("Replies", "").trim();

        Enquiry enquiry = new Enquiry(id, content, status, applicantNric, applicantName, projectName);

        if (!repliesRaw.isEmpty()) {
            String[] replyParts = repliesRaw.split("\\|\\|");
            for (int i = 0; i < replyParts.length; i++) {
                String part = replyParts[i].trim();
                String[] pieces = part.split(":", 2); // Format: ResponderName:ReplyContent
                if (pieces.length == 2) {
                    String responderName = pieces[0].trim();
                    String replyContent = pieces[1].trim();
                    User responder = new User("unknown", "", responderName, 0, "") {
                        // Anonymous User subclass as fallback
                    };
                    enquiry.addReply(new EnquiryReply(i + 1, replyContent, responder));
                }
            }
        }

        return enquiry;
    }

    /**
     * Converts an {@link Enquiry} object to a CSV row representation.
     *
     * @param e The enquiry.
     * @return The CSV row representation.
     */
    public static Map<String, String> toCsvRow(Enquiry e) {
        Map<String, String> row = new LinkedHashMap<>();
        row.put("EnquiryId", String.valueOf(e.getEnquiryId()));
        row.put("Content", e.getContent());
        row.put("Status", e.getStatus());
        row.put("ApplicantNric", e.getApplicantNric());
        row.put("ApplicantName", e.getApplicantName());
        row.put("ProjectName", e.getProjectName());

        String repliesRaw = e.getReplies().stream()
            .map(r -> r.getResponder().getName() + ":" + r.getContent())
            .reduce((a, b) -> a + "||" + b)
            .orElse("");
        row.put("Replies", repliesRaw);

        return row;
    }

    /**
     * Loads all enquiries from the CSV file, skipping rows with missing IDs and handling reply parsing.
     *
     * @return List of all {@link Enquiry} objects.
     */
    public List<Enquiry> loadAll() {
        List<Map<String, String>> rawRows = CsvUtil.read(FilePath.ENQUIRY_LIST_FILE);
        List<Enquiry> enquiries = new ArrayList<>();

        for (Map<String, String> row : rawRows) {
            boolean noId = row.getOrDefault("EnquiryId", "").trim().isEmpty();
            boolean noContent = row.getOrDefault("Content", "").trim().isEmpty();
            if (noId && noContent) continue;

            try {
                enquiries.add(fromCsvRow(row));
            } catch (Exception e) {
                System.err.println("⚠️ Error parsing enquiry row: " + row);
                e.printStackTrace();
            }
        }

        return enquiries;
    }

    /**
     * Saves a full list of enquiries to the CSV file.
     *
     * @param enquiries The list of enquiries to save.
     */
    public void saveAll(List<Enquiry> enquiries) {
        List<Map<String, String>> rows = new ArrayList<>();
        for (Enquiry e : enquiries) {
            rows.add(toCsvRow(e));
        }
        CsvUtil.write(FilePath.ENQUIRY_LIST_FILE, rows);
    }

    /**
     * Appends a new enquiry to the CSV file.
     *
     * @param newEnquiry The enquiry to add.
     */
    public void add(Enquiry newEnquiry) {
        CsvUtil.append(FilePath.ENQUIRY_LIST_FILE, toCsvRow(newEnquiry));
    }

    /**
     * Updates an existing enquiry by ID and saves the new list to CSV.
     *
     * @param updatedEnquiry The modified {@link Enquiry} object.
     */
    public void update(Enquiry updatedEnquiry) {
        List<Enquiry> all = loadAll();
        for (int i = 0; i < all.size(); i++) {
            if (all.get(i).getEnquiryId() == updatedEnquiry.getEnquiryId()) {
                all.set(i, updatedEnquiry);
                break;
            }
        }
        saveAll(all);
    }
    
}
