package com.example.cab302assignment.dao;

import com.example.cab302assignment.model.Prompt;

import java.util.List;

public interface PromptDAO {
    void addPrompt(Prompt prompt);
    Prompt getPromptById(int promptId);
    List<Prompt> getPromptsByUser(int userId);
    List<Prompt> getPromptsByOrg(int orgId);
    void deletePrompt(int promptId);
}
