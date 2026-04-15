package com.example.cab302assignment.dao;

import com.example.cab302assignment.model.RedactionResult;

public interface RedactionResultDAO {
    void addResult(RedactionResult result);
    RedactionResult getByPromptId(int promptId);
    void deleteByPromptId(int promptId);
}
