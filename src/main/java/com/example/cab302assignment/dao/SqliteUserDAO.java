package com.example.cab302assignment.dao;

import com.example.cab302assignment.db.DatabaseConnection;
import com.example.cab302assignment.model.User;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * The real {@link UserDAO} that actually talks to the SQLite database.
 *
 * <p>Every method here runs an SQL statement against the users table using
 * prepared statements (so we don't have to worry about SQL injection). If
 * something goes wrong we just print the error and carry on rather than
 * crashing the whole app.</p>
 */
public class SqliteUserDAO implements UserDAO {
    /** The date format SQLite stores timestamps in, so we can parse them back. */
    private static final DateTimeFormatter DT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /** The database connection we run all our queries on. */
    private final Connection connection;

    /**
     * Default constructor - grabs the shared singleton database connection.
     * This is what the real app uses.
     */
    public SqliteUserDAO() {
        this.connection = DatabaseConnection.getInstance();
    }

    /**
     * Constructor that lets you pass in your own connection. Handy for tests
     * where we want to use a temporary in-memory database.
     *
     * @param connection the connection to use
     */
    public SqliteUserDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * Inserts a new user into the users table and then reads back the
     * auto-generated ID so the User object knows its own ID.
     *
     * @param user the user to add
     */
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

    /**
     * Looks up a user by ID.
     *
     * @param id the user's ID
     * @return the user, or null if nothing matched (or an error happened)
     */
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

    /**
     * Looks up a user by email (this is what login uses).
     *
     * @param email the email to search for
     * @return the user, or null if no one has that email
     */
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

    /**
     * Grabs every row from the users table and turns them into User objects.
     *
     * @return a list of all users (empty if there are none or something broke)
     */
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

    /**
     * Updates an existing user's email, name and password hash, matched by ID.
     *
     * @param user the user with the updated details
     */
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

    /**
     * Deletes the user with the given ID from the database.
     *
     * @param id the ID of the user to delete
     */
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


    /**
     * Helper that turns one row of a ResultSet into a User object. The
     * createdAt column can be null, so we check for that before parsing it.
     *
     * @param rs the result set, already positioned on the row we want
     * @return a User built from that row
     * @throws SQLException if reading a column goes wrong
     */
    private User mapUser(ResultSet rs) throws SQLException {
        String createdAtStr = rs.getString("createdAt");
        LocalDateTime createdAt = createdAtStr == null ? null : LocalDateTime.parse(createdAtStr, DT);
        return new User(rs.getInt("userId"), rs.getString("email"), rs.getString("name"), rs.getString("passwordHash"), createdAt);
    }
}
