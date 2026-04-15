package com.example.cab302assignment.dao;

import com.example.cab302assignment.model.UserRiskScore;

public interface UserRiskScoreDAO {
    void addScore(UserRiskScore score);
    UserRiskScore getLatestForUser(int userId);
    void updateScore(UserRiskScore score);
    void deleteScore(int scoreId);
}
