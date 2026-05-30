package com.example.cab302assignment.dao;

import com.example.cab302assignment.model.UserRiskScore;

/**
 * DAO interface for user risk scores.
 *
 * <p>A user risk score is the running overall score we keep for each user,
 * based on all the prompts they've had analysed. This interface lets us save,
 * fetch the latest one, update it and delete it.</p>
 */
public interface UserRiskScoreDAO {
    /**
     * Adds a new risk score for a user.
     *
     * @param score the score to save
     */
    void addScore(UserRiskScore score);

    /**
     * Gets the most recent risk score for a user.
     *
     * @param userId the user's ID
     * @return their latest score, or null if they don't have one yet
     */
    UserRiskScore getLatestForUser(int userId);

    /**
     * Updates an existing risk score.
     *
     * @param score the score with the updated values (matched by ID)
     */
    void updateScore(UserRiskScore score);

    /**
     * Deletes a risk score by its ID.
     *
     * @param scoreId the ID of the score to delete
     */
    void deleteScore(int scoreId);
}
