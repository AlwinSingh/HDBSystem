package src.interfaces;

import java.util.List;
import java.util.Scanner;

import src.model.Applicant;
import src.model.HDBOfficer;
import src.model.Project;

/**
 * Interface that defines booking-related operations
 * HDB Officers can perform on eligible applicants.
 */
public interface IOfficerBookingService {

    /**
     * Retrieves all applicants eligible for booking in a given project.
     *
     * @param assignedProject The officer's assigned project.
     * @return A list of bookable applicants.
     */
    List<Applicant> getBookableApplicants(Project assignedProject);

    /**
     * Handles flat booking workflow for an applicant.
     *
     * @param officer The logged-in officer.
     * @param sc      Scanner for input.
     */
    void bookFlat(HDBOfficer officer, Scanner sc);

    /**
     * Executes booking and invoice generation for the given applicant.
     *
     * @param officer   The officer performing the booking.
     * @param applicant The target applicant.
     * @return True if booking and invoice were successful.
     */
    boolean bookFlatAndGenerateInvoice(HDBOfficer officer, Applicant applicant);
}
