package com.example.hostelcomplaintapp;

import com.google.firebase.firestore.DocumentId;

public class ComplaintModel {

    @DocumentId
    private String docId;
    private String title, description, room, bednumber, studentId, status, category, assignedWorkerId, imageUrl;
    private long timestamp,dl;

    public ComplaintModel() {}

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getRoom() { return room; }
    public void setRoom(String room) { this.room = room; }

    public String getRoomNumber() { return room; }
    public void setRoomNumber(String roomNumber) { this.room = roomNumber; }

    public String getBednumber() { return bednumber; }
    public void setBednumber(String bednumber) { this.bednumber = bednumber; }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getAssignedWorkerId() { return assignedWorkerId; }
    public void setAssignedWorkerId(String assignedWorkerId) { this.assignedWorkerId = assignedWorkerId; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public String getDocId() { return docId; }
    public void setDocId(String docId) { this.docId = docId; }

    public void setDeadline(long dl) {
        this.dl = dl;
    }

    public long getDeadline() {
        return dl;
    }
}