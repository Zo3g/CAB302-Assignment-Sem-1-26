package com.example.cab302assignment.model;

import com.example.cab302assignment.model.enums.RiskLevel;

import java.time.LocalDateTime;
import java.util.List;

public class UserRiskScore {
    private int scoreId;
    private int userId;
    private double score;
    private int totalPromptsAnalysed;
    private LocalDateTime lastUpdated;

    public UserRiskScore() {
    }

    public UserRiskScore(int userId, double score, int totalPromptsAnalysed, LocalDateTime lastUpdated) {
        this.userId = userId;
        this.score = score;
        this.totalPromptsAnalysed = totalPromptsAnalysed;
        this.lastUpdated = lastUpdated;
    }

    public int getScoreId() { return scoreId; }
    public void setScoreId(int scoreId) { this.scoreId = scoreId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public double getScore() { return score; }
    public void setScore(double score) { this.score = score; }

    public int getTotalPromptsAnalysed() { return totalPromptsAnalysed; }
    public void setTotalPromptsAnalysed(int totalPromptsAnalysed) { this.totalPromptsAnalysed = totalPromptsAnalysed; }

    public LocalDateTime getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; }

    public void recalculate(List<RiskAnalysis> history) {
        if (history == null || history.isEmpty()) {
            this.score = 0.0;
            this.totalPromptsAnalysed = 0;
            this.lastUpdated = LocalDateTime.now();
            return;
        }
        double total = 0.0;
        for (RiskAnalysis analysis : history) {
            total += riskLevelToScore(analysis.getRiskLevel());
        }
        this.score = total / history.size();
        this.totalPromptsAnalysed = history.size();
        this.lastUpdated = LocalDateTime.now();
    }

    private static double riskLevelToScore(RiskLevel level) {
        if (level == null) return 0.0;
        return switch (level) {
            case LOW -> 25.0;
            case MEDIUM -> 50.0;
            case HIGH -> 75.0;
            case CRITICAL -> 100.0;
        };
    }
}
