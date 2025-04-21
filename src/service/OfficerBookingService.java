package src.service;

import java.util.List;
import java.util.Scanner;

import src.interfaces.IOfficerBookingService;
import src.model.Applicant;
import src.model.Application;
import src.model.HDBOfficer;
import src.model.Invoice;
import src.model.Project;
import src.repository.ApplicantRepository;
import src.repository.ProjectRepository;
import src.util.ApplicantCsvMapper;
import src.util.ProjectCsvMapper;


/**
 * Handles the booking of flats by HDB officers for applicants.
 * Includes logic to list bookable applicants, perform bookings, and generate invoices.
 */
public class OfficerBookingService implements IOfficerBookingService {

    private static final ProjectRepository projectRepository = new ProjectCsvMapper();
    private static final ApplicantRepository applicantRepository = new ApplicantCsvMapper();


    /**
     * Retrieves all applicants who are eligible to be booked under the officer’s assigned project.
     * Only applicants with status SUCCESSFUL and matching project name are returned.
     *
     * @param assignedProject The officer’s assigned project.
     * @return A list of bookable applicants.
     */
    public List<Applicant> getBookableApplicants(Project assignedProject) {
        return applicantRepository.loadAll().stream()
            .filter(a -> a.getApplication() != null)
            .filter(a -> {
                Project appProject = a.getApplication().getProject();
                return appProject != null &&
                       appProject.getProjectName().equalsIgnoreCase(assignedProject.getProjectName());
            })
            .filter(a -> Applicant.AppStatusType.SUCCESSFUL.name().equalsIgnoreCase(a.getApplication().getStatus()))
            .toList();
    }

    /**
     * Books a flat for an applicant by selecting their NRIC and desired flat type.
     * This updates the applicant’s status and project flat count.
     *
     * @param officer The logged-in officer.
     * @param sc      Scanner for input.
     */
    public void bookFlat(HDBOfficer officer, Scanner sc) {
        Project assigned = officer.getAssignedProject();
        if (assigned == null) {
            System.out.println("❌ No assigned project.");
            return;
        }
    
        if (HDBOfficer.RegistrationStatusType.PENDING.name().equalsIgnoreCase(officer.getRegistrationStatus())) {
            System.out.println("⚠️ You cannot perform bookings as your registration to this project is still pending approval.");
            return;
        }
    
        List<Applicant> eligible = getBookableApplicants(assigned);
    
        if (eligible.isEmpty()) {
            System.out.println("❌ No applicants ready for booking.");
            return;
        }
    
        System.out.println("\n📋 Eligible Applicants:");
        for (int i = 0; i < eligible.size(); i++) {
            Applicant a = eligible.get(i);
            System.out.printf("[%d] %s (NRIC: %s)\n", i + 1, a.getName(), a.getNric());
        }
    
        System.out.print("Select applicant to book: ");
        try {
            int idx = Integer.parseInt(sc.nextLine().trim()) - 1;
            if (idx < 0 || idx >= eligible.size()) throw new IndexOutOfBoundsException();
    
            Applicant selected = eligible.get(idx);
            boolean success = bookFlatAndGenerateInvoice(officer, selected);
            if (success) {
                System.out.println("✅ Booking successful.");
            } else {
                System.out.println("❌ Booking failed. Please check eligibility and data.");
            }
    
        } catch (Exception e) {
            System.out.println("❌ Invalid booking.");
        }
    }


    /**
     * Performs the booking and automatically generates an invoice for the booked applicant.
     * Updates the applicant’s status, project availability, and persists all changes.
     *
     * @param officer   The officer performing the booking.
     * @param applicant The applicant to book for.
     * @return True if booking and invoice generation succeed; false otherwise.
     */
    public boolean bookFlatAndGenerateInvoice(HDBOfficer officer, Applicant applicant) {
        Application app = applicant.getApplication();
        if (app == null || officer.getAssignedProject() == null) return false;

        // Refresh full project info
        Project fullProject = projectRepository.loadAll().stream()
            .filter(p -> p.getProjectName().equalsIgnoreCase(officer.getAssignedProject().getProjectName()))
            .findFirst()
            .orElse(null);

        if (fullProject == null) return false;

        // Inject full project reference
        app.setProject(fullProject);

        // Perform booking
        officer.bookFlat(app, app.getFlatType());
        app.setStatus(Applicant.AppStatusType.BOOKED.name());

        // Persist updates
        applicantRepository.update(applicant);

        int nextInvoiceId = InvoiceService.getNextInvoiceId();
        Invoice invoice = HDBOfficer.generateInvoiceForBooking(app, nextInvoiceId);
        InvoiceService.addInvoice(invoice);

        projectRepository.updateProject(fullProject);

        System.out.println("🧾 Invoice generated and saved (Invoice ID: " + invoice.getPaymentId() + ")");
        return true;
    }

}
