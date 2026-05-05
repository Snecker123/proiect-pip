package com.student.tuiasi.moderation.model;

public class ChildAccount {

    private int id;
    private String username;
    private int age;
    private int parentId;

    public ChildAccount(int id, String username, int age, int parentId) {
        this.id = id;
        this.username = username;
        this.age = age;
        this.parentId = parentId;
    }

    public int getId() { return id; }
    public String getUsername() { return username; }
    public int getAge() { return age; }
    public int getParentId() { return parentId; }

    public void setUsername(String username) { this.username = username; }
    public void setAge(int age) { this.age = age; }
}