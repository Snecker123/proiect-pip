package com.student.tuiasi.moderation.model;

public class ParentAccount {

    private int id;
    private String username;
    private String email;
    private int birthYear;
    private String reason;
    private boolean approved;
    private String rejectionReason;

    public ParentAccount(int id, String username, String email, int birthYear, String reason) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.birthYear = birthYear;
        this.reason = reason;
        this.approved = false;
        this.rejectionReason = null;
    }

    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public int getBirthYear() { return birthYear; }
    public String getReason() { return reason; }
    public boolean isApproved() { return approved; }
    public String getRejectionReason() { return rejectionReason; }

    public void setApproved(boolean approved) { this.approved = approved; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }
}