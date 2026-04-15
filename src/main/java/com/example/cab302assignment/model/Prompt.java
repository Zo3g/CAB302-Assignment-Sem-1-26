package com.example.cab302assignment.model;

import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

import java.time.LocalDateTime;

public class Prompt {
    private int promptId;
    private int orgId;
    private int userId;
    private String redactedText;
    private LocalDateTime submittedAt;

    private RedactionResult redactionResult;
    private RiskAnalysis riskAnalysis;

    public Prompt() {
    }

    public Prompt(int orgId, int userId, String redactedText) {
        this.orgId = orgId;
        this.userId = userId;
        this.redactedText = redactedText;
    }

    public Prompt(int promptId, int orgId, int userId, String redactedText, LocalDateTime submittedAt) {
        this.promptId = promptId;
        this.orgId = orgId;
        this.userId = userId;
        this.redactedText = redactedText;
        this.submittedAt = submittedAt;
    }

    public int getPromptId() { return promptId; }
    public void setPromptId(int promptId) { this.promptId = promptId; }

    public int getOrgId() { return orgId; }
    public void setOrgId(int orgId) { this.orgId = orgId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getRedactedText() { return redactedText; }
    public void setRedactedText(String redactedText) { this.redactedText = redactedText; }

    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; }

    public RedactionResult getRedactionResult() { return redactionResult; }
    public void setRedactionResult(RedactionResult redactionResult) { this.redactionResult = redactionResult; }

    public RiskAnalysis getRiskAnalysis() { return riskAnalysis; }
    public void setRiskAnalysis(RiskAnalysis riskAnalysis) { this.riskAnalysis = riskAnalysis; }

    public void copyRedactedToClipboard() {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(redactedText == null ? "" : redactedText);
        clipboard.setContent(content);
    }
}
