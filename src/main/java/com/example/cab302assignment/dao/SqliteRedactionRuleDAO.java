package com.example.cab302assignment.dao;

import com.example.cab302assignment.db.DatabaseConnection;
import com.example.cab302assignment.model.RedactionRule;
import com.example.cab302assignment.model.enums.SensitiveDataType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * The real {@link RedactionRuleDAO} backed by SQLite.
 *
 * <p>Handles the redaction_rules table. Each rule has its data type stored as
 * text (the enum's name) and its enabled flag stored as 1 or 0, since SQLite
 * doesn't have a proper boolean type.</p>
 */
public class SqliteRedactionRuleDAO implements RedactionRuleDAO {
    /** The database connection used for all queries. */
    private final Connection connection;

    /** Default constructor - uses the shared singleton connection. */
    public SqliteRedactionRuleDAO() { this.connection = DatabaseConnection.getInstance(); }

    /**
     * Constructor for passing in your own connection (used in tests).
     *
     * @param connection the connection to use
     */
    public SqliteRedactionRuleDAO(Connection connection) { this.connection = connection; }

    /**
     * Inserts a new rule and reads back its auto-generated ID.
     *
     * @param r the rule to add
     */
    @Override
    public void addRule(RedactionRule r) {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                "INSERT INTO redaction_rules (rulesetId, name, regexPattern, dataType, enabled, placeholderFormat) "
                + "VALUES (?, ?, ?, ?, ?, ?)"
            );
            stmt.setInt(1, r.getRulesetId());
            stmt.setString(2, r.getName());
            stmt.setString(3, r.getRegexPattern());
            stmt.setString(4, r.getDataType().name());
            stmt.setInt(5, r.isEnabled() ? 1 : 0);
            stmt.setString(6, r.getPlaceholderFormat());
            stmt.execute();
            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) r.setRuleId(keys.getInt(1));
        } catch (SQLException ex) { System.err.println(ex); }
    }

    /**
     * Finds a single rule by its ID.
     *
     * @param ruleId the rule's ID
     * @return the rule, or null if not found
     */
    @Override
    public RedactionRule getRuleById(int ruleId) {
        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM redaction_rules WHERE ruleId = ?");
            stmt.setInt(1, ruleId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return map(rs);
        } catch (SQLException ex) { System.err.println(ex); }
        return null;
    }

    /**
     * Gets all the rules that belong to a given ruleset.
     *
     * @param rulesetId the ruleset's ID
     * @return a list of rules in that ruleset
     */
    @Override
    public List<RedactionRule> getRulesByRuleset(int rulesetId) {
        List<RedactionRule> list = new ArrayList<>();
        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM redaction_rules WHERE rulesetId = ?");
            stmt.setInt(1, rulesetId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) list.add(map(rs));
        } catch (SQLException ex) { System.err.println(ex); }
        return list;
    }

    /**
     * Updates an existing rule's details, matched by ID.
     *
     * @param r the rule with the updated info
     */
    @Override
    public void updateRule(RedactionRule r) {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                "UPDATE redaction_rules SET name = ?, regexPattern = ?, dataType = ?, enabled = ?, placeholderFormat = ? WHERE ruleId = ?"
            );
            stmt.setString(1, r.getName());
            stmt.setString(2, r.getRegexPattern());
            stmt.setString(3, r.getDataType().name());
            stmt.setInt(4, r.isEnabled() ? 1 : 0);
            stmt.setString(5, r.getPlaceholderFormat());
            stmt.setInt(6, r.getRuleId());
            stmt.execute();
        } catch (SQLException ex) { System.err.println(ex); }
    }

    /**
     * Deletes a rule by its ID.
     *
     * @param ruleId the ID of the rule to delete
     */
    @Override
    public void deleteRule(int ruleId) {
        try {
            PreparedStatement stmt = connection.prepareStatement("DELETE FROM redaction_rules WHERE ruleId = ?");
            stmt.setInt(1, ruleId);
            stmt.execute();
        } catch (SQLException ex) { System.err.println(ex); }
    }

    /**
     * Helper that builds a RedactionRule from a result set row, converting the
     * data type text back into the enum and the enabled int back into a boolean.
     *
     * @param rs the result set positioned on the row to read
     * @return a RedactionRule built from that row
     * @throws SQLException if a column can't be read
     */
    private RedactionRule map(ResultSet rs) throws SQLException {
        return new RedactionRule(
            rs.getInt("ruleId"),
            rs.getInt("rulesetId"),
            rs.getString("name"),
            rs.getString("regexPattern"),
            SensitiveDataType.valueOf(rs.getString("dataType")),
            rs.getInt("enabled") == 1,
            rs.getString("placeholderFormat")
        );
    }
}
