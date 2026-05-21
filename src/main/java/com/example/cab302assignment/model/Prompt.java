package com.example.cab302assignment.model;

import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

import java.time.LocalDateTime;

/**
 * Domain model representing a user-submitted prompt that has been processed
 * through the redaction and risk analysis pipeline.
 *
 * <p>A {@code Prompt} stores the redacted text that is safe to retain, the
 * organisation and user it belongs to, and the time it was submitted. It
 * also carries optional references to the associated
 * {@link RedactionResult} (the redaction step's findings) and
 * {@link RiskAnalysis} (the LLM's compliance assessment), which together
 * describe the full pipeline outcome.</p>
 */
public class Prompt {
    private int promptId;
    private int orgId;
    private int userId;
    private String redactedText;
    private LocalDateTime submittedAt;

    private RedactionResult redactionResult;
    private RiskAnalysis riskAnalysis;

    /**
     * Creates an empty {@code Prompt}.
     *
     * <p>Provided primarily for frameworks (JavaBeans, DAOs) that construct
     * the instance and then populate it through setters.</p>
     */
    public Prompt() {
    }

    /**
     * Creates a new {@code Prompt} prior to persistence.
     *
     * <p>The {@code promptId} and {@code submittedAt} fields are not set by
     * this constructor and are expected to be assigned by the persistence
     * layer when the prompt is saved.</p>
     *
     * @param orgId        identifier of the organisation the prompt belongs to
     * @param userId       identifier of the user who submitted the prompt
     * @param redactedText the prompt text after redaction
     */
    public Prompt(int orgId, int userId, String redactedText) {
        this.orgId = orgId;
        this.userId = userId;
        this.redactedText = redactedText;
    }

    /**
     * Creates a fully populated {@code Prompt}, typically when loading an
     * existing record from the database.
     *
     * @param promptId     database identifier of this prompt
     * @param orgId        identifier of the organisation the prompt belongs to
     * @param userId       identifier of the user who submitted the prompt
     * @param redactedText the prompt text after redaction
     * @param submittedAt  the timestamp at which the prompt was submitted
     */
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

    /**
     * Copies this prompt's redacted text onto the system clipboard.
     *
     * <p>If {@code redactedText} is {@code null}, an empty string is placed
     * on the clipboard instead. This is intended to be called from JavaFX
     * UI handlers so the user can paste the safe, redacted version of their
     * prompt into an external tool.</p>
     */
    public void copyRedactedToClipboard() {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(redactedText == null ? "" : redactedText);
        clipboard.setContent(content);
    }
}
