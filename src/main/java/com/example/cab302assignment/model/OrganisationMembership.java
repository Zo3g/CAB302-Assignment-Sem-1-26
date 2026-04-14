package com.example.cab302assignment.model;

import com.example.cab302assignment.model.enums.MemberRole;

import java.time.LocalDateTime;

public class OrganisationMembership {
    private int userId;
    private int orgId;
    private MemberRole memberRole;
    private boolean active;
    private LocalDateTime joinedAt;

    public OrganisationMembership() {
    }

    public OrganisationMembership(int userId, int orgId, MemberRole memberRole, boolean active, LocalDateTime joinedAt) {
        this.userId = userId;
        this.orgId = orgId;
        this.memberRole = memberRole;
        this.active = active;
        this.joinedAt = joinedAt;
    }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getOrgId() { return orgId; }
    public void setOrgId(int orgId) { this.orgId = orgId; }

    public MemberRole getMemberRole() { return memberRole; }
    public void setMemberRole(MemberRole memberRole) { this.memberRole = memberRole; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public LocalDateTime getJoinedAt() { return joinedAt; }
    public void setJoinedAt(LocalDateTime joinedAt) { this.joinedAt = joinedAt; }
}
