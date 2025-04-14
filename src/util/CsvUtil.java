package src.util;

import com.opencsv.CSVReader;

import src.model.Applicant;
import src.model.Application;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class CsvUtil {
    public static List<Map<String, String>> read(String path) {
        List<Map<String, String>> data = new ArrayList<>();

        try (CSVReader reader = new CSVReader(new FileReader(path))) {
            String[] headers = reader.readNext();
            if (headers == null) return data;

            String[] values;
            while ((values = reader.readNext()) != null) {
                Map<String, String> row = new LinkedHashMap<>();
                for (int i = 0; i < headers.length; i++) {
                    row.put(headers[i].trim(), i < values.length ? values[i].trim() : "");
                }
                data.add(row);
            }
        } catch (Exception e) {
            System.out.println("❌ CSV Read Error: " + path);
            e.printStackTrace();
        }

        return data;
    }
    public static void write(String path, List<Map<String, String>> rows) {
        if (rows.isEmpty()) return;
    
        try (PrintWriter writer = new PrintWriter(new FileWriter(path))) {
            // Write headers from first row
            Set<String> headers = rows.get(0).keySet();
            writer.println(String.join(",", headers));
    
            // Write each row
            for (Map<String, String> row : rows) {
                List<String> values = new ArrayList<>();
                for (String header : headers) {
                    values.add(row.getOrDefault(header, ""));
                }
                writer.println(String.join(",", values));
            }
    
        } catch (IOException e) {
            System.out.println("❌ Error writing to CSV: " + path);
            e.printStackTrace();
        }
    }

    public static void updateApplicantApplication(String path, Applicant applicant) {
        List<Map<String, String>> rows = read(path);
    
        for (Map<String, String> row : rows) {
            if (row.get("NRIC").equalsIgnoreCase(applicant.getNric())) {
                Application app = applicant.getApplication();
                if (app == null) {
                    System.out.println("⚠️ No application data found for " + applicant.getName());
                    return;
                }
    
                // Update fields with correct headers
                row.put("AppliedProjectName", app.getProject().getProjectName());
                row.put("FlatTypeApplied", app.getFlatType());
                row.put("ApplicationStatus", app.getStatus());
                break;
            }
        }
    
        write(path, rows);
    }
    

}
