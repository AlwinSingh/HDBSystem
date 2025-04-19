package src.util;

import src.model.*;

import java.util.*;

public class EnquiryCsvMapper {
    private static int safeParseInt(String s, int def) {
        if (s == null || s.trim().isEmpty()) return def;
        try { return Integer.parseInt(s.trim()); }
        catch (NumberFormatException e) { return def; }
    }

    public static Enquiry fromCsvRow(Map<String, String> row) {
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

    public static List<Enquiry> loadAll() {
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

    public static void saveAll(List<Enquiry> enquiries) {
        List<Map<String, String>> rows = new ArrayList<>();
        for (Enquiry e : enquiries) {
            rows.add(toCsvRow(e));
        }
        CsvUtil.write(FilePath.ENQUIRY_LIST_FILE, rows);
    }

    public static void add(Enquiry newEnquiry) {
        CsvUtil.append(FilePath.ENQUIRY_LIST_FILE, toCsvRow(newEnquiry));
    }
    
    public static void update(Enquiry updatedEnquiry) {
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
