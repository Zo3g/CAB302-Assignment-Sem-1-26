package com.example.cab302assignment.model;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * Represents an organisation within the system.
 *
 * <p>This model stores organisation metadata, configuration
 * settings, and risk-related activity information, including
 * rulesets and historical risk scores.</p>
 */
public class Organisation {

    /** Unique identifier of the organisation */
    private int orgId;

    /** Name of the organisation */
    private String name;

    /** Timestamp recording when the organisation was created */
    private LocalDateTime createdAt;

    /**
     * Constructs an empty Organisation object.
     */
    public Organisation() {
    }

    /**
     * Constructs an Organisation with a specified name.
     *
     * @param name the organisation name
     */
    public Organisation(String name) {
        this.name = name;
    }

    /**
     * Constructs a fully initialized Organisation object.
     *
     * @param orgId the organisation ID
     * @param name the organisation name
     * @param createdAt the organisation creation timestamp
     */
    public Organisation(int orgId, String name, LocalDateTime createdAt) {
        this.orgId = orgId;
        this.name = name;
        this.createdAt = createdAt;
    }

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
     * Gets the organisation name.
     *
     * @return the organisation name
     */
    public String getName() { return name; }

    /**
     * Sets the organisation name.
     *
     * @param name the organisation name to set
     */
    public void setName(String name) { this.name = name; }

    /**
     * Gets the organisation creation timestamp.
     *
     * @return the creation timestamp
     */
    public LocalDateTime getCreatedAt() { return createdAt; }

    /**
     * Sets the organisation creation timestamp.
     *
     * @param createdAt the timestamp to set
     */
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public void addMember(User user) {
    }

    public void removeMember(User user) {
    }

    public List<User> getMembers() {
        return Collections.emptyList();
    }

    /**
     * Gets the organisation's reduction ruleset.
     *
     * @return the reduction ruleset
     */
    public Ruleset getRuleset() {
        return null;
    }

    /**
     * Gets the organisation's historical risk score.
     *
     * @return the historical risk score
     */
    public double getHistoricalRiskScore() {
        return 0.0;
    }
}
