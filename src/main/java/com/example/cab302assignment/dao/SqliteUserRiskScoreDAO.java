package com.example.cab302assignment.dao;

import com.example.cab302assignment.db.DatabaseConnection;
import com.example.cab302assignment.model.UserRiskScore;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * The real {@link UserRiskScoreDAO} backed by SQLite.
 *
 * <p>Handles the user_risk_scores table, which keeps each user's overall risk
 * score. When adding a score we set lastUpdated ourselves (falling back to
 * "now" if it wasn't given), and when updating we let SQLite set it with
 * CURRENT_TIMESTAMP. The getLatestForUser method orders by lastUpdated so we
 * always get the freshest one.</p>
 */
public class SqliteUserRiskScoreDAO implements UserRiskScoreDAO {
    /** The timestamp format SQLite uses, for formatting and parsing lastUpdated. */
    private static final DateTimeFormatter DT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /** The database connection used for all queries. */
    private final Connection connection;

    /** Default constructor - uses the shared singleton connection. */
    public SqliteUserRiskScoreDAO() { this.connection = DatabaseConnection.getInstance(); }

    /**
     * Constructor for passing in your own connection (used in tests).
     *
     * @param connection the connection to use
     */
    public SqliteUserRiskScoreDAO(Connection connection) { this.connection = connection; }

    /**
     * Inserts a new risk score. If the score doesn't already have a
     * lastUpdated time we just use the current time instead. Reads back the
     * generated score ID afterwards.
     *
     * @param s the score to add
     */
    @Override
    public void addScore(UserRiskScore s) {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                "INSERT INTO user_risk_scores (userId, score, totalPromptsAnalysed, lastUpdated) VALUES (?, ?, ?, ?)"
            );
            stmt.setInt(1, s.getUserId());
            stmt.setDouble(2, s.getScore());
            stmt.setInt(3, s.getTotalPromptsAnalysed());
            stmt.setString(4, s.getLastUpdated() != null ? s.getLastUpdated().format(DT) : LocalDateTime.now().format(DT));
            stmt.executeUpdate();
            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) s.setScoreId(keys.getInt(1));
        } catch (SQLException ex) { System.err.println(ex); }
    }

    /**
     * Gets a user's most recent risk score by ordering on lastUpdated and only
     * keeping the top row.
     *
     * @param userId the user's ID
     * @return their latest score, or null if they don't have one yet
     */
    @Override
    public UserRiskScore getLatestForUser(int userId) {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                "SELECT * FROM user_risk_scores WHERE userId = ? ORDER BY lastUpdated DESC LIMIT 1"
            );
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return map(rs);
        } catch (SQLException ex) { System.err.println(ex); }
        return null;
    }

    /**
     * Updates an existing score's value and prompt count, and bumps
     * lastUpdated to now using CURRENT_TIMESTAMP. Matched by score ID.
     *
     * @param s the score with the updated values
     */
    @Override
    public void updateScore(UserRiskScore s) {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                "UPDATE user_risk_scores SET score = ?, totalPromptsAnalysed = ?, lastUpdated = CURRENT_TIMESTAMP WHERE scoreId = ?"
            );
            stmt.setDouble(1, s.getScore());
            stmt.setInt(2, s.getTotalPromptsAnalysed());
            stmt.setInt(3, s.getScoreId());
            stmt.execute();
        } catch (SQLException ex) { System.err.println(ex); }
    }

    /**
     * Deletes a risk score by its ID.
     *
     * @param scoreId the ID of the score to delete
     */
    @Override
    public void deleteScore(int scoreId) {
        try {
            PreparedStatement stmt = connection.prepareStatement("DELETE FROM user_risk_scores WHERE scoreId = ?");
            stmt.setInt(1, scoreId);
            stmt.execute();
        } catch (SQLException ex) { System.err.println(ex); }
    }

    /**
     * Helper that builds a UserRiskScore from a result set row, handling a
     * possibly-null lastUpdated and setting the score ID afterwards.
     *
     * @param rs the result set positioned on the row to read
     * @return a UserRiskScore built from that row
     * @throws SQLException if a column can't be read
     */
    private UserRiskScore map(ResultSet rs) throws SQLException {
        String t = rs.getString("lastUpdated");
        LocalDateTime lastUpdated = t == null ? null : LocalDateTime.parse(t, DT);
        UserRiskScore s = new UserRiskScore(
            rs.getInt("userId"),
            rs.getDouble("score"),
            rs.getInt("totalPromptsAnalysed"),
            lastUpdated
        );
        s.setScoreId(rs.getInt("scoreId"));
        return s;
    }
}
