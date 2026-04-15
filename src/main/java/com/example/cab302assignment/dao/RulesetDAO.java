package com.example.cab302assignment.dao;

import com.example.cab302assignment.model.Ruleset;

public interface RulesetDAO {
    void addRuleset(Ruleset ruleset);
    Ruleset getRulesetById(int rulesetId);
    Ruleset getRulesetByOrg(int orgId);
    void updateRuleset(Ruleset ruleset);
    void deleteRuleset(int rulesetId);
}
