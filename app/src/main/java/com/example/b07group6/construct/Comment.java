package com.example.b07group6.construct;


/** The representation of a Comment stored in Firebase */
public class Comment {
    private String id;       // set manually after reading, not stored in DB
    private String text;
    private String username;
    private String uid;
    private Long timestamp;

    /**
     * Constructs a new, empty {@code Comment} instance.
     * Required as a no-argument constructor for Firebase deserialization.
     */
    public Comment() { }

    /**
     * Constructs a new {@code Comment} with specified text, author metadata, and creation timestamp.
     * @param text the body text of the comment
     * @param username the display name of the author
     * @param uid the unique user ID of the author
     * @param timestamp the epoch timestamp indicating when the comment was created
     */
    public Comment(String text, String username, String uid, Long timestamp) {
        this.text = text;
        this.username = username;
        this.uid = uid;
        this.timestamp = timestamp;
    }

    /**
     * Returns the comment id.
     * @return the comment id
     */
    public String getId() { return id; }

    /**
     * Sets the comment id.
     * @param id the comment id to set
     */
    public void setId(String id) { this.id = id; }

    /**
     * Returns the body text of the comment.
     * @return the comment text
     */
    public String getText() { return text; }

    /**
     * Sets the body text of the comment.
     * @param text the comment text to set
     */
    public void setText(String text) { this.text = text; }

    /**
     * Returns the username of the user who wrote the comment.
     * @return the username
     */
    public String getUsername() { return username; }

    /**
     * Sets the username of the user who wrote the comment.
     * @param username the username to set
     */
    public void setUsername(String username) { this.username = username; }

    /**
     * Returns the unique user id of the user who wrote the comment.
     * @return the user id
     */
    public String getUid() { return uid; }

    /**
     * Sets the unique user id of the user who wrote the comment.
     * @param uid the user id to set
     */
    public void setUid(String uid) { this.uid = uid; }

    /**
     * Returns the epoch timestamp indicating when the comment was created.
     * @return the creation timestamp
     */
    public Long getTimestamp() { return timestamp; }

    /**
     * Sets the epoch timestamp indicating when the comment was created.
     * @param timestamp the creation timestamp to set
     */
    public void setTimestamp(Long timestamp) { this.timestamp = timestamp; }
}