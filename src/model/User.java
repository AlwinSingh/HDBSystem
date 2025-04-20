package src.model;

/**
 * Abstract base class representing a user in the HDB system.
 * Shared by applicants, officers, and managers.
 */
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

    /**
     * Checks if the provided password matches the user's actual password.
     * Not in use now....moved to AuthService.authenticate() method instead...
     *
     * @param inputPassword The password to verify.
     * @return True if the password matches.
     */
    public boolean login(String inputPassword) {
        return this.password.equals(inputPassword);
    }

    /**
     * Prints a logout message to the console.
     * Not in use now....moved to AuthService.logout() method instead...
     */
    public void logout() {
        System.out.println(name + " has logged out.");
    }

    public String getPassword() {
        return this.password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Replaces user's password with new password that has been input
     * Not in use now...moved to AuthService.changePassword() method instead...
     *
     * @param newPassword
     */
    public void changePassword(String newPassword) {
        this.password = newPassword;
    }
    public String getName() { return name; }
    public int getAge() { return age; }
    public String getMaritalStatus() { return maritalStatus; }
    public String getNric() { return nric; }
}

