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

    public static Project getAssignedProject(HDBOfficer officer) {
        return officer.getAssignedProject();
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

