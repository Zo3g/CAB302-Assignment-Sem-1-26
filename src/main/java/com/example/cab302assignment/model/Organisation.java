package com.example.cab302assignment.model;

import java.time.LocalDateTime;

public class Organisation {
    private int id;
    private String name;
    private LocalDateTime createdAt;
    // Create rulesetList later?

    public Organisation(String name) {
        this.name = name;
        this.createdAt = LocalDateTime.now();
    }

    public Organisation(int id, String name, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}