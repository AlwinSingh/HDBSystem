package src.util;

import src.model.Enquiry;

import java.util.*;

public class EnquiryCsvMapper {
    // 1. Safe parser for integer IDs
    private static int safeParseInt(String s, int defaultValue) {
        if (s == null || s.trim().isEmpty()) return defaultValue;
        try {
            return Integer.parseInt(s.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
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
            String[] replies = repliesRaw.split("\\|\\|"); // double‑pipe delimiter
            for (String reply : replies) {
                enquiry.addReply(reply.trim());
            }
        }

        return enquiry;
    }

    public static Map<String, String> toCsvRow(Enquiry enquiry) {
        Map<String, String> row = new LinkedHashMap<>();
        row.put("EnquiryId", String.valueOf(enquiry.getEnquiryId()));
        row.put("Content", enquiry.getContent());
        row.put("Status", enquiry.getStatus());
        row.put("ApplicantNric", enquiry.getApplicantNric());
        row.put("ApplicantName", enquiry.getApplicantName());
        row.put("ProjectName", enquiry.getProjectName());

        // Join replies using ||
        String repliesJoined = String.join("||", enquiry.getReplies());
        row.put("Replies", repliesJoined);

        return row;
    }

    public static List<Enquiry> loadAll(String path) {
        List<Map<String, String>> rawRows = CsvUtil.read(path);
        List<Enquiry> enquiries = new ArrayList<>();

        for (Map<String, String> row : rawRows) {
            // 2. Skip rows that have neither an ID nor Content
            boolean noId     = row.getOrDefault("EnquiryId", "").trim().isEmpty();
            boolean noContent= row.getOrDefault("Content", "").trim().isEmpty();
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

    public static void saveAll(String path, List<Enquiry> enquiries) {
        List<Map<String, String>> rows = new ArrayList<>();
        for (Enquiry e : enquiries) {
            rows.add(toCsvRow(e));
        }
        CsvUtil.write(path, rows);
    }
}
