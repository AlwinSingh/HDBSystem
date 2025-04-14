package src.model;

import java.time.LocalDate;

public class RoleAudit {
    private int auditId;
    private String oldRole;
    private String newRole;
    private LocalDate changedDate;

    public RoleAudit(int auditId, String oldRole, String newRole) {
        this.auditId = auditId;
        this.oldRole = oldRole;
        this.newRole = newRole;
        this.changedDate = LocalDate.now();
    }

    public void recordRoleChange() {
        System.out.println("Role changed from " + oldRole + " to " + newRole + " on " + changedDate);
    }

    public String getAuditSummary() {
        return "[" + changedDate + "] " + oldRole + " → " + newRole;
    }
}

