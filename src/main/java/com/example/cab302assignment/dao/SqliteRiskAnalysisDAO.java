package com.example.cab302assignment.dao;

import com.example.cab302assignment.db.DatabaseConnection;
import com.example.cab302assignment.model.RiskAnalysis;
import com.example.cab302assignment.model.enums.RiskLevel;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * The real {@link RiskAnalysisDAO} backed by SQLite.
 *
 * <p>Handles the risk_analyses table. The risk level is stored as text (the
 * enum name) and the type counts map is stored using the same string format as
 * the redaction results, so we just reuse the serialise/deserialise helpers
 * from {@link SqliteRedactionResultDAO} rather than writing them twice.</p>
 */
public class SqliteRiskAnalysisDAO implements RiskAnalysisDAO {
    /** The timestamp format SQLite uses, for parsing analysedAt. */
    private static final DateTimeFormatter DT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /** The database connection used for all queries. */
    private final Connection connection;

    /** Default constructor - uses the shared singleton connection. */
    public SqliteRiskAnalysisDAO() { this.connection = DatabaseConnection.getInstance(); }

    /**
     * Constructor for passing in your own connection (used in tests).
     *
     * @param connection the connection to use
     */
    public SqliteRiskAnalysisDAO(Connection connection) { this.connection = connection; }

    /**
     * Inserts a new risk analysis (serialising the type counts to text) and
     * reads back the generated analysis ID.
     *
     * @param a the analysis to add
     */
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

    /**
     * Gets the risk analysis for a given prompt.
     *
     * @param promptId the ID of the prompt
     * @return the analysis for that prompt, or null if there isn't one
     */
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

    /**
     * Gets all the risk analyses for prompts that a particular user submitted.
     *
     * <p>It joins risk_analyses onto prompts so we can filter by the prompt's
     * userId, since the analyses table itself doesn't store the user.</p>
     *
     * @param userId the user's ID
     * @return a list of that user's risk analyses
     */
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

    /**
     * Deletes the risk analysis tied to a prompt.
     *
     * @param promptId the ID of the prompt whose analysis we want removed
     */
    @Override
    public void deleteByPromptId(int promptId) {
        try {
            PreparedStatement stmt = connection.prepareStatement("DELETE FROM risk_analyses WHERE promptId = ?");
            stmt.setInt(1, promptId);
            stmt.execute();
        } catch (SQLException ex) { System.err.println(ex); }
    }

    /**
     * Helper that builds a RiskAnalysis from a result set row. It turns the
     * risk level text back into the enum, rebuilds the type counts map, handles
     * a possibly-null analysedAt, and sets the analysis ID afterwards.
     *
     * @param rs the result set positioned on the row to read
     * @return a RiskAnalysis built from that row
     * @throws SQLException if a column can't be read
     */
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
