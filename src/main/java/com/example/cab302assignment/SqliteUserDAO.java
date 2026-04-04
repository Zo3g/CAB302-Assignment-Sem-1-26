package com.example.cab302assignment;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class SqliteUserDAO implements UserDAO {
    private Connection connection;

    public SqliteUserDAO() {
        connection = DatabaseConnection.getInstance();
    }

    public SqliteUserDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void addUser(User user) {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                "INSERT INTO users (email, fullName, passwordHash) VALUES (?, ?, ?)"
            );
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getFullName());
            stmt.setString(3, user.getPasswordHash());
            stmt.execute();

            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) {
                user.setId(keys.getInt(1));
            }
        } catch (SQLException ex) {
            System.err.println(ex);
        }
    }

    @Override
    public User getUserById(int id) {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                "SELECT * FROM users WHERE id = ?"
            );
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapUser(rs);
            }
        } catch (SQLException ex) {
            System.err.println(ex);
        }
        return null;
    }

    @Override
    public User getUserByEmail(String email) {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                "SELECT * FROM users WHERE email = ?"
            );
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapUser(rs);
            }
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
            while (rs.next()) {
                users.add(mapUser(rs));
            }
        } catch (SQLException ex) {
            System.err.println(ex);
        }
        return users;
    }

    @Override
    public void updateUser(User user) {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                "UPDATE users SET email = ?, fullName = ?, passwordHash = ? WHERE id = ?"
            );
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getFullName());
            stmt.setString(3, user.getPasswordHash());
            stmt.setInt(4, user.getId());
            stmt.execute();
        } catch (SQLException ex) {
            System.err.println(ex);
        }
    }

    @Override
    public void deleteUser(int id) {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                "DELETE FROM users WHERE id = ?"
            );
            stmt.setInt(1, id);
            stmt.execute();
        } catch (SQLException ex) {
            System.err.println(ex);
        }
    }

    private User mapUser(ResultSet rs) throws SQLException {
        String createdAtStr = rs.getString("createdAt");
        LocalDateTime createdAt = null;
        if (createdAtStr != null) {
            createdAt = LocalDateTime.parse(createdAtStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }
        return new User(
            rs.getInt("id"),
            rs.getString("email"),
            rs.getString("fullName"),
            rs.getString("passwordHash"),
            createdAt
        );
    }
}
