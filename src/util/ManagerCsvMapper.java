package src.util;

import java.util.*;
import src.model.HDBManager;

public class ManagerCsvMapper {

    /**
     * Converts a CSV row into a HDBManager object.
     *
     * @param row The CSV row.
     * @return A manager instance.
     */
    public static HDBManager fromCsvRow(Map<String, String> row) {
        String nric = row.getOrDefault("NRIC", "").trim();
        String password = row.getOrDefault("Password", "").trim();
        String name = row.getOrDefault("Name", "").trim();
        int age = Integer.parseInt(row.getOrDefault("Age", "0").trim());
        String maritalStatus = row.getOrDefault("Marital Status", "Single").trim();

        return new HDBManager(nric, password, name, age, maritalStatus);
    }

    /**
     * Converts an HDBManager object into a CSV-compatible row map.
     *
     * @param manager The manager.
     * @return Map of column names and values.
     */
    public static Map<String, String> toCsvRow(HDBManager manager) {
        Map<String, String> row = new LinkedHashMap<>();
        row.put("NRIC", manager.getNric());
        row.put("Password", manager.getPassword());
        row.put("Name", manager.getName());
        row.put("Age", String.valueOf(manager.getAge()));
        row.put("Marital Status", manager.getMaritalStatus());
        return row;
    }

    /**
     * Loads all managers from the CSV file.
     *
     * @return List of managers.
     */
    public static List<HDBManager> loadAll() {
        List<Map<String, String>> raw = CsvUtil.read(FilePath.MANAGER_LIST_FILE);
        List<HDBManager> managers = new ArrayList<>();
        for (Map<String, String> row : raw) {
            managers.add(fromCsvRow(row));
        }
        return managers;
    }

    /**
     * Saves the list of managers to the CSV file.
     *
     * @param managers List of manager objects.
     */
    public static void saveAll(List<HDBManager> managers) {
        List<Map<String, String>> rows = new ArrayList<>();
        for (HDBManager manager : managers) {
            rows.add(toCsvRow(manager));
        }
        CsvUtil.write(FilePath.MANAGER_LIST_FILE, rows);
    }

    /**
     * Finds a manager by NRIC from the CSV file.
     *
     * @param nric The NRIC to search.
     * @return The matching manager or null.
     */
    public static HDBManager findByNric(String nric) {
        List<HDBManager> allManagers = loadAll();
        for (HDBManager manager : allManagers) {
            if (manager.getNric().equalsIgnoreCase(nric)) {
                return manager;
            }
        }
        return null;
    }

    /**
     * Updates a manager in the CSV based on their NRIC.
     *
     * @param updatedManager The new version of the manager.
     */
    public static void updateManager(HDBManager updatedManager) {
        List<HDBManager> all = loadAll();
        for (int i = 0; i < all.size(); i++) {
            if (all.get(i).getNric().equalsIgnoreCase(updatedManager.getNric())) {
                all.set(i, updatedManager);
                break;
            }
        }
        saveAll(all);
    }
}