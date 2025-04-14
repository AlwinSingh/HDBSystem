package src.model;

import java.util.List;

public abstract class User {
    protected String nric;
    protected String password;
    protected String name;
    protected int age;
    protected String maritalStatus;

    public User(String nric, String password, String name, int age, String maritalStatus) {
        this.nric = nric;
        this.password = password;
        this.name = name;
        this.age = age;
        this.maritalStatus = maritalStatus;
    }

    public boolean login(String inputPassword) {
        return this.password.equals(inputPassword);
    }

    public void logout() {
        System.out.println(name + " has logged out.");
    }

    public void changePassword(String newPassword) {
        this.password = newPassword;
    }

    // Placeholder methods for subclasses
    public List<Enquiry> viewEnquiries() { return null; }
    public Enquiry createEnquiry(String content) { return null; }
    public void editEnquiry(Enquiry e, String newContent) {}
    public void deleteEnquiry(Enquiry e) {}

    public String getName() { return name; }
    public int getAge() { return age; }
    public String getMaritalStatus() { return maritalStatus; }
    public String getNric() { return nric; }
}

