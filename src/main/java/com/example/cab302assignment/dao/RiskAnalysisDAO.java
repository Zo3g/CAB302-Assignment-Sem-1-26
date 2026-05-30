package com.example.cab302assignment.dao;

import com.example.cab302assignment.model.RiskAnalysis;

import java.util.List;

/**
 * DAO interface for risk analyses.
 *
 * <p>A risk analysis is the score/level we work out for a prompt after looking
 * at what sensitive data it contained. Each one is linked to a prompt, so most
 * of these methods work off the prompt ID.</p>
 */
public interface RiskAnalysisDAO {
    /**
     * Saves a risk analysis to the database.
     *
     * @param analysis the analysis to add
     */
    void addAnalysis(RiskAnalysis analysis);

    /**
     * Gets the risk analysis for a given prompt.
     *
     * @param promptId the ID of the prompt
     * @return the analysis for that prompt, or null if there isn't one
     */
    RiskAnalysis getByPromptId(int promptId);

    /**
     * Gets all the risk analyses for prompts submitted by a user.
     *
     * <p>Useful for working out a user's overall risk across everything
     * they've submitted.</p>
     *
     * @param userId the user's ID
     * @return a list of that user's risk analyses
     */
    List<RiskAnalysis> getByUserId(int userId);

    /**
     * Deletes the risk analysis tied to a prompt.
     *
     * @param promptId the ID of the prompt whose analysis we want removed
     */
    void deleteByPromptId(int promptId);
}
