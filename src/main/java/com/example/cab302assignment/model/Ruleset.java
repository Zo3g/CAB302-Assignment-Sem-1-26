package com.example.cab302assignment.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a collection of {@link RedactionRule} objects to be applied by the redaction engine.
 * A ruleset acts as a container that groups together all the specific data patterns an
 * organisation wants to detect and mask within their users' prompts.
 * * Note: Custom redaction rulesets was a planned feature, but currently, organisations cannot view, modify, or assign custom rulesets.
 * The RedactionEngine universally applies a single, hardcoded default ruleset to every
 * redaction request. Database integration fields (such as rulesetId and orgId) are
 * included strictly as dormant architecture to support planned future functionality.
 */
public class Ruleset {
    private int rulesetId;
    private int orgId;
    private LocalDateTime updatedAt;
    private final List<RedactionRule> rules = new ArrayList<>();

    /**
     * Default constructor required for database operations and serialization.
     * (Planned functionality for custom database rulesets).
     */
    public Ruleset() {
    }

    /**
     * Constructs a new Ruleset tied to a specific organisation.
     * (Planned functionality for custom database rulesets).
     *
     * @param orgId The unique database identifier of the organisation owning this ruleset.
     */
    public Ruleset(int orgId) {
        this.orgId = orgId;
    }

    /**
     * Constructs a fully populated Ruleset, typically used when loading from the database.
     * (Planned functionality for custom database rulesets).
     *
     * @param rulesetId The unique database identifier for this ruleset.
     * @param orgId     The unique database identifier of the owning organisation.
     * @param updatedAt The timestamp of when this ruleset was last modified.
     */
    public Ruleset(int rulesetId, int orgId, LocalDateTime updatedAt) {
        this.rulesetId = rulesetId;
        this.orgId = orgId;
        this.updatedAt = updatedAt;
    }

    /**
     * Gets the unique database identifier for this ruleset.
     * (Planned functionality for custom rulesets).
     * @return The ruleset ID.
     */
    public int getRulesetId() { return rulesetId; }

    /**
     * Sets the unique database identifier for this ruleset.
     * (Planned functionality for custom rulesets).
     * @param rulesetId The ruleset ID.
     */
    public void setRulesetId(int rulesetId) { this.rulesetId = rulesetId; }

    /**
     * Gets the unique database identifier of the organisation that owns this ruleset.
     * (Planned functionality for custom rulesets).
     * @return The organisation ID.
     */
    public int getOrgId() { return orgId; }

    /**
     * Sets the unique database identifier of the organisation that owns this ruleset.
     * (Planned functionality for custom rulesets).
     * @param orgId The organisation ID.
     */
    public void setOrgId(int orgId) { this.orgId = orgId; }

    /**
     * Gets the timestamp of when this ruleset was last modified in the database.
     * (Planned functionality for custom rulesets).
     * @return The last updated timestamp.
     */
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    /**
     * Sets the timestamp of when this ruleset was last modified in the database.
     * (Planned functionality for custom rulesets).
     * @param updatedAt The last updated timestamp.
     */
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    /**
     * Retrieves the active, in-memory list of redaction rules contained in this ruleset.
     * This list is iterated over by the redaction engine during the text sanitization process.
     *
     * @return A list of {@link RedactionRule} objects.
     */
    public List<RedactionRule> getRules() {
        return rules;
    }

    /**
     * Adds a new redaction rule to this ruleset.
     *
     * @param rule The {@link RedactionRule} to add.
     */
    public void addRule(RedactionRule rule) {
        rules.add(rule);
    }

    /**
     * Removes an existing redaction rule from this ruleset.
     *
     * @param rule The {@link RedactionRule} to remove.
     */
    public void removeRule(RedactionRule rule) {
        rules.remove(rule);
    }
}