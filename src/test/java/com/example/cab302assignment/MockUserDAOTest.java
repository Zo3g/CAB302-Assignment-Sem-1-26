package com.example.cab302assignment;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class MockUserDAOTest {
    private UserDAO userDAO;

    @BeforeEach
    void setUp() {
        userDAO = new MockUserDAO();
    }

    @Test
    void testAddAndGetById() {
        User user = new User("test@example.com", "Test User", "hashedpw");
        userDAO.addUser(user);
        User retrieved = userDAO.getUserById(user.getId());
        assertNotNull(retrieved);
        assertEquals("test@example.com", retrieved.getEmail());
        assertEquals("Test User", retrieved.getFullName());
    }

    @Test
    void testGetByEmail() {
        User user = new User("alice@example.com", "Alice", "hashedpw");
        userDAO.addUser(user);
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
    void testAutoIncrementIds() {
        User user1 = new User("a@test.com", "A", "hash");
        User user2 = new User("b@test.com", "B", "hash");
        userDAO.addUser(user1);
        userDAO.addUser(user2);
        assertEquals(1, user1.getId());
        assertEquals(2, user2.getId());
    }
}
