package src.service;

import java.util.Scanner;

import src.model.HDBManager;

public class ManagerEnquiryService {

        /**
     * Shows the enquiry options available to managers, allowing them to respond.
     */
    public static void showEnquiryOptions(HDBManager manager, Scanner sc) {
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
