package com.example.cab302assignment.dao;

import com.example.cab302assignment.db.DatabaseConnection;
import com.example.cab302assignment.model.Prompt;
import com.example.cab302assignment.model.PromptHistorySummary;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class SqlitePromptDAO implements PromptDAO {
    private static final DateTimeFormatter DT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final Connection connection;

    public SqlitePromptDAO() { this.connection = DatabaseConnection.getInstance(); }
    public SqlitePromptDAO(Connection connection) { this.connection = connection; }

    @Override
    public void addPrompt(Prompt p) {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                "INSERT INTO prompts (orgId, userId, redactedText) VALUES (?, ?, ?)"
            );
            stmt.setInt(1, p.getOrgId());
            stmt.setInt(2, p.getUserId());
            stmt.setString(3, p.getRedactedText());
            stmt.execute();
            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) p.setPromptId(keys.getInt(1));
        } catch (SQLException ex) { System.err.println(ex); }
    }

    @Override
    public Prompt getPromptById(int promptId) {
        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM prompts WHERE promptId = ?");
            stmt.setInt(1, promptId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return map(rs);
        } catch (SQLException ex) { System.err.println(ex); }
        return null;
    }

    @Override
    public List<Prompt> getPromptsByUser(int userId) {
        return listBy("userId", userId);
    }

    @Override
    public List<Prompt> getPromptsByOrg(int orgId) {
        return listBy("orgId", orgId);
    }

    @Override
    public void deletePrompt(int promptId) {
        try {
            PreparedStatement stmt = connection.prepareStatement("DELETE FROM prompts WHERE promptId = ?");
            stmt.setInt(1, promptId);
            stmt.execute();
        } catch (SQLException ex) { System.err.println(ex); }
    }

    public List<PromptHistorySummary> getHistoryForUser(int userId) {
        List<PromptHistorySummary> history = new ArrayList<>();

        try {
            PreparedStatement stmt = connection.prepareStatement("""
            SELECT p.submittedAt, p.redactedText, 
            COALESCE(ra.riskLevel, 'PENDING') AS riskLevel, 
            COALESCE(ra.score, 0.0) AS score, 
            COALESCE(rr.totalDetections, 0) AS totalDetections
            FROM prompts p
            LEFT JOIN risk_analyses ra ON p.promptId = ra.promptId
            LEFT JOIN redaction_results rr ON p.promptId = rr.promptId
            WHERE p.userId = ?
            ORDER BY p.submittedAt DESC
        """);
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                history.add(new PromptHistorySummary(
                        rs.getString("submittedAt"),
                        rs.getString("redactedText"),
                        rs.getString("riskLevel"),
                        rs.getDouble("score"),
                        rs.getInt("totalDetections")
                ));
            }
        } catch (SQLException ex) { System.err.println(ex);}
        return history;
    }

    private List<Prompt> listBy(String column, int value) {
        List<Prompt> list = new ArrayList<>();
        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM prompts WHERE " + column + " = ?");
            stmt.setInt(1, value);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) list.add(map(rs));
        } catch (SQLException ex) { System.err.println(ex); }
        return list;
    }

    private Prompt map(ResultSet rs) throws SQLException {
        String t = rs.getString("submittedAt");
        LocalDateTime submittedAt = t == null ? null : LocalDateTime.parse(t, DT);
        return new Prompt(
            rs.getInt("promptId"),
            rs.getInt("orgId"),
            rs.getInt("userId"),
            rs.getString("redactedText"),
            submittedAt
        );
    }
}
