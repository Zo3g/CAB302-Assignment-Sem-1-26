package com.example.cab302assignment.dao;

import com.example.cab302assignment.model.RiskAnalysis;

import java.util.List;

public interface RiskAnalysisDAO {
    void addAnalysis(RiskAnalysis analysis);
    RiskAnalysis getByPromptId(int promptId);
    List<RiskAnalysis> getByUserId(int userId);
    void deleteByPromptId(int promptId);
}
