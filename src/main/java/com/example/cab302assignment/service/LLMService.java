package com.example.cab302assignment.service;

import com.example.cab302assignment.model.RedactionResult;
import com.example.cab302assignment.model.RiskAnalysis;
import com.example.cab302assignment.model.UserRiskScore;

import java.util.List;

/**
 * Abstraction over a large language model used for compliance risk
 * assessment.
 *
 * <p>Implementations wrap a specific LLM provider (e.g.
 * {@link GeminiService}) and expose the two operations the application
 * needs: analysing the risk of a single redacted prompt, and aggregating a
 * user's analysis history into a {@link UserRiskScore}.</p>
 *
 * <p>Decoupling the rest of the codebase from the concrete provider via
 * this interface allows providers to be swapped (or stubbed in tests)
 * without changing the analysis pipeline or persistence layer.</p>
 */
public interface LLMService {
    /**
     * Analyses the compliance risk of a single redacted prompt.
     *
     * <p>The input is expected to be the output of a redaction step (e.g.
     * a {@link RedactionResult}'s redacted text) where sensitive values
     * have been replaced with bracketed placeholders. The returned string
     * is the raw model response and the format is implementation-defined;
     * callers should consult the specific implementation for the expected
     * structure.</p>
     *
     * @param redactedText the prompt text after redaction
     * @return the raw model response describing the risk assessment
     */
    String analysePromptRisk(String redactedText);

    /**
     * Calculates an aggregate {@link UserRiskScore} for the given user
     * from their analysis history.
     *
     * @param userId  identifier of the user the score is being calculated
     *                for
     * @param history the user's prior {@link RiskAnalysis} records that
     *                should contribute to the aggregate score
     * @return the calculated user risk score, or {@code null} if the
     *         implementation does not support aggregate scoring
     */
    UserRiskScore calculateUserRiskScore(int userId, List<RiskAnalysis> history);
}
