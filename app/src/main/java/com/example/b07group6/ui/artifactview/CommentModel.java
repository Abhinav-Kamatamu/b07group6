package com.example.b07group6.ui.artifactview;

import java.util.Date;

public class CommentModel {
    String username;
    Date datePosted;
    String commentBody;

    public CommentModel(String username, Date datePosted, String commentBody) {
        this.username = username;
        this.datePosted = datePosted;
        this.commentBody = commentBody;
    }

    public String getUsername() {
        return username;
    }

    public Date getDatePosted() {
        return datePosted;
    }

    public String getCommentBody() {
        return commentBody;
    }
}
