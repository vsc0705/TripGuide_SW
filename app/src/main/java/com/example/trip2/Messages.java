package com.example.trip2;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.ServerTimestamp;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Messages {
    private String from;
    private String message;
    private String type;
    @ServerTimestamp
    private Date time;

    public Messages(String from, String message, String type, Date time) {
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
        return currentTimeFormat.format(time);
    }

}
