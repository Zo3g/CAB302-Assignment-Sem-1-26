package com.example.cab302assignment.model;

import com.example.cab302assignment.model.enums.RiskLevel;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Model representing a user's aggregate risk score.
 *
 * <p>A {@code UserRiskScore} summarises a user's accumulated risk across all
 * of their analysed prompts. It holds the latest numerical {@code score},
 * the derived categorical {@link RiskLevel}, the number of prompts that
 * contributed to the score, and the timestamp of the most recent update.</p>
 *
 * <p>Instances are typically loaded from the database, refreshed by
 * {@link #recalculate(List)} when new {@link RiskAnalysis} records become
 * available, and persisted via a {@code UserRiskScoreDAO}.</p>
 */
public class UserRiskScore {
    private int scoreId;
    private int userId;
    private double score;
    private RiskLevel riskLevel;
    private int totalPromptsAnalysed;
    private LocalDateTime lastUpdated;

    /**
     * Creates an empty {@code UserRiskScore}.
     *
     * <p>Provided primarily for frameworks (JavaBeans, DAOs) that construct
     * the instance and then populate it through setters.</p>
     */
    public UserRiskScore() {
    }

    /**
     * Creates a fully populated {@code UserRiskScore}.
     *
     * <p>The score is applied via {@link #setScore(double)} so the derived
     * {@link RiskLevel} is set consistently. The {@code scoreId} is not set
     * by this constructor and is expected to be assigned by the persistence
     * layer.</p>
     *
     * @param userId                identifier of the user this score belongs to
     * @param score                 the aggregate numerical risk score
     * @param totalPromptsAnalysed  the number of prompts that contributed to
     *                              the score
     * @param lastUpdated           the timestamp at which the score was last
     *                              recalculated
     */
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

    /**
     * Sets the numerical risk score and updates the derived
     * {@link RiskLevel} to match.
     *
     * <p>The risk level is chosen by comparing {@code score} against the
     * thresholds defined on the {@link RiskLevel} enum, in descending order
     * from {@link RiskLevel#CRITICAL} down to {@link RiskLevel#NO}.</p>
     *
     * @param score the new aggregate risk score
     */
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

    /**
     * Recalculates this user's aggregate risk score from their analysis
     * history.
     *
     * <p>The new score is the mean of the {@link RiskLevel#scoreDouble()}
     * values for every entry in {@code history}; analyses with a {@code null}
     * risk level contribute {@code 0.0}. The {@code totalPromptsAnalysed}
     * and {@code lastUpdated} fields are updated to reflect the new
     * calculation.</p>
     *
     * <p>If {@code history} is {@code null} or empty, the score is reset to
     * {@code 0.0} with zero prompts analysed.</p>
     *
     * @param history the user's prior risk analyses; may be {@code null} or
     *                empty to indicate no history
     */
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
