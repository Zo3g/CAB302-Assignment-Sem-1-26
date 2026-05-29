package com.example.cab302assignment.model;

/**
 * A data transfer object representing a summarized view of a single prompt analysis event.
 * This class is primarily used to populate the history table in the Member Drilldown view,
 * providing managers with a quick overview of an employee's interactions with the AI.
 */
public class PromptHistorySummary {
    private final String date;
    private final String redactedText;
    private final String riskLevel;
    private final double score;
    private final int totalDetections;

    /**
     * Constructs a new summary of a prompt history event.
     *
     * @param date            The formatted timestamp (e.g., UTC) of when the prompt was submitted.
     * @param redactedText    The safe, sanitized version of the user's prompt after sensitive data was scrubbed.
     * @param riskLevel       The categorical string representation of the risk (e.g., "LOW", "CRITICAL").
     * @param score           The raw numerical risk score calculated by the AI engine.
     * @param totalDetections The total count of sensitive data instances flagged and redacted in the prompt.
     */
    public PromptHistorySummary(String date, String redactedText, String riskLevel, double score, int totalDetections) {
        this.date = date;
        this.redactedText = redactedText;
        this.riskLevel = riskLevel;
        this.score = score;
        this.totalDetections = totalDetections;
    }

    /**
     * Gets the date the prompt was submitted.
     * @return A formatted date string.
     */
    public String getDate() { return date; }

    /**
     * Gets the sanitized text of the prompt.
     * @return The prompt text with sensitive information replaced by redaction markers.
     */
    public String getRedactedText() { return redactedText; }

    /**
     * Gets the categorical risk assessment of the prompt.
     * @return The calculated risk level category.
     */
    public String getRiskLevel() { return riskLevel; }

    /**
     * Gets the raw risk score assigned to the prompt.
     * @return The numerical risk score.
     */
    public double getScore() { return score; }

    /**
     * Gets the total number of sensitive entities detected during redaction.
     * @return The integer count of detections.
     */
    public int getTotalDetections() { return totalDetections; }
}