package src.interfaces;

import java.util.List;
import java.util.Scanner;
import src.model.Enquiry;
import src.model.HDBOfficer;

/**
 * Interface for enquiry-related operations performed by HDB officers.
 */
public interface IOfficerEnquiryService {

    /**
     * Displays and allows handling of enquiries related to the officer's assigned project.
     *
     * @param officer The logged-in officer.
     * @param sc      Scanner for input.
     */
    void handleEnquiries(HDBOfficer officer, Scanner sc);

    /**
     * Retrieves pending enquiries related to the officer's assigned project.
     *
     * @param officer The officer.
     * @return List of pending enquiries.
     */
    List<Enquiry> getPendingEnquiriesForProject(HDBOfficer officer);

    /**
     * Replies to the specified enquiry with the provided content and closes it.
     *
     * @param enquiry The enquiry to respond to.
     * @param officer The officer sending the reply.
     * @param reply   The message content.
     * @return True if the reply was successful.
     */
    boolean replyToEnquiry(Enquiry enquiry, HDBOfficer officer, String reply);
}
