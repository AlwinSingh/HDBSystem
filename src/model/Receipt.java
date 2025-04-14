package src.model;
public class Receipt {
    private String applicantName;
    private String applicantNRIC;
    private int age;
    private String maritalStatus;
    private String projectName;
    private String neighborhood;
    private String flatTypeBooked;

    public Receipt(String applicantName, String applicantNRIC, int age, String maritalStatus,
                   String projectName, String neighborhood, String flatTypeBooked) {
        this.applicantName = applicantName;
        this.applicantNRIC = applicantNRIC;
        this.age = age;
        this.maritalStatus = maritalStatus;
        this.projectName = projectName;
        this.neighborhood = neighborhood;
        this.flatTypeBooked = flatTypeBooked;
    }

    public void generatePDF() {
        // TODO: Stub - simulate PDF generation
        System.out.println("Receipt generated for " + applicantName);
    }

    // toString for CLI display
    @Override
    public String toString() {
        return "Name: " + applicantName + "\nNRIC: " + applicantNRIC +
               "\nAge: " + age + "\nStatus: " + maritalStatus +
               "\nProject: " + projectName + "\nFlat Type: " + flatTypeBooked;
    }
}
