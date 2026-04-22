//package com.example.cab302assignment.service;
//
//import com.example.cab302assignment.model.RedactionResult;
//import com.example.cab302assignment.model.RiskAnalysis;
//import com.example.cab302assignment.model.UserRiskScore;
//import com.example.cab302assignment.model.enums.RiskLevel;
//
//import java.time.LocalDateTime;
//import java.util.List;
//
//public class StubLLMService implements LLMService {
//
//    @Override
//    public RiskAnalysis analysePromptRisk(String redactedText, RedactionResult result) {
//        int detections = result == null ? 0 : result.getTotalDetections();
//        RiskLevel level;
//        if (detections == 0) level = RiskLevel.LOW;
//        else if (detections <= 2) level = RiskLevel.MEDIUM;
//        else if (detections <= 5) level = RiskLevel.HIGH;
//        else level = RiskLevel.CRITICAL;
//
//        String summary = "Stub analysis — detected " + detections + " sensitive item(s).";
//        return new RiskAnalysis(
//                0,
//                level,
//                summary,
//                result == null ? null : result.getTypeCounts(),
//                LocalDateTime.now()
//        );
//    }
//
//    @Override
//    public UserRiskScore calculateUserRiskScore(int userId, List<RiskAnalysis> history) {
//        UserRiskScore score = new UserRiskScore(userId, 0.0, 0, LocalDateTime.now());
//        score.recalculate(history);
//        return score;
//    }
//}
