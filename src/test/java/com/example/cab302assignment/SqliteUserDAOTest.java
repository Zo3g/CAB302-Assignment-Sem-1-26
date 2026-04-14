package com.example.cab302assignment;

import com.example.cab302assignment.dao.SqliteUserDAO;
import com.example.cab302assignment.db.DatabaseInitialiser;
import com.example.cab302assignment.model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
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
}
