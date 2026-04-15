package com.example.cab302assignment.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Ruleset {
    private int rulesetId;
    private int orgId;
    private LocalDateTime updatedAt;
    private final List<RedactionRule> rules = new ArrayList<>();

    public Ruleset() {
    }

    public Ruleset(int orgId) {
        this.orgId = orgId;
    }

    public Ruleset(int rulesetId, int orgId, LocalDateTime updatedAt) {
        this.rulesetId = rulesetId;
        this.orgId = orgId;
        this.updatedAt = updatedAt;
    }

    public int getRulesetId() { return rulesetId; }
    public void setRulesetId(int rulesetId) { this.rulesetId = rulesetId; }

    public int getOrgId() { return orgId; }
    public void setOrgId(int orgId) { this.orgId = orgId; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public List<RedactionRule> getRules() {
        return rules;
    }

    public void addRule(RedactionRule rule) {
        rules.add(rule);
    }

    public void removeRule(RedactionRule rule) {
        rules.remove(rule);
    }
}
