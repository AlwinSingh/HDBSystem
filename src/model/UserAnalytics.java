package src.model;

import java.util.HashMap;
import java.util.Map;

public class UserAnalytics {
    private Map<User, Integer> loginFrequency;
    private Map<Project, Integer> applicationStats;

    public UserAnalytics() {
        loginFrequency = new HashMap<>();
        applicationStats = new HashMap<>();
    }

    public void trackLogin(User user) {
        loginFrequency.put(user, loginFrequency.getOrDefault(user, 0) + 1);
    }

    public void trackApplication(Project project) {
        applicationStats.put(project, applicationStats.getOrDefault(project, 0) + 1);
    }

    public void analyzeUserData() {
        System.out.println("Login frequency:");
        loginFrequency.forEach((user, count) -> {
            System.out.println(user.getName() + ": " + count + " logins");
        });
    }

    public String getUserInsights(User user) {
        int freq = loginFrequency.getOrDefault(user, 0);
        return user.getName() + " has logged in " + freq + " times.";
    }
}

