package src.interfaces;

import java.util.Scanner;
import src.model.HDBManager;

/**
 * Interface for manager operations related to officer approval.
 */
public interface IManagerOfficerApprovalService {

    /**
     * Displays all officer registrations tied to the manager's assigned projects.
     *
     * @param manager The logged-in manager.
     */
    void viewOfficerRegistrations(HDBManager manager);

    /**
     * Allows a manager to approve or reject officer registrations.
     *
     * @param manager The manager performing the operation.
     * @param sc      Scanner to read user input.
     */
    void handleOfficerApproval(HDBManager manager, Scanner sc);
}
