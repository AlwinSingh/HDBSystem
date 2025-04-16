package src.util;

import java.io.*;
import java.util.*;

public class CsvUtil {

    public static List<Map<String, String>> read(String path) {
        List<Map<String, String>> result = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String[] headers = br.readLine().split(",");
            String line;

            while ((line = br.readLine()) != null) {
                String[] values = line.split(",", -1); // include empty strings
                Map<String, String> row = new LinkedHashMap<>();
                for (int i = 0; i < headers.length && i < values.length; i++) {
                    row.put(headers[i].trim(), values[i].trim());
                }
                result.add(row);
            }
        } catch (IOException e) {
            System.err.println("❌ Error reading CSV: " + e.getMessage());
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
}
