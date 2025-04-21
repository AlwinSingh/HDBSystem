package src.service;

import java.util.List;
import java.util.Scanner;

import src.model.Applicant;
import src.model.HDBOfficer;
import src.model.Invoice;
import src.model.Payment;
import src.model.Project;
import src.model.Receipt;
import src.repository.ApplicantRepository;
import src.util.ApplicantCsvMapper;
import src.util.ProjectCsvMapper;

public class OfficerInvoiceService {

    public static List<Invoice> getInvoicesAwaitingReceipt(HDBOfficer officer) {
        return InvoiceService.getAllInvoices().stream()
            .filter(i -> "Awaiting Receipt".equalsIgnoreCase(i.getStatus()))
            .filter(i -> ReceiptService.findByInvoiceId(i.getPaymentId()) == null)
            .filter(i -> i.getProjectName().equalsIgnoreCase(officer.getAssignedProject().getProjectName()))
            .toList();
    }

}
