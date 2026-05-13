package com.example.cab302assignment.model;

// This is used for the member drilldown view

public class PromptHistorySummary {
    private final String date;
    private final String redactedText;
    private final String riskLevel;
    private final double score;
    private final int totalDetections; // NEW

    public PromptHistorySummary(String date, String redactedText, String riskLevel, double score, int totalDetections) {
        this.date = date;
        this.redactedText = redactedText;
        this.riskLevel = riskLevel;
        this.score = score;
        this.totalDetections = totalDetections; // NEW
    }

    public String getDate() { return date; }
    public String getRedactedText() { return redactedText; }
    public String getRiskLevel() { return riskLevel; }
    public double getScore() { return score; }
    public int getTotalDetections() { return totalDetections; } // NEW
}