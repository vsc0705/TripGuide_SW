package com.example.trip2;

import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;

public class Messages {
    private String from;
    private String message;
    private String type;
    private Timestamp time;

    public Messages(String from, String message, String type, Timestamp time) {
        this.from = from;
        this.message = message;
        this.type = type;
        this.time = time;
    }

    public Messages() {
    }

    public String getFrom() {
        return from;
    }

    public String getMessage() {
        return message;
    }

    public String getType() {
        return type;
    }

    public String getTime() {
        SimpleDateFormat currentTimeFormat = new SimpleDateFormat("hh:mm a");
        return currentTimeFormat.format(time.toDate());
    }

}
