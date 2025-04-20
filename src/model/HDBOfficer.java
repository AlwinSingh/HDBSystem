package src.model;

import java.time.LocalDate;
import java.util.List;

/**
 * Represents an HDB Officer, extending an Applicant.
 * Officers can register for projects, manage bookings, and generate receipts.
 */
public class HDBOfficer extends Applicant {
    private Project assignedProject;
    private String registrationStatus; // e.g., PENDING, APPROVED, REJECTED

    public enum RegistrationStatusType {
        PENDING,
        APPROVED,
        REJECTED
    }

    public HDBOfficer(String nric, String password, String name, int age, String maritalStatus) {
        super(nric, password, name, age, maritalStatus);
    }

    /**
     * Registers the officer to handle a project, marking their status as PENDING.
     *
     * @return True if registration is successful; false if already registered or has an application.
     */
    public boolean registerToHandleProject(Project project) {
        if (assignedProject == null && application == null) {
            assignedProject = project;
            registrationStatus = RegistrationStatusType.PENDING.name();
            return true;
        }
        return false;
    }

    /**
     * Prints the officer's registration status and details of the assigned project.
     */
    public void viewOfficerRegistrationStatus() {
        System.out.println("🔍 Officer Registration Overview");
        System.out.println("   📄 Registration Status : " + (registrationStatus != null ? registrationStatus : "N/A"));
    
        if (assignedProject != null) {
            System.out.println("   🏢 Assigned Project     : " + assignedProject.getProjectName());
            System.out.println("   📍 Neighborhood        : " + assignedProject.getNeighborhood());
            System.out.println("   🗓️ Application Period  : " + assignedProject.getOpenDate() + " to " + assignedProject.getCloseDate());
            System.out.println("   🧍 Officer Slots       : " + assignedProject.getOfficerSlots());
            System.out.println("   🏠 2-Room Units Left   : " + assignedProject.getRemainingFlats("2-Room"));
            System.out.println("   💰 2-Room Price        : $" + String.format("%.2f", assignedProject.getPrice2Room()));
            System.out.println("   🏠 3-Room Units Left   : " + assignedProject.getRemainingFlats("3-Room"));
            System.out.println("   💰 3-Room Price        : $" + String.format("%.2f", assignedProject.getPrice3Room()));
            System.out.println("   👀 Public Visibility   : " + (assignedProject.isVisible() ? "Yes ✅" : "No ❌"));
        } else {
            System.out.println("   🛑 No assigned project.");
        }
    }

    /**
     * Books a flat for an applicant if assigned to the same project and already approved.
     */
    public void bookFlat(Application app, String flatType) {
        if (assignedProject != null && app.getProject().equals(assignedProject)) {
            if (app.getStatus().equalsIgnoreCase(AppStatusType.SUCCESSFUL.name())) {
                app.setStatus(AppStatusType.BOOKED.name());
                assignedProject.decrementFlatCount(flatType);
            }
        }
    }

    /**
     * Generates a receipt for a processed booking by this officer.
     */
    public Receipt generateReceipt(Application app, int nextPaymentId, PaymentMethod selectedMethod) {
        double price = app.getFlatPrice();
    
        Invoice invoice = new Invoice(
            nextPaymentId,
            price,
            LocalDate.now(),
            selectedMethod,
            Payment.PaymentStatusType.PROCESSED.name(),
            app.getApplicant().getNric(),
            app.getProject().getProjectName(),
            app.getFlatType()
        );
    
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

    /**
     * Generates an invoice (unpaid) for a successful application.
     */
    public static Invoice generateInvoiceForBooking(Application app, int paymentId) {
        return new Invoice(
            paymentId,
            app.getFlatPrice(),
            LocalDate.now(),
            null,  
            "Awaiting Payment",
            app.getApplicant().getNric(),
            app.getProject().getProjectName(),
            app.getFlatType()
        );
    }

    /**
     * Sets the assigned project using a project name and a list of all available projects.
     */
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

    /**
     * Indicates that this user is an officer. Always returns true.
     */
    @Override
    public boolean isOfficer() {
        return true;
    }

    
}
