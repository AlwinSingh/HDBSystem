package src.interfaces;

import src.model.HDBManager;

import java.util.Scanner;

/**
 * Interface for manager-level enquiry interaction services.
 * Supports displaying enquiry menus and handling replies under assigned projects.
 */
public interface IManagerEnquiryService {

    /**
     * Displays the enquiry options menu for the given manager.
     * Allows viewing all enquiries or replying to enquiries related to the manager's projects.
     *
     * @param manager The currently logged-in manager.
     * @param sc      Scanner object for console input.
     */
    void showEnquiryOptions(HDBManager manager, Scanner sc);
}
