package com.example.hostelcomplaintapp;

import android.widget.Button;

public class Notificationpgdatastore {

    String text, wardenId;
    long timestamp;
    String id;

    public Notificationpgdatastore() {
        // required empty constructor
    }

    public String getText() {
        return text;
    }

    public String getWardenId() {
        return wardenId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}