package com.example.cab302assignment.dao;

import com.example.cab302assignment.db.DatabaseConnection;
import com.example.cab302assignment.model.Organisation;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class SqliteOrganisationDAO implements OrganisationDAO {
    private static final DateTimeFormatter DT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final Connection connection;

    public SqliteOrganisationDAO() { this.connection = DatabaseConnection.getInstance(); }
    public SqliteOrganisationDAO(Connection connection) { this.connection = connection; }

    @Override
    public void addOrganisation(Organisation org) {
        try {
            PreparedStatement stmt = connection.prepareStatement("INSERT INTO organisations (name) VALUES (?)");
            stmt.setString(1, org.getName());
            stmt.execute();
            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) org.setOrgId(keys.getInt(1));
        } catch (SQLException ex) { System.err.println(ex); }
    }

    @Override
    public Organisation getOrganisationById(int orgId) {
        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM organisations WHERE orgId = ?");
            stmt.setInt(1, orgId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return map(rs);
        } catch (SQLException ex) { System.err.println(ex); }
        return null;
    }

    @Override
    public Organisation getOrganisationByName(String name) {
        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM organisations WHERE name = ?");
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return map(rs);
        } catch (SQLException ex) { System.err.println(ex); }
        return null;
    }

    @Override
    public List<Organisation> getAllOrganisations() {
        List<Organisation> orgs = new ArrayList<>();
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM organisations");
            while (rs.next()) orgs.add(map(rs));
        } catch (SQLException ex) { System.err.println(ex); }
        return orgs;
    }

    @Override
    public void updateOrganisation(Organisation org) {
        try {
            PreparedStatement stmt = connection.prepareStatement("UPDATE organisations SET name = ? WHERE orgId = ?");
            stmt.setString(1, org.getName());
            stmt.setInt(2, org.getOrgId());
            stmt.execute();
        } catch (SQLException ex) { System.err.println(ex); }
    }

    @Override
    public void deleteOrganisation(int orgId) {
        try {
            PreparedStatement stmt = connection.prepareStatement("DELETE FROM organisations WHERE orgId = ?");
            stmt.setInt(1, orgId);
            stmt.execute();
        } catch (SQLException ex) { System.err.println(ex); }
    }

    private Organisation map(ResultSet rs) throws SQLException {
        String createdAtStr = rs.getString("createdAt");
        LocalDateTime createdAt = createdAtStr == null ? null : LocalDateTime.parse(createdAtStr, DT);
        return new Organisation(rs.getInt("orgId"), rs.getString("name"), createdAt);
    }
}
