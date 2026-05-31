package com.example.cab302assignment.dao;

import com.example.cab302assignment.db.DatabaseConnection;
import com.example.cab302assignment.model.Ruleset;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * The real {@link RulesetDAO} backed by SQLite.
 *
 * <p>Handles the rulesets table. A ruleset is pretty lightweight - it's mostly
 * just a link to an organisation plus an updatedAt timestamp. When we update
 * one we let SQLite set updatedAt itself using CURRENT_TIMESTAMP.</p>
 */
public class SqliteRulesetDAO implements RulesetDAO {
    /** The timestamp format SQLite uses, for parsing updatedAt. */
    private static final DateTimeFormatter DT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /** The database connection used for all queries. */
    private final Connection connection;

    /** Default constructor - uses the shared singleton connection. */
    public SqliteRulesetDAO() { this.connection = DatabaseConnection.getInstance(); }

    /**
     * Constructor for passing in your own connection (used in tests).
     *
     * @param connection the connection to use
     */
    public SqliteRulesetDAO(Connection connection) { this.connection = connection; }

    /**
     * Inserts a new ruleset and reads back its auto-generated ID.
     *
     * @param r the ruleset to add
     */
    @Override
    public void addRuleset(Ruleset r) {
        try {
            PreparedStatement stmt = connection.prepareStatement("INSERT INTO rulesets (orgId) VALUES (?)");
            stmt.setInt(1, r.getOrgId());
            stmt.execute();
            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) r.setRulesetId(keys.getInt(1));
        } catch (SQLException ex) { System.err.println(ex); }
    }

    /**
     * Finds a ruleset by its ID.
     *
     * @param rulesetId the ruleset's ID
     * @return the ruleset, or null if not found
     */
    @Override
    public Ruleset getRulesetById(int rulesetId) {
        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM rulesets WHERE rulesetId = ?");
            stmt.setInt(1, rulesetId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return map(rs);
        } catch (SQLException ex) { System.err.println(ex); }
        return null;
    }

    /**
     * Finds the ruleset belonging to a given organisation.
     *
     * @param orgId the organisation's ID
     * @return that org's ruleset, or null if it doesn't have one
     */
    @Override
    public Ruleset getRulesetByOrg(int orgId) {
        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM rulesets WHERE orgId = ?");
            stmt.setInt(1, orgId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return map(rs);
        } catch (SQLException ex) { System.err.println(ex); }
        return null;
    }

    /**
     * Updates a ruleset's org link and bumps its updatedAt to now (handled by
     * SQLite's CURRENT_TIMESTAMP), matched by ID.
     *
     * @param r the ruleset with the updated info
     */
    @Override
    public void updateRuleset(Ruleset r) {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                "UPDATE rulesets SET orgId = ?, updatedAt = CURRENT_TIMESTAMP WHERE rulesetId = ?"
            );
            stmt.setInt(1, r.getOrgId());
            stmt.setInt(2, r.getRulesetId());
            stmt.execute();
        } catch (SQLException ex) { System.err.println(ex); }
    }

    /**
     * Deletes a ruleset by its ID.
     *
     * @param rulesetId the ID of the ruleset to delete
     */
    @Override
    public void deleteRuleset(int rulesetId) {
        try {
            PreparedStatement stmt = connection.prepareStatement("DELETE FROM rulesets WHERE rulesetId = ?");
            stmt.setInt(1, rulesetId);
            stmt.execute();
        } catch (SQLException ex) { System.err.println(ex); }
    }

    /**
     * Helper that builds a Ruleset from a result set row, handling a
     * possibly-null updatedAt.
     *
     * @param rs the result set positioned on the row to read
     * @return a Ruleset built from that row
     * @throws SQLException if a column can't be read
     */
    private Ruleset map(ResultSet rs) throws SQLException {
        String t = rs.getString("updatedAt");
        LocalDateTime updatedAt = t == null ? null : LocalDateTime.parse(t, DT);
        return new Ruleset(rs.getInt("rulesetId"), rs.getInt("orgId"), updatedAt);
    }
}
