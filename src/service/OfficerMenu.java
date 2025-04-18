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
            System.out.println("7. Update project location");     
            System.out.println("8. Add an amenity"); 
            System.out.println("9. Change Password");
            System.out.println("0. Logout");
            System.out.print("➡️ Enter your choice: ");

            switch (sc.nextLine().trim()) {
                case "1" -> viewRegistrationStatus(officer);
                case "2" -> registerForProject(officer, sc);
                case "3" -> viewAssignedProjectDetails(officer);
                case "4" -> bookFlat(officer, sc);
                case "5" -> generateReceipt(officer, sc);
                case "6" -> handleEnquiries(officer, sc);
                case "7" -> updateLocation(officer, sc);         
                case "8" -> addAmenity(officer, sc);
                case "9" -> AuthService.changePassword(officer, sc);
                case "0" -> {
                    AuthService.logout();
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
    
        List<Project> projects = ProjectCsvMapper.loadAll();
    
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
                OfficerCsvMapper.updateOfficer(officer);
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
    
        if (HDBOfficer.RegistrationStatusType.PENDING.name().equalsIgnoreCase(officer.getRegistrationStatus())) {
            System.out.println("⚠️ You cannot perform bookings as your registration to this project is still pending approval.");
            return;
        }
    
        List<Applicant> applicants = ApplicantCsvMapper.loadAll();
        List<Applicant> pending = applicants.stream()
            .filter(a -> a.getApplication() != null)
            .filter(a -> {
                Project appProject = a.getApplication().getProject();
                return appProject != null && appProject.getProjectName().equalsIgnoreCase(assigned.getProjectName());
            })
            .filter(a -> Applicant.AppStatusType.SUCCESSFUL.name().equalsIgnoreCase(a.getApplication().getStatus()))
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
    
            // ✅ FIX: Rebind full project with correct pricing
            Project fullProject = ProjectCsvMapper.loadAll().stream()
                .filter(p -> p.getProjectName().equalsIgnoreCase(assigned.getProjectName()))
                .findFirst()
                .orElse(null);
    
            if (fullProject == null) {
                System.out.println("❌ Failed to find full project details.");
                return;
            }
    
            // Ensure application points to full project so prices are correct
            selected.getApplication().setProject(fullProject);
    
            officer.bookFlat(selected.getApplication(), flatType);
            selected.getApplication().setStatus(Applicant.AppStatusType.BOOKED.name());
            ApplicantCsvMapper.updateApplicant(selected);
    
            int nextInvoiceId = InvoiceService.getNextInvoiceId();
            Invoice invoice = InvoiceService.generateInvoiceForBooking(selected.getApplication(), nextInvoiceId);
            InvoiceService.addInvoice(invoice);
    
            System.out.println("🧾 Invoice generated and saved (Invoice ID: " + invoice.getPaymentId() + ")");
            ProjectCsvMapper.saveAll(ProjectCsvMapper.loadAll());
    
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
    
        if (!HDBOfficer.RegistrationStatusType.APPROVED.name().equalsIgnoreCase(officer.getRegistrationStatus())) {
            System.out.println("⚠️ You are not authorized to view or reply to enquiries until your registration is approved.");
            return;
        }
    
        List<Enquiry> allEnquiries = EnquiryCsvMapper.loadAll();
        List<Enquiry> projectEnquiries = allEnquiries.stream()
            .filter(e -> e.getProject().getProjectName().equalsIgnoreCase(assignedProject.getProjectName()))
            .filter(e -> Enquiry.STATUS_PENDING.equalsIgnoreCase(e.getStatus()))
            .toList();
    
        if (projectEnquiries.isEmpty()) {
            System.out.println("📭 No open enquiries found for your project.");
            return;
        }
    
        System.out.println("\n📬 Enquiries for Project: " + assignedProject.getProjectName());
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
    
            selected.addReply(reply, officer); // NEW REPLY HANDLING
            EnquiryCsvMapper.saveAll(allEnquiries);
            System.out.println("✅ Reply sent and enquiry marked as CLOSED.");
    
        } catch (Exception e) {
            System.out.println("❌ Invalid selection.");
        }
    }
    
    private static void generateReceipt(HDBOfficer officer, Scanner sc) {
        List<Invoice> invoices = InvoiceService.loadAll();
        List<Applicant> applicants = ApplicantCsvMapper.loadAll();
        List<Project> allProjects = ProjectCsvMapper.loadAll();
    
        List<Invoice> awaitingReceipts = invoices.stream()
            .filter(i -> "Awaiting Receipt".equalsIgnoreCase(i.getStatus()))
            .filter(i -> ReceiptService.findByInvoiceId(i.getPaymentId()) == null)
            .collect(Collectors.toList());
    
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
            if (idx < 1 || idx > awaitingReceipts.size()) throw new Exception();
    
            Invoice selectedInvoice = awaitingReceipts.get(idx - 1);
    
            // Officer validation
            if (!selectedInvoice.getProjectName().equalsIgnoreCase(officer.getAssignedProject().getProjectName())) {
                System.out.println("❌ You are not authorized to issue receipts for this project.");
                return;
            }
    
            Applicant applicant = applicants.stream()
                .filter(a -> a.getNric().equalsIgnoreCase(selectedInvoice.getApplicantNRIC()))
                .findFirst()
                .orElse(null);
    
            if (applicant == null) {
                System.out.println("❌ Applicant not found.");
                return;
            }
    
            // Fix flat price issue
            Project fullProject = allProjects.stream()
                .filter(p -> p.getProjectName().equalsIgnoreCase(selectedInvoice.getProjectName()))
                .findFirst()
                .orElse(null);
    
            if (fullProject != null && applicant.getApplication() != null) {
                applicant.getApplication().setProject(fullProject);
            }
    
            // Generate and save receipt
            Receipt receipt = officer.generateReceipt(
                applicant.getApplication(),
                selectedInvoice.getPaymentId(),
                selectedInvoice.getMethod()
            );
            ReceiptService.addReceipt(receipt);
    
            // Update invoice to PROCESSED
            selectedInvoice.setStatus(Payment.PaymentStatusType.PROCESSED.name());
            InvoiceService.updateInvoice(selectedInvoice);
    
            // Update matching payment
            Payment payment = PaymentService.getAllPayments().stream()
                .filter(p -> p.getPaymentId() == selectedInvoice.getPaymentId())
                .findFirst()
                .orElse(null);
    
            if (payment != null) {
                payment.setStatus(Payment.PaymentStatusType.PROCESSED.name());
                PaymentService.persist();
            }
    
            System.out.println("✅ Receipt generated:\n" + receipt);
    
        } catch (Exception e) {
            System.out.println("❌ Invalid input.");
        }
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
        ProjectCsvMapper.updateProject(p);
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
