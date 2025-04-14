package src.model;

import java.util.ArrayList;
import java.util.List;

public class Scheduler {
    private List<Appointment> tasks;

    public Scheduler() {
        this.tasks = new ArrayList<>();
    }

    public void addTask(Appointment task) {
        tasks.add(task);
        System.out.println("Task added: " + task.getClass().getSimpleName());
    }

    public void removeTask(Appointment task) {
        tasks.remove(task);
        System.out.println("Task removed.");
    }

    public void executeTask() {
        for (Appointment task : tasks) {
            System.out.println("Executing task at: " + task.toString());
            // Could simulate notifications or print task summaries
        }
    }

    public List<Appointment> getAllTasks() {
        return tasks;
    }
}

