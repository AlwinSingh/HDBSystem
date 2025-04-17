package src.service;

import java.util.*;
import java.util.stream.Collectors;
import src.model.*;
import src.util.ApplicantCsvMapper;
import src.util.AmenitiesCsvMapper;
import src.util.EnquiryCsvMapper;
import src.util.OfficerCsvMapper;
import src.util.ProjectCsvMapper;

public class OfficerMenu {

    private static final String APPLICANT_PATH = "data/ApplicantList.csv";
    private static final String OFFICER_PATH   = "data/OfficerList.csv";
    private static final String PROJECT_PATH   = "data/ProjectList.csv";
    private static final String ENQUIRY_PATH   = "data/EnquiryList.csv";

    public static void show(HDBOfficer officer) {
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("\n===== 🧑‍💼 HDB Officer Dashboard =====");
            System.out.println("Welcome, Officer " + officer.getName());
            System.out.println("1. View registration status");
            System.out.println("2. Register for project");
            System.out.println("3. View assigned project details");
            System.out.println("4. Book flat for applicant");
            System.out.println("5. Generate receipt for applicant");
            System.out.println("6. View & reply to enquiries");
            System.out.println("7. Change Password");
            System.out.println("8. Update project location");     // ← new
            System.out.println("9. Add an amenity");               // ← new
            System.out.println("0. Logout");
            System.out.print("Enter choice: ");

            switch (sc.nextLine().trim()) {
                case "1" -> viewRegistrationStatus(officer);
                case "2" -> registerForProject(officer, sc);
                case "3" -> viewAssignedProjectDetails(officer);
                case "4" -> bookFlat(officer, sc);
                case "5" -> generateReceipt(officer);
                case "6" -> handleEnquiries(officer, sc);
                case "7" -> AuthService.changePassword(officer, sc);
                case "8" -> updateLocation(officer, sc);         // ← new
                case "9" -> addAmenity(officer, sc);             // ← new
                case "0" -> {
                    System.out.println("👋 Logging out...");
                    return;
                }
                default  -> System.out.println("❌ Invalid input.");
            }
        }
    }

    private static void viewRegistrationStatus(HDBOfficer officer) {
        officer.viewOfficerRegistrationStatus();
    }

    private static void registerForProject(HDBOfficer officer, Scanner sc) {
        if (officer.getAssignedProject() != null) {
            System.out.println("✅ You are already registered to project: " +
                officer.getAssignedProject().getProjectName());
            return;
        }
    
        List<Project> projects = ProjectCsvMapper.loadAll(PROJECT_PATH);
    
        System.out.println("\n📋 Available Projects:");
        List<Project> available = projects.stream()
            .filter(p -> p.isVisible() && !p.getOfficerNRICs().contains(officer.getNric()))
            .collect(Collectors.toList());
    
        if (available.isEmpty()) {
            System.out.println("❌ No visible projects available.");
            return;
        }
    
        for (int i = 0; i < available.size(); i++) {
            System.out.printf("[%d] %s (%s)\n", i + 1, available.get(i).getProjectName(), available.get(i).getNeighborhood());
        }
    
        System.out.print("Choose project number to register: ");
        try {
            int idx = Integer.parseInt(sc.nextLine().trim()) - 1;
            if (idx < 0 || idx >= available.size()) throw new IndexOutOfBoundsException();
    
            Project selected = available.get(idx);
            boolean registered = officer.registerToHandleProject(selected);
            if (registered) {
                OfficerCsvMapper.updateOfficer(OFFICER_PATH, officer);
                System.out.println("✅ Registration submitted.");
            } else {
                System.out.println("❌ Could not register. Check your current assignment or application status.");
            }
    
        } catch (Exception e) {
            System.out.println("❌ Invalid selection.");
        }
    }
    
    private static void viewAssignedProjectDetails(HDBOfficer officer) {
        Project p = officer.getAssignedProject();
        if (p == null) {
            System.out.println("❌ No assigned project.");
            return;
        }
    
        System.out.println("\n📌 Assigned Project Details:");
        System.out.println("🏢 Project Name       : " + p.getProjectName());
        System.out.println("📍 Neighborhood       : " + p.getNeighborhood());
    
        // ### New: location fields ###
        ProjectLocation loc = p.getLocation();
        System.out.println("🌆 District & Town     : " + loc.getDistrict() + ", " + loc.getTown());
        System.out.println("📫 Address             : " + loc.getAddress());
        System.out.printf("🗺️ Coordinates         : %.6f, %.6f%n", loc.getLat(), loc.getLng());
    
        System.out.println("🧍 Officer Slots      : " + p.getOfficerSlots());
        System.out.println("🏠 2‑Room Units       : " + p.getRemainingFlats("2‑Room"));
        System.out.println("🏠 3‑Room Units       : " + p.getRemainingFlats("3‑Room"));
        System.out.println("📅 Application Period : " + p.getOpenDate() + " to " + p.getCloseDate());
        System.out.println("👀 Visible to Public  : " + (p.isVisible() ? "Yes ✅" : "No ❌"));
        System.out.println("📊 Your Registration  : " + officer.getRegistrationStatus());
    
        // ### New: amenities ###
        if (!p.getAmenities().isEmpty()) {
            System.out.println("\n🏞️ Nearby Amenities:");
            for (Amenities a : p.getAmenities()) {
                System.out.println("   - " + a.getAmenityDetails());
            }
        }
    }
    

    private static void bookFlat(HDBOfficer officer, Scanner sc) {
        Project assigned = officer.getAssignedProject();
        if (assigned == null) {
            System.out.println("❌ No assigned project.");
            return;
        }
    
        if ("PENDING".equalsIgnoreCase(officer.getRegistrationStatus())) {
            System.out.println("⚠️ You cannot perform bookings as your registration to this project is still pending approval.");
            return;
        }
    
        List<Applicant> applicants = ApplicantCsvMapper.loadAll(APPLICANT_PATH);
        List<Applicant> pending = applicants.stream()
            .filter(a -> a.getApplication() != null)
            .filter(a -> a.getApplication().getProject().getProjectName().equalsIgnoreCase(assigned.getProjectName()))
            .filter(a -> "SUCCESSFUL".equalsIgnoreCase(a.getApplication().getStatus()))
            .collect(Collectors.toList());
    
        if (pending.isEmpty()) {
            System.out.println("❌ No applicants ready for booking.");
            return;
        }
    
        System.out.println("\n📋 Eligible Applicants:");
        for (int i = 0; i < pending.size(); i++) {
            Applicant a = pending.get(i);
            System.out.printf("[%d] %s (NRIC: %s)\n", i + 1, a.getName(), a.getNric());
        }
    
        System.out.print("Select applicant to book: ");
        try {
            int idx = Integer.parseInt(sc.nextLine().trim()) - 1;
            if (idx < 0 || idx >= pending.size()) throw new IndexOutOfBoundsException();
    
            Applicant selected = pending.get(idx);
            String flatType = selected.getApplication().getFlatType();
    
            officer.bookFlat(selected.getApplication(), flatType);
            selected.getApplication().setStatus("BOOKED"); // ✅ Update status here
            ApplicantCsvMapper.updateApplicant(APPLICANT_PATH, selected);
            ProjectCsvMapper.saveAll(PROJECT_PATH, ProjectCsvMapper.loadAll(PROJECT_PATH));
    
            System.out.println("✅ Booking successful.");
        } catch (Exception e) {
            System.out.println("❌ Invalid booking.");
        }
    }
    
    private static void handleEnquiries(HDBOfficer officer, Scanner sc) {
        Project assignedProject = officer.getAssignedProject();
        if (assignedProject == null) {
            System.out.println("❌ No assigned project.");
            return;
        }
    
        if (!"SUCCESSFUL".equalsIgnoreCase(officer.getRegistrationStatus())) {
            System.out.println("⚠️ You are not authorized to view or reply to enquiries until your registration is approved.");
            return;
        }
    
        List<Enquiry> allEnquiries = EnquiryCsvMapper.loadAll(ENQUIRY_PATH);
        List<Enquiry> projectEnquiries = allEnquiries.stream()
            .filter(e -> e.getProject().getProjectName().equalsIgnoreCase(assignedProject.getProjectName()))
            .collect(Collectors.toList());
    
        if (projectEnquiries.isEmpty()) {
            System.out.println("📭 No enquiries found for your project.");
            return;
        }
    
        System.out.println("\n📬 Enquiries for Project: " + assignedProject.getProjectName());
        for (int i = 0; i < projectEnquiries.size(); i++) {
            Enquiry e = projectEnquiries.get(i);
            System.out.printf("[%d] %s: %s\n", i + 1, e.getApplicant().getName(), e.getContent());
            if (!e.getReplies().isEmpty()) {
                System.out.println("    💬 Latest Reply: " + e.getReplies().get(e.getReplies().size() - 1));
            }
        }
    
        System.out.print("Select an enquiry to reply (or 0 to cancel): ");
        try {
            int idx = Integer.parseInt(sc.nextLine().trim());
            if (idx == 0) return;
            if (idx < 1 || idx > projectEnquiries.size()) throw new IndexOutOfBoundsException();
    
            Enquiry selected = projectEnquiries.get(idx - 1);
            if (selected.isClosed()) {
                System.out.println("⚠️ This enquiry is already closed. You cannot reply.");
                return;
            }
            
            System.out.print("Enter your reply: ");
            String reply = sc.nextLine().trim();
            
            selected.replyFromOfficer(reply);
            EnquiryCsvMapper.saveAll(ENQUIRY_PATH, allEnquiries);
            System.out.println("✅ Reply sent and enquiry marked as CLOSED.");
            
    
        } catch (Exception e) {
            System.out.println("❌ Invalid selection.");
        }
    }
    

    private static void generateReceipt(HDBOfficer officer) {
        System.out.println("\n🧾 Generate Receipt: Work in Progress...");
        System.out.println("This feature is currently under development and will be available soon.");
    }

    private static void updateLocation(HDBOfficer officer, Scanner sc) {
        Project p = officer.getAssignedProject();
        if (p == null) {
            System.out.println("❌ No assigned project to update.");
            return;
        }
    
        System.out.println("\n✏️  Update location for " + p.getProjectName());
        System.out.printf("Current District [%s]: ", p.getLocation().getDistrict());
        String input = sc.nextLine().trim();
        if (!input.isEmpty()) p.getLocation().setDistrict(input);
    
        System.out.printf("Current Town     [%s]: ", p.getLocation().getTown());
        input = sc.nextLine().trim();
        if (!input.isEmpty()) p.getLocation().setTown(input);
    
        System.out.printf("Current Address  [%s]: ", p.getLocation().getAddress());
        input = sc.nextLine().trim();
        if (!input.isEmpty()) p.getLocation().setAddress(input);
    
        try {
            System.out.printf("Current Latitude [%.6f]: ", p.getLocation().getLat());
            input = sc.nextLine().trim();
            if (!input.isEmpty()) p.getLocation().setLat(Double.parseDouble(input));
    
            System.out.printf("Current Longitude[%.6f]: ", p.getLocation().getLng());
            input = sc.nextLine().trim();
            if (!input.isEmpty()) p.getLocation().setLng(Double.parseDouble(input));
        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid coordinates – update aborted.");
            return;
        }
    
        // Persist only the one updated project
        ProjectCsvMapper.updateProject(PROJECT_PATH, p);
        System.out.println("✅ Location updated.");
    }
    

    private static void addAmenity(HDBOfficer officer, Scanner sc) {
        Project p = officer.getAssignedProject();
        if (p == null) {
            System.out.println("❌ No assigned project to add amenities.");
            return;
        }
    
        System.out.println("\n➕ Adding amenity for " + p.getProjectName());
    
        // compute next ID
        int nextId = AmenitiesCsvMapper.loadAll().stream()
                          .map(Amenities::getAmenityId)
                          .max(Integer::compareTo)
                          .orElse(0) + 1;
    
        System.out.print("Type (e.g. MRT, Clinic): ");
        String type = sc.nextLine().trim();
        System.out.print("Name: ");
        String name = sc.nextLine().trim();
    
        double dist;
        try {
            System.out.print("Distance (km): ");
            dist = Double.parseDouble(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid distance. Amenity not added.");
            return;
        }
    
        // hand off to the mapper
        Amenities newAmenity = new Amenities(nextId, type, name, dist, p.getProjectName());
        AmenitiesCsvMapper.add(newAmenity);
    
        System.out.println("✅ Amenity added (ID=" + nextId + ").");
    }

}
