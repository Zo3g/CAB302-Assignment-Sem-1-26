package com.example.cab302assignment.model;

//This risk summary is used for the org risk dashboards' table of members.

public class MemberRiskSummary {
    private final int userId;
    private final String name;
    private final double riskScore;

    public MemberRiskSummary(int userId, String name, double riskScore) {
        this.userId = userId;
        this.name = name;
        this.riskScore = riskScore;
    }

    public int getUserId() { return userId; }
    public String getName() { return name; }
    public double getRiskScore() { return riskScore; }
}