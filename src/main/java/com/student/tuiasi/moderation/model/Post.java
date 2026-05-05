package com.student.tuiasi.moderation.model;

import java.time.LocalDateTime;

public class Post {

    private int id;
    private int childId;
    private String content;
    private String type;
    private String status;
    private LocalDateTime createdAt;

    public Post(int id, int childId, String content, String type, String status) {
        this.id = id;
        this.childId = childId;
        this.content = content;
        this.type = type;
        this.status = status;
        this.createdAt = LocalDateTime.now();
    }

    public int getId() { return id; }
    public int getChildId() { return childId; }
    public String getContent() { return content; }
    public String getType() { return type; }
    public String getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setStatus(String status) { this.status = status; }
}