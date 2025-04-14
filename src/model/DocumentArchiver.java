package src.model;

import java.time.LocalDate;

public class DocumentArchiver {
    private int archiveId;
    private LocalDate archiveDate;
    private String documentType;
    private String filePath;

    public DocumentArchiver(int archiveId, String documentType, String filePath) {
        this.archiveId = archiveId;
        this.archiveDate = LocalDate.now();
        this.documentType = documentType;
        this.filePath = filePath;
    }

    public void archiveDocument() {
        System.out.println("Archiving document: " + documentType + " at " + filePath);
        // TODO: Actual file copying logic
    }

    public String retrieveArchivedDocument(int archiveId) {
        // TODO: Return path or dummy text
        return "Retrieved archived document #" + archiveId + " from " + filePath;
    }
}

