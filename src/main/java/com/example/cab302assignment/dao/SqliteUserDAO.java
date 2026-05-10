package com.example.cab302assignment.dao;

import com.example.cab302assignment.db.DatabaseConnection;
import com.example.cab302assignment.model.MemberRiskSummary;
import com.example.cab302assignment.model.User;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class SqliteUserDAO implements UserDAO {
    private static final DateTimeFormatter DT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final Connection connection;

    public SqliteUserDAO() {
        this.connection = DatabaseConnection.getInstance();
    }

    public SqliteUserDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void addUser(User user) {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                "INSERT INTO users (email, name, passwordHash) VALUES (?, ?, ?)"
            );
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getName());
            stmt.setString(3, user.getPasswordHash());
            stmt.execute();

            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) {
                user.setUserId(keys.getInt(1));
            }
        } catch (SQLException ex) {
            System.err.println(ex);
        }
    }

    @Override
    public User getUserById(int id) {
        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM users WHERE userId = ?");
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return mapUser(rs);
        } catch (SQLException ex) {
            System.err.println(ex);
        }
        return null;
    }

    @Override
    public User getUserByEmail(String email) {
        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM users WHERE email = ?");
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return mapUser(rs);
        } catch (SQLException ex) {
            System.err.println(ex);
        }
        return null;
    }

    @Override
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM users");
            while (rs.next()) users.add(mapUser(rs));
        } catch (SQLException ex) {
            System.err.println(ex);
        }
        return users;
    }

    @Override
    public void updateUser(User user) {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                "UPDATE users SET email = ?, name = ?, passwordHash = ? WHERE userId = ?"
            );
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getName());
            stmt.setString(3, user.getPasswordHash());
            stmt.setInt(4, user.getUserId());
            stmt.execute();
        } catch (SQLException ex) {
            System.err.println(ex);
        }
    }

    @Override
    public void deleteUser(int id) {
        try {
            PreparedStatement stmt = connection.prepareStatement("DELETE FROM users WHERE userId = ?");
            stmt.setInt(1, id);
            stmt.execute();
        } catch (SQLException ex) {
            System.err.println(ex);
        }
    }

    @Override
    public List<MemberRiskSummary> getMemberRiskSummary(int orgId) {
        List<MemberRiskSummary> summaries = new ArrayList<>();

        try {
            PreparedStatement stmt = connection.prepareStatement("""
            SELECT u.userId, u.name, COALESCE(urs.score, 0.0) AS riskScore
            FROM users u
            JOIN organisation_memberships om ON u.userId = om.userId
            LEFT JOIN user_risk_scores urs ON u.userId = urs.userId
            WHERE om.orgId = ? AND om.active = 1
        """);
            stmt.setInt(1, orgId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                summaries.add(new MemberRiskSummary(
                        rs.getInt("userId"),
                        rs.getString("name"),
                        rs.getDouble("riskScore")
                ));
            }
        } catch (SQLException ex) {
            System.err.println(ex);
        }

        return summaries;
    }

    private User mapUser(ResultSet rs) throws SQLException {
        String createdAtStr = rs.getString("createdAt");
        LocalDateTime createdAt = createdAtStr == null ? null : LocalDateTime.parse(createdAtStr, DT);
        return new User(rs.getInt("userId"), rs.getString("email"), rs.getString("name"), rs.getString("passwordHash"), createdAt);
    }
}
