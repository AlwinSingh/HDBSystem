package src.model;

/**
 * Abstract base class representing a user in the HDB system.
 * This includes shared fields and methods for applicants, officers, and managers.
 */
public abstract class User {
    protected String nric;
    protected String password;
    protected String name;
    protected int age;
    protected String maritalStatus;

     /**
     * Constructs a user with the specified personal information.
     *
     * @param nric          Unique NRIC identifier.
     * @param password      Login password (plain text).
     * @param name          Full name of the user.
     * @param age           Age of the user.
     * @param maritalStatus Marital status (e.g., "Single", "Married").
     */
    public User(String nric, String password, String name, int age, String maritalStatus) {
        this.nric = nric;
        this.password = password;
        this.name = name;
        this.age = age;
        this.maritalStatus = maritalStatus;
    }

    /**
     * Checks if the provided password matches the user's actual password.
     * used in AuthService.authenticate() method instead
     *
     * @param inputPassword The password to verify.
     * @return True if the password matches.
     */
    public boolean login(String inputPassword) {
        return this.password.equals(inputPassword);
    }

    /**
     * Prints a logout message to the console.
     * used in AuthService
     */
    public void logout() {
        System.out.println(name + " has logged out.");
    }

    /**
     * Returns the user's plain text password.
     */
    public String getPassword() {
        return this.password;
    }
    
    /**
     * Updates the user's password (no validation or encryption).
     *
     * @param password The new password.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Replaces user's password with new password that has been input
     * used in AuthService.changePassword() method
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

