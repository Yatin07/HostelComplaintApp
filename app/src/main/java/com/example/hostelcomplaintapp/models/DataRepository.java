package com.example.hostelcomplaintapp.models;

import java.util.ArrayList;
import java.util.List;

public class DataRepository {
    private static DataRepository instance;
    private List<Task> tasks;

    private DataRepository() {
        tasks = new ArrayList<>();
        long currentTime = System.currentTimeMillis();
        long oneDay = 24 * 60 * 60 * 1000L;

        // Dummy Data
        tasks.add(new Task("T101", "Fan not working", "The ceiling fan in my room is making a weird noise and not spinning properly.", "Room 101", "John Doe", "High", "Pending", currentTime + (oneDay * 2)));
        tasks.add(new Task("T102", "Leaking sink", "The bathroom sink pipe is leaking water continuously.", "Room 205", "Alice Smith", "Medium", "In Progress", currentTime + (oneDay * 3)));
        tasks.add(new Task("T103", "Broken window lock", "The latch on the main window is broken.", "Room 304", "Bob Johnson", "Low", "Completed", currentTime - oneDay));
        tasks.add(new Task("T104", "Fuse blown", "No power in my half of the room. I think the fuse is blown.", "Room 112", "Charlie Brown", "High", "Overdue", currentTime - (oneDay * 2)));
        tasks.add(new Task("T105", "AC cooling issue", "The AC is turning on but not blowing cool air.", "Room 210", "David Wilson", "Medium", "Pending", currentTime + (oneDay * 4)));
    }

    public static DataRepository getInstance() {
        if (instance == null) {
            instance = new DataRepository();
        }
        return instance;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public Task getTaskById(String taskId) {
        for (Task task : tasks) {
            if (task.getTaskId().equals(taskId)) {
                return task;
            }
        }
        return null;
    }

    public void updateTaskStatus(String taskId, String status) {
        Task task = getTaskById(taskId);
        if (task != null) {
            task.setStatus(status);
        }
    }
}
