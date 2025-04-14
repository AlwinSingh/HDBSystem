package src.model;

import java.util.ArrayList;
import java.util.List;

public class HDBManager extends User {
    private List<Project> managedProjects;

    public HDBManager(String nric, String password, String name, int age, String maritalStatus) {
        super(nric, password, name, age, maritalStatus);
    }  

    public Project createProject(Project project) {
        managedProjects.add(project);
        return project;
    }

    public void editProject(Project project, String newName) {
        project.closeProject(); // dummy edit: name + close
    }

    public void deleteProject(Project project) {
        managedProjects.remove(project);
    }

    public void toggleVisibility(Project project, boolean visible) {
        if (visible) project.openProject();
        else project.closeProject();
    }

    public List<String> generateReport(String filterType) {
        List<String> reports = new ArrayList<>();
        for (Project p : managedProjects) {
            reports.add("Report for: " + p.toString());
        }
        return reports;
    }
}

