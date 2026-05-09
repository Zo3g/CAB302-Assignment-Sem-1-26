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

public class PromptService {
    private final PromptDAO promptDAO;
    private final RedactionResultDAO redactionResultDAO;
    private final RiskAnalysisDAO riskAnalysisDAO; // Added RiskAnalysis dependency

    public PromptService() {
        this.promptDAO = new SqlitePromptDAO();
        this.redactionResultDAO = new SqliteRedactionResultDAO();
        this.riskAnalysisDAO = new SqliteRiskAnalysisDAO(); // Initialized
    }

    public PromptService(PromptDAO promptDAO, RedactionResultDAO redactionResultDAO, RiskAnalysisDAO riskAnalysisDAO) {
        this.promptDAO = promptDAO;
        this.redactionResultDAO = redactionResultDAO;
        this.riskAnalysisDAO = riskAnalysisDAO;
    }

    public void savePromptAndResult(RedactionResult redactionResult) {
        int orgId = SessionManager.getCurrentOrgId();
        int userId = SessionManager.getCurrentUserId();

        Prompt prompt = new Prompt(orgId, userId, redactionResult.getRedactedText());

        promptDAO.addPrompt(prompt);

        redactionResult.setPromptId(prompt.getPromptId());
        redactionResultDAO.addResult(redactionResult);
    }

    public void saveRiskAnalysis(RiskAnalysis riskAnalysis) {
        if (riskAnalysis != null) {
            riskAnalysisDAO.addAnalysis(riskAnalysis);
        }
    }

    public RiskAnalysisDAO getRiskAnalysisDAO() {
        return this.riskAnalysisDAO;
    }
}