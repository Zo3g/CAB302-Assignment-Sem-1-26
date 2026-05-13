package com.example.cab302assignment.model;

import com.example.cab302assignment.model.enums.RiskLevel;

import java.time.LocalDateTime;
import java.util.List;

public class UserRiskScore {
    private int scoreId;
    private int userId;
    private double score;
    private RiskLevel riskLevel;
    private int totalPromptsAnalysed;
    private LocalDateTime lastUpdated;

    public UserRiskScore() {
    }

    public UserRiskScore(int userId, double score, int totalPromptsAnalysed, LocalDateTime lastUpdated) {
        this.userId = userId;
        setScore(score);
        this.totalPromptsAnalysed = totalPromptsAnalysed;
        this.lastUpdated = lastUpdated;
    }

    public int getScoreId() { return scoreId; }
    public void setScoreId(int scoreId) { this.scoreId = scoreId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public double getScore() { return score; }
    public void setScore(double score) {
        this.score = score;

        if (score >= RiskLevel.CRITICAL.scoreDouble()) {
            setRiskLevel(RiskLevel.CRITICAL);
        } else if (score >= RiskLevel.HIGH.scoreDouble()) {
            setRiskLevel(RiskLevel.HIGH);
        } else if (score >= RiskLevel.MEDIUM.scoreDouble()) {
            setRiskLevel(RiskLevel.MEDIUM);
        } else if (score >= RiskLevel.LOW.scoreDouble()) {
            setRiskLevel(RiskLevel.LOW);
        } else {
            setRiskLevel(RiskLevel.NO);
        }
    }

    public RiskLevel getRiskLevel() { return riskLevel; }
    public void setRiskLevel(RiskLevel riskLevel) { this.riskLevel = riskLevel; }

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
            RiskLevel level = analysis.getRiskLevel();
            total += level == null ? 0.0 : level.scoreDouble();
        }
        this.score = total / history.size();
        this.totalPromptsAnalysed = history.size();
        this.lastUpdated = LocalDateTime.now();
    }
}
