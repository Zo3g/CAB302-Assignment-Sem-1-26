package com.example.cab302assignment;

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
        Statement stmt = connection.createStatement();
        stmt.execute(
            "CREATE TABLE IF NOT EXISTS users ("
            + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
            + "email VARCHAR NOT NULL UNIQUE, "
            + "fullName VARCHAR NOT NULL, "
            + "passwordHash VARCHAR NOT NULL, "
            + "createdAt DATETIME DEFAULT CURRENT_TIMESTAMP"
            + ")"
        );
        userDAO = new SqliteUserDAO(connection);
    }

    @AfterEach
    void tearDown() throws SQLException {
        connection.close();
    }

    @Test
    void testAddAndGetById() {
        User user = new User("test@example.com", "Test User", "hashedpw");
        userDAO.addUser(user);
        assertTrue(user.getId() > 0);
        User retrieved = userDAO.getUserById(user.getId());
        assertNotNull(retrieved);
        assertEquals("test@example.com", retrieved.getEmail());
        assertEquals("Test User", retrieved.getFullName());
    }

    @Test
    void testGetByEmail() {
        userDAO.addUser(new User("alice@example.com", "Alice", "hashedpw"));
        User retrieved = userDAO.getUserByEmail("alice@example.com");
        assertNotNull(retrieved);
        assertEquals("Alice", retrieved.getFullName());
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
        User user = new User("old@test.com", "Old Name", "oldhash");
        userDAO.addUser(user);
        user.setFullName("New Name");
        user.setEmail("new@test.com");
        userDAO.updateUser(user);
        User updated = userDAO.getUserById(user.getId());
        assertEquals("New Name", updated.getFullName());
        assertEquals("new@test.com", updated.getEmail());
    }

    @Test
    void testDeleteUser() {
        User user = new User("del@test.com", "Delete Me", "hash");
        userDAO.addUser(user);
        int id = user.getId();
        userDAO.deleteUser(id);
        assertNull(userDAO.getUserById(id));
    }

    @Test
    void testDuplicateEmailRejected() {
        userDAO.addUser(new User("dup@test.com", "First", "hash"));
        User duplicate = new User("dup@test.com", "Second", "hash");
        userDAO.addUser(duplicate);
        List<User> users = userDAO.getAllUsers();
        assertEquals(1, users.size());
    }
}
