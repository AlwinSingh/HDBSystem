package src.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserRoleManager {
    private Map<User, List<String>> userRoles;

    public UserRoleManager() {
        this.userRoles = new HashMap<>();
    }

    public void assignRole(User user, String roleName) {
        userRoles.putIfAbsent(user, new ArrayList<>());
        if (!userRoles.get(user).contains(roleName)) {
            userRoles.get(user).add(roleName);
            System.out.println("Assigned role '" + roleName + "' to " + user.getName());
        }
    }

    public void removeRole(User user, String roleName) {
        if (userRoles.containsKey(user)) {
            userRoles.get(user).remove(roleName);
            System.out.println("Removed role '" + roleName + "' from " + user.getName());
        }
    }

    public boolean checkPermission(User user, String requiredRole) {
        return userRoles.containsKey(user) && userRoles.get(user).contains(requiredRole);
    }

    public List<String> getUserRoles(User user) {
        return userRoles.getOrDefault(user, new ArrayList<>());
    }
}
