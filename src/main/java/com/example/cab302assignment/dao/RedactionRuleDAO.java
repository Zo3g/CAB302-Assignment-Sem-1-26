package com.example.cab302assignment.dao;

import com.example.cab302assignment.model.RedactionRule;

import java.util.List;

public interface RedactionRuleDAO {
    void addRule(RedactionRule rule);
    RedactionRule getRuleById(int ruleId);
    List<RedactionRule> getRulesByRuleset(int rulesetId);
    void updateRule(RedactionRule rule);
    void deleteRule(int ruleId);
}
