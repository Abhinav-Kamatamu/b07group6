package com.example.b07group6.construct;

public class Comment {
    private String id;       // set manually after reading, not stored in DB
    private String text;
    private String username;
    private String uid;
    private Object timestamp;

    public Comment() {
        // required no-arg constructor for Firebase deserialization
    }

    public Comment(String text, String username, String uid, Object timestamp) {
        this.text = text;
        this.username = username;
        this.uid = uid;
        this.timestamp = timestamp;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }

    public Object getTimestamp() { return timestamp; }
    public void setTimestamp(Object timestamp) { this.timestamp = timestamp; }
}