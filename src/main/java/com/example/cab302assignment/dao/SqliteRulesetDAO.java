package com.example.cab302assignment.dao;

import com.example.cab302assignment.db.DatabaseConnection;
import com.example.cab302assignment.model.Ruleset;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SqliteRulesetDAO implements RulesetDAO {
    private static final DateTimeFormatter DT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final Connection connection;

    public SqliteRulesetDAO() { this.connection = DatabaseConnection.getInstance(); }
    public SqliteRulesetDAO(Connection connection) { this.connection = connection; }

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

    @Override
    public void deleteRuleset(int rulesetId) {
        try {
            PreparedStatement stmt = connection.prepareStatement("DELETE FROM rulesets WHERE rulesetId = ?");
            stmt.setInt(1, rulesetId);
            stmt.execute();
        } catch (SQLException ex) { System.err.println(ex); }
    }

    private Ruleset map(ResultSet rs) throws SQLException {
        String t = rs.getString("updatedAt");
        LocalDateTime updatedAt = t == null ? null : LocalDateTime.parse(t, DT);
        return new Ruleset(rs.getInt("rulesetId"), rs.getInt("orgId"), updatedAt);
    }
}
