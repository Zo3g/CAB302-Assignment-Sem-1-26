package com.example.cab302assignment.service;

import com.example.cab302assignment.model.Prompt;
import com.example.cab302assignment.dao.PromptDAO;
import com.example.cab302assignment.dao.SqlitePromptDAO;
import com.example.cab302assignment.model.RedactionResult;
import com.example.cab302assignment.dao.RedactionResultDAO;
import com.example.cab302assignment.dao.SqliteRedactionResultDAO;
import com.example.cab302assignment.model.RiskAnalysis;
import com.example.cab302assignment.dao.RiskAnalysisDAO;
import com.example.cab302assignment.dao.SqliteRiskAnalysisDAO;

/**
 * Service class responsible for managing prompt-related persistence.
 *
 * Coordinates saving of:
 * - User prompts
 * - Redaction results
 * - AI risk analysis results
 *
 * Acts as a bridge between controllers and DAO layer,
 * ensuring all prompt-related data is stored consistently.
 */
public class PromptService {
    private final PromptDAO promptDAO;
    private final RedactionResultDAO redactionResultDAO;
    private final RiskAnalysisDAO riskAnalysisDAO; // Added RiskAnalysis dependency

    /**
     * Default constructor using SQLite DAO implementations
     * for prompts, redaction results, and risk analysis.
     */
    public PromptService() {
        this.promptDAO = new SqlitePromptDAO();
        this.redactionResultDAO = new SqliteRedactionResultDAO();
        this.riskAnalysisDAO = new SqliteRiskAnalysisDAO(); // Initialized
    }

    /**
     * Constructor allowing dependency injection of DAOs.
     *
     * @param promptDAO DAO for prompt persistence
     * @param redactionResultDAO DAO for redaction results
     * @param riskAnalysisDAO DAO for AI risk analysis results
     */
    public PromptService(PromptDAO promptDAO, RedactionResultDAO redactionResultDAO, RiskAnalysisDAO riskAnalysisDAO) {
        this.promptDAO = promptDAO;
        this.redactionResultDAO = redactionResultDAO;
        this.riskAnalysisDAO = riskAnalysisDAO;
    }

    /**
     * Saves a user prompt and its associated redaction result.
     *
     * Process:
     * 1. Retrieves current user and organisation from session
     * 2. Creates a Prompt entity using redacted text
     * 3. Persists prompt to database
     * 4. Links and saves the RedactionResult to the generated prompt
     *
     * Ensures prompt and redaction data remain linked in storage.
     *
     * @param redactionResult result containing original prompt and redacted text
     */
    public void savePromptAndResult(RedactionResult redactionResult) {
        int orgId = SessionManager.getCurrentOrgId();
        int userId = SessionManager.getCurrentUserId();

        Prompt prompt = new Prompt(orgId, userId, redactionResult.getRedactedText());

        promptDAO.addPrompt(prompt);

        redactionResult.setPromptId(prompt.getPromptId());
        redactionResultDAO.addResult(redactionResult);

        System.out.println("Prompt saved!");
    }

    /**
     * Saves AI-generated risk analysis to the database.
     *
     * Only persists the analysis if it is not null.
     *
     * @param riskAnalysis structured AI risk analysis result
     */
    public void saveRiskAnalysis(RiskAnalysis riskAnalysis) {
        if (riskAnalysis != null) {
            riskAnalysisDAO.addAnalysis(riskAnalysis);
        }

        System.out.println("Risk Analyses saved!");
    }

    /**
     * Returns the RiskAnalysisDAO instance used by this service.
     *
     * @return DAO responsible for risk analysis persistence
     */
    public RiskAnalysisDAO getRiskAnalysisDAO() {
        return this.riskAnalysisDAO;
    }
}