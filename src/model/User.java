package src.model;

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

    public String getPassword() {
        return this.password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public void changePassword(String newPassword) {
        this.password = newPassword;
    }
    public String getName() { return name; }
    public int getAge() { return age; }
    public String getMaritalStatus() { return maritalStatus; }
    public String getNric() { return nric; }
}

