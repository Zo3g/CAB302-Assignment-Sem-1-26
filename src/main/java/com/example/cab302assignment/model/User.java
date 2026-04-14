package com.example.cab302assignment.model;

import com.example.cab302assignment.service.PasswordUtil;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

public class User {
    private int userId;
    private String email;
    private String passwordHash;
    private LocalDateTime createdAt;

    public User() {
    }

    public User(String email, String passwordHash) {
        this.email = email;
        this.passwordHash = passwordHash;
    }

    public User(int userId, String email, String passwordHash, LocalDateTime createdAt) {
        this.userId = userId;
        this.email = email;
        this.passwordHash = passwordHash;
        this.createdAt = createdAt;
    }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public boolean authenticate(String password) {
        return PasswordUtil.verifyPassword(password, this.passwordHash);
    }

    public List<Prompt> getPromptHistory() {
        return Collections.emptyList();
    }

    public void leaveOrganisation() {
    }
}
