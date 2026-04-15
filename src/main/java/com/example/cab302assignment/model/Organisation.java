package com.example.cab302assignment.model;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

public class Organisation {
    private int orgId;
    private String name;
    private LocalDateTime createdAt;

    public Organisation() {
    }

    public Organisation(String name) {
        this.name = name;
    }

    public Organisation(int orgId, String name, LocalDateTime createdAt) {
        this.orgId = orgId;
        this.name = name;
        this.createdAt = createdAt;
    }

    public int getOrgId() { return orgId; }
    public void setOrgId(int orgId) { this.orgId = orgId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public void addMember(User user) {
    }

    public void removeMember(User user) {
    }

    public List<User> getMembers() {
        return Collections.emptyList();
    }

    public Ruleset getRuleset() {
        return null;
    }

    public double getHistoricalRiskScore() {
        return 0.0;
    }
}
