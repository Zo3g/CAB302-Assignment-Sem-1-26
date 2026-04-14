package com.example.cab302assignment.model;

import com.example.cab302assignment.model.enums.RiskLevel;
import com.example.cab302assignment.model.enums.SensitiveDataType;

import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.Map;

public class RiskAnalysis {
    private int analysisId;
    private int promptId;
    private RiskLevel riskLevel;
    private String summary;
    private Map<SensitiveDataType, Integer> typeCounts = new EnumMap<>(SensitiveDataType.class);
    private LocalDateTime analysedAt;

    public RiskAnalysis() {
    }

    public RiskAnalysis(int promptId, RiskLevel riskLevel, String summary,
                        Map<SensitiveDataType, Integer> typeCounts, LocalDateTime analysedAt) {
        this.promptId = promptId;
        this.riskLevel = riskLevel;
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

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }

    public Map<SensitiveDataType, Integer> getTypeCounts() { return typeCounts; }
    public void setTypeCounts(Map<SensitiveDataType, Integer> typeCounts) {
        this.typeCounts = typeCounts == null ? new EnumMap<>(SensitiveDataType.class) : typeCounts;
    }

    public LocalDateTime getAnalysedAt() { return analysedAt; }
    public void setAnalysedAt(LocalDateTime analysedAt) { this.analysedAt = analysedAt; }
}
