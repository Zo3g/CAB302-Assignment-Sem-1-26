package com.example.cab302assignment.model;

import com.example.cab302assignment.model.enums.MemberRole;

import java.time.LocalDateTime;

/**
 * Represents a membership relationship between a user and an organisation.
 *
 * <p>This class stores organisation membership information including
 * the member role, active status, and join timestamp.</p>
 */
public class OrganisationMembership {
    /** Unique identifier of the user */
    private int userId;

    /** Unique identifier of the organisation */
    private int orgId;

    /** Role assigned to the member within the organisation */
    private MemberRole memberRole;

    /** Indicates whether the membership is currently active */
    private boolean active;

    /** Timestamp recording when the user joined the organisation */
    private LocalDateTime joinedAt;

    /**
     * Constructs an empty OrganisationMembership object.
     */
    public OrganisationMembership() {
    }

    /**
     * Constructs a fully initialized OrganisationMembership object.
     *
     * @param userId the user ID
     * @param orgId the organisation ID
     * @param memberRole the member role
     * @param active whether the membership is active
     * @param joinedAt the membership join timestamp
     */
    public OrganisationMembership(int userId, int orgId, MemberRole memberRole, boolean active, LocalDateTime joinedAt) {
        this.userId = userId;
        this.orgId = orgId;
        this.memberRole = memberRole;
        this.active = active;
        this.joinedAt = joinedAt;
    }

    /**
     * Gets the user ID.
     *
     * @return the user ID
     */
    public int getUserId() { return userId; }

    /**
     * Sets the user ID.
     *
     * @param userId the user ID to set
     */
    public void setUserId(int userId) { this.userId = userId; }

    /**
     * Gets the organisation ID.
     *
     * @return the organisation ID
     */
    public int getOrgId() { return orgId; }

    /**
     * Sets the organisation ID.
     *
     * @param orgId the organisation ID to set
     */
    public void setOrgId(int orgId) { this.orgId = orgId; }

    /**
     * Gets the member role.
     *
     * @return the member role
     */
    public MemberRole getMemberRole() { return memberRole; }

    /**
     * Sets the member role.
     *
     * @param memberRole the member role to set
     */
    public void setMemberRole(MemberRole memberRole) { this.memberRole = memberRole; }

    /**
     * Checks whether the membership is active.
     *
     * @return true if the membership is active, otherwise false
     */
    public boolean isActive() { return active; }

    /**
     * Sets the membership active status.
     *
     * @param active the active status to set
     */
    public void setActive(boolean active) { this.active = active; }

    /**
     * Gets the membership join timestamp.
     *
     * @return the join timestamp
     */
    public LocalDateTime getJoinedAt() { return joinedAt; }

    /**
     * Sets the membership join timestamp.
     *
     * @param joinedAt the join timestamp to set
     */
    public void setJoinedAt(LocalDateTime joinedAt) { this.joinedAt = joinedAt; }
}
