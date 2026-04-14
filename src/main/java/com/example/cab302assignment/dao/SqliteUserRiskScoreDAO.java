package com.example.cab302assignment.dao;

import com.example.cab302assignment.db.DatabaseConnection;
import com.example.cab302assignment.model.UserRiskScore;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SqliteUserRiskScoreDAO implements UserRiskScoreDAO {
    private static final DateTimeFormatter DT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final Connection connection;

    public SqliteUserRiskScoreDAO() { this.connection = DatabaseConnection.getInstance(); }
    public SqliteUserRiskScoreDAO(Connection connection) { this.connection = connection; }

    @Override
    public void addScore(UserRiskScore s) {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                "INSERT INTO user_risk_scores (userId, score, totalPromptsAnalysed) VALUES (?, ?, ?)"
            );
            stmt.setInt(1, s.getUserId());
            stmt.setDouble(2, s.getScore());
            stmt.setInt(3, s.getTotalPromptsAnalysed());
            stmt.execute();
            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) s.setScoreId(keys.getInt(1));
        } catch (SQLException ex) { System.err.println(ex); }
    }

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

    @Override
    public void deleteScore(int scoreId) {
        try {
            PreparedStatement stmt = connection.prepareStatement("DELETE FROM user_risk_scores WHERE scoreId = ?");
            stmt.setInt(1, scoreId);
            stmt.execute();
        } catch (SQLException ex) { System.err.println(ex); }
    }

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
