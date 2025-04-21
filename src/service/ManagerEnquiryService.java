package src.service;

import java.util.Scanner;

import src.interfaces.IManagerEnquiryService;
import src.model.HDBManager;

/**
 * Provides services for HDB managers to handle and respond to applicant enquiries.
 * Enables managers to view all enquiries or reply only to enquiries under their assigned projects.
 */
public class ManagerEnquiryService implements IManagerEnquiryService {

    /**
     * Displays the enquiry options menu for the manager, and options based on input.
     *
     * Options include:
     * <ul>
     *     <li>Viewing all enquiries in the system</li>
     *     <li>Replying only to enquiries under the manager's assigned projects</li>
     *     <li>Returning to the previous menu</li>
     * </ul>
     *
     * @param manager The currently logged-in HDB manager.
     * @param sc      Scanner object for receiving console input.
     */
    @Override
    public void showEnquiryOptions(HDBManager manager, Scanner sc) {
        while (true) {
            System.out.println("\n📨 Enquiry Options");
            System.out.println(" [1] View All Enquiries");
            System.out.println(" [2] Reply to Enquiries for My Projects");
            System.out.println(" [0] Back");
            System.out.print("➡️ Enter your choice: ");
    
            String choice = sc.nextLine().trim();
            switch (choice) {
                case "1" -> EnquireService.viewAllEnquiries();
                case "2" -> EnquireService.replyAsManager(manager, sc);
                case "0" -> {
                    System.out.println("🔙 Returning to manager menu...");
                    return;
                }
                default -> System.out.println("❌ Invalid input.");
            }
        }
    }

}
