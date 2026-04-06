package com.example.cab302assignment.model;

import java.time.LocalDateTime;

public class OrganisationMembership {
    private int userId;
    private int organisationId;
    private MemberRole memberRole;
    private boolean active;
    private LocalDateTime joinedAt;

    public OrganisationMembership(int userId, int organisationId, MemberRole memberRole, boolean active) {
        this.userId = userId;
        this.organisationId = organisationId;
        this.memberRole = memberRole;
        this.active = active;
        this.joinedAt = LocalDateTime.now();
    }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getOrganisationId() { return organisationId; }
    public void setOrganisationId(int organisationId) { this.organisationId = organisationId; }

    public MemberRole getMemberRole() { return memberRole; }
    public void setMemberRole(MemberRole memberRole) { this.memberRole = memberRole; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public LocalDateTime getJoinedAt() { return joinedAt; }
    public void setJoinedAt(LocalDateTime joinedAt) { this.joinedAt = joinedAt; }
}
