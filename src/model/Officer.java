package src.model;

import src.service.ApplicantService;
import src.service.OfficerService;
import src.service.ProjectService;
import src.service.UserService;
import src.util.CSVWriter;
import src.util.ConsoleUtils;
import src.util.InputValidator;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    public Officer(String nric, String password, String name, int age, String maritalStatus, String assignedProjectName, String registrationStatus) {
        super(nric, password, name, age, maritalStatus);
        this.assignedProjectName = assignedProjectName;
        this.registrationStatus = registrationStatus;
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

    private void printVacantProjects(Officer officer, ProjectService projectService) {
        Map<String, Project> allProjects = projectService.getAllProjects();

        System.out.println("=== Vacant Projects (Open & Visible) ===");
        boolean found = false;

        LocalDate today = LocalDate.now();

        for (Project project : allProjects.values()) {
            // Only include projects that are:
            // 1. Visible
            // 2. Application window is open
            // 3. Officer slots are not fully filled
            boolean isVisible = project.isVisible();
            boolean isOpen = (today.isEqual(project.getOpenDate()) || today.isAfter(project.getOpenDate())) &&
                    (today.isBefore(project.getCloseDate()) || today.isEqual(project.getCloseDate()));
            int assigned = project.getOfficerNRICs().size();
            int allowed = project.getOfficerSlot();

            if (isVisible && isOpen && assigned < allowed) {
                found = true;
                System.out.printf("• %s (%s) — %d Vacant Officer Slot%s\n\n",
                        project.getName(),
                        project.getNeighbourhood(),
                        allowed - assigned,
                        (allowed - assigned == 1 ? "" : "s")
                );
            }
        }

        if (!found) {
            System.out.println("⚠️ No projects currently open and available for officer registration.\n");
        }
    }

    @Override
    public void showMenu(ProjectService ps, UserService us) {
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

            try {
                choice = InputValidator.getInt("Enter your choice: ");
            } catch (NumberFormatException e) {
                choice = -1; // keep loop going safely
            }

            ConsoleUtils.clear();

            switch (choice) {
                case 1 -> {
                    if (this.getAssignedProjectName() != null && !this.getAssignedProjectName().isEmpty()) {
                        System.out.println("⚠️ You have already applied for project " + this.getAssignedProjectName());
                    } else {
                        printVacantProjects(this, ps);
                        String projName = InputValidator.getNonEmptyString("Enter project name to register: ");
                        officerService.registerForProject(this, projName);
                    }
                }
                case 2 -> officerService.viewAssignedProject(this);
                case 3 -> officerService.viewApplicantList(this);
                case 4 -> {
                    if (this.getAssignedProjectName() == null || this.getAssignedProjectName().isEmpty()) {
                        System.out.println("⚠️ You have not applied for any project.");
                    } else {
                        if (this.getRegistrationStatus().equalsIgnoreCase(RegistrationStatusType.APPROVED.name())) {
                            Project project = ps.getProjectByName(this.getAssignedProjectName());
                            if (project.getApplicantNRICs().size() == 0) {
                                System.out.println("⚠️ You have no applicants to approve/reject for " + this.getAssignedProjectName() + ".");
                            } else {
                                System.out.println("Applicant List:");
                                for (int i = 0; i < project.getApplicantNRICs().size(); i++) {
                                    Applicant applicant = us.getApplicantByNric(project.getApplicantNRICs().get(i));
                                    System.out.println((i+1) + ". " + applicant.getName() + " (" + applicant.getNric() + ")");
                                }

                                String nric = InputValidator.getNonEmptyString("Enter applicant NRIC to approve/reject: ");
                                if (project.getApplicantNRICs().contains(nric)) {
                                    boolean approve = InputValidator.getYesNo("Approve this applicant?");
                                    officerService.handleApplication(project, nric, approve);
                                } else {
                                    System.out.println("⚠️ Applicant does not exist.");
                                }
                            }
                        } else {
                            System.out.println("⚠️ Your registration status has not been approved for " + this.getAssignedProjectName());
                        }
                    }
                }
                case 5 -> {
                    if (this.getAssignedProjectName() == null || this.getAssignedProjectName().isEmpty()) {
                        System.out.println("⚠️ You have not applied for any project.");
                    } else {
                        if (this.getRegistrationStatus().equalsIgnoreCase(RegistrationStatusType.APPROVED.name())) {
                            Project project = ps.getProjectByName(this.getAssignedProjectName());

                            List<String> applicantNrics = project.getApplicantNRICs();
                            ArrayList<Applicant> approvedApplicants = new ArrayList<Applicant>();
                            List<String> approvedApplicantsNrics = project.getApplicantNRICs();

                            for (int i = 0; i < applicantNrics.size(); i++) {
                                Applicant applicant = us.getApplicantByNric(applicantNrics.get(i));
                                if (applicant.getApplicationStatus().equalsIgnoreCase(AppStatusType.SUCCESSFUL.name())) {
                                    approvedApplicants.add(applicant);
                                    approvedApplicantsNrics.add(applicant.getNric());
                                }
                            }

                            if (approvedApplicants.size() == 0) {
                                System.out.println("⚠️ You have no approved applicants to set as booked for " + this.getAssignedProjectName() + ".");
                            } else {
                                System.out.println("Approved Applicant List:");
                                for (int i = 0; i < approvedApplicants.size(); i++) {
                                    System.out.println((i+1) + ". " + approvedApplicants.get(i).getName() + " (" + approvedApplicants.get(i).getNric() + ")");
                                }

                                String nric = InputValidator.getNonEmptyString("Enter applicant NRIC to book flat: ");
                                if (approvedApplicantsNrics.contains(nric)) {
                                    officerService.bookFlat(nric);
                                } else {
                                    System.out.println("⚠️ Applicant does not exist.");
                                }
                            }
                        } else {
                            System.out.println("⚠️ Your registration status has not been approved for " + this.getAssignedProjectName());
                        }
                    }
                }
                case 6 -> {
                    if (this.getAssignedProjectName() == null || this.getAssignedProjectName().isEmpty()) {
                        System.out.println("⚠️ You have not applied for any project.");
                    } else {
                        if (this.getRegistrationStatus().equalsIgnoreCase(RegistrationStatusType.APPROVED.name())) {
                            Project project = ps.getProjectByName(this.getAssignedProjectName());

                            List<String> applicantNrics = project.getApplicantNRICs();
                            ArrayList<Applicant> bookedApplicants = new ArrayList<Applicant>();
                            List<String> bookedApplicantsNrics = project.getApplicantNRICs();

                            for (int i = 0; i < applicantNrics.size(); i++) {
                                Applicant applicant = us.getApplicantByNric(applicantNrics.get(i));
                                if (applicant.getApplicationStatus().equalsIgnoreCase(AppStatusType.BOOKED.name())) {
                                    bookedApplicants.add(applicant);
                                    bookedApplicantsNrics.add(applicant.getNric());
                                }
                            }

                            if (bookedApplicants.size() == 0) {
                                System.out.println("⚠️ You have no booked applicants to generate receipt for " + this.getAssignedProjectName() + ".");
                            } else {
                                System.out.println("Booked Applicant List:");
                                for (int i = 0; i < bookedApplicants.size(); i++) {
                                    System.out.println((i+1) + ". " + bookedApplicants.get(i).getName() + " (" + bookedApplicants.get(i).getNric() + ")");
                                }

                                String nric = InputValidator.getNonEmptyString("Enter applicant NRIC to generate receipt: ");
                                if (bookedApplicantsNrics.contains(nric)) {
                                    officerService.generateReceipt(nric);
                                } else {
                                    System.out.println("⚠️ Applicant does not exist.");
                                }
                            }
                        } else {
                            System.out.println("⚠️ Your registration status has not been approved for " + this.getAssignedProjectName());
                        }
                    }
                }
                case 7 -> {
                    String newPass = InputValidator.getNonEmptyString("Enter new password: ");
                    changePassword(newPass);
                    boolean updatedSuccessfully = CSVWriter.updateUserPassword(this);
                    System.out.println(updatedSuccessfully ? "✅ Password updated, please log out and log back in, redirecting in 3 seconds!" : "❌ Failed to update password.");

                    try {
                        Thread.sleep(3000); // Wait 3 seconds
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    choice = 0;
                }
                case 0 -> System.out.println("Logging out...");
                default -> System.out.println("Invalid option.");
            }

            if (choice != 0) ConsoleUtils.pause();
        } while (choice != 0);
    }
}
