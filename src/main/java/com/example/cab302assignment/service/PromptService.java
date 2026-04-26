package com.example.cab302assignment.service;

import com.example.cab302assignment.model.Prompt;
import com.example.cab302assignment.dao.PromptDAO;
import com.example.cab302assignment.dao.SqlitePromptDAO;
import com.example.cab302assignment.model.RedactionResult;
import com.example.cab302assignment.dao.RedactionResultDAO;
import com.example.cab302assignment.dao.SqliteRedactionResultDAO;

public class PromptService {
    private final PromptDAO promptDAO;
    private final RedactionResultDAO redactionResultDAO;

    public PromptService() {
        this.promptDAO = new SqlitePromptDAO();
        this.redactionResultDAO = new SqliteRedactionResultDAO();
    }

    public PromptService(PromptDAO promptDAO, RedactionResultDAO redactionResultDAO) {
        this.promptDAO = promptDAO;
        this.redactionResultDAO = redactionResultDAO;
    }

    public void savePromptAndResult(RedactionResult redactionResult) {
        int orgId = SessionManager.getCurrentOrgId();
        int userId = SessionManager.getCurrentUserId();

        Prompt prompt = new Prompt(orgId, userId, redactionResult.getRedactedText());

        promptDAO.addPrompt(prompt);

        redactionResult.setPromptId(prompt.getPromptId());
        redactionResultDAO.addResult(redactionResult);
    }
}
