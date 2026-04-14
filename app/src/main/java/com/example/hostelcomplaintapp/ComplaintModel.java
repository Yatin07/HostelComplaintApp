package com.example.hostelcomplaintapp;

import com.google.firebase.firestore.DocumentId;

public class ComplaintModel {

    @DocumentId
    private String docId;
    private String title, description, roomNumber, bednumber, studentId, status, category, assignedWorkerId;
    private long timestamp;
    private long deadline;

    public ComplaintModel() {}

    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getRoomNumber() { return roomNumber; }
    public String getBednumber() { return bednumber; }
    public String getStudentId() { return studentId; }
    public String getStatus() { return status; }
    public String getCategory() { return category; }
    public String getAssignedWorkerId() { return assignedWorkerId; }
    public long getTimestamp() { return timestamp; }
    public long getDeadline() { return deadline; }
    public String getDocId() { return docId; }

    public void setDocId(String docId) { this.docId = docId; }
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }
    public void setBednumber(String bednumber) { this.bednumber = bednumber; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
    public void setStatus(String status) { this.status = status; }
    public void setCategory(String category) { this.category = category; }
    public void setAssignedWorkerId(String assignedWorkerId) { this.assignedWorkerId = assignedWorkerId; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    public void setDeadline(long deadline) { this.deadline = deadline; }
}