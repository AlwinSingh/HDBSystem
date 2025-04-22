package src.interfaces;

import src.model.HDBManager;
import java.util.Scanner;

/**
 * Interface for managing applicant feedback by HDB Managers.
 * Focuses on listing, filtering, and resolving feedback from projects under the manager's control.
 */
public interface IManagerFeedbackService {

    /**
     * Displays the feedback management menu for the given manager.
     * Allows filtering, viewing, and resolving feedback.
     *
     * @param manager The logged-in manager.
     * @param sc      Scanner for reading user input.
     */
    void viewAndResolveFeedback(HDBManager manager, Scanner sc);
}
