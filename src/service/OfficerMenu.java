package src.service;

import java.util.*;
import java.util.stream.Collectors;
import src.model.*;
import src.util.ApplicantCsvMapper;
import src.util.AmenitiesCsvMapper;
import src.util.EnquiryCsvMapper;
import src.util.OfficerCsvMapper;
import src.util.PaymentCsvMapper;
import src.util.ProjectCsvMapper;

public class OfficerMenu {

    public static void show(HDBOfficer officer) {
        Scanner sc = new Scanner(System.in);
    
        while (true) {
            System.out.println("\n===== 🧑‍💼 HDB Officer Dashboard =====");
            System.out.println("Welcome, Officer " + officer.getName());
    
            System.out.println("\n📋 Registration");
            System.out.printf(" [1] 📝 View Status           [2] 🏗️ Register for Project%n");
    
            System.out.println("\n📂 Project");
            System.out.printf(" [3] 📄 View Details          [4] 🏠 Book Flat for Applicant%n");
            System.out.printf(" [5] 🧾 Generate Receipt      [6] 📍 Update Location%n");
            System.out.printf(" [7] ➕ Add Amenity%n");
    
            System.out.println("\n📬 Enquiries");
            System.out.printf(" [8] 💬 View & Reply to Enquiries%n");
    
            System.out.println("\n🔐 Account");
            System.out.printf(" [9] 🔑 Change Password");
    
            if (officer.getRegistrationStatus() == null ||
                officer.getRegistrationStatus().equalsIgnoreCase("REJECTED")) {
                System.out.printf("   [10] 🔁 Switch to Applicant Dashboard%n");
            }
    
            System.out.printf("   [0] 🚪 Logout%n");
    
            System.out.print("\n➡️ Enter your choice: ");
            String choice = sc.nextLine().trim();
    
            switch (choice) {
                case "1" -> viewRegistrationStatus(officer);
                case "2" -> registerForProject(officer, sc);
                case "3" -> viewAssignedProjectDetails(officer);
                case "4" -> bookFlat(officer, sc);
                case "5" -> generateReceipt(officer, sc);
                case "6" -> updateLocation(officer, sc);
                case "7" -> addOrUpdateAmenity(officer, sc);
                case "8" -> handleEnquiries(officer, sc);
                case "9" -> AuthService.changePassword(officer, sc);
                case "10" -> {
                    if (officer.getRegistrationStatus() == null ||
                        officer.getRegistrationStatus().equalsIgnoreCase("REJECTED")) {
                        System.out.println("🔁 Switching to Applicant Dashboard...");
                        ApplicantMenu.show(officer);
                        return;
                    } else {
                        System.out.println("❌ You are not eligible to access the Applicant dashboard.");
                    }
                }
                case "0" -> {
                    AuthService.logout();
                    return;
                }
                default -> System.out.println("❌ Invalid input. Please try again.");
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
        System.out.println("🏠 2-Room Units       : " + p.getRemainingFlats("2-Room"));
        System.out.println("💰 2-Room Price       : $" + String.format("%.2f", p.getPrice2Room()));
        System.out.println("🏠 3-Room Units       : " + p.getRemainingFlats("3-Room"));
        System.out.println("💰 3-Room Price       : $" + String.format("%.2f", p.getPrice3Room()));
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
        List<Applicant> eligible = applicants.stream()
            .filter(a -> a.getApplication() != null)
            .filter(a -> {
                Project appProject = a.getApplication().getProject();
                return appProject != null && appProject.getProjectName().equalsIgnoreCase(assigned.getProjectName());
            })
            .filter(a -> Applicant.AppStatusType.SUCCESSFUL.name().equalsIgnoreCase(a.getApplication().getStatus()))
            .collect(Collectors.toList());
    
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
            String flatType = selected.getApplication().getFlatType();
    
            // ✅ Get full project info for accurate pricing
            Project fullProject = ProjectCsvMapper.loadAll().stream()
                .filter(p -> p.getProjectName().equalsIgnoreCase(assigned.getProjectName()))
                .findFirst()
                .orElse(null);
    
            if (fullProject == null) {
                System.out.println("❌ Failed to find full project details.");
                return;
            }
    
            // ✅ Update full project reference in the application
            selected.getApplication().setProject(fullProject);
    
            officer.bookFlat(selected.getApplication(), flatType);
            selected.getApplication().setStatus(Applicant.AppStatusType.BOOKED.name());
    
            ApplicantCsvMapper.updateApplicant(selected);
    
            int nextInvoiceId = InvoiceService.getNextInvoiceId();
            Invoice invoice = InvoiceService.generateInvoiceForBooking(selected.getApplication(), nextInvoiceId);
            InvoiceService.addInvoice(invoice);
    
            // ✅ Update only the project involved
            ProjectCsvMapper.updateProject(fullProject);
    
            System.out.println("🧾 Invoice generated and saved (Invoice ID: " + invoice.getPaymentId() + ")");
            System.out.println("✅ Booking successful.");
        } catch (Exception e) {
            System.out.println("❌ Invalid booking.");
            e.printStackTrace(); // ADD THIS to see the real cause

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
    
            selected.addReply(reply, officer);
            EnquiryCsvMapper.update(selected);  // ✅ efficient single-row update
            System.out.println("✅ Reply sent and enquiry marked as CLOSED.");
    
        } catch (Exception e) {
            System.out.println("❌ Invalid selection.");
        }
    }
    
    
    private static void generateReceipt(HDBOfficer officer, Scanner sc) {
        if (!"APPROVED".equalsIgnoreCase(officer.getRegistrationStatus())) {
            System.out.println("❌ Access denied. Officer registration status must be APPROVED to generate receipts.");
            return;
        }
    
        List<Invoice> invoices = InvoiceService.getAllInvoices();
        List<Applicant> applicants = ApplicantCsvMapper.loadAll();
        List<Project> allProjects = ProjectCsvMapper.loadAll();
    
        List<Invoice> awaitingReceipts = invoices.stream()
            .filter(i -> "Awaiting Receipt".equalsIgnoreCase(i.getStatus()))
            .filter(i -> ReceiptService.findByInvoiceId(i.getPaymentId()) == null)
            .toList();
    
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
    
            Project fullProject = allProjects.stream()
                .filter(p -> p.getProjectName().equalsIgnoreCase(selectedInvoice.getProjectName()))
                .findFirst()
                .orElse(null);
    
            if (fullProject != null && applicant.getApplication() != null) {
                applicant.getApplication().setProject(fullProject);
            }
    
            // ✅ Generate receipt and update records
            Receipt receipt = officer.generateReceipt(
                applicant.getApplication(),
                selectedInvoice.getPaymentId(),
                selectedInvoice.getMethod()
            );
            ReceiptService.addReceipt(receipt);
    
            selectedInvoice.setStatus(Payment.PaymentStatusType.PROCESSED.name());
            InvoiceService.updateInvoice(selectedInvoice);
    
            Payment payment = PaymentService.getAllPayments().stream()
                .filter(p -> p.getPaymentId() == selectedInvoice.getPaymentId())
                .findFirst()
                .orElse(null);
    
            if (payment != null) {
                payment.setStatus(Payment.PaymentStatusType.PROCESSED.name());
                PaymentService.updatePayment(payment);  // ✅ This updates PaymentList.csv
            }
    
            System.out.println("✅ Receipt generated:\n" + receipt);
    
        } catch (Exception e) {
            System.out.println("❌ Invalid input.");
        }
    }
    


    private static void updateLocation(HDBOfficer officer, Scanner sc) {
        if (!"APPROVED".equalsIgnoreCase(officer.getRegistrationStatus())) {
            System.out.println("❌ Access denied. Officer registration status must be APPROVED to update project location.");
            return;
        }
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
    

    private static void addOrUpdateAmenity(HDBOfficer officer, Scanner sc) {
        if (!"APPROVED".equalsIgnoreCase(officer.getRegistrationStatus())) {
            System.out.println("❌ Access denied. Officer registration status must be APPROVED to manage amenities.");
            return;
        }
    
        Project p = officer.getAssignedProject();
        if (p == null) {
            System.out.println("❌ No assigned project to manage amenities.");
            return;
        }
    
        System.out.println("\n🏗️ Managing amenities for " + p.getProjectName());
        System.out.print("Would you like to (A)dd or (U)pdate an amenity? ");
        String action = sc.nextLine().trim().toUpperCase();
    
        switch (action) {
            case "A" -> {
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
    
                Amenities newAmenity = new Amenities(nextId, type, name, dist, p.getProjectName());
                AmenitiesCsvMapper.add(newAmenity);
                System.out.println("✅ Amenity added (ID=" + nextId + ").");
            }
    
            case "U" -> {
                List<Amenities> amenities = AmenitiesCsvMapper.loadAll().stream()
                    .filter(a -> a.getProjectName().equalsIgnoreCase(p.getProjectName()))
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
    
            default -> System.out.println("❌ Invalid option. Please choose A or U.");
        }
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
