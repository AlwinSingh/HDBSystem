package src.model;

import java.util.Scanner;

import src.service.OfficerService;
import src.service.ProjectService;
import src.service.UserService;
import src.util.ConsoleUtils;

/**
 * Officer class, inherits from Applicant and adds BTO project handling capability.
 */
public class Officer extends Applicant {
    private String assignedProjectName;
    private String registrationStatus; // "PENDING", "APPROVED", "REJECTED"

    public enum RegistrationStatusType {
        PENDING,
        APPROVED,
        REJECTED
    }

    public Officer(String nric, String password, String name, int age, String maritalStatus) {
        super(nric, password, name, age, maritalStatus);
        this.assignedProjectName = null;
        this.registrationStatus = ""; // By default, not registered
    }

    public String getAssignedProjectName() {
        return assignedProjectName;
    }

    public String getRegistrationStatus() {
        return registrationStatus;
    }

    public void registerForProject(String projectName) {
        if (assignedProjectName != null) {
            System.out.println("You are already registered for a project: " + assignedProjectName);
        } else {
            this.assignedProjectName = projectName;
            this.registrationStatus = RegistrationStatusType.PENDING.name();
            System.out.println("Officer registration submitted for project: " + projectName);
        }
    }

    public void setRegistrationStatus(String status) {
        this.registrationStatus = status;
    }

    public void setAssignedProjectName(String assignedProjectName) {
        this.assignedProjectName = assignedProjectName;
    }

    public void bookFlatForApplicant(Applicant applicant, String flatType) {
        if (!AppStatusType.SUCCESSFUL.name().equals(applicant.getApplicationStatus())) {
            System.out.println("Applicant is not eligible for flat booking.");
            return;
        }

        applicant.applicationStatus = AppStatusType.BOOKED.name();
        applicant.flatTypeApplied = flatType;

        System.out.println("Flat booked successfully for " + applicant.getName());
        // Todo: We also need to reduce flat count, update project data, generate receipt, etc.
    }

    public void generateReceipt(Applicant applicant) {
        if (!AppStatusType.BOOKED.name().equals(applicant.getApplicationStatus())) {
            System.out.println("Cannot generate receipt. Applicant has not booked a flat.");
            return;
        }

        System.out.println("\n--- Flat Booking Receipt ---");
        System.out.println("Name: " + applicant.getName());
        System.out.println("NRIC: " + applicant.getNric());
        System.out.println("Age: " + applicant.getAge());
        System.out.println("Marital Status: " + applicant.getMaritalStatus());
        System.out.println("Flat Type: " + applicant.getFlatTypeApplied());
        System.out.println("Project: " + applicant.getAppliedProjectName());
        System.out.println("-----------------------------");
    }

    public void replyToEnquiry(String enquiryId, String replyMessage) {
        // Placeholder — will be connected to Enquiry system later
        System.out.println("Reply submitted to enquiry: " + enquiryId);
    }

    @Override
    public void showMenu(ProjectService ps, UserService us) {
        Scanner sc = new Scanner(System.in);
        OfficerService officerService = new OfficerService(ps, us);

        int choice;
        do {
            // ConsoleUtils.clear(); // Optional: comment if you want to disable screen clearing

            System.out.println("=== Officer Menu ===");
            System.out.println("1. Register for Project");
            System.out.println("2. View Assigned Project Details");
            System.out.println("3. View Applicant List");
            System.out.println("4. Approve/Reject Applicants");
            System.out.println("5. Book Flat for Applicant");
            System.out.println("6. Generate Receipt");
            System.out.println("7. Change Password");
            System.out.println("0. Logout");
            ConsoleUtils.lineBreak();
            System.out.print("Enter your choice: ");
            choice = Integer.parseInt(sc.nextLine());

            ConsoleUtils.clear();

            switch (choice) {
                case 1 -> {
                    System.out.print("Enter project name to register: ");
                    String projName = sc.nextLine();
                    officerService.registerForProject(this, projName);
                }
                case 2 -> officerService.viewAssignedProject(this);
                case 3 -> officerService.viewApplicantList(this);
                case 4 -> {
                    System.out.print("Enter applicant NRIC to approve/reject: ");
                    String nric = sc.nextLine().trim();
                    System.out.print("Approve? (Y/N): ");
                    String input = sc.nextLine().trim().toUpperCase();
                    boolean approve = input.equals("Y");
                    officerService.handleApplication(nric, approve);
                }
                case 5 -> {
                    System.out.print("Enter applicant NRIC to book flat: ");
                    String nric = sc.nextLine().trim();
                    officerService.bookFlat(nric);
                }
                case 6 -> {
                    System.out.print("Enter applicant NRIC to generate receipt: ");
                    String nric = sc.nextLine().trim();
                    officerService.generateReceipt(nric);
                }
                case 7 -> {
                    System.out.print("Enter new password: ");
                    String newPass = sc.nextLine();
                    changePassword(newPass);
                    System.out.println("✅ Password updated.");
                }
                case 0 -> System.out.println("Logging out...");
                default -> System.out.println("Invalid option.");
            }

            if (choice != 0) ConsoleUtils.pause();
        } while (choice != 0);
    }
}
