package src.util;

import java.io.*;
import java.util.*;

public class CsvUtil {

    public static List<Map<String, String>> read(String path) {
        List<Map<String, String>> result = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String headerLine = br.readLine();
            if (headerLine == null || headerLine.isBlank()) return result;
    
            String[] headers = headerLine.split(",");
            String line;
    
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                Map<String, String> row = new HashMap<>();
                for (int i = 0; i < headers.length && i < values.length; i++) {
                    row.put(headers[i].trim(), values[i].trim());
                }
                result.add(row);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
    

    public static void write(String path, List<Map<String, String>> rows) {
        if (rows == null || rows.isEmpty()) return;

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(path))) {
            List<String> headers = new ArrayList<>(rows.get(0).keySet());
            bw.write(String.join(",", headers));
            bw.newLine();

            for (Map<String, String> row : rows) {
                List<String> values = new ArrayList<>();
                for (String h : headers) {
                    values.add(row.getOrDefault(h, ""));
                }
                bw.write(String.join(",", values));
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("❌ Error writing CSV: " + e.getMessage());
        }
    }

    public static void append(String path, Map<String, String> newRow) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(path, true))) {
            List<String> headers = new ArrayList<>(newRow.keySet());
            List<String> values = new ArrayList<>();
            for (String h : headers) {
                values.add(newRow.getOrDefault(h, ""));
            }
            bw.write(String.join(",", values));
            bw.newLine();
        } catch (IOException e) {
            System.err.println("❌ Error appending to CSV: " + e.getMessage());
        }
    }
    
}
