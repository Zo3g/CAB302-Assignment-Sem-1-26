package com.example.cab302assignment;

import com.example.cab302assignment.dao.SqliteUserDAO;
import com.example.cab302assignment.db.DatabaseInitialiser;
import com.example.cab302assignment.model.MemberRiskSummary;
import com.example.cab302assignment.model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SqliteUserDAOTest {
    private Connection connection;
    private SqliteUserDAO userDAO;

    @BeforeEach
    void setUp() throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite::memory:");
        DatabaseInitialiser.createTables(connection);
        userDAO = new SqliteUserDAO(connection);
    }

    @AfterEach
    void tearDown() throws SQLException {
        connection.close();
    }

    @Test
    void testAddAndGetById() {
        User user = new User("test@example.com", "Test", "hashedpw");
        userDAO.addUser(user);
        assertTrue(user.getUserId() > 0);
        User retrieved = userDAO.getUserById(user.getUserId());
        assertNotNull(retrieved);
        assertEquals("test@example.com", retrieved.getEmail());
    }

    @Test
    void testGetByEmail() {
        userDAO.addUser(new User("alice@example.com", "Alice", "hashedpw"));
        User retrieved = userDAO.getUserByEmail("alice@example.com");
        assertNotNull(retrieved);
        assertEquals("alice@example.com", retrieved.getEmail());
    }

    @Test
    void testGetByEmailNotFound() {
        assertNull(userDAO.getUserByEmail("nonexistent@example.com"));
    }

    @Test
    void testGetByIdNotFound() {
        assertNull(userDAO.getUserById(999));
    }

    @Test
    void testGetAllUsers() {
        userDAO.addUser(new User("a@test.com", "A", "hash1"));
        userDAO.addUser(new User("b@test.com", "B", "hash2"));
        List<User> users = userDAO.getAllUsers();
        assertEquals(2, users.size());
    }

    @Test
    void testUpdateUser() {
        User user = new User("old@test.com", "Old", "oldhash");
        userDAO.addUser(user);
        user.setEmail("new@test.com");
        user.setPasswordHash("newhash");
        userDAO.updateUser(user);
        User updated = userDAO.getUserById(user.getUserId());
        assertEquals("new@test.com", updated.getEmail());
        assertEquals("newhash", updated.getPasswordHash());
    }

    @Test
    void testDeleteUser() {
        User user = new User("del@test.com", "Del", "hash");
        userDAO.addUser(user);
        int id = user.getUserId();
        userDAO.deleteUser(id);
        assertNull(userDAO.getUserById(id));
    }

    @Test
    void testDuplicateEmailRejected() {
        userDAO.addUser(new User("dup@test.com", "Dup", "hash"));
        User duplicate = new User("dup@test.com", "Dup", "hash");
        userDAO.addUser(duplicate);
        List<User> users = userDAO.getAllUsers();
        assertEquals(1, users.size());
    }

    @Test
    void testGetMemberRiskSummary() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            //SETUP
            // Two different organisations
            stmt.execute("INSERT INTO organisations (orgId, name) VALUES (1, 'Target Org')");
            stmt.execute("INSERT INTO organisations (orgId, name) VALUES (2, 'Other Org')");

            // Four test users
            stmt.execute("INSERT INTO users (userId, email, name, passwordHash) VALUES (1, 'active@test.com', 'John', 'hash')");
            stmt.execute("INSERT INTO users (userId, email, name, passwordHash) VALUES (2, 'inactive@test.com', 'June', 'hash')");
            stmt.execute("INSERT INTO users (userId, email, name, passwordHash) VALUES (3, 'other@test.com', 'Bob', 'hash')");
            stmt.execute("INSERT INTO users (userId, email, name, passwordHash) VALUES (4, 'noscore@test.com', 'Sarah', 'hash')");

            // Assign memberships
            stmt.execute("INSERT INTO organisation_memberships (userId, orgId, memberRole, active) VALUES (1, 1, 'MEMBER', 1)"); // Valid!
            stmt.execute("INSERT INTO organisation_memberships (userId, orgId, memberRole, active) VALUES (2, 1, 'MEMBER', 0)"); // Fails: Inactive
            stmt.execute("INSERT INTO organisation_memberships (userId, orgId, memberRole, active) VALUES (3, 2, 'MEMBER', 1)"); // Fails: Wrong Org
            stmt.execute("INSERT INTO organisation_memberships (userId, orgId, memberRole, active) VALUES (4, 1, 'MEMBER', 1)"); // Valid, but no score yet

            // Add a risk score ONLY for User 1
            stmt.execute("INSERT INTO user_risk_scores (userId, score) VALUES (1, 75.5)");
        }

        //Fetch the dashboard data for Org 1
        List<MemberRiskSummary> dashboardData = userDAO.getMemberRiskSummary(1);

        // TESTS

        // Tests that inactive users and users from another organisation are not included in the summary for Org 1
        assertEquals(2, dashboardData.size());

        // Tests that its the correct users, and that they are in the correct order (ordered by risk scores)
        assertEquals("John", dashboardData.get(0).getName());
        assertEquals("Sarah", dashboardData.get(1).getName());

        MemberRiskSummary john = dashboardData.get(0);
        MemberRiskSummary sarah = dashboardData.get(1);

        // Tests that the LEFT JOIN for risk scores works
        assertEquals(75.5, john.getRiskScore());

        // Tests that the COALESCE for missing data works
        assertEquals(0.0, sarah.getRiskScore());
    }
}
