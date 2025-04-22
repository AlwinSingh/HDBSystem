package src.interfaces;

import src.model.HDBManager;
import src.model.Project;

import java.util.List;
import java.util.Scanner;

/**
 * Interface for managing HDB housing projects as a manager.
 * Supports project creation, editing, deletion, visibility toggling,
 * and advanced filtered views.
 */
public interface IManagerProjectService {

    /**
     * Allows the manager to create a new housing project.
     * @param manager The logged-in HDB manager.
     * @param sc Scanner for input.
     */
    void createProject(HDBManager manager, Scanner sc);

    /**
     * Allows the manager to edit a project they manage.
     * @param manager The logged-in manager.
     * @param sc Scanner for input.
     */
    void editProject(HDBManager manager, Scanner sc);

    /**
     * Allows the manager to delete one of their projects, if no users are attached.
     * @param manager The logged-in manager.
     * @param sc Scanner for input.
     */
    void deleteProject(HDBManager manager, Scanner sc);

    /**
     * Allows the manager to toggle visibility of a project.
     * @param manager The manager who owns the project.
     * @param sc Scanner for input.
     */
    void toggleVisibility(HDBManager manager, Scanner sc);

    /**
     * Allows the manager to view all projects using a multi-criteria filter.
     * @param sc Scanner for input.
     */
    void viewAllProjectsWithFilter(Scanner sc);

    /**
     * Returns the list of projects owned by the specified manager.
     * @param manager The logged-in manager.
     * @return A list of their projects.
     */
    List<Project> getProjectsByManager(HDBManager manager);

    /**
     * Displays all projects created by the manager.
     * @param manager The logged-in manager.
     */
    void viewMyProjects(HDBManager manager);
}
