package com.example.hostelcomplaintapp.models;

import java.io.Serializable;

public class Task implements Serializable {
    private String taskId;
    private String title;
    private String description;
    private String roomNumber;
    private String studentName;
    private String priority; // Low, Medium, High
    private String status; // Pending, In Progress, Completed, Overdue
    private String assignedWorkerId;
    private long deadline; // Timestamp
    private long createdAt;

    public Task() {}

    public Task(String taskId, String title, String description, String roomNumber, String studentName, String priority, String status, long deadline) {
        this.taskId = taskId;
        this.title = title;
        this.description = description;
        this.roomNumber = roomNumber;
        this.studentName = studentName;
        this.priority = priority;
        this.status = status;
        this.deadline = deadline;
        this.createdAt = System.currentTimeMillis();
    }

    public String getTaskId() { return taskId; }
    public void setTaskId(String taskId) { this.taskId = taskId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getAssignedWorkerId() { return assignedWorkerId; }
    public void setAssignedWorkerId(String assignedWorkerId) { this.assignedWorkerId = assignedWorkerId; }

    public long getDeadline() { return deadline; }
    public void setDeadline(long deadline) { this.deadline = deadline; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
}
