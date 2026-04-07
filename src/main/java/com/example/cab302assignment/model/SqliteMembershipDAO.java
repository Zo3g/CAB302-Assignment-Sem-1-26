package com.example.cab302assignment.model;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class SqliteMembershipDAO implements MembershipDAO {
    private Connection connection;

    public SqliteMembershipDAO() {
        connection = DatabaseConnection.getInstance();
    }

    @Override
    public void addMembership(Membership membership) {
        try {
            PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO memberships (user_id, organisation_id, role, active) VALUES (?, ?, ?, ?)"
            );
            statement.setInt(1, membership.getUserId());
            statement.setInt(2, membership.getOrganisationId());
            statement.setString(3, membership.getMemberRole().name());
            statement.setBoolean(4, membership.isActive());
            statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deactivateMember(int userId, int orgId) {
        try {
            PreparedStatement statement = connection.prepareStatement(
                "UPDATE memberships SET active = ? WHERE user_id = ? AND organisation_id = ?"
            );
            statement.setBoolean(1, false);
            statement.setInt(2, userId);
            statement.setInt(3, orgId);
            statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void activateMember(int userId, int orgId) {
        try {
            PreparedStatement statement = connection.prepareStatement(
                "UPDATE memberships SET active = ? WHERE user_id = ? AND organisation_id = ?"
            );
            statement.setBoolean(1, true);
            statement.setInt(2, userId);
            statement.setInt(3, orgId);
            statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateMemberRole(int userId, int orgId, MemberRole memberRole) {
        try {
            PreparedStatement statement = connection.prepareStatement(
                "UPDATE memberships SET role = ? WHERE user_id = ? AND organisation_id = ?"
            );
            statement.setString(1, memberRole.name());
            statement.setInt(2, userId);
            statement.setInt(3, orgId);
            statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Membership getMembership(int userId, int orgId) {
        try {
            PreparedStatement statement = connection.prepareStatement(
                "SELECT * FROM memberships WHERE user_id = ? AND organisation_id = ?"
            );
            statement.setInt(1, userId);
            statement.setInt(2, orgId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return mapMembership(resultSet);
            }
        } catch (SQLException ex) {
            System.err.println(ex);
        }
        return null;
    }

    @Override
    public List<Membership> getAllMembers(int orgId) {
        List<Membership> memberships = new ArrayList<>();
        try {
            PreparedStatement statement = connection.prepareStatement(
                "SELECT * FROM memberships WHERE organisation_id = ? AND active = true"
            );
            statement.setInt(1, orgId);

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                memberships.add(mapMembership(resultSet));
            }
        } catch (SQLException ex) {
            System.err.println(ex);
        }
        return memberships;
    }

    private Membership mapMembership(ResultSet resultSet) throws SQLException {
        MemberRole memberRole = MemberRole.valueOf(resultSet.getString("role"));
        String joinedAtStr = resultSet.getString("joined_at");
        LocalDateTime joinedAt = null;
        if (joinedAtStr != null) {
            joinedAt = LocalDateTime.parse(joinedAtStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }
        return new Membership(
            resultSet.getInt("user_id"),
            resultSet.getInt("organisation_id"),
            memberRole,
            resultSet.getBoolean("active"),
            joinedAt
        );
    }
}
