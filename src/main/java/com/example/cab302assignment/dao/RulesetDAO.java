package com.example.cab302assignment.dao;

import com.example.cab302assignment.model.Ruleset;

/**
 * DAO interface for rulesets.
 *
 * <p>A ruleset is basically a container of redaction rules that belongs to an
 * organisation (each org has its own one). This interface covers the database
 * operations for those rulesets.</p>
 */
public interface RulesetDAO {
    /**
     * Adds a new ruleset.
     *
     * @param ruleset the ruleset to save
     */
    void addRuleset(Ruleset ruleset);

    /**
     * Finds a ruleset by its ID.
     *
     * @param rulesetId the ruleset's ID
     * @return the ruleset, or null if not found
     */
    Ruleset getRulesetById(int rulesetId);

    /**
     * Finds the ruleset that belongs to a particular organisation.
     *
     * @param orgId the organisation's ID
     * @return that org's ruleset, or null if it doesn't have one
     */
    Ruleset getRulesetByOrg(int orgId);

    /**
     * Updates a ruleset.
     *
     * @param ruleset the ruleset with the updated info (matched by ID)
     */
    void updateRuleset(Ruleset ruleset);

    /**
     * Deletes a ruleset by its ID.
     *
     * @param rulesetId the ID of the ruleset to delete
     */
    void deleteRuleset(int rulesetId);
}
