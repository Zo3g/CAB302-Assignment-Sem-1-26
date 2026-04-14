package com.example.cab302assignment.model;

import com.example.cab302assignment.model.enums.SensitiveDataType;

import java.util.EnumMap;
import java.util.Map;

public class RedactionResult {
    private int resultId;
    private int promptId;
    private String redactedText;
    private int totalDetections;
    private Map<SensitiveDataType, Integer> typeCounts = new EnumMap<>(SensitiveDataType.class);

    public RedactionResult() {
    }

    public RedactionResult(String redactedText, int totalDetections, Map<SensitiveDataType, Integer> typeCounts) {
        this.redactedText = redactedText;
        this.totalDetections = totalDetections;
        if (typeCounts != null) this.typeCounts.putAll(typeCounts);
    }

    public int getResultId() { return resultId; }
    public void setResultId(int resultId) { this.resultId = resultId; }

    public int getPromptId() { return promptId; }
    public void setPromptId(int promptId) { this.promptId = promptId; }

    public String getRedactedText() { return redactedText; }
    public void setRedactedText(String redactedText) { this.redactedText = redactedText; }

    public int getTotalDetections() { return totalDetections; }
    public void setTotalDetections(int totalDetections) { this.totalDetections = totalDetections; }

    public Map<SensitiveDataType, Integer> getTypeCounts() { return typeCounts; }
    public void setTypeCounts(Map<SensitiveDataType, Integer> typeCounts) {
        this.typeCounts = typeCounts == null ? new EnumMap<>(SensitiveDataType.class) : typeCounts;
    }
}
