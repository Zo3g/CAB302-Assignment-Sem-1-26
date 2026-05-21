package com.example.cab302assignment.model;

import com.example.cab302assignment.model.enums.RiskLevel;
import com.example.cab302assignment.model.enums.SensitiveDataType;

import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.Map;

/**
 * Model representing the outcome of a risk analysis run against a
 * single user prompt.
 *
 * <p>A {@code RiskAnalysis} captures the overall {@link RiskLevel}, a
 * numerical {@code score}, a human-readable {@code summary}, a breakdown of
 * detected {@link SensitiveDataType} occurrences in {@code typeCounts}, and
 * the timestamp at which the analysis was performed.</p>
 *
 * <p>Instances are typically produced by the analysis pipeline and
 * persisted via a {@code RiskAnalysisDAO}; they are then aggregated into a
 * {@link UserRiskScore} for display in the profile and dashboards.</p>
 */
public class RiskAnalysis {
    private int analysisId;
    private int promptId;
    private RiskLevel riskLevel;
    private double score;
    private String summary;
    private Map<SensitiveDataType, Integer> typeCounts = new EnumMap<>(SensitiveDataType.class);
    private LocalDateTime analysedAt;

    /**
     * Creates an empty {@code RiskAnalysis}.
     *
     * <p>Provided primarily for frameworks (JavaBeans, DAOs) that construct
     * the instance and then populate it through setters.</p>
     */
    public RiskAnalysis() {
    }

    /**
     * Creates a fully populated {@code RiskAnalysis}.
     *
     * <p>The {@code analysisId} is not set by this constructor and is
     * expected to be assigned by the persistence layer.</p>
     *
     * @param promptId    identifier of the prompt that was analysed
     * @param riskLevel   the categorical risk level assigned to the prompt
     * @param score       the numerical risk score
     * @param summary     a human-readable summary of the analysis
     * @param typeCounts  counts of each sensitive data type detected;
     *                    may be {@code null}, in which case the internal
     *                    map remains empty
     * @param analysedAt  the timestamp at which the analysis was performed
     */
    public RiskAnalysis(int promptId, RiskLevel riskLevel, double score, String summary,
                        Map<SensitiveDataType, Integer> typeCounts, LocalDateTime analysedAt) {
        this.promptId = promptId;
        this.riskLevel = riskLevel;
        this.score = score;
        this.summary = summary;
        if (typeCounts != null) this.typeCounts.putAll(typeCounts);
        this.analysedAt = analysedAt;
    }

    public int getAnalysisId() { return analysisId; }
    public void setAnalysisId(int analysisId) { this.analysisId = analysisId; }

    public int getPromptId() { return promptId; }
    public void setPromptId(int promptId) { this.promptId = promptId; }

    public RiskLevel getRiskLevel() { return riskLevel; }
    public void setRiskLevel(RiskLevel riskLevel) { this.riskLevel = riskLevel; }

    public Map<SensitiveDataType, Integer> getTypeCounts() { return typeCounts; }

    /**
     * Replaces the per-type sensitive data counts.
     *
     * <p>If {@code typeCounts} is {@code null}, the internal map is reset to
     * an empty {@link EnumMap}; otherwise the supplied map is adopted
     * directly.</p>
     *
     * @param typeCounts the new counts, or {@code null} to clear
     */
    public void setTypeCounts(Map<SensitiveDataType, Integer> typeCounts) {
        this.typeCounts = typeCounts == null ? new EnumMap<>(SensitiveDataType.class) : typeCounts;
    }

    public LocalDateTime getAnalysedAt() { return analysedAt; }
    public void setAnalysedAt(LocalDateTime analysedAt) { this.analysedAt = analysedAt; }

    public double getScore() { return score; }
    public void setScore(double score) { this.score = score; }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
}
