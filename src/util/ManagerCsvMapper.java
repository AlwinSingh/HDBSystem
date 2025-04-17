package src.util;

import java.util.*;
import src.model.HDBManager;

public class ManagerCsvMapper {

    public static HDBManager fromCsvRow(Map<String, String> row) {
        String nric = row.getOrDefault("NRIC", "").trim();
        String password = row.getOrDefault("Password", "").trim();
        String name = row.getOrDefault("Name", "").trim();
        int age = Integer.parseInt(row.getOrDefault("Age", "0").trim());
        String maritalStatus = row.getOrDefault("Marital Status", "Single").trim();

        return new HDBManager(nric, password, name, age, maritalStatus);
    }

    public static Map<String, String> toCsvRow(HDBManager manager) {
        Map<String, String> row = new LinkedHashMap<>();
        row.put("NRIC", manager.getNric());
        row.put("Password", manager.getPassword());
        row.put("Name", manager.getName());
        row.put("Age", String.valueOf(manager.getAge()));
        row.put("Marital Status", manager.getMaritalStatus());
        return row;
    }

    public static List<HDBManager> loadAll(String csvPath) {
        List<Map<String, String>> raw = CsvUtil.read(csvPath);
        List<HDBManager> managers = new ArrayList<>();
        for (Map<String, String> row : raw) {
            managers.add(fromCsvRow(row));
        }
        return managers;
    }

    public static void saveAll(String csvPath, List<HDBManager> managers) {
        List<Map<String, String>> rows = new ArrayList<>();
        for (HDBManager manager : managers) {
            rows.add(toCsvRow(manager));
        }
        CsvUtil.write(csvPath, rows);
    }
    public static HDBManager findByNric(String nric) {
        List<HDBManager> allManagers = loadAll("data/ManagerList.csv");
        for (HDBManager manager : allManagers) {
            if (manager.getNric().equalsIgnoreCase(nric)) {
                return manager;
            }
        }
        return null;
    }
    public static void updateManager(String csvPath, HDBManager updatedManager) {
        List<HDBManager> all = loadAll(csvPath);
        for (int i = 0; i < all.size(); i++) {
            if (all.get(i).getNric().equalsIgnoreCase(updatedManager.getNric())) {
                all.set(i, updatedManager);
                break;
            }
        }
        saveAll(csvPath, all);
    }
    
    
}