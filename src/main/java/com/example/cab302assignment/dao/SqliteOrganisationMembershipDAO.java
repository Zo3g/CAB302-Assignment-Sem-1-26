package com.example.cab302assignment.dao;

import com.example.cab302assignment.db.DatabaseConnection;
import com.example.cab302assignment.model.OrganisationMembership;
import com.example.cab302assignment.model.enums.MemberRole;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class SqliteOrganisationMembershipDAO implements OrganisationMembershipDAO {
    private static final DateTimeFormatter DT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final Connection connection;

    public SqliteOrganisationMembershipDAO() { this.connection = DatabaseConnection.getInstance(); }
    public SqliteOrganisationMembershipDAO(Connection connection) { this.connection = connection; }

    @Override
    public void addMembership(OrganisationMembership m) {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                "INSERT INTO organisation_memberships (userId, orgId, memberRole, active) VALUES (?, ?, ?, ?)"
            );
            stmt.setInt(1, m.getUserId());
            stmt.setInt(2, m.getOrgId());
            stmt.setString(3, m.getMemberRole().name());
            stmt.setInt(4, m.isActive() ? 1 : 0);
            stmt.execute();
        } catch (SQLException ex) { System.err.println(ex); }
    }

    @Override
    public OrganisationMembership getMembership(int userId, int orgId) {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                "SELECT * FROM organisation_memberships WHERE userId = ? AND orgId = ?"
            );
            stmt.setInt(1, userId);
            stmt.setInt(2, orgId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return map(rs);
        } catch (SQLException ex) { System.err.println(ex); }
        return null;
    }

    @Override
    public List<OrganisationMembership> getMembershipsForUser(int userId) {
        List<OrganisationMembership> list = new ArrayList<>();
        try {
            PreparedStatement stmt = connection.prepareStatement(
                "SELECT * FROM organisation_memberships WHERE userId = ?"
            );
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) list.add(map(rs));
        } catch (SQLException ex) { System.err.println(ex); }
        return list;
    }

    @Override
    public List<OrganisationMembership> getMembershipsForOrg(int orgId) {
        List<OrganisationMembership> list = new ArrayList<>();
        try {
            PreparedStatement stmt = connection.prepareStatement(
                "SELECT * FROM organisation_memberships WHERE orgId = ?"
            );
            stmt.setInt(1, orgId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) list.add(map(rs));
        } catch (SQLException ex) { System.err.println(ex); }
        return list;
    }

    @Override
    public void updateMembership(OrganisationMembership m) {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                "UPDATE organisation_memberships SET memberRole = ?, active = ? WHERE userId = ? AND orgId = ?"
            );
            stmt.setString(1, m.getMemberRole().name());
            stmt.setInt(2, m.isActive() ? 1 : 0);
            stmt.setInt(3, m.getUserId());
            stmt.setInt(4, m.getOrgId());
            stmt.execute();
        } catch (SQLException ex) { System.err.println(ex); }
    }

    @Override
    public void deleteMembership(int userId, int orgId) {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                "DELETE FROM organisation_memberships WHERE userId = ? AND orgId = ?"
            );
            stmt.setInt(1, userId);
            stmt.setInt(2, orgId);
            stmt.execute();
        } catch (SQLException ex) { System.err.println(ex); }
    }

    private OrganisationMembership map(ResultSet rs) throws SQLException {
        String joinedAtStr = rs.getString("joinedAt");
        LocalDateTime joinedAt = joinedAtStr == null ? null : LocalDateTime.parse(joinedAtStr, DT);
        return new OrganisationMembership(
            rs.getInt("userId"),
            rs.getInt("orgId"),
            MemberRole.valueOf(rs.getString("memberRole")),
            rs.getInt("active") == 1,
            joinedAt
        );
    }
}
