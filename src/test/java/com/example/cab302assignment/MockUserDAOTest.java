package com.example.cab302assignment;

import com.example.cab302assignment.dao.MockUserDAO;
import com.example.cab302assignment.dao.UserDAO;
import com.example.cab302assignment.model.User;
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
        User user = new User("test@example.com", "Test", "hashedpw");
        userDAO.addUser(user);
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
        userDAO.updateUser(user);
        User updated = userDAO.getUserById(user.getUserId());
        assertEquals("new@test.com", updated.getEmail());
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
    void testAutoIncrementIds() {
        User user1 = new User("a@test.com", "A", "hash");
        User user2 = new User("b@test.com", "B", "hash");
        userDAO.addUser(user1);
        userDAO.addUser(user2);
        assertEquals(1, user1.getUserId());
        assertEquals(2, user2.getUserId());
    }
}
