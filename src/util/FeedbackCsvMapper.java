package src.util;

import src.model.Feedback;
import java.util.*;
import java.time.LocalDate;
import static src.util.CsvUtil.*;


/**
 * Utility class for reading and writing Feedback data to and from a CSV file.
 * Handles parsing, serialization, and persistence of {@link Feedback} objects.
 */
public class FeedbackCsvMapper {
    private static final String CSV_PATH = FilePath.FEEDBACK_LIST_FILE;

    /**
     * Loads all feedback records from the CSV file and parses them into Feedback objects.
     *
     * @return List of {@link Feedback} objects.
     */
    public static List<Feedback> loadAll() {
        List<Map<String, String>> rows = read(CSV_PATH);
        List<Feedback> list = new ArrayList<>();

        for (Map<String, String> row : rows) {
            try {
                int feedbackId = Integer.parseInt(row.get("FeedbackID"));
                String applicantNRIC = row.get("ApplicantNRIC");
                String content = row.get("Content");
                String status = row.get("Status");
                LocalDate submittedDate = LocalDate.parse(row.get("SubmittedDate"));

                String resolverName = row.getOrDefault("ResolverName", "");
                String resolvedRaw = row.getOrDefault("ResolvedDate", "");
                LocalDate resolvedDate = (!resolvedRaw.isEmpty()) ? LocalDate.parse(resolvedRaw) : null;

                String projectName = row.getOrDefault("ProjectName", "");

                Feedback fb = new Feedback(
                    feedbackId,
                    applicantNRIC,
                    content,
                    status,
                    submittedDate,
                    resolverName.isEmpty() ? null : resolverName,
                    resolvedDate,
                    projectName.isEmpty() ? null : projectName
                );
                list.add(fb);
            } catch (Exception ignored) {

            }
        }
        return list;
    }

    /**
     * Saves a complete list of feedback entries to the CSV, overwriting existing data.
     *
     * @param feedbacks List of {@link Feedback} objects to save.
     */
    public static void saveAll(List<Feedback> feedbacks) {
        List<Map<String, String>> rows = new ArrayList<>();
        for (Feedback fb : feedbacks) {
            Map<String, String> row = new LinkedHashMap<>();
            row.put("FeedbackID", String.valueOf(fb.getFeedbackId()));
            row.put("ApplicantNRIC", fb.getApplicantNRIC());
            row.put("Content", fb.getContent());
            row.put("Status", fb.getStatus());
            row.put("SubmittedDate", fb.getSubmittedDate().toString());
            row.put("ResolverName", fb.getResolverName() != null ? fb.getResolverName() : "");
            row.put("ResolvedDate", fb.getResolvedDate() != null ? fb.getResolvedDate().toString() : "");
            row.put("ProjectName", fb.getProjectName() != null ? fb.getProjectName() : "");
            rows.add(row);
        }

        write(CSV_PATH, rows);
    }

    
}
