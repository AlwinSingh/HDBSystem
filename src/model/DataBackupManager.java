package src.model;

import java.time.LocalDate;

public class DataBackupManager {
    private int backupId;
    private String backupLocation;
    private LocalDate lastBackupDate;

    public DataBackupManager(int backupId, String backupLocation) {
        this.backupId = backupId;
        this.backupLocation = backupLocation;
        this.lastBackupDate = LocalDate.now();
    }

    public boolean performBackup() {
        System.out.println("Performing data backup to: " + backupLocation);
        // TODO: Stub for actual backup logic
        lastBackupDate = LocalDate.now();
        return true;
    }

    public boolean restoreBackup(String file) {
        System.out.println("Restoring backup from: " + file);
        // TODO: Stub for restore logic
        return true;
    }
}

