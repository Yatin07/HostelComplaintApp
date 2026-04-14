package com.example.hostelcomplaintapp;

import com.google.firebase.firestore.DocumentId;

public class ComplaintModel {

    @DocumentId
    private String docId;
    private String title, description, roomNumber, bednumber, studentId, status, category, assignedWorkerId;
    private long timestamp;

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
    public String getDocId() { return docId; }

    public void setDocId(String docId) { this.docId = docId; }
}