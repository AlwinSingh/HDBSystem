package src.service;

import java.util.List;
import java.util.Scanner;

import src.model.Amenities;
import src.model.Applicant;
import src.model.Application;
import src.model.Enquiry;
import src.model.HDBOfficer;
import src.model.Invoice;
import src.model.Payment;
import src.model.Project;
import src.model.ProjectLocation;
import src.model.Receipt;
import src.util.AmenitiesCsvMapper;
import src.util.ApplicantCsvMapper;
import src.util.EnquiryCsvMapper;
import src.util.OfficerCsvMapper;
import src.util.ProjectCsvMapper;

public class OfficerService {

    public static boolean registerForProject(HDBOfficer officer, Project selectedProject) {
        if (officer.getAssignedProject() != null) {
            return false;
        }

        boolean registered = officer.registerToHandleProject(selectedProject);
        if (registered) {
            OfficerCsvMapper.updateOfficer(officer);
        }

        return registered;
    }

    public static List<Project> getAvailableProjectsForOfficer(HDBOfficer officer) {
        return ProjectCsvMapper.loadAll().stream()
            .filter(Project::isVisible)
            .filter(p -> !p.getOfficerNRICs().contains(officer.getNric()))
            .toList();
    }

    public static List<Amenities> getProjectAmenities(Project project) {
        return project.getAmenities();
    }

    public static String getProjectSummary(Project p, HDBOfficer officer) {
        StringBuilder sb = new StringBuilder();
        ProjectLocation loc = p.getLocation();

        sb.append("🏢 Project Name       : ").append(p.getProjectName()).append("\n")
          .append("📍 Neighborhood       : ").append(p.getNeighborhood()).append("\n")
          .append("🌆 District & Town    : ").append(loc.getDistrict()).append(", ").append(loc.getTown()).append("\n")
          .append("📫 Address            : ").append(loc.getAddress()).append("\n")
          .append(String.format("🗺️ Coordinates         : %.6f, %.6f\n", loc.getLat(), loc.getLng()))
          .append("🧍 Officer Slots      : ").append(p.getOfficerSlots()).append("\n")
          .append("🏠 2-Room Units       : ").append(p.getRemainingFlats("2-Room")).append("\n")
          .append("💰 2-Room Price       : $").append(String.format("%.2f", p.getPrice2Room())).append("\n")
          .append("🏠 3-Room Units       : ").append(p.getRemainingFlats("3-Room")).append("\n")
          .append("💰 3-Room Price       : $").append(String.format("%.2f", p.getPrice3Room())).append("\n")
          .append("📅 Application Period : ").append(p.getOpenDate()).append(" to ").append(p.getCloseDate()).append("\n")
          .append("👀 Visible to Public  : ").append(p.isVisible() ? "Yes ✅" : "No ❌").append("\n")
          .append("📊 Your Registration  : ").append(officer.getRegistrationStatus());

        return sb.toString();
    }

    public static void browseAndFilterProjects(Scanner sc) {
        System.out.println("\n🔍 Browse & Filter Available Projects (for reference only)");
    
        List<Project> all = ProjectCsvMapper.loadAll();
        if (all.isEmpty()) {
            System.out.println("📭 No projects found.");
            return;
        }
    
        class Filter { // inner mutable holder
            String name = null;
            String neighborhood = null;
            Integer minSlots = null;
            Boolean visible = null;
        }
    
        Filter filter = new Filter();
    
        while (true) {
            List<Project> filtered = all.stream()
                .filter(p -> filter.name == null || p.getProjectName().toLowerCase().contains(filter.name.toLowerCase()))
                .filter(p -> filter.neighborhood == null || p.getNeighborhood().toLowerCase().contains(filter.neighborhood.toLowerCase()))
                .filter(p -> filter.minSlots == null || p.getOfficerSlots() >= filter.minSlots)
                .filter(p -> filter.visible == null || p.isVisible() == filter.visible)
                .toList();
    
            System.out.println("\n📋 Filtered Projects (" + filtered.size() + "):");
            if (filtered.isEmpty()) {
                System.out.println("📭 No matching projects.");
            } else {
                for (int i = 0; i < filtered.size(); i++) {
                    Project p = filtered.get(i);
                    System.out.printf("[%d] 📌 %s (%s) | Slots: %d | Visible: %s%n",
                        i + 1, p.getProjectName(), p.getNeighborhood(), p.getOfficerSlots(),
                        p.isVisible() ? "Yes" : "No");
                }
            }
    
            System.out.println("\n🔧 Filter Options:");
            System.out.println(" [1] Filter by Project Name");
            System.out.println(" [2] Filter by Neighborhood");
            System.out.println(" [3] Filter by Min Officer Slots");
            System.out.println(" [4] Filter by Visibility (true/false)");
            System.out.println(" [5] Clear All Filters");
            System.out.println(" [0] Back");
            System.out.print("➡️ Enter your choice: ");
            String choice = sc.nextLine().trim();
    
            switch (choice) {
                case "1" -> {
                    System.out.print("🔤 Enter partial project name: ");
                    String input = sc.nextLine().trim();
                    filter.name = input.isEmpty() ? null : input;
                }
                case "2" -> {
                    System.out.print("📍 Enter neighborhood: ");
                    String input = sc.nextLine().trim();
                    filter.neighborhood = input.isEmpty() ? null : input;
                }
                case "3" -> {
                    System.out.print("👥 Enter minimum officer slots: ");
                    try {
                        filter.minSlots = Integer.parseInt(sc.nextLine().trim());
                    } catch (Exception e) {
                        filter.minSlots = null;
                    }
                }
                case "4" -> {
                    System.out.print("👀 Show visible only? (true/false): ");
                    String vis = sc.nextLine().trim().toLowerCase();
                    filter.visible = vis.equals("true") ? true : vis.equals("false") ? false : null;
                }
                case "5" -> {
                    filter.name = filter.neighborhood = null;
                    filter.minSlots = null;
                    filter.visible = null;
                    System.out.println("✅ Filters cleared.");
                }
                case "0" -> {
                    System.out.println("🔙 Returning...");
                    return;
                }
                default -> System.out.println("❌ Invalid input. Try again.");
            }
        }
    }

    public static void registerForProject(HDBOfficer officer, Scanner sc) {
        if (officer.getAssignedProject() != null) {
            System.out.println("✅ You are already registered to project: " +
                officer.getAssignedProject().getProjectName());
            return;
        }
    
        List<Project> available = OfficerService.getAvailableProjectsForOfficer(officer);
    
        if (available.isEmpty()) {
            System.out.println("❌ No visible projects available.");
            return;
        }
    
        System.out.println("\n📋 Available Projects:");
        for (int i = 0; i < available.size(); i++) {
            System.out.printf("[%d] %s (%s)\n", i + 1, available.get(i).getProjectName(), available.get(i).getNeighborhood());
        }
    
        System.out.print("Choose project number to register: ");
        try {
            int idx = Integer.parseInt(sc.nextLine().trim()) - 1;
            if (idx < 0 || idx >= available.size()) throw new IndexOutOfBoundsException();
    
            Project selected = available.get(idx);
            boolean registered = OfficerService.registerForProject(officer, selected);
            if (registered) {
                System.out.println("✅ Registration submitted.");
            } else {
                System.out.println("❌ Could not register. Check your current assignment or application status.");
            }
    
        } catch (Exception e) {
            System.out.println("❌ Invalid selection.");
        }
    }

    public static void viewAssignedProjectDetails(HDBOfficer officer) {
        Project p = officer.getAssignedProject();
        if (p == null) {
            System.out.println("❌ No assigned project.");
            return;
        }
    
        System.out.println("\n📌 Assigned Project Details:");
        System.out.println(OfficerService.getProjectSummary(p, officer));
    
        List<Amenities> amenities = OfficerService.getProjectAmenities(p);
        if (!amenities.isEmpty()) {
            System.out.println("\n🏞️ Nearby Amenities:");
            for (Amenities a : amenities) {
                System.out.println("   - " + a.getAmenityDetails());
            }
        }
    } 
    

    public static List<Applicant> getBookableApplicants(Project assignedProject) {
        return ApplicantCsvMapper.loadAll().stream()
            .filter(a -> a.getApplication() != null)
            .filter(a -> {
                Project appProject = a.getApplication().getProject();
                return appProject != null &&
                       appProject.getProjectName().equalsIgnoreCase(assignedProject.getProjectName());
            })
            .filter(a -> Applicant.AppStatusType.SUCCESSFUL.name().equalsIgnoreCase(a.getApplication().getStatus()))
            .toList();
    }

    public static void bookFlat(HDBOfficer officer, Scanner sc) {
        Project assigned = officer.getAssignedProject();
        if (assigned == null) {
            System.out.println("❌ No assigned project.");
            return;
        }
    
        if (HDBOfficer.RegistrationStatusType.PENDING.name().equalsIgnoreCase(officer.getRegistrationStatus())) {
            System.out.println("⚠️ You cannot perform bookings as your registration to this project is still pending approval.");
            return;
        }
    
        List<Applicant> eligible = OfficerService.getBookableApplicants(assigned);
    
        if (eligible.isEmpty()) {
            System.out.println("❌ No applicants ready for booking.");
            return;
        }
    
        System.out.println("\n📋 Eligible Applicants:");
        for (int i = 0; i < eligible.size(); i++) {
            Applicant a = eligible.get(i);
            System.out.printf("[%d] %s (NRIC: %s)\n", i + 1, a.getName(), a.getNric());
        }
    
        System.out.print("Select applicant to book: ");
        try {
            int idx = Integer.parseInt(sc.nextLine().trim()) - 1;
            if (idx < 0 || idx >= eligible.size()) throw new IndexOutOfBoundsException();
    
            Applicant selected = eligible.get(idx);
            boolean success = OfficerService.bookFlatAndGenerateInvoice(officer, selected);
            if (success) {
                System.out.println("✅ Booking successful.");
            } else {
                System.out.println("❌ Booking failed. Please check eligibility and data.");
            }
    
        } catch (Exception e) {
            System.out.println("❌ Invalid booking.");
        }
    }

    public static boolean bookFlatAndGenerateInvoice(HDBOfficer officer, Applicant applicant) {
        Application app = applicant.getApplication();
        if (app == null || officer.getAssignedProject() == null) return false;

        // Refresh full project info
        Project fullProject = ProjectCsvMapper.loadAll().stream()
            .filter(p -> p.getProjectName().equalsIgnoreCase(officer.getAssignedProject().getProjectName()))
            .findFirst()
            .orElse(null);

        if (fullProject == null) return false;

        // Inject full project reference
        app.setProject(fullProject);

        // Perform booking
        officer.bookFlat(app, app.getFlatType());
        app.setStatus(Applicant.AppStatusType.BOOKED.name());

        // Persist updates
        ApplicantCsvMapper.updateApplicant(applicant);

        int nextInvoiceId = InvoiceService.getNextInvoiceId();
        Invoice invoice = HDBOfficer.generateInvoiceForBooking(app, nextInvoiceId);
        InvoiceService.addInvoice(invoice);

        ProjectCsvMapper.updateProject(fullProject);

        System.out.println("🧾 Invoice generated and saved (Invoice ID: " + invoice.getPaymentId() + ")");
        return true;
    }

    public static List<Invoice> getInvoicesAwaitingReceipt(HDBOfficer officer) {
        return InvoiceService.getAllInvoices().stream()
            .filter(i -> "Awaiting Receipt".equalsIgnoreCase(i.getStatus()))
            .filter(i -> ReceiptService.findByInvoiceId(i.getPaymentId()) == null)
            .filter(i -> i.getProjectName().equalsIgnoreCase(officer.getAssignedProject().getProjectName()))
            .toList();
    }

    public static Applicant findApplicantByNRIC(String nric) {
        return ApplicantCsvMapper.loadAll().stream()
            .filter(a -> a.getNric().equalsIgnoreCase(nric))
            .findFirst()
            .orElse(null);
    }

    public static Project findFullProjectByName(String projectName) {
        return ProjectCsvMapper.loadAll().stream()
            .filter(p -> p.getProjectName().equalsIgnoreCase(projectName))
            .findFirst()
            .orElse(null);
    }

    public static void handleEnquiries(HDBOfficer officer, Scanner sc) {
        List<Enquiry> projectEnquiries = OfficerService.getPendingEnquiriesForProject(officer);
    
        if (projectEnquiries.isEmpty()) {
            System.out.println("📭 No open enquiries found for your project.");
            return;
        }
    
        System.out.println("\n📬 Enquiries for Project: " + officer.getAssignedProject().getProjectName());
        for (int i = 0; i < projectEnquiries.size(); i++) {
            Enquiry e = projectEnquiries.get(i);
            System.out.printf("[%d] %s: %s\n", i + 1, e.getApplicant().getName(), e.getContent());
        }
    
        System.out.print("Select an enquiry to reply (or 0 to cancel): ");
        try {
            int idx = Integer.parseInt(sc.nextLine().trim());
            if (idx == 0) return;
            if (idx < 1 || idx > projectEnquiries.size()) throw new IndexOutOfBoundsException();
    
            Enquiry selected = projectEnquiries.get(idx - 1);
            System.out.print("Enter your reply: ");
            String reply = sc.nextLine().trim();
    
            boolean ok = OfficerService.replyToEnquiry(selected, officer, reply);
            if (ok) {
                System.out.println("✅ Reply sent and enquiry marked as CLOSED.");
            } else {
                System.out.println("❌ Failed to reply.");
            }
    
        } catch (Exception e) {
            System.out.println("❌ Invalid selection.");
        }
    }

    public static List<Enquiry> getPendingEnquiriesForProject(HDBOfficer officer) {
        Project assigned = officer.getAssignedProject();
        if (assigned == null || !"APPROVED".equalsIgnoreCase(officer.getRegistrationStatus())) return List.of();

        return EnquiryCsvMapper.loadAll().stream()
            .filter(e -> e.getProject().getProjectName().equalsIgnoreCase(assigned.getProjectName()))
            .filter(e -> Enquiry.STATUS_PENDING.equalsIgnoreCase(e.getStatus()))
            .toList();
    }

    public static boolean replyToEnquiry(Enquiry enquiry, HDBOfficer officer, String reply) {
        enquiry.addReply(reply, officer);
        EnquiryCsvMapper.update(enquiry); // Efficient single-row update
        return true;
    }

    public static void generateReceipt(HDBOfficer officer, Scanner sc) {
        if (!"APPROVED".equalsIgnoreCase(officer.getRegistrationStatus())) {
            System.out.println("❌ Access denied. Officer registration status must be APPROVED to generate receipts.");
            return;
        }
    
        List<Invoice> awaitingReceipts = OfficerService.getInvoicesAwaitingReceipt(officer);
    
        if (awaitingReceipts.isEmpty()) {
            System.out.println("📭 No paid invoices awaiting receipts.");
            return;
        }
    
        System.out.println("\n📋 Paid Invoices (Awaiting Receipt):");
        for (int i = 0; i < awaitingReceipts.size(); i++) {
            Invoice inv = awaitingReceipts.get(i);
            System.out.printf("[%d] Invoice #%d | %s | %s | $%.2f\n",
                i + 1, inv.getPaymentId(), inv.getApplicantNRIC(), inv.getFlatType(), inv.getAmount());
        }
    
        System.out.print("Select invoice to issue receipt for (0 to cancel): ");
        try {
            int idx = Integer.parseInt(sc.nextLine().trim());
            if (idx == 0) return;
            if (idx < 1 || idx > awaitingReceipts.size()) {
                System.out.println("❌ Invalid selection.");
                return;
            }
    
            Invoice selectedInvoice = awaitingReceipts.get(idx - 1);
            boolean success = OfficerService.generateReceiptForInvoice(officer, selectedInvoice);
    
            if (!success) {
                System.out.println("❌ Receipt generation failed.");
            }
    
        } catch (Exception e) {
            System.out.println("❌ Invalid input.");
        }
    }

    public static boolean generateReceiptForInvoice(HDBOfficer officer, Invoice invoice) {
        if (!invoice.getProjectName().equalsIgnoreCase(officer.getAssignedProject().getProjectName())) {
            System.out.println("❌ You are not authorized to issue receipts for this project.");
            return false;
        }

        Applicant applicant = findApplicantByNRIC(invoice.getApplicantNRIC());
        if (applicant == null || applicant.getApplication() == null) {
            System.out.println("❌ Applicant or application not found.");
            return false;
        }

        Project fullProject = findFullProjectByName(invoice.getProjectName());
        if (fullProject != null) {
            applicant.getApplication().setProject(fullProject);
        }

        Receipt receipt = officer.generateReceipt(
            applicant.getApplication(),
            invoice.getPaymentId(),
            invoice.getMethod()
        );

        ReceiptService.addReceipt(receipt);

        invoice.setStatus(Payment.PaymentStatusType.PROCESSED.name());
        InvoiceService.updateInvoice(invoice);

        Payment payment = PaymentService.getAllPayments().stream()
            .filter(p -> p.getPaymentId() == invoice.getPaymentId())
            .findFirst()
            .orElse(null);

        if (payment != null) {
            payment.setStatus(Payment.PaymentStatusType.PROCESSED.name());
            PaymentService.updatePayment(payment);
        }

        System.out.println("✅ Receipt generated:\n" + receipt);
        return true;
    }

    public static void updateLocation(HDBOfficer officer, Scanner sc) {
        if (!OfficerService.canUpdateLocation(officer)) {
            System.out.println("❌ Access denied. Officer registration must be APPROVED and a project must be assigned.");
            return;
        }

        Project p = officer.getAssignedProject();
        boolean success = OfficerService.updateProjectLocation(p, sc);
        if (success) {
            System.out.println("✅ Location updated.");
        }
    }

    public static boolean canUpdateLocation(HDBOfficer officer) {
        return "APPROVED".equalsIgnoreCase(officer.getRegistrationStatus())
            && officer.getAssignedProject() != null;
    }

    public static boolean updateProjectLocation(Project p, Scanner sc) {
        if (p == null) return false;

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
            return false;
        }

        ProjectCsvMapper.updateProject(p);
        return true;
    }

    public static void addOrUpdateAmenity(HDBOfficer officer, Scanner sc) {
        if (!OfficerService.canManageAmenities(officer)) {
            System.out.println("❌ Access denied. Officer registration status must be APPROVED to manage amenities.");
            return;
        }
    
        OfficerService.manageAmenityInteraction(officer.getAssignedProject(), sc);
    } 

    public static boolean canManageAmenities(HDBOfficer officer) {
        return "APPROVED".equalsIgnoreCase(officer.getRegistrationStatus())
            && officer.getAssignedProject() != null;
    }

    public static void manageAmenityInteraction(Project project, Scanner sc) {
        System.out.println("\n🏗️ Managing amenities for " + project.getProjectName());
        System.out.print("Would you like to (A)dd or (U)pdate an amenity? ");
        String action = sc.nextLine().trim().toUpperCase();

        switch (action) {
            case "A" -> addAmenity(project, sc);
            case "U" -> updateAmenity(project, sc);
            default -> System.out.println("❌ Invalid option. Please choose A or U.");
        }
    }

    private static void addAmenity(Project project, Scanner sc) {
        int nextId = AmenitiesCsvMapper.loadAll().stream()
            .map(Amenities::getAmenityId)
            .max(Integer::compareTo)
            .orElse(0) + 1;

        System.out.print("Type (e.g. MRT, Clinic): ");
        String type = sc.nextLine().trim();

        System.out.print("Name: ");
        String name = sc.nextLine().trim();

        System.out.print("Distance (km): ");
        double dist = getDoubleInput(sc);

        Amenities newAmenity = new Amenities(nextId, type, name, dist, project.getProjectName());
        AmenitiesCsvMapper.add(newAmenity);
        System.out.println("✅ Amenity added (ID=" + nextId + ").");
    }

    private static void updateAmenity(Project project, Scanner sc) {
        List<Amenities> amenities = AmenitiesCsvMapper.loadAll().stream()
            .filter(a -> a.getProjectName().equalsIgnoreCase(project.getProjectName()))
            .toList();

        if (amenities.isEmpty()) {
            System.out.println("📭 No amenities found for this project.");
            return;
        }

        System.out.println("\n📋 Existing Amenities:");
        for (Amenities a : amenities) {
            System.out.printf("ID: %d | Type: %s | Name: %s | Distance: %.2f km\n",
                a.getAmenityId(), a.getType(), a.getName(), a.getDistance());
        }

        System.out.print("Enter Amenity ID to update: ");
        int idToUpdate;
        try {
            idToUpdate = Integer.parseInt(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid ID.");
            return;
        }

        Amenities target = amenities.stream()
            .filter(a -> a.getAmenityId() == idToUpdate)
            .findFirst()
            .orElse(null);

        if (target == null) {
            System.out.println("❌ Amenity ID not found for this project.");
            return;
        }

        System.out.printf("Current Type [%s]: ", target.getType());
        String type = sc.nextLine().trim();
        if (!type.isEmpty()) target.setType(type);

        System.out.printf("Current Name [%s]: ", target.getName());
        String name = sc.nextLine().trim();
        if (!name.isEmpty()) target.setName(name);

        System.out.printf("Current Distance [%.2f]: ", target.getDistance());
        String distInput = sc.nextLine().trim();
        if (!distInput.isEmpty()) {
            try {
                target.setDistance(Double.parseDouble(distInput));
            } catch (NumberFormatException e) {
                System.out.println("❌ Invalid distance. Update skipped.");
                return;
            }
        }

        AmenitiesCsvMapper.update(target);
        System.out.println("✅ Amenity updated.");
    }

    private static double getDoubleInput(Scanner sc) {
        while (true) {
            try {
                return Double.parseDouble(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.print("❌ Invalid number. Please enter again: ");
            }
        }
    }

}

