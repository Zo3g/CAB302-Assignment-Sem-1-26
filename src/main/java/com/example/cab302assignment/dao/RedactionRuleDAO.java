package com.example.cab302assignment.dao;

import com.example.cab302assignment.model.RedactionRule;

import java.util.List;

/**
 * DAO interface for redaction rules.
 *
 * <p>A redaction rule is a single pattern (like a regex for emails or phone
 * numbers) that belongs to a ruleset. This interface handles adding, fetching,
 * updating and deleting those rules.</p>
 */
public interface RedactionRuleDAO {
    /**
     * Adds a new redaction rule.
     *
     * @param rule the rule to save
     */
    void addRule(RedactionRule rule);

    /**
     * Finds a single rule by its ID.
     *
     * @param ruleId the rule's ID
     * @return the rule, or null if not found
     */
    RedactionRule getRuleById(int ruleId);

    /**
     * Gets all the rules that belong to a particular ruleset.
     *
     * @param rulesetId the ruleset's ID
     * @return a list of rules in that ruleset
     */
    List<RedactionRule> getRulesByRuleset(int rulesetId);

    /**
     * Updates an existing rule's details.
     *
     * @param rule the rule with the updated info (matched by ID)
     */
    void updateRule(RedactionRule rule);

    /**
     * Deletes a rule by its ID.
     *
     * @param ruleId the ID of the rule to delete
     */
    void deleteRule(int ruleId);
}
