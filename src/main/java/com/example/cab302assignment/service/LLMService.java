package com.example.cab302assignment.service;

import com.example.cab302assignment.model.RedactionResult;
import com.example.cab302assignment.model.RiskAnalysis;
import com.example.cab302assignment.model.UserRiskScore;

import java.util.List;

public interface LLMService {
    String analysePromptRisk(String redactedText);
    UserRiskScore calculateUserRiskScore(int userId, List<RiskAnalysis> history);
}
