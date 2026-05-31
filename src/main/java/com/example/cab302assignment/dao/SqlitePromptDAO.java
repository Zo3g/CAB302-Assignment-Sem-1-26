package com.example.cab302assignment.dao;

import com.example.cab302assignment.db.DatabaseConnection;
import com.example.cab302assignment.model.Prompt;
import com.example.cab302assignment.model.PromptHistorySummary;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * The real {@link PromptDAO} backed by SQLite.
 *
 * <p>Handles the prompts table. We only ever store the redacted text (never
 * the original), which keeps the sensitive stuff out of the database. There's
 * also a method here that joins prompts up with their risk analysis and
 * redaction result to build the dashboard history.</p>
 */
public class SqlitePromptDAO implements PromptDAO {
    /** The timestamp format SQLite uses, for parsing submittedAt. */
    private static final DateTimeFormatter DT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /** The database connection used for all queries. */
    private final Connection connection;

    /** Default constructor - uses the shared singleton connection. */
    public SqlitePromptDAO() { this.connection = DatabaseConnection.getInstance(); }

    /**
     * Constructor for passing in your own connection (used in tests).
     *
     * @param connection the connection to use
     */
    public SqlitePromptDAO(Connection connection) { this.connection = connection; }

    /**
     * Inserts a new prompt and reads back its auto-generated ID.
     *
     * @param p the prompt to add
     */
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

    /**
     * Finds a single prompt by its ID.
     *
     * @param promptId the prompt's ID
     * @return the prompt, or null if not found
     */
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

    /**
     * Gets all the prompts submitted by a user. Uses the listBy helper so we
     * don't repeat the same query code.
     *
     * @param userId the user's ID
     * @return a list of that user's prompts
     */
    @Override
    public List<Prompt> getPromptsByUser(int userId) {
        return listBy("userId", userId);
    }

    /**
     * Gets all the prompts belonging to an organisation.
     *
     * @param orgId the organisation's ID
     * @return a list of prompts for that org
     */
    @Override
    public List<Prompt> getPromptsByOrg(int orgId) {
        return listBy("orgId", orgId);
    }

    /**
     * Deletes a prompt by its ID.
     *
     * @param promptId the ID of the prompt to delete
     */
    @Override
    public void deletePrompt(int promptId) {
        try {
            PreparedStatement stmt = connection.prepareStatement("DELETE FROM prompts WHERE promptId = ?");
            stmt.setInt(1, promptId);
            stmt.execute();
        } catch (SQLException ex) { System.err.println(ex); }
    }

    /**
     * Builds the prompt history for the dashboard table.
     *
     * <p>This left-joins each prompt with its risk analysis and redaction
     * result, so even prompts that haven't been analysed yet still show up
     * (they get defaults like 'PENDING' and 0 thanks to COALESCE). Results
     * come back newest first.</p>
     *
     * @param userId the user's ID
     * @return a list of history summary rows for that user, newest first
     */
    @Override
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

    /**
     * Shared helper for the "get prompts by some column" queries. We pass the
     * column name in directly (it's only ever called with our own hard-coded
     * values, never user input, so it's safe) and the value as a parameter.
     *
     * @param column the column to filter on, e.g. "userId" or "orgId"
     * @param value the value that column should equal
     * @return a list of matching prompts
     */
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

    /**
     * Helper that turns one result set row into a Prompt object, handling a
     * possibly-null submittedAt.
     *
     * @param rs the result set positioned on the row to read
     * @return a Prompt built from that row
     * @throws SQLException if a column can't be read
     */
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
