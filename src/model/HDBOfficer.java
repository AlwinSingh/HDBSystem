package src.model;

import java.time.LocalDate;
import java.util.List;

public class HDBOfficer extends Applicant {
    private Project assignedProject;
    private String registrationStatus; // e.g., PENDING, APPROVED, REJECTED

    public HDBOfficer(String nric, String password, String name, int age, String maritalStatus) {
        super(nric, password, name, age, maritalStatus);
    }

    public boolean registerToHandleProject(Project project) {
        if (assignedProject == null && application == null) {
            assignedProject = project;
            registrationStatus = "PENDING";
            return true;
        }
        return false;
    }

    public void viewOfficerRegistrationStatus() {
        System.out.println("🔍 Registration Status: " + registrationStatus);
        if (assignedProject != null) {
            System.out.println("🏢 Assigned Project: " + assignedProject.getProjectName());
        }
    }

    public void bookFlat(Application app, String flatType) {
        if (assignedProject != null && app.getProject().equals(assignedProject)) {
            if (app.getStatus().equalsIgnoreCase("SUCCESSFUL")) {
                app.setStatus("BOOKED");
                assignedProject.decrementFlatCount(flatType);
            }
        }
    }

    public Receipt generateReceipt(Application app, int nextPaymentId, String selectedMethod) {
        // Validate payment method
        if (!PaymentMethod.isValid(selectedMethod)) {
            throw new IllegalArgumentException("❌ Invalid payment method: " + selectedMethod);
        }
    
        double price = app.getFlatPrice();
    
        // Generate invoice
        Invoice invoice = new Invoice(
            nextPaymentId,
            price,
            LocalDate.now(),
            selectedMethod,
            "Processed",
            app.getApplicant().getNric(),
            app.getProject().getProjectName(),
            app.getFlatType()
        );
    
        // Generate receipt
        return new Receipt(
            app.getApplicant().getName(),
            app.getApplicant().getNric(),
            app.getApplicant().getAge(),
            app.getApplicant().getMaritalStatus(),
            app.getProject().getProjectName(),
            app.getProject().getNeighborhood(),
            app.getFlatType(),
            invoice
        );
    }
    


    public void setAssignedProjectByName(String projectName, List<Project> allProjects) {
        for (Project p : allProjects) {
            if (p.getProjectName().equalsIgnoreCase(projectName)) {
                this.assignedProject = p;
                return;
            }
        }
    }

    public void setRegistrationStatus(String status) {
        this.registrationStatus = status;
    }

    public String getRegistrationStatus() {
        return registrationStatus;
    }

    public Project getAssignedProject() {
        return assignedProject;
    }

    public void setAssignedProject(Project project) {
        this.assignedProject = project;
    }
    
}
