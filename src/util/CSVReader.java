package src.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Utility for reading CSV files into maps with header validation and indexing.
 */
public class CSVReader {

    /* Reads a CSV and returns a list of maps (1 map = 1 row) */
    public static List<Map<String, String>> readCSV(String path, List<String> requiredHeaders) {
        List<Map<String, String>> records = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String headerLine = reader.readLine();
            if (headerLine == null) return records;

            String[] headers = headerLine.split(",");
            List<String> existingHeaders = new ArrayList<>(Arrays.asList(headers));

            String line;
            while ((line = reader.readLine()) != null) {
                String[] values = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);

                Map<String, String> row = new LinkedHashMap<>();
                for (int i = 0; i < headers.length; i++) {
                    row.put(headers[i], i < values.length ? values[i].replace("\"", "") : "");
                }

                records.add(row);
            }

            // Check for missing columns and delegate update
            boolean needsUpdate = false;
            for (String required : requiredHeaders) {
                if (!existingHeaders.contains(required)) {
                    needsUpdate = true;
                    break;
                }
            }

            if (needsUpdate) {
                System.out.println("⚠️ Missing headers detected in: " + path + " — Updating...");
                CSVWriter.addMissingColumns(path, requiredHeaders, existingHeaders, records);
            }

        } catch (IOException e) {
            System.err.println("⚠️ Failed to read CSV: " + path);
            e.printStackTrace();
        }

        return records;
    }

    /* Reads a CSV and returns a map where key = a specific column (In this case we used NRIC and Project Name) */
    public static Map<String, Map<String, String>> readCSVByKey(String path, List<String> requiredHeaders, String keyColumn) {
        List<Map<String, String>> rows = readCSV(path, requiredHeaders);
        Map<String, Map<String, String>> indexedMap = new LinkedHashMap<>();

        for (Map<String, String> row : rows) {
            String key = row.getOrDefault(keyColumn, "").trim();
            if (!key.isEmpty()) {
                indexedMap.put(key, row);
            }
        }

        return indexedMap;
    }
}
