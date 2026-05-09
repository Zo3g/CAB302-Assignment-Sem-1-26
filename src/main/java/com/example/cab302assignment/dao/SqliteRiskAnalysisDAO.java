package com.example.cab302assignment.dao;

import com.example.cab302assignment.db.DatabaseConnection;
import com.example.cab302assignment.model.RiskAnalysis;
import com.example.cab302assignment.model.enums.RiskLevel;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class SqliteRiskAnalysisDAO implements RiskAnalysisDAO {
    private static final DateTimeFormatter DT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final Connection connection;

    public SqliteRiskAnalysisDAO() { this.connection = DatabaseConnection.getInstance(); }
    public SqliteRiskAnalysisDAO(Connection connection) { this.connection = connection; }

    @Override
    public void addAnalysis(RiskAnalysis a) {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                    "INSERT INTO risk_analyses (promptId, riskLevel, score, summary, typeCountsJson) VALUES (?, ?, ?, ?, ?)"
            );
            stmt.setInt(1, a.getPromptId());
            stmt.setString(2, a.getRiskLevel().name());
            stmt.setDouble(3, a.getScore());
            stmt.setString(4, a.getSummary());
            stmt.setString(5, SqliteRedactionResultDAO.serialise(a.getTypeCounts()));
            stmt.execute();
            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) a.setAnalysisId(keys.getInt(1));
        } catch (SQLException ex) { System.err.println(ex); }
    }

    @Override
    public RiskAnalysis getByPromptId(int promptId) {
        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM risk_analyses WHERE promptId = ?");
            stmt.setInt(1, promptId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return map(rs);
        } catch (SQLException ex) { System.err.println(ex); }
        return null;
    }

    @Override
    public List<RiskAnalysis> getByUserId(int userId) {
        List<RiskAnalysis> list = new ArrayList<>();
        try {
            PreparedStatement stmt = connection.prepareStatement(
                    "SELECT ra.* FROM risk_analyses ra JOIN prompts p ON ra.promptId = p.promptId WHERE p.userId = ?"
            );
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) list.add(map(rs));
        } catch (SQLException ex) { System.err.println(ex); }
        return list;
    }

    @Override
    public void deleteByPromptId(int promptId) {
        try {
            PreparedStatement stmt = connection.prepareStatement("DELETE FROM risk_analyses WHERE promptId = ?");
            stmt.setInt(1, promptId);
            stmt.execute();
        } catch (SQLException ex) { System.err.println(ex); }
    }

    private RiskAnalysis map(ResultSet rs) throws SQLException {
        String t = rs.getString("analysedAt");
        LocalDateTime analysedAt = t == null ? null : LocalDateTime.parse(t, DT);

        RiskAnalysis a = new RiskAnalysis(
                rs.getInt("promptId"),
                RiskLevel.valueOf(rs.getString("riskLevel")),
                rs.getDouble("score"),
                rs.getString("summary"),
                SqliteRedactionResultDAO.deserialise(rs.getString("typeCountsJson")),
                analysedAt
        );
        a.setAnalysisId(rs.getInt("analysisId"));
        return a;
    }
}