package com.example.hostelcomplaintapp;

public class ComplaintModel {

    private String text;
    private String room;
    private long timestamp;
    private String status;
    private String bedNumber;
    private String studentId;

    // Empty constructor (REQUIRED for Firestore)
    public ComplaintModel() {
    }

    // Constructor
    public ComplaintModel(String text, String room, long timestamp, String status, String bedNumber, String studentId) {
        this.text = text;
        this.room = room;
        this.timestamp = timestamp;
        this.status = status;
        this.bedNumber = bedNumber;
        this.studentId = studentId;
    }

    // Getter methods
    public String getText() {
        return text;
    }

    public String getRoom() {
        return room;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getStatus() {
        return status;
    }

    public String getBedNumber() {
        return bedNumber;
    }

    public String getStudentId() {
        return bedNumber;
    }
}