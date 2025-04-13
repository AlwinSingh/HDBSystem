package src.model;

import src.service.ProjectService;
import src.service.UserService;

/**
 * Abstract class representing a general User in the BTO system.
 * Subclasses: Applicant, Officer, Manager.
 * However, take note that Officer is mostly a subclass of Applicant which inherits User
 */
public abstract class User {
    protected String nric;
    protected String password;
    protected String name;
    protected int age;
    protected String maritalStatus; // "Single" or "Married"

    public User(String nric, String password, String name, int age, String maritalStatus) {
        this.nric = nric;
        this.password = password;
        this.name = name;
        this.age = age;
        this.maritalStatus = maritalStatus;
    }

    public String getNric() {
        return nric;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public String getMaritalStatus() {
        return maritalStatus;
    }

    public boolean checkPassword(String inputPassword) {
        return password.equals(inputPassword);
    }

    public void changePassword(String newPassword) {
        this.password = newPassword;
    }

    public abstract void showMenu(ProjectService projectService, UserService userService);
}

